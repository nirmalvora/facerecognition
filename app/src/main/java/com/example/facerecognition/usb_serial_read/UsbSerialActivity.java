package com.example.facerecognition.usb_serial_read;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.facerecognition.R;
import com.example.facerecognition.databinding.ActivityMainBinding;
import com.example.facerecognition.databinding.ActivityUsbSerialBinding;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;

public class UsbSerialActivity extends AppCompatActivity  implements SerialInputOutputManager.Listener {
    private enum UsbPermission { Unknown, Requested, Granted, Denied }

    private final ArrayList<UsbDeviceModel> listItems = new ArrayList<>();
    private int deviceId, portNum;
    private int  baudRate =115200;
    private boolean withIoManager = true;
    private UsbSerialPort usbSerialPort;
    private static final String INTENT_ACTION_GRANT_USB = "com.example.facerecognition" + ".GRANT_USB";
    private SerialInputOutputManager usbIoManager;
    private BroadcastReceiver broadcastReceiver;
    private Handler mainLooper;
    private boolean connected = false;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private ActivityUsbSerialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsbSerialBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mainLooper = new Handler(Looper.getMainLooper());
        getDevices();
    }



    void getDevices() {
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        for(UsbDevice device : usbManager.getDeviceList().values()) {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if(driver == null) {
                driver = usbCustomProber.probeDevice(device);
            }
            if(driver != null) {
                for(int port = 0; port < driver.getPorts().size(); port++)
                    listItems.add(new UsbDeviceModel(device, port, driver));
            } else {
                listItems.add(new UsbDeviceModel(device, 0, null));
            }
        }
        if(listItems.size()!=0) {
            connectToDevice(0);
        }
    }

    private void connectToDevice(int devicePosition) {
        deviceId = listItems.get(devicePosition).device.getDeviceId();
        portNum =  listItems.get(devicePosition).port;
        setPermissionListener();

    }
    private void setPermissionListener() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    binding.receiveText.append(usbPermission.toString());
                    connect();
                }
            }
        };

        this.registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        if(usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::connect);
    }

    private void connect() {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        for(UsbDevice v : usbManager.getDeviceList().values())
            if(v.getDeviceId() == deviceId)
                device = v;
        if(device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if(driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            int flags = PendingIntent.FLAG_MUTABLE;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        binding.receiveText.append(usbPermission.toString());
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            try{
                usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
                usbSerialPort.setRTS(true);
                usbSerialPort.setDTR(true);
            }catch (UnsupportedOperationException e){
                status("unsupport setparameters");
            }
            if(withIoManager) {
                usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
                usbIoManager.start();
            }
            status("connected");
            connected = true;
        } catch (Exception e) {
            status("connection failed: " + e.getMessage());
            disconnect();
        }
    }
    private void disconnect() {
        connected = false;
        if(usbIoManager != null) {
            usbIoManager.setListener(null);
            usbIoManager.stop();
        }
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }
    void status(String str) {
        binding.receiveText.append("Connection Status"+ "status: "+str+'\n' );
        Log.e("Connection Status", "status: "+str+'\n' );
    }

    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
            receive(data);
        });
    }

    @Override
    public void onRunError(Exception e) {
        mainLooper.post(() -> {
            status("connection lost: " + e.getMessage());
            disconnect();
        });
    }

    private void receive(byte[] data) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        if(data.length ==32) {
            spn.append(new String(data)).append("\n");
            binding.receiveText.append(spn);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        if(usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::connect);
    }
    @Override
    public void onPause() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        if(connected) {
            status("disconnected");
            disconnect();
        }
        this.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        if(connected) {
            status("disconnected");
            disconnect();
        }
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
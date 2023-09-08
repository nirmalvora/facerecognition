package com.example.facerecognition.usb_serial_read;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

 class UsbDeviceModel {
    UsbDevice device;
    int port;
    UsbSerialDriver driver;

    UsbDeviceModel(UsbDevice device, int port, UsbSerialDriver driver) {
        this.device = device;
        this.port = port;
        this.driver = driver;
    }
}
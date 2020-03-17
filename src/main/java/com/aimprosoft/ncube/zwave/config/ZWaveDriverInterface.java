package com.aimprosoft.ncube.zwave.config;

public class ZWaveDriverInterface {
    private final HardwareAccess fakeDeviceConnection;

    public ZWaveDriverInterface(final HardwareAccess fakeDeviceConnection) {
        this.fakeDeviceConnection = fakeDeviceConnection;
    }

    public void readAttribute(final String deviceId, final String attributeId) {
        fakeDeviceConnection.downStreamMessage(String.format("READ_ATTRIBUTE:%s,%s", deviceId, attributeId));
    }

    public void writeAttribute(final String deviceId, final String attributeId, final String value) {
        fakeDeviceConnection.downStreamMessage(String.format("WRITE_ATTRIBUTE:%s,%s,%s", deviceId, attributeId, value));
    }
}

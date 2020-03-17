package com.aimprosoft.ncube.zwave.service;

import io.imont.lion.api.Device;
import io.imont.lion.api.DeviceId;

import java.util.Map;

public interface ZWaveService {
    void addDevice();

    Map<DeviceId, Device> getReadyToConnectDevices();
}

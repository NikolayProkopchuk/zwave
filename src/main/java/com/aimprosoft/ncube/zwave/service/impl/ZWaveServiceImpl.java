package com.aimprosoft.ncube.zwave.service.impl;

import com.aimprosoft.ncube.zwave.service.ZWaveService;
import io.imont.lion.Lion;
import io.imont.lion.api.Device;
import io.imont.lion.api.DeviceId;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@DependsOn("lionCoordinator")
public class ZWaveServiceImpl implements ZWaveService {

    private Lion lionCoordinator;

    public ZWaveServiceImpl(Lion lionCoordinator) {
        this.lionCoordinator = lionCoordinator;
    }

    @Override
    public void addDevice() {

    }

    @Override
    public Map<DeviceId, Device> getReadyToConnectDevices() {
        Map<DeviceId, Device> devices = lionCoordinator.getAllDevices();
        return devices;
    }

    @PostConstruct
    private void startLion() {
        this.lionCoordinator.start();
    }

}

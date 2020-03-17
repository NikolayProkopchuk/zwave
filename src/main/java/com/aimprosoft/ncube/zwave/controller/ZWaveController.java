package com.aimprosoft.ncube.zwave.controller;

import com.aimprosoft.ncube.zwave.service.ZWaveService;
import io.imont.lion.api.Device;
import io.imont.lion.api.DeviceId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v3/z_devices")
public class ZWaveController {

    private ZWaveService zWaveService;

    public ZWaveController(ZWaveService zWaveService) {
        this.zWaveService = zWaveService;
    }

    @PostMapping
    public void addDevice() {
        zWaveService.addDevice();
    }

    @GetMapping("/discovered")
    public Map<DeviceId, Device> getReadyToConnectDevices() {
        return zWaveService.getReadyToConnectDevices();
    }

}

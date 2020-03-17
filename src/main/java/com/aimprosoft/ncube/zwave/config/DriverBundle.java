/*
 * Copyright (C) 2018 IMONT Technologies Limited
 *
 */
package com.aimprosoft.ncube.zwave.config;

import io.imont.lion.drivers.LionBundle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DriverBundle implements LionBundle {

    private static final String[] DRIVERS = new String[] {
            "example-dimmer.js"
    };

    @Override
    public Map<String, URL> getLibraries() {
        return Collections.emptyMap();
    }

    @Override
    public List<URL> getDrivers() {
        List<URL> ret = new ArrayList<>();
        for (String driver : DRIVERS) {
            ret.add(getClass().getClassLoader().getResource("drivers/" + driver));
        }
        return ret;
    }
}

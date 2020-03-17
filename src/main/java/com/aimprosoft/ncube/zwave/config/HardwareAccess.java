package com.aimprosoft.ncube.zwave.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A connection to a simulated device coordinator running our example network protocol.
 *
 * In order to show how a network layer is implemented, a simple network protocol has been developed.  This class implements our example protocol and would be
 * considered as the device coordinator in this imaginary network.  Whenever put into acquisition mode, it will "find" and emit a new dimmable lightbulb.
 * These bulbs can then be toggled on and off, dimmed or removed from the network.
 *
 */
public class HardwareAccess {

    private static final Logger log = LoggerFactory.getLogger(HardwareAccess.class);

    private static final String ON_OFF_ATTR_ID = "0";


    private Map<String, Map<String, String>> deviceStatesForAllDevices = new HashMap<>();

    private Subject<String, String> msgSubject;

    public void connect() {
        this.msgSubject = new SerializedSubject<>(PublishSubject.create());
    }

    public void disconnect() {
        msgSubject.onCompleted();
    }

    public Observable<String> upStreamMessages() {
        return msgSubject.asObservable().delay(500, TimeUnit.MILLISECONDS);
    }

    public void downStreamMessage(final String msg) {
        log.info("Received downStreamMessage:{}", msg);
        String prefix = msg.split(":")[0];
        String[] values = msg.split(":")[1].split(",");

        switch (prefix) {
            case "ACQUISITION_MODE":
                if (values[0].equals("OPEN")) {
                    openNetwork();
                } else if (values[0].equals("CLOSE")) {
                    closeNetwork();
                }
                break;
            case "READ_ATTRIBUTE":
                // READ_ATTRIBUTE messages are in the format READ_ATTRIBUTE:<DEVICE_ID>,<ATTRIBUTE_ID>
                String deviceId = values[0];
                String attribId = values[1];
                Map<String, String> deviceState = deviceStatesForAllDevices.get(deviceId);
                if (deviceState == null) {
                    // This must be a device which was create in a previous session.  Re-populate the map for this device with default values.
                    deviceState = new HashMap<>();
                    deviceState.put(ON_OFF_ATTR_ID, "0");
                    deviceStatesForAllDevices.put(deviceId, deviceState);
                }
                String value = deviceState.get(attribId);
                String rptMsg = String.format("REPORT_ATTRIBUTE:%s,%s,%s", deviceId, attribId, value);
                log.info("Sending upStreamMessage:{}", rptMsg);
                msgSubject.onNext(rptMsg);
                break;
            case "WRITE_ATTRIBUTE":
                // WRITE_ATTRIBUTE messages are in the format WRITE_ATTRIBUTE:<DEVICE_ID>,<ATTRIBUTE_ID>,<VALUE>
                writeAttribute(values[0], values[1], values[2]);
                break;
            case "REMOVE_DEVICE":
                removeDevice(values[0]);
                break;
            default:
        }
    }

    private void openNetwork() {
        // Emit a new device, each time acquisition is enabled.

        String deviceId = UUID.randomUUID().toString();
        Map<String, String> attribStateMap = new HashMap<>();
        attribStateMap.put(ON_OFF_ATTR_ID, "0");
        deviceStatesForAllDevices.put(deviceId, attribStateMap);

        msgSubject.onNext(String.format("NEW_DEVICE:%s,%s,%s,%s", deviceId, "IMONT", "SUPER_DIMMER", "1.0"));
    }

    private void closeNetwork() {
        // Do nothing.
    }

    private void removeDevice(final String deviceId) {
        deviceStatesForAllDevices.remove(deviceId);
    }

    private void writeAttribute(final String deviceId, final String attributeId, final String value) {
        Map<String, String> deviceState = deviceStatesForAllDevices.get(deviceId);
        deviceState.put(attributeId, value);
        // Report back the given value asynchronously via the events stream.
        msgSubject.onNext(String.format("REPORT_ATTRIBUTE:%s,%s,%s", deviceId, attributeId, value));
    }
}

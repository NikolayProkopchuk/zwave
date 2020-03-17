package com.aimprosoft.ncube.zwave.config;

import io.imont.lion.drivers.DriverMetadata;
import io.imont.lion.network.AbstractNetworkLayer;
import io.imont.lion.network.DeviceCandidate;
import io.imont.lion.network.NetworkContext;
import io.imont.lion.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class ZWaveNetworkLayer extends AbstractNetworkLayer {
    private static final Logger log = LoggerFactory.getLogger(ZWaveNetworkLayer.class);

    private static final String NETWORK_IDENTIFIER = "z-wave";

    private Subject<DeviceCandidate, DeviceCandidate> candidateSubject;

    private Subject<NetworkEvent, NetworkEvent> eventSubject;

    private HardwareAccess hardwareAccess = new HardwareAccess();

    private ZWaveDriverInterface drvInf = new ZWaveDriverInterface(hardwareAccess);

    public ZWaveNetworkLayer() {
        candidateSubject = new SerializedSubject<>(PublishSubject.create());
        eventSubject = new SerializedSubject<>(PublishSubject.create());
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    @Override
    public String getIdentifier() {
        return NETWORK_IDENTIFIER;
    }

    @Override
    public Object getPublicInterface() {
        return drvInf;
    }

    @Override
    public Observable<NetworkEvent> events() {
        return eventSubject.asObservable();
    }

    @Override
    public Observable<DeviceCandidate> discover() {
        return newDevices().doOnSubscribe(() -> hardwareAccess.downStreamMessage("ACQUISITION_MODE:OPEN"));
    }

    @Override
    public Observable<DeviceCandidate> newDevices() {
        return candidateSubject.asObservable();
    }

    @Override
    public void stopDiscovering() {
        hardwareAccess.downStreamMessage("ACQUISITION_MODE:CLOSE");
    }

    @Override
    public void removeDevice(final String deviceId) {
        hardwareAccess.downStreamMessage(String.format("REMOVE_DEVICE:%s", deviceId));
    }

    @Override
    public void start(final NetworkContext networkContext) {
        hardwareAccess.connect();
        hardwareAccess.upStreamMessages().subscribe(msg -> {
            String prefix = msg.split(":")[0];
            String[] values = msg.split(":")[1].split(",");
            String deviceId = values[0];

            switch (prefix) {
                case "NEW_DEVICE":
                    String manufacturer = values[1];
                    String model = values[2];
                    String version = values[3];
                    DeviceCandidate dc = new DeviceCandidate.Builder(NETWORK_IDENTIFIER, deviceId)
                            .autoAcquired(true).hardwareAddress(deviceId).driverMetadata(
                                    new DriverMetadata(NETWORK_IDENTIFIER, manufacturer, model, version)).build();
                    candidateSubject.onNext(dc);
                    break;
                case "REPORT_ATTRIBUTE":
                    String attributeId = values[1];
                    String value = values[2];
                    // Note, the type field here much be matched with an "onXXX" function in the driver.  In this case, onReportAttribute().
                    NetworkEvent evt = new NetworkEvent(deviceId, "ReportAttribute", String.format("%s=%s", attributeId, value), null);
                    eventSubject.onNext(evt);
                    break;
                default:
                    log.warn("Unrecognised message received from network");
            }
        });

    }

    @Override
    public void recoverDevice(final String s) {
        log.info("Recovering device {}", s);
    }

    @Override
    public Single<DeviceCandidate> interrogate(final DeviceCandidate deviceCandidate) {
        return Single.just(deviceCandidate);
    }

    @Override
    public void close() {
        hardwareAccess.disconnect();
    }
}

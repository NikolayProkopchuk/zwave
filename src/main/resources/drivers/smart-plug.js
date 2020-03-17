/*
 * Copyright (C) 2018 IMONT Technologies Limited
 *
 */
function metadata() {
    return {
        "network": "Z-Wave",
        "manufacturer": "Everspring",
        "model": "SMART_PLUG",
        "hardwareVersion": "1.0",
        "primaryFeature": framework.stdevents.levelControl.LEVEL_CONTROL_FEATURE
    }
}

const ON_OFF_ATTR_ID = "0";

function onLoad() {
    framework.example.readAttribute(context.id, ON_OFF_ATTR_ID);
    context.log.info("Hello my name is {}", "Mole")
}

function onReportAttribute(evt) {
    context.log.debug("onReportAttribute");
    var attributeId = evt.message.split("=")[0];
    var value = evt.message.split("=")[1];
    context.log.warn("AttributeId={}", attributeId);
    context.log.warn("Value={}", value);
    if (attributeId == "0") {
        reportIfChanged(context.id, framework.stdevents.onOff.ON_OFF_EVENT, "" + value, null);
    } else if (attributeId == "1") {
        reportIfChanged(context.id, framework.stdevents.levelControl.LEVEL_EVENT, "" + value, null);
    }
}

function onRequestEvent(evt) {
    context.log.debug("onRequestEvent:{}", evt);
    if (evt.key == framework.stdevents.onOff.ON_OFF_EVENT.FQEventKey) {
        context.log.info("Switching device {} onOff {}", context.id, evt.value);
        framework.example.writeAttribute(context.id, ON_OFF_ATTR_ID, evt.value);
    }
}

function acquire(device) {
    return true;
}

function reportIfChanged(id, key, value, metadata) {
    var currentValue = context.cache.get(key);
    if (currentValue != value) {
        framework.db.raiseEvent(id, key, value, metadata);
        context.cache.put(key, value);
    }
}

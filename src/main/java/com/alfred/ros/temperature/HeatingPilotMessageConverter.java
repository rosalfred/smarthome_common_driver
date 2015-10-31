package com.alfred.ros.temperature;

import media_msgs.Command;

import org.ros.node.ConnectedNode;

import com.alfred.ros.core.MessageConverter;

import heater_msgs.HeatingPilotAction;

public class HeatingPilotMessageConverter implements MessageConverter<HeatingPilotAction> {
    public HeatingPilotAction toMessage(ConnectedNode node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

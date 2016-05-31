package org.rosbuilding.common.temperature;

import smarthome_comm_msgs.Command;

import org.ros.node.ConnectedNode;
import org.rosbuilding.common.MessageConverter;

import smarthome_heater_msgs.HeatingPilotAction;

public class HeatingPilotMessageConverter implements MessageConverter<HeatingPilotAction> {
    public HeatingPilotAction toMessage(ConnectedNode node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

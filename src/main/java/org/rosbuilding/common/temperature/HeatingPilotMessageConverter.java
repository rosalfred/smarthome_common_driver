package org.rosbuilding.common.temperature;

import rosbuilding_msgs.Command;

import org.ros.node.ConnectedNode;
import org.rosbuilding.common.MessageConverter;

import heater_msgs.HeatingPilotAction;

public class HeatingPilotMessageConverter implements MessageConverter<HeatingPilotAction> {
    public HeatingPilotAction toMessage(ConnectedNode node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

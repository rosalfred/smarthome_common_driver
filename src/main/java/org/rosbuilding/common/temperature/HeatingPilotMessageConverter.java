package org.rosbuilding.common.temperature;

import smarthome_comm_msgs.msg.Command;
import smarthome_heater_msgs.msg.HeatingPilotAction;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.MessageConverter;

public class HeatingPilotMessageConverter implements MessageConverter<HeatingPilotAction> {
    public HeatingPilotAction toMessage(Node node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

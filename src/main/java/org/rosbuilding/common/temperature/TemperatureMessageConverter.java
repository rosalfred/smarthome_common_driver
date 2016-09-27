package org.rosbuilding.common.temperature;

import sensor_msgs.msg.Temperature;
import smarthome_comm_msgs.msg.Command;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.MessageConverter;

public class TemperatureMessageConverter implements MessageConverter<Temperature> {
    public Temperature toMessage(Node node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

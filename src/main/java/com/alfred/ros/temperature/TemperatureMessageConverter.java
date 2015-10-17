package com.alfred.ros.temperature;

import media_msgs.Command;
import sensor_msgs.Temperature;

import org.ros.node.ConnectedNode;

import com.alfred.ros.core.MessageConverter;

public class TemperatureMessageConverter implements MessageConverter<Temperature> {
    public Temperature toMessage(ConnectedNode node, Command command) {
        throw new RuntimeException("Not implemented!");
    }
}

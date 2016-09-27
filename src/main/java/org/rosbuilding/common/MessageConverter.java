package org.rosbuilding.common;

import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.Node;

import smarthome_comm_msgs.msg.Command;


public interface MessageConverter<TMessage extends Message> {
    TMessage toMessage(Node node, Command command);
}

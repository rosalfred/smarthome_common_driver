package com.alfred.ros.core;

import media_msgs.Command;

import org.ros.internal.message.Message;
import org.ros.node.ConnectedNode;

public interface MessageConverter<TMessage extends Message> {
    TMessage toMessage(ConnectedNode node, Command command);
}

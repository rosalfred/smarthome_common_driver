package org.rosbuilding.common.light;

import smarthome_comm_msgs.msg.Command;
import smarthome_light_msgs.msg.LightAction;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.MessageConverter;

public class LightMessageConverter implements MessageConverter<LightAction> {

    public LightAction toMessage(Node node, Command command) {
        LightAction lightAction = null;

        if (node != null) {
            lightAction = new LightAction();

            String[] hsb = command.getSubject().split(",");

            lightAction.getHsb().setHue(Integer.parseInt(hsb[0]));
            lightAction.getHsb().setSaturation(Integer.parseInt(hsb[0]));
            lightAction.getHsb().setBrightness(Integer.parseInt(hsb[0]));
        }

        return lightAction;
    }
}

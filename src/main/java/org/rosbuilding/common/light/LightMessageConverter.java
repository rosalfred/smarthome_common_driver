package org.rosbuilding.common.light;

import java.util.Arrays;
import java.util.List;

import smarthome_comm_msgs.msg.Command;
import smarthome_light_msgs.msg.LightAction;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.IModule;
import org.rosbuilding.common.MessageConverter;

public class LightMessageConverter implements MessageConverter<LightAction> {
    public LightAction toMessage(Node node, Command command) {
        LightAction lightAction = null;

        if (node != null) {
            lightAction = new LightAction();

//            String[] action = command.getAction().split(IModule.SEP);
//
//            if (action.length > 1) {
//                lightAction.setType(action[0]);
//                lightAction.setMethod(action[1]);
//            } else {
//                lightAction.setMethod(action[0]);
//            }
//
//            String subject = command.getSubject();
//            if (subject.startsWith(IModule.URI_DATA)) {
//                List<String> data = Arrays.asList(
//                        subject.substring(IModule.URI_DATA.length())
//                        .split(IModule.SEP));
//
//                lightAction.setData(data);
//            } else {
//                lightAction.setUri(command.getSubject());
//            }
        }

        return lightAction;
    }
}

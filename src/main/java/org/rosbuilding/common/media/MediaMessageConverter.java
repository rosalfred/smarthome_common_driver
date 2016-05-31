package org.rosbuilding.common.media;

import java.util.Arrays;
import java.util.List;

import smarthome_media_msgs.MediaAction;
import smarthome_comm_msgs.Command;

import org.ros.node.ConnectedNode;
import org.rosbuilding.common.IModule;
import org.rosbuilding.common.MessageConverter;

public class MediaMessageConverter implements MessageConverter<MediaAction> {
    public MediaAction toMessage(ConnectedNode node, Command command) {
        MediaAction mediaAction = null;

        if (node != null) {
            mediaAction = node.getTopicMessageFactory()
                    .newFromType(MediaAction._TYPE);

            String[] action = command.getAction().split(IModule.SEP);

            if (action.length > 1) {
                mediaAction.setType(action[0]);
                mediaAction.setMethod(action[1]);
            } else {
                mediaAction.setMethod(action[0]);
            }

            String subject = command.getSubject();
            if (subject.startsWith(IModule.URI_DATA)) {
                List<String> data = Arrays.asList(
                        subject.substring(IModule.URI_DATA.length())
                        .split(IModule.SEP));

                mediaAction.setData(data);
            } else {
                mediaAction.setUri(command.getSubject());
            }
        }
        return mediaAction;
    }
}

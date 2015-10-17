package com.alfred.ros.media;

import java.util.Arrays;
import java.util.List;

import media_msgs.Command;
import media_msgs.MediaAction;

import org.ros.node.ConnectedNode;

import com.alfred.ros.core.IModule;
import com.alfred.ros.core.MessageConverter;

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

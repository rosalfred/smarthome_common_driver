/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.media;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.ros.node.ConnectedNode;

import com.google.common.base.Joiner;

import media_msgs.Command;
import media_msgs.MediaAction;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public class CommandUtil {

    public enum Action {
        SAY("say"),
        SHOW("show"),
        VIEW("mediamovie" + IPlayer.OP_OPEN);

        private String label;
        private Action(String label) {
            this.label = label;
        }

        public String getValue() {
            return this.label;
        }
    }

    public static MediaAction toMediaAction(ConnectedNode node, Command command) {
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

    public static Command toCommand(ConnectedNode node, MediaAction mediaAction) {
        Command command = null;

        if (node != null) {
            command = node.getTopicMessageFactory().newFromType(Command._TYPE);
            command.setAction(mediaAction.getType() + IModule.SEP + mediaAction.getMethod());
            command.setSubject(mediaAction.getUri());
        }

        return command;
    }

    public static Command toCommand(ConnectedNode node, String iplayer, URI uri, String type) {
        Command command = null;

        if (node != null) {
            command = node.getTopicMessageFactory().newFromType(Command._TYPE);
            command.setAction(type + IModule.SEP + iplayer);
            command.setSubject(uri.toString());
        }

        return command;
    }

    public static Command toCommand(ConnectedNode node, String iplayer, String...data) {
        Command command = node.getTopicMessageFactory()
                .newFromType(Command._TYPE);

        command.setAction(IModule.SEP + iplayer);
        command.setSubject(IModule.URI_DATA + Joiner.on(IModule.SEP).join(data));

        return command;
    }
}

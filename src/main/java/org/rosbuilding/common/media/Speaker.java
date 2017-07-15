/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common.media;

import org.rosbuilding.common.Module;

import smarthome_media_msgs.msg.MediaAction;
import smarthome_media_msgs.msg.StateData;
import smarthome_media_msgs.srv.ToggleMuteSpeaker_Request;
import smarthome_media_msgs.srv.ToggleMuteSpeaker_Response;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public abstract class Speaker extends Module<StateData, MediaAction> {
    public static final int LEVEL_MIN = 0;
    public static final int LEVEL_MAX = 100;
    public static final int LEVEL_STEP = 10;

    public static final String OP_VOLUME_UP    = "volumeup";
    public static final String OP_VOLUME_DOWN  = "volumedown";
    public static final String OP_MUTE_TOGGLE  = "mutetoggle";
    public static final String OP_VOLUME_TO    = "volumeto";
    public static final String OP_MUTE         = "mute";
    public static final String OP_CHANNEL      = "channel";

    /**
     * Method callback for SpeakerMuteToggle service.
     * @param request
     * @param response
     */
    public abstract void handleSpeakerMuteToggle(ToggleMuteSpeaker_Request request, ToggleMuteSpeaker_Response response);
}

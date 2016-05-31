/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common.media;

import org.rosbuilding.common.IModule;

import smarthome_media_msgs.MediaAction;
import smarthome_media_msgs.StateData;
import smarthome_media_msgs.ToggleMuteSpeakerRequest;
import smarthome_media_msgs.ToggleMuteSpeakerResponse;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface ISpeaker extends IModule<StateData, MediaAction> {
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
    void handleSpeakerMuteToggle(ToggleMuteSpeakerRequest request,
            ToggleMuteSpeakerResponse response);
}

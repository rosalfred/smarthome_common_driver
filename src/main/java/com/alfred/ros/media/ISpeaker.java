package com.alfred.ros.media;

import media_msgs.SpeakerInfo;
import media_msgs.ToggleMuteSpeakerRequest;
import media_msgs.ToggleMuteSpeakerResponse;

public interface ISpeaker extends IModule<SpeakerInfo> {
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

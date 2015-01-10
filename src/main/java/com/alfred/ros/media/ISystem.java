package com.alfred.ros.media;

import media_msgs.StateData;

public interface ISystem extends IModule<StateData> {

    public static final String OP_POWER    = "power";
    public static final String OP_SHUTDOWN = "shutdown";

}

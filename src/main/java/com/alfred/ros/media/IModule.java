package com.alfred.ros.media;

import java.io.IOException;

import media_msgs.MediaAction;
import media_msgs.StateData;

import org.ros.internal.message.Message;

public interface IModule<T extends Message> {

    public static final String SEP = "::";
    public static final String URI_DATA = "data://";
    
    /**
     * Refresh stateDate info.
     * @param stateData Current {@link StateData}
     */
    void load(T stateData);

    /**
     * Method callback for publisher.
     * @param message
     * @param stateData
     * @throws InterruptedException 
     * @throws IOException 
     */
    void callbackCmdAction(MediaAction message, StateData stateData) 
            throws IOException, InterruptedException;
}

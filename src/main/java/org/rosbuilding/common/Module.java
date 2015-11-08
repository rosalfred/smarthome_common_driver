package org.rosbuilding.common;

import java.io.IOException;

import org.ros.internal.message.Message;

public class Module<T extends Message, V extends Message> implements IModule<T, V> {

    @Override
    public void load(T stateData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void callbackCmdAction(V message, T stateData) throws IOException,
            InterruptedException {
        // TODO Auto-generated method stub

    }

}

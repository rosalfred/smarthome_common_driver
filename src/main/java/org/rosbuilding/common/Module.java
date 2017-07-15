package org.rosbuilding.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ros2.rcljava.internal.message.Message;


public abstract class Module<T extends Message, V extends Message> implements IModule<T, V> {

    private final List<String> availableMehods = new ArrayList<>();

    protected Module() {
        this.initializeAvailableMethods(this.availableMehods);
    }

    protected abstract void initializeAvailableMethods(List<String> availableMehods);

    @Override
    public List<String> getAvailableMethods() {
        return this.availableMehods;
    }

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

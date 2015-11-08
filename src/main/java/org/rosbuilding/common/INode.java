package org.rosbuilding.common;

import org.ros.internal.message.Message;

public interface INode<TStateData> {
    <T extends Message> T getNewMessageInstance(String type);
    TStateData getStateData();
    void logD(final Object message);
    void logI(final Object message);
    void logE(final Object message);
    void logE(final Exception message);
}

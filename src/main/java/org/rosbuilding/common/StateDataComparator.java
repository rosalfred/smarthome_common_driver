package org.rosbuilding.common;

import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.Node;

import builtin_interfaces.msg.Duration;

public abstract class StateDataComparator<TStateData extends Message> {
    public abstract boolean isEquals(TStateData newStateData, TStateData oldStateData);
    public abstract TStateData makeNewCopy(Node conectedNode, String frameId, TStateData stateData);

    protected static boolean isEqual(Duration stamp1, Duration stamp2) {
        return stamp1 == null && stamp2 != null
                || stamp1 != null && stamp2 == null
                || (stamp1 != null && stamp2 != null
                    && stamp1.getSec() == stamp2.getSec() && stamp1.getNanosec() == stamp2.getNanosec());
    }
}

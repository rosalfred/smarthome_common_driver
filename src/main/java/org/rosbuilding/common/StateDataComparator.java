package org.rosbuilding.common;

import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.Node;

public interface StateDataComparator<TStateData extends Message> {
    boolean isEquals(TStateData newStateData, TStateData oldStateData);
    TStateData makeNewCopy(Node conectedNode, String frameId, TStateData stateData);
}

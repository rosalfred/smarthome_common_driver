package org.rosbuilding.common;

import org.ros.internal.message.Message;
import org.ros.node.ConnectedNode;

public interface StateDataComparator<TStateData extends Message> {
    boolean isEquals(TStateData newStateData, TStateData oldStateData);
    TStateData makeNewCopy(ConnectedNode conectedNode, String frameId, TStateData stateData);
}

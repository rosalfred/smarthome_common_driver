package org.rosbuilding.common.mock;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.NodeSimpleConfig;

public class NodeSimpleConfigMock extends NodeSimpleConfig {

    public static final String NAMESPACE = "namespace";
    public static final String NAMENODE  = "namenode";
    public static final String FRAME     = "framebase";
    public static final int    RATE      = 50;

    public NodeSimpleConfigMock(Node connectedNode, String defaultNameSpace, String defaultNodeName,
            String defaultFixedFrame, int defaultRate) {
        super(connectedNode, defaultNameSpace, defaultNodeName, defaultFixedFrame, defaultRate);
    }

}

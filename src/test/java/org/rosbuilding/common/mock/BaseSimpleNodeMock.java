package org.rosbuilding.common.mock;

import org.rosbuilding.common.BaseSimpleNode;

public class BaseSimpleNodeMock extends BaseSimpleNode<NodeSimpleConfigMock> {

    @Override
    protected NodeSimpleConfigMock makeConfiguration() {
        return new NodeSimpleConfigMock(this.getConnectedNode(),
                NodeSimpleConfigMock.NAMESPACE,
                NodeSimpleConfigMock.NAMENODE,
                NodeSimpleConfigMock.FRAME,
                NodeSimpleConfigMock.RATE);
    }

}

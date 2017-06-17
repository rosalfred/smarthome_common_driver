package org.rosbuilding.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.mock.BaseSimpleNodeMock;
import org.rosbuilding.common.mock.NodeSimpleConfigMock;

import org.junit.Assert;

public class NodeSimpleConfigTest {

    private NodeSimpleConfig configMock;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        Node nodeMock = new BaseSimpleNodeMock().getConnectedNode();
        this.configMock = new NodeSimpleConfigMock(nodeMock,
                NodeSimpleConfigMock.NAMESPACE,
                NodeSimpleConfigMock.NAMENODE,
                NodeSimpleConfigMock.FRAME,
                NodeSimpleConfigMock.RATE);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    @Test
    public final void test() {
        Assert.assertEquals(NodeSimpleConfigMock.FRAME, this.configMock.getFixedFrame());
        Assert.assertEquals(NodeSimpleConfigMock.NAMESPACE, this.configMock.getPrefix());
        Assert.assertEquals(NodeSimpleConfigMock.RATE, this.configMock.getRate());
    }

}

package org.rosbuilding.common;

import java.util.Arrays;

import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;


public abstract class NodeDriverConfig extends NodeSimpleConfig {

    public static final String PARAM_MAC = "~mac";

    private String mac;

    /**
     *
     * @param connectedNode Ros node
     * @param defaultPrefix
     * @param defaultFixedFrame
     * @param defaultRate
     */
    protected NodeDriverConfig(
            final Node connectedNode,
            final String defaultNameSpace,
            final String defaultNodeName,
            final String defaultFixedFrame,
            final int defaultRate,
            final String macAddress) {
        super(connectedNode, defaultNameSpace, defaultNodeName, defaultFixedFrame, defaultRate);

        this.connectedNode.setParameters(
                Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<String>(PARAM_MAC, macAddress)
        ));
    }

    @Override
    protected void loadParameters() {
        super.loadParameters();

        this.mac = this.connectedNode.getParameter(PARAM_MAC).toParameterValue().getStringValue();
    }

    public String getMac() {
        return this.mac;
    }
}

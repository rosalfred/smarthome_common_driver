package org.rosbuilding.common;

import java.util.ArrayList;

import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class NodeDriverConfig extends NodeSimpleConfig {

    public static final String PARAM_MAC = "mac";

    private static final Logger logger = LoggerFactory.getLogger(NodeDriverConfig.class);

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

        this.initParameters(macAddress);
    }

    @Override
    protected void loadParameters() {
        super.loadParameters();

        NodeDriverConfig.logger.debug("Load Configuration... (mac)");

        this.mac = this.connectedNode.getParameter(PARAM_MAC).toParameterValue().getStringValue();
    }

    private void initParameters(final String macAddress) {
        NodeDriverConfig.logger.debug("Initialize configuration... (mac)");

        ArrayList<ParameterVariant<?>> notSet = new ArrayList<>();
        if (this.connectedNode.getParameter(PARAM_MAC) == null) {
            notSet.add(new ParameterVariant<String>(PARAM_MAC, macAddress));
        }

        if (notSet.size() > 0) {
            this.connectedNode.setParameters(notSet);
        }
    }

    public String getMac() {
        return this.mac;
    }
}

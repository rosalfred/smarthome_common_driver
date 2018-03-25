package org.rosbuilding.common;

import java.util.Arrays;

import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class NodeSimpleConfig {

    public static final String PARAM_NAMESPACE  = "prefix";
    public static final String PARAM_NODENAME   = "node";
    public static final String PARAM_RATE       = "rate";
    public static final String PARAM_FRAME      = "fixed_frame";

    private static final Logger logger = LoggerFactory.getLogger(NodeSimpleConfig.class);

    protected final Node connectedNode;

    // Parameters
    private String namespace;
    private String nodeName;
    private String fixedFrame;
    private long rate;

    /**
     *
     * @param connectedNode Ros node
     * @param defaultPrefix
     * @param defaultFixedFrame
     * @param defaultRate
     */
    protected NodeSimpleConfig(
            final Node connectedNode,
            final String defaultNameSpace,
            final String defaultNodeName,
            final String defaultFixedFrame,
            final int defaultRate) {
        this.connectedNode = connectedNode;

        this.connectedNode.getLogger().info("Initialize configuration...");
        NodeSimpleConfig.logger.debug("Initialize configuration... (ns, name, rate, frame)");

        this.connectedNode.setParameters(
                Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<String>(PARAM_NAMESPACE,  defaultNameSpace),
                        new ParameterVariant<String>(PARAM_NODENAME,  defaultNodeName),
                        new ParameterVariant<Long>  (PARAM_RATE,    1L),
                        new ParameterVariant<String>(PARAM_FRAME,   defaultFixedFrame)
        ));
    }

    protected void loadParameters() {
        this.connectedNode.getLogger().info("Load configuration...");
        NodeSimpleConfig.logger.debug("Load Configuration... (ns, name, rate, frame)");

        this.namespace  = this.connectedNode.getParameter(PARAM_NAMESPACE).toParameterValue().getStringValue();
        this.nodeName   = this.connectedNode.getParameter(PARAM_NODENAME).toParameterValue().getStringValue();
        this.fixedFrame = this.connectedNode.getParameter(PARAM_FRAME).toParameterValue().getStringValue();
        this.rate       = this.connectedNode.getParameter(PARAM_RATE).toParameterValue().getIntegerValue();

        // Check zero or negative rate.
        if (this.rate <= 0) {
            this.rate = 1;
            this.connectedNode.setParameters(
                    Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<Long>(PARAM_RATE, this.rate)
            ));
        }
    }

    public String getPrefix() {
        return GraphName.getFullName(this.connectedNode, "~", null);
    }

    public String getFixedFrame() {
        return this.fixedFrame;
    }

    public void setFixedFrame(final String fixedFrame) {
        this.fixedFrame = fixedFrame;
    }

    public long getRate() {
        return this.rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }
}

package org.rosbuilding.common;

import java.util.Arrays;

import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;


public abstract class NodeSimpleConfig {

    public static final String PARAM_NAMESPACE = "~ns";
    public static final String PARAM_NODENAME = "~node";
    public static final String PARAM_RATE = "~rate";
    public static final String PARAM_FRAME = "~fixed_frame";


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
        this.connectedNode.setParameters(
                Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<String>(PARAM_NAMESPACE,  namespace),
                        new ParameterVariant<String>(PARAM_NODENAME,  nodeName),
                        new ParameterVariant<Long>  (PARAM_RATE,    1L),
                        new ParameterVariant<String>(PARAM_FRAME,   defaultFixedFrame)
        ));
    }

    protected void loadParameters() {
        this.connectedNode.getLog().info("Load parameters.");

        this.namespace  = this.connectedNode.getParameter(PARAM_NAMESPACE).toParameterValue().getStringValue();
        this.nodeName   = this.connectedNode.getParameter(PARAM_NODENAME).toParameterValue().getStringValue();
        this.fixedFrame = this.connectedNode.getParameter(PARAM_FRAME).toParameterValue().getStringValue();
        this.rate       = this.connectedNode.getParameter(PARAM_RATE).toParameterValue().getIntegerValue();

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

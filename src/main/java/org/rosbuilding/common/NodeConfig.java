package org.rosbuilding.common;

import java.util.Arrays;

import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;


public abstract class NodeConfig {

    public static final String PARAM_PREFIX = "";
    public static final String PARAM_RATE = "~rate";
    public static final String PARAM_FRAME = "~fixed_frame";
    public static final String PARAM_MAC = "~mac";


    protected final Node connectedNode;

    // Parameters
    private String prefix;
    private String fixedFrame;
    private long rate;
    private String mac;

    /**
     *
     * @param connectedNode Ros node
     * @param defaultPrefix
     * @param defaultFixedFrame
     * @param defaultRate
     */
    protected NodeConfig(
            final Node connectedNode,
            final String defaultPrefix,
            final String defaultFixedFrame,
            final int defaultRate) {

        this.connectedNode = connectedNode;
        this.connectedNode.setParameters(
                Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<String>(PARAM_PREFIX,  defaultPrefix),
                        new ParameterVariant<Long>  (PARAM_RATE,    1L),
                        new ParameterVariant<String>(PARAM_FRAME,   defaultFixedFrame),
                        new ParameterVariant<String>(PARAM_MAC,     "00:00:00:00:00:00")
        ));

        this.prefix     = defaultPrefix;
        this.fixedFrame = defaultFixedFrame;
        this.rate       = defaultRate;
    }

    protected void loadParameters() {
        this.connectedNode.getLog().info("Load parameters.");

        this.prefix     = this.connectedNode.getParameter(PARAM_PREFIX).toParameterValue().getStringValue();
        this.fixedFrame = this.connectedNode.getParameter(PARAM_FRAME).toParameterValue().getStringValue();
        this.rate       = this.connectedNode.getParameter(PARAM_RATE).toParameterValue().getIntegerValue();
        this.mac        = this.connectedNode.getParameter(PARAM_MAC).toParameterValue().getStringValue() ;

        if (this.rate <= 0) {
            this.rate = 1;
            this.connectedNode.setParameters(
                    Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<Long>(PARAM_RATE, this.rate)
            ));
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
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

    public String getMac() {
        return this.mac;
    }

    public void setMac(final String mac) {
        this.mac = mac;
    }
}

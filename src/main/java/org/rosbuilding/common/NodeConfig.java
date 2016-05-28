package org.rosbuilding.common;

import org.ros.dynamic_reconfigure.server.BaseConfig;
import org.ros.node.ConnectedNode;

public abstract class NodeConfig extends BaseConfig {

    public static final String RATE = "rate";

    protected final ConnectedNode connectedNode;

    // Parameters
    private String prefix;
    private String fixedFrame;
    private int rate;
    private String mac;

    /**
     *
     * @param connectedNode Ros node
     * @param defaultPrefix
     * @param defaultFixedFrame
     * @param defaultRate
     */
    protected NodeConfig(ConnectedNode connectedNode,
            String defaultPrefix, String defaultFixedFrame, int defaultRate) {
        super(connectedNode);

        this.connectedNode = connectedNode;
        this.addField(RATE, "int", 0, "rate processus", 1, 0, 200);

        this.prefix = defaultPrefix;
        this.fixedFrame = defaultFixedFrame;
        this.rate = defaultRate;
    }

    protected void loadParameters() {
        //this.logI("Load parameters.");

        this.prefix = String.format("/%s/", this.connectedNode.getParameterTree()
                .getString("~tf_prefix", this.prefix));
        this.fixedFrame = this.connectedNode.getParameterTree()
                .getString("~fixed_frame", this.fixedFrame);
        this.rate = this.connectedNode.getParameterTree()
                .getInteger("~" + RATE, this.rate);
        this.mac = this.connectedNode.getParameterTree()
                .getString("~mac", "00:00:00:00:00:00");

        if (this.rate <= 0) {
            this.rate = 1;
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFixedFrame() {
        return this.fixedFrame;
    }

    public void setFixedFrame(String fixedFrame) {
        this.fixedFrame = fixedFrame;
    }

    public int getRate() {
        return this.rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}

package org.rosbuilding.common;

import java.util.ArrayList;

import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class NodeDriverConnectedConfig extends NodeDriverConfig {

    public static final String PARAM_HOST = "ip";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_USER = "user";
    public static final String PARAM_PASSWORD = "password";

    private static final Logger logger = LoggerFactory.getLogger(NodeDriverConnectedConfig.class);

    private String host;
    private long   port;
    private String user;
    private String password;

    /**
     *
     * @param connectedNode Ros node
     * @param defaultPrefix
     * @param defaultFixedFrame
     * @param defaultRate
     */
    protected NodeDriverConnectedConfig(
            final Node connectedNode,
            final String defaultNameSpace,
            final String defaultNodeName,
            final String defaultFixedFrame,
            final int defaultRate,
            final String macAddress,
            final String host,
            final long   port,
            final String user,
            final String password) {
        super(connectedNode, defaultNameSpace, defaultNodeName, defaultFixedFrame, defaultRate, macAddress);

        this.initParameters(host, port, user, password);
    }

    @Override
    protected void loadParameters() {
        super.loadParameters();

        NodeDriverConnectedConfig.logger.debug("Load Configuration... (ip, port, user, pwd)");

        this.host       = this.connectedNode.getParameter(PARAM_HOST).toParameterValue().getStringValue();
        this.port       = this.connectedNode.getParameter(PARAM_PORT).toParameterValue().getIntegerValue() ;
        this.user       = this.connectedNode.getParameter(PARAM_USER).toParameterValue().getStringValue();
        this.password   = this.connectedNode.getParameter(PARAM_PASSWORD).toParameterValue().getStringValue();

      this.connectedNode.getLogger().info(
          String.format("Dump configuration parameters\nrate \t\t: %s\nprefix \t\t: %s\nfixedFrame \t: %s\nip \t\t: %s\nmac \t\t: %s\nport \t\t: %s\nuser \t\t: %s\npassword \t: %s",
              this.getRate(),
              this.getPrefix(),
              this.getFixedFrame(),
              this.getHost(),
              this.getMac(),
              this.getPort(),
              this.getUser(),
              "*****"));
    }

    private void initParameters(final String host, final long port, final String user, final String password) {
        NodeDriverConnectedConfig.logger.debug("Initialize configuration... (ip, port, user, pwd)");

        ArrayList<ParameterVariant<?>> notSet = new ArrayList<>();
        if (this.connectedNode.getParameter(PARAM_HOST) == null) {
            notSet.add(new ParameterVariant<String>(PARAM_HOST, host));
        }

        if (this.connectedNode.getParameter(PARAM_PORT) == null) {
            notSet.add(new ParameterVariant<Long>(PARAM_PORT, port));
        }

        if (this.connectedNode.getParameter(PARAM_USER) == null) {
            notSet.add(new ParameterVariant<String>(PARAM_USER, user));
        }

        if (this.connectedNode.getParameter(PARAM_PASSWORD) == null) {
            notSet.add(new ParameterVariant<String>(PARAM_PASSWORD, password));
        }

        if (notSet.size() > 0) {
            this.connectedNode.setParameters(notSet);
        }
    }

    public String getHost() {
        return this.host;
    }

    public long getPort() {
        return this.port;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }
}

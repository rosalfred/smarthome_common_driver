package org.rosbuilding.common;

import java.util.Arrays;

import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.parameter.ParameterVariant;


public abstract class NodeDriverConnectedConfig extends NodeDriverConfig {

    public static final String PARAM_HOST = "~ip";
    public static final String PARAM_PORT = "~port";
    public static final String PARAM_USER = "~user";
    public static final String PARAM_PASSWORD = "~password";

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

        this.connectedNode.setParameters(
                Arrays.<ParameterVariant<?>>asList(
                        new ParameterVariant<String>(PARAM_HOST,     host),
                        new ParameterVariant<Long>(PARAM_PORT,       port),
                        new ParameterVariant<String>(PARAM_USER,     user),
                        new ParameterVariant<String>(PARAM_PASSWORD, password)
        ));
    }

    @Override
    protected void loadParameters() {
        super.loadParameters();

        this.host       = this.connectedNode.getParameter(PARAM_HOST).toParameterValue().getStringValue();
        this.port       = this.connectedNode.getParameter(PARAM_PORT).toParameterValue().getIntegerValue() ;
        this.user       = this.connectedNode.getParameter(PARAM_USER).toParameterValue().getStringValue();
        this.password   = this.connectedNode.getParameter(PARAM_PASSWORD).toParameterValue().getStringValue();

      this.connectedNode.getLog().info(
      String.format("rate : %s\nprefix : %s\nfixedFrame : %s\nip : %s\nmac : %s\nport : %s\nuser : %s\npassword : %s",
              this.getRate(),
              this.getPrefix(),
              this.getFixedFrame(),
              this.getHost(),
              this.getMac(),
              this.getPort(),
              this.getUser(),
              this.getPassword()));
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

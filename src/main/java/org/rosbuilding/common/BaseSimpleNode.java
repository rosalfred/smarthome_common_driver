/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common;

import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 */
public abstract class BaseSimpleNode<TConfiguration extends NodeSimpleConfig> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseSimpleNode.class);

    protected Node connectedNode;
    protected TConfiguration configuration;

    protected abstract TConfiguration getConfig();

    /**
     * Load parameters of launcher
     */
    protected void loadParameters() { }

    /**
     * Initialize all node publishers & subscribers Topics.
     */
    protected void initTopics() { }

    public final Node getConnectedNode() {
        return this.connectedNode;
    }

    public GraphName getDefaultNodeName() {
        return null; //GraphName.of(this.nodeName);
    }

    public void onStart(final Node connectedNode) {
        this.connectedNode = connectedNode;

        this.configuration = this.getConfig();
        this.configuration.loadParameters();

        this.logI(String.format("Start %s node...", this.connectedNode.getName()));
    }

    public void onShutdown(Node node) {
        this.logI("Stop node !");

        this.connectedNode = null;
    }

    public TConfiguration onReconfigure(TConfiguration config, int level) {
//      this.configuration.setRate(
//              config.getInteger(NodeConfig.RATE, this.configuration.getRate()));

        return config;
    }

    /**
     * On node error is throw.
     */
    public void onError(Node node, Throwable throwable) {
        this.logE(throwable.getMessage());
    }

    // Log assessors
    /**
     * Log a message with debug log level.
     * @param message this message
     */
    public void logD(final Object message) {
        this.connectedNode.getLog().debug(message);
    }

    /**
     * Log a message with info log level.
     * @param message this message
     */
    public void logI(final Object message) {
        this.connectedNode.getLog().info(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Object message) {
        this.connectedNode.getLog().error(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Exception message) {
        this.connectedNode.getLog().error(message.getStackTrace());
    }
}

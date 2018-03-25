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

    /** Node */
    protected Node connectedNode;

    /** Configuration */
    protected TConfiguration configuration;

    /**
     * @return Lazy instance of configuration.
     */
    protected abstract TConfiguration makeConfiguration();

    public BaseSimpleNode() {
    }

    /**
     * Load parameters of launcher
     */
    protected void loadParameters() {
        BaseSimpleNode.logger.debug("Load parameters.");
        this.configuration.loadParameters();
    }

    /**
     * Initialize all node publishers & subscribers Topics.
     */
    protected void initTopics() {
        BaseSimpleNode.logger.debug("Initialize topics.");
    }

    public void onStart(final Node connectedNode) {
        BaseSimpleNode.logger.debug("onStart event !");

        this.connectedNode = connectedNode;
        this.configuration = this.makeConfiguration();
        this.configuration.loadParameters();

        this.logI(String.format("Started %s node !", this.connectedNode.getName()));
    }

    public void onStarted() {
        BaseSimpleNode.logger.debug("onStarted event !");
    }

    public void onShutdown() {
        BaseSimpleNode.logger.debug("onShutdown event !");

        // Need to make before dispose node for log !
        this.logI(String.format("Stoped %s node !", this.connectedNode.getName()));

    }

    public void onShutdowned() {
        BaseSimpleNode.logger.debug("onShutdowned event !");

        this.connectedNode.dispose();
        this.connectedNode = null;
    }

    public TConfiguration onReconfigure(TConfiguration config, int level) {
        BaseSimpleNode.logger.debug("onReconfigure event !");
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
        this.connectedNode.getLogger().debug(message);
    }

    /**
     * Log a message with info log level.
     * @param message this message
     */
    public void logI(final Object message) {
        this.connectedNode.getLogger().info(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Object message) {
        this.connectedNode.getLogger().error(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Exception message) {
        this.connectedNode.getLogger().error(message.getMessage());
    }

    public final Node getConnectedNode() {
        return this.connectedNode;
    }

    public GraphName getDefaultNodeName() {
        return null; //TODO GraphName.of(this.nodeName);
    }

    public TConfiguration getConfiguration() {
        return this.configuration;
    }
}

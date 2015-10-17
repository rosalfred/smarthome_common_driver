/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.core;

import java.util.List;

import media_msgs.Command;

import org.ros.concurrent.CancellableLoop;
import org.ros.dynamic_reconfigure.server.Server;
import org.ros.dynamic_reconfigure.server.Server.ReconfigureListener;
import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import com.alfred.ros.zeroconf.DiscoveredService;
import com.alfred.ros.zeroconf.NodeConfiguration;
import com.alfred.ros.zeroconf.NodeConfiguration.NodeCapability;
import com.alfred.ros.zeroconf.Zeroconf;
import com.google.common.base.Strings;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 * BaseNodeMain<TConfiguration extends NodeConfig,
        TStateData extends Message, TMessage extends Message>
 */
public abstract class BaseNodeMain<TConfiguration extends NodeConfig,
        TStateData extends Message, TMessage extends Message>
        extends AbstractNodeMain
        implements ReconfigureListener<TConfiguration>, INode<TStateData> {

    // Constants
    public static final String PUB_WOL      = "/wol";
    public static final String PUB_STATE    = "statedata";
    public static final String SUB_CMD      = "cmd_action";
    public static final String SUB_STATE_ROBOT    = "robotsay";

    // Fields
    private boolean isConnected = false;
    private ConnectedNode connectedNode;
    private TStateData stateData;
    private TStateData oldStateData;

    // Capabilities
    private List<IModule<TStateData, TMessage>> modules;

    // Topics
    private Server<TConfiguration> serverReconfig;
    private Publisher<TStateData> pubStateData;
    private Publisher<std_msgs.String> pubWol;

    private final StateDataComparator<TStateData> comparator;
    private final MessageConverter<TMessage> converter;
    private final String messageType;
    private final String stateDataType;
    public TConfiguration configuration; //TODO make private
    private final String nodeName;

    private Thread threadZeroconf = null;

    protected BaseNodeMain(
            String nodeName,
            StateDataComparator<TStateData> comparator,
            MessageConverter<TMessage> converter,
            String messageType, String stateDataType) {
        this.nodeName = nodeName;
        this.comparator = comparator;
        this.converter = converter;
        this.messageType = messageType;
        this.stateDataType = stateDataType;
    }

    /**
     * Connect to object
     */
    protected abstract boolean connect();

    /**
     * Load parameters of launcher
     */
    protected void loadParameters() {
        this.serverReconfig = new Server<TConfiguration>(
                this.getConnectedNode(),
                this.configuration,
                this);
    }

    protected abstract void onConnected();
    protected abstract void onDisconnected();

    public final TMessage getNewMessageInstance() {
        return getNewMessageInstance(this.messageType);
    }

    public <T extends Message> T getNewMessageInstance(String type) {
        return this.getConnectedNode().getTopicMessageFactory().newFromType(type);
    }

    public final TStateData getStateData() {
        return this.stateData;
    }

    protected final boolean isConnected() {
        return this.isConnected;
    }

    protected final void addModule(IModule<TStateData, TMessage> module) {
        if (module != null) {
            this.modules.add(module);
        }
    }

    /**
     * Refresh {@link StateData} if connected object is available or try to connect it.
     * @throws InterruptedException
     */
    protected void refreshStateData() throws InterruptedException {

        if (this.isConnected ) {
            this.onConnected();

            for (IModule<TStateData, TMessage> module : this.modules) {
                module.load(this.stateData);
            }
        } else {
            this.isConnected = this.connect();
        }

        if (!this.comparator.isEquals(this.stateData, this.oldStateData)) {
            TStateData stateDataNew = this.comparator.makeNewCopy(
                    this.connectedNode,
                    this.configuration.getFixedFrame(),
                    this.stateData);
            this.oldStateData = stateDataNew;
            this.pubStateData.publish(stateDataNew);
        }

        Thread.sleep(1000 / this.configuration.getRate());
    }

    /**
     * On new message callback.
     * @param message the received media action message.
     */
    public void onNewMessage(TMessage message) {
        if (message != null) {
            try {
                for (IModule<TStateData, TMessage> module : this.modules) {
                    module.callbackCmdAction(message, this.stateData);
                }
            } catch (Exception e) {
                this.logE(e);

                this.isConnected = false;
                this.onDisconnected();
            }
        }
    }

    /**
     * On new message callback.
     * @param command the received generic command message.
     */
    protected void onNewMessage(Command command) {
        String[] wheres = command.getContext().getWhere().split(" ");

        for (String where : wheres) {
            if (this.configuration.getPrefix().contains(where)) {
                this.onNewMessage(this.converter.toMessage(this.connectedNode, command));
            }
        }
    }

    /**
     * Initialize all node publishers & subscribers Topics.
     */
    protected void initTopics() {
        this.pubWol = this.connectedNode.newPublisher(
                PUB_WOL,
                std_msgs.String._TYPE);

        if (!Strings.isNullOrEmpty(this.stateDataType)) {
            this.pubStateData = this.connectedNode.newPublisher(
                    this.configuration.getPrefix() + PUB_STATE,
                    this.stateDataType);
            this.pubStateData.setLatchMode(true);
        }

        if (!Strings.isNullOrEmpty(this.messageType)) {
            // Local topic (mapped by prefix and namespace)
            Subscriber<TMessage> messageSubscriber = this.connectedNode.newSubscriber(
                    this.configuration.getPrefix() + SUB_CMD,
                    this.messageType);

            messageSubscriber.addMessageListener(new MessageListener<TMessage>() {
                @Override
                public void onNewMessage(TMessage msg) {
                    BaseNodeMain.this.onNewMessage(msg);
                }
            });
        }

        if (this.converter != null) {
            // Global topic registration (no prefix...)
            Subscriber<Command> commandSubscriber = this.connectedNode.newSubscriber(
                    "/" + SUB_STATE_ROBOT,
                    Command._TYPE);

            commandSubscriber.addMessageListener(new MessageListener<Command>() {
                @Override
                public void onNewMessage(Command msg) {
                    BaseNodeMain.this.onNewMessage(msg);
                }
            });
        }

        this.initSubscribers();
        this.initPublishers();
    }

    /**
     * Initialize all node services.
     */
    protected void initServices() { }

    protected void initSubscribers() { }
    protected void initPublishers() { }

    public final ConnectedNode getConnectedNode() {
        return this.connectedNode;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(this.nodeName);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        this.connectedNode = connectedNode;

        this.configuration = this.getConfig();

        this.logI(String.format("Start %s node...", this.nodeName));
    }

    @Override
    public void onShutdown(Node node) {
        this.logI("Stop node !");

        if (this.serverReconfig != null)
            this.serverReconfig.close();

        if (this.threadZeroconf != null && this.threadZeroconf.isAlive()) {
            this.threadZeroconf.interrupt();
        }

        super.onShutdown(node);
        this.connectedNode = null;
    }

    /**
     * On node error is throw.
     */
    @Override
    public void onError(Node node, Throwable throwable) {
        super.onError(node, throwable);
        this.logE(throwable.getMessage());
    }

    public void startFinal() {
        this.initialize();

        // This CancellableLoop will be canceled automatically when the node
        // shuts down.
        this.connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                refreshStateData();
            }
        });
    }

    /**
     * On node shutdown start.
     */
    protected void initialize() {
        this.logI("Start main loop.");

        this.loadParameters();
        this.isConnected = false;

        this.initTopics();
        this.initServices();

        this.publishZeroConf();

        this.stateData = this.pubStateData.newMessage();
    }

    public final void wakeOnLan() {
        std_msgs.String message = this.connectedNode.getTopicMessageFactory()
                .newFromType(std_msgs.String._TYPE);
        message.setData(this.configuration.getMac());

        this.pubWol.publish(message);
    }

    private void publishZeroConf() {
        final Zeroconf publisher = new Zeroconf();

        final DiscoveredService service = this.getConfiguration().toDiscoveredService();

        service.name = this.nodeName;
        service.type = "_ros-node._tcp";
        service.domain = "local";
        service.port = 8888;

        this.threadZeroconf = new Thread(new Runnable() {

            @Override
            public void run() {
                publisher.addService(service);
            }
        });

        this.threadZeroconf.start();
    }

    protected abstract TConfiguration getConfig();

    protected NodeConfiguration getConfiguration() {
        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setMasterAddress(this.connectedNode.getMasterUri().getHost());
        configuration.setNodePath(this.configuration.getPrefix().substring(
                0, this.configuration.getPrefix().length() - 1));
        configuration.setNodeType(this.getClass().getName());

        NodeConfiguration.NodePermission permission =
                new NodeConfiguration.NodePermission();
        permission.setExclude(true);
        permission.setName("Permission");
        configuration.getPermissions().add(permission);
        configuration.getCapabilities().add(NodeCapability.ALL);

        return configuration;
    }

    @Override
    public TConfiguration onReconfigure(TConfiguration config, int level) {
        this.configuration.setRate(
                config.getInteger(NodeConfig.RATE, this.configuration.getRate()));

        return config;
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

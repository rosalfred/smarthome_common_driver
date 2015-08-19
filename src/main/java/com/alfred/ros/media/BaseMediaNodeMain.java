/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.media;

import media_msgs.Command;
import media_msgs.MediaAction;
import media_msgs.StateData;

import org.ros.concurrent.CancellableLoop;
import org.ros.dynamic_reconfigure.server.Server;
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

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public abstract class BaseMediaNodeMain
        extends AbstractNodeMain {

    // Constants
    public static final String PUB_WOL      = "/wol";
    public static final String PUB_STATE    = "statedata";
    public static final String SUB_CMD      = "cmd_action";
    public static final String SUB_STATE_ROBOT    = "robotsay";

    public static String nodeName = "unknow";

    // Parameters
    protected String prefix;
    protected String fixedFrame;
    protected int    rate;

    protected String mac;
    protected String host;
    protected int    port;
    protected String user;
    protected String password;

    // Fields
    public boolean isConnected = false;
    protected ConnectedNode connectedNode;
    protected StateData stateData;
    private StateData oldStateData;

    // Capabilities
    protected IPlayer   player  = null;
    protected ISpeaker  speaker = null;
    protected IMonitor  monitor = null;
    protected ISystem   system  = null;
    protected ILibrary  library = null;

    // Topics
    protected Publisher<StateData> pubStateData;
    protected Subscriber<MediaAction> subLocalMediaAction;
    protected Subscriber<Command> subGlobalCommand;
    protected Publisher<std_msgs.String> pubWol;
    protected Server<?> serverReconfig;

    /**
     * Connect to object
     */
    protected abstract void connect();

    /**
     * Load parameters of launcher
     */
    protected abstract void loadParameters();

    /**
     * Refresh {@link StateData} if connected object is available or try to connect it.
     * @throws InterruptedException
     */
    protected void refreshStateData() throws InterruptedException {

        if (this.isConnected ) { //&& this.pingXbmc()) {
            this.stateData.setState(StateData.ENABLE);

            if (this.speaker != null) {
                this.speaker.load(this.stateData.getSpeaker());
            }

            if (this.player != null) {
                this.player.load(this.stateData.getPlayer());
            }

            if (this.monitor != null) {
                this.monitor.load(this.stateData);
            }

            if (this.system != null) {
                this.system.load(this.stateData);
            }
        } else {
            this.connect();
        }

        if (!StateDataUtil.isEqual(this.stateData , this.oldStateData)) {
            StateData stateDataNew = StateDataUtil.makeNewCopy(
                    this.connectedNode,
                    fixedFrame,
                    this.stateData);
            this.oldStateData = stateDataNew;
            this.pubStateData.publish(stateDataNew);
        }
      Thread.sleep(1000 / this.rate);
    }

    /**
     * On new message callback.
     * @param message the received media action message.
     */
    public void onNewMessage(MediaAction message) {
        this.logI(String.format("Command \"%s\"... for %s",
                message.getMethod(),
                message.getUri()));

        try {
            if (this.monitor != null) {
                this.monitor.callbackCmdAction(message, this.stateData);
            }

            if (this.player != null) {
                this.player.callbackCmdAction(message, this.stateData);
            }

            if (this.speaker != null) {
                this.speaker.callbackCmdAction(message, this.stateData);
            }

            if (this.system != null) {
                this.system.callbackCmdAction(message, this.stateData);
            }
        } catch (Exception e) {
            this.logE( e );
            this.isConnected = false;
            this.stateData.setState(StateData.UNKNOWN);
        }
    }

    /**
     * On new message callback.
     * @param message the received generic command message.
     */
    protected void onNewMessage(Command msg) {
        String[] wheres = msg.getContext().getWhere().split(" ");

        for (String where : wheres) {
            if (this.prefix.contains(where)) { // "/home/salon/")) {
                this.onNewMessage(CommandUtil.toMediaAction(connectedNode, msg));
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

        this.pubStateData = this.connectedNode.newPublisher(
                this.prefix + PUB_STATE,
                StateData._TYPE);
        this.pubStateData.setLatchMode(true);

        // Local topic (mapped by prefix and namespace)
        this.subLocalMediaAction = this.connectedNode.newSubscriber(
                this.prefix + SUB_CMD,
                MediaAction._TYPE);
        this.subLocalMediaAction.addMessageListener(new MessageListener<MediaAction>() {

            @Override
            public void onNewMessage(MediaAction msg) {
                BaseMediaNodeMain.this.onNewMessage(msg);
            }

        });

        // Global topic registration (no prefix...)
        this.subGlobalCommand = this.connectedNode.newSubscriber(
                "/" + SUB_STATE_ROBOT,
                Command._TYPE);
        this.subGlobalCommand.addMessageListener(new MessageListener<Command>(){

            @Override
            public void onNewMessage(Command msg) {
                BaseMediaNodeMain.this.onNewMessage(msg);
            }

        });
    }

    /**
     * Initialize all node services.
     */
    protected void initServices() { }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(nodeName);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        this.connectedNode = connectedNode;

        this.logI(String.format("Start %s node...", nodeName));
    }

    @Override
    public void onShutdown(Node node) {
        this.logI("Stop node !");

        if (this.serverReconfig != null)
            this.serverReconfig.close();

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

    public void wakeOnLan() {
        std_msgs.String message = this.connectedNode.getTopicMessageFactory()
        		.newFromType(std_msgs.String._TYPE);
        message.setData(this.getMac());

        this.pubWol.publish(message);
    }

    private void publishZeroConf() {
        final Zeroconf publisher = new Zeroconf();

        final DiscoveredService service =
                this.getConfiguration().toDiscoveredService();

        service.name = nodeName;
        service.type = "_ros-node._tcp";
        service.domain = "local";
        service.port = 8888;

        new Thread(new Runnable() {

            @Override
            public void run() {
                publisher.addService(service);
            }
        }).start();
    }

    protected NodeConfiguration getConfiguration() {
        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setMasterAddress(this.connectedNode.getMasterUri().getHost());
        configuration.setNodePath(this.prefix.substring(0, this.prefix.length() - 1));
        configuration.setNodeType(this.getClass().getName());

        NodeConfiguration.NodePermission permission =
                new NodeConfiguration.NodePermission();
        permission.setExclude(true);
        permission.setName("Permission");
        configuration.getPermissions().add(permission);
        configuration.getCapabilities().add(NodeCapability.ALL);

        return configuration;
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

    // Fields assessors
    /**
     * @return the rate
     */
    public int getRate() {
        return this.rate;
    }

    /**
     * @return the MAC address of network card.
     */
    public String getMac() {
        return this.mac;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }
}

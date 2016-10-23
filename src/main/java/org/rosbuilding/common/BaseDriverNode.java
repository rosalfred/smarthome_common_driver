/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common;

import java.util.ArrayList;
import java.util.List;

import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.topic.Consumer;
import org.ros2.rcljava.node.topic.Publisher;
import org.ros2.rcljava.node.topic.Subscription;

import com.google.common.base.Strings;

import smarthome_media_msgs.msg.StateData;
import smarthome_comm_msgs.msg.Command;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * BaseNodeMain<TConfiguration extends NodeConfig, TStateData extends Message, TMessage extends Message>
 */
public abstract class BaseDriverNode<
        TConfiguration extends NodeConfig,
        TStateData extends Message,
        TMessage extends Message>
        extends BaseSimpleNode<TConfiguration>
{

    // Constants
    public static final String PUB_WOL          = "/wol";
    public static final String PUB_STATE        = "statedata";
    public static final String SUB_CMD          = "cmd_action";
    public static final String SUB_STATE_ROBOT  = "robotsay";

    // Fields
    private boolean isConnected = false;
    private TStateData stateData;
    private TStateData oldStateData;

    // Capabilities
    private List<IModule<TStateData, TMessage>> modules = new ArrayList<>();

    // Topics
    private Publisher<TStateData> pubStateData;
    private Publisher<std_msgs.msg.String> pubWol;

    private final StateDataComparator<TStateData> comparator;
    private final MessageConverter<TMessage> converter;
    private final String messageType;
    private final String stateDataType;

    private Thread th;
//    private Thread threadZeroconf = null; // Native on ROS2

    protected BaseDriverNode(
            String nodeName,
            StateDataComparator<TStateData> comparator,
            MessageConverter<TMessage> converter,
            String messageType,
            String stateDataType) {
        super(nodeName);

        this.comparator = comparator;
        this.converter = converter;
        this.messageType = messageType;
        this.stateDataType = stateDataType;
    }

    /**
     * Connect to object
     */
    protected abstract boolean connect();

    protected abstract void onConnected();
    protected abstract void onDisconnected();

    public final TMessage getNewMessageInstance() {
        return this.getNewMessageInstance(this.messageType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Message> T getNewMessageInstance(String type) {
        T obj = null;
        try {
            Class<?> ref = Class.forName(type);
            obj = (T) ref.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static Class<?> makeClass(String type) {
        Class<?> ref = null;
        try {
            ref = Class.forName(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ref;
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

        if (this.comparator != null && !this.comparator.isEquals(this.stateData, this.oldStateData)) {
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
        this.pubWol = this.connectedNode.createPublisher(std_msgs.msg.String.class, PUB_WOL);

        if (!Strings.isNullOrEmpty(this.stateDataType)) {
            this.pubStateData = this.connectedNode.createPublisher(
                    (Class<TStateData>)makeClass(this.stateDataType),
                    this.configuration.getPrefix() + PUB_STATE
                    );
        }

        if (!Strings.isNullOrEmpty(this.messageType)) {
            // Local topic (mapped by prefix and namespace)
            Subscription<TMessage> messageSubscriber = this.connectedNode.createSubscription(
                    (Class<TMessage>)makeClass(this.messageType),
                    this.configuration.getPrefix() + SUB_CMD,
                    new Consumer<TMessage>() {
                @Override
                public void accept(TMessage msg) {
                    BaseDriverNode.this.onNewMessage(msg);
                }
            });
        }

        if (this.converter != null) {
            // Global topic registration (no prefix...)
            Subscription<Command> commandSubscriber = this.connectedNode.createSubscription(
                    Command.class,
                    "/" + SUB_STATE_ROBOT,
                    new Consumer<Command>() {
                @Override
                public void accept(Command msg) {
                    BaseDriverNode.this.onNewMessage(msg);
                }
            });
        }
    }

    /**
     * On node shutdown start.
     */
    @Override
    public void onShutdown(Node node) {
        this.th.interrupt();

     // Native on ROS2
//        if (this.serverReconfig != null)
//            this.serverReconfig.close();
//
//        if (this.threadZeroconf != null && this.threadZeroconf.isAlive()) {
//            this.threadZeroconf.interrupt();
//        }

        super.onShutdown(node);
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        super.onError(node, throwable);
        this.logE(throwable.getMessage());
    }

    public void startFinal() {
        this.initialize();

        // This CancellableLoop will be canceled automatically when the node
        // shuts down.
//        this.connectedNode.executeCancellableLoop(new CancellableLoop() {
//            @Override
//            protected void loop() throws InterruptedException {
//                refreshStateData();
//            }
//        });

        this.th = new Thread(new Runnable() {

            @Override
            public void run()  {
                while(true) {
                    try {
                        refreshStateData();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        this.th.start();

    }

    protected void initialize() {
        this.logI("Start main loop.");

        this.loadParameters();
        this.isConnected = false;

        this.initTopics();

//        this.publishZeroConf(); // Native on ROS2

        this.stateData = this.getNewMessageInstance(this.stateDataType);
                //this.pubStateData.newMessage();
    }

    public final void wakeOnLan() {
        std_msgs.msg.String message = new std_msgs.msg.String();
        message.setData(this.configuration.getMac());

        this.pubWol.publish(message);
    }

// Native on ROS2
//    private void publishZeroConf() {
//        final Zeroconf publisher = new Zeroconf();
//
//        final DiscoveredService service = this.getConfiguration().toDiscoveredService();
//
//        service.name = this.nodeName;
//        service.type = "_ros-node._tcp";
//        service.domain = "local";
//        service.port = 8888;
//
//        this.threadZeroconf = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                publisher.addService(service);
//            }
//        });
//
//        this.threadZeroconf.start();
//    }

//    protected NodeConfiguration getConfiguration() {
//        NodeConfiguration configuration = new NodeConfiguration();
//        configuration.setMasterAddress(this.connectedNode.getMasterUri().getHost());
//        configuration.setNodePath(this.configuration.getPrefix().substring(
//                0, this.configuration.getPrefix().length() - 1));
//        configuration.setNodeType(this.getClass().getName());
//
//        NodeConfiguration.NodePermission permission =
//                new NodeConfiguration.NodePermission();
//        permission.setExclude(true);
//        permission.setName("Permission");
//        configuration.getPermissions().add(permission);
//        configuration.getCapabilities().add(NodeCapability.ALL);
//
//        return configuration;
//    }

    @Override
    public TConfiguration onReconfigure(TConfiguration config, int level) {
        this.configuration.setPrefix(this.connectedNode.getParameter(NodeConfig.PARAM_PREFIX).toParameterValue().getStringValue());
        this.configuration.setRate(this.connectedNode.getParameter(NodeConfig.PARAM_RATE).toParameterValue().getIntegerValue());
        this.configuration.setFixedFrame(this.connectedNode.getParameter(NodeConfig.PARAM_FRAME).toParameterValue().getStringValue());
        this.configuration.setMac(this.connectedNode.getParameter(NodeConfig.PARAM_MAC).toParameterValue().getStringValue());

        return config;
    }

    public StateDataComparator<TStateData> getComparator() {
        return this.comparator;
    }
}

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
import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.topic.SubscriptionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        TConfiguration extends NodeDriverConfig,
        TStateData extends Message,
        TMessage extends Message>
        extends BaseSimpleNode<TConfiguration>
{

    // Constants
    public static final String PUB_WOL          = "/wol";
    public static final String PUB_STATE        = "~/statedata";
    public static final String SUB_CMD          = "~/cmd_action";
    public static final String SUB_STATE_ROBOT  = "/robotsay";

    private static final Logger logger = LoggerFactory.getLogger(BaseDriverNode.class);

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
            StateDataComparator<TStateData> comparator,
            MessageConverter<TMessage> converter,
            String messageType,
            String stateDataType) {
        BaseDriverNode.logger.debug("Initialize Managed Alfred Node.");

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
     * Event when connection is establish.
     */
    protected abstract void onConnected();

    /**
     * Event when connection is lost.
     */
    protected abstract void onDisconnected();

    public final TMessage getNewMessageInstance() {
        return this.getNewMessageInstance(this.messageType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Message> T getNewMessageInstance(String type) {
        BaseDriverNode.logger.debug("Initialize Message.");

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
        BaseDriverNode.logger.debug("Get class for " + type + " .");

        Class<?> ref = null;
        try {
            ref = Class.forName(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ref;
    }

    protected final void addModule(IModule<TStateData, TMessage> module) {
        if (module != null) {
            BaseDriverNode.logger.debug("Add module : " + module.getClass().getSimpleName());

            this.modules.add(module);
        }
    }

    /**
     * Refresh {@link StateData} if connected object is available or try to connect it.
     * @throws InterruptedException
     */
    protected void refreshStateData() throws InterruptedException {

        if (this.isConnected ) {
            for (IModule<TStateData, TMessage> module : this.modules) {
                module.load(this.stateData);
            }
        } else {
            this.isConnected = this.connect();

            if (this.isConnected) {
                this.onConnected();
            }
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
            BaseDriverNode.logger.debug("onNewMessage events ! (" + message.getClass().getName() + ")");

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
        BaseDriverNode.logger.debug("onNewMessage events ! (" + Command.class.getName() + ")");

        String[] wheres = command.getContext().getWhere().split(" ");
        List<String> availableMethodes;

        for (String where : wheres) {
            if (this.configuration.getPrefix().contains(where)) {
                for (IModule<TStateData, TMessage> module : this.modules) {
                    availableMethodes = module.getAvailableMethods();

                    if (availableMethodes != null && availableMethodes.contains(command.getAction().substring(2))) {
                        this.onNewMessage(this.converter.toMessage(this.connectedNode, command));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public TConfiguration onReconfigure(TConfiguration config, int level) {
        BaseDriverNode.logger.debug("onReconfigure event !");

//        this.configuration.setPrefix(this.connectedNode.getParameter(NodeSimpleConfig.PARAM_PREFIX).toParameterValue().getStringValue());
        this.configuration.setRate(this.connectedNode.getParameter(NodeSimpleConfig.PARAM_RATE).toParameterValue().getIntegerValue());
        this.configuration.setFixedFrame(this.connectedNode.getParameter(NodeSimpleConfig.PARAM_FRAME).toParameterValue().getStringValue());

        return config;
    }

    /**
     * On node shutdown start.
     */
    @Override
    public void onShutdown() {
        BaseDriverNode.logger.debug("onShutdown event !");
        this.th.interrupt();

        super.onShutdown();
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        super.onError(node, throwable);
        this.logE(throwable.getMessage());
    }

    protected void initialize() {
        this.logI("Custom Managed node.");
        this.isConnected = false;

        this.loadParameters();

        this.initTopics();

        this.stateData = this.getNewMessageInstance(this.stateDataType);
        //this.pubStateData.newMessage();
    }

    /**
     * Initialize all node publishers & subscribers Topics.
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @Override
    protected void initTopics() {
        BaseDriverNode.logger.debug("Initialize Topics.");
        super.initTopics();

        this.pubWol = this.connectedNode.createPublisher(std_msgs.msg.String.class, PUB_WOL);

        if (!Strings.isNullOrEmpty(this.stateDataType)) {
            this.pubStateData = this.connectedNode.createPublisher(
                    (Class<TStateData>)makeClass(this.stateDataType),
                    GraphName.getFullName(this.connectedNode, PUB_STATE, null)
                    );
        }

        if (!Strings.isNullOrEmpty(this.messageType)) {
            // Local topic (mapped by prefix and name space)
            Subscription<TMessage> messageSubscriber = this.connectedNode.createSubscription(
                    (Class<TMessage>)makeClass(this.messageType),
                    GraphName.getFullName(this.connectedNode, SUB_CMD, null),
                    new SubscriptionCallback<TMessage>() {
                @Override
                public void dispatch(TMessage msg) {
                    BaseDriverNode.this.onNewMessage(msg);
                }
            });
        }

        if (this.converter != null) {
            // Global topic registration (no prefix...)
            Subscription<Command> commandSubscriber = this.connectedNode.createSubscription(
                    Command.class,
                    GraphName.getFullName(this.connectedNode, SUB_STATE_ROBOT, null),
                    new SubscriptionCallback<Command>() {
                @Override
                public void dispatch(Command msg) {
                    BaseDriverNode.this.onNewMessage(msg);
                }
            });
        }
    }

    @Override
    public void onStarted() {
        BaseDriverNode.logger.debug("onStarted event !");
        super.onStarted();
        this.initialize();


        // This CancellableLoop will be canceled automatically when the node
        // shuts down.
//        this.connectedNode.executeCancellableLoop(new CancellableLoop() {
//            @Override
//            protected void loop() throws InterruptedException {
//                refreshStateData();
//            }
//        });

        this.logI("Start main loop.");
        BaseDriverNode.logger.debug("Starting thread for refresh data.");

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
        this.th.setName("Refresh state data");
        this.th.start();

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

    public final TStateData getStateData() {
        return this.stateData;
    }

    protected final boolean isConnected() {
        return this.isConnected;
    }

    public StateDataComparator<TStateData> getComparator() {
        return this.comparator;
    }
}

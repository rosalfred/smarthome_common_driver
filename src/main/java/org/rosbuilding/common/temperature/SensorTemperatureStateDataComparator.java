package org.rosbuilding.common.temperature;

import sensor_msgs.msg.Temperature;
import smarthome_heater_msgs.msg.SensorTemperatureStateData;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.StateDataComparator;

public class SensorTemperatureStateDataComparator extends StateDataComparator<SensorTemperatureStateData> {

    @Override
    public boolean isEquals(SensorTemperatureStateData state1, SensorTemperatureStateData state2) {
        boolean result = (state1 == null && state2 == null)
                || (state1 != null && state2 != null
                        && state1.getTemperatureAmbiant().getTemperature()
                                == state2.getTemperatureAmbiant().getTemperature()
                        && state1.getTemperatureAmbiant().getVariance()
                                == state2.getTemperatureAmbiant().getVariance()
                        && state1.getTemperatureObject().getTemperature()
                                == state2.getTemperatureObject().getTemperature()
                        && state1.getTemperatureObject().getVariance()
                                == state2.getTemperatureObject().getVariance());
        return result;
    }

    @Override
    public SensorTemperatureStateData makeNewCopy(Node connectedNode, String frameId,
            SensorTemperatureStateData stateData) {
        SensorTemperatureStateData result = new SensorTemperatureStateData(); //connectedNode.getTopicMessageFactory().newFromType(SensorTemperatureStateData._TYPE);
//        result.getHeader().setFrameId(frameId); // Remove on ROS2
        result.getHeader().setStamp(connectedNode.getCurrentTime());
        result.setState(stateData.getState());
        result.setTemperatureAmbiant(makeNewCopy(connectedNode, frameId, stateData.getTemperatureAmbiant()));
        result.setTemperatureObject(makeNewCopy(connectedNode, frameId, stateData.getTemperatureObject()));

        return result;
    }

    public static Temperature makeNewCopy(
            Node connectedNode,
            String frameId,
            Temperature temperature) {

        Temperature result = new Temperature(); // connectedNode.getTopicMessageFactory().newFromType(Temperature._TYPE);

        result.getHeader().setStamp(connectedNode.getCurrentTime());
//        result.getHeader().setFrameId(frameId); // Remove on ROS2
        result.setTemperature(temperature.getTemperature());
        result.setVariance(temperature.getVariance());

        return result;
    }

}

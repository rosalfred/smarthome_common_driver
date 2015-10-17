package com.alfred.ros.temperature;

import sensor_msgs.Temperature;

import org.ros.node.ConnectedNode;

import com.alfred.ros.core.StateDataComparator;

import heater_msgs.SensorTemperatureStateData;

public class SensorTemperatureStateDataComparator implements StateDataComparator<SensorTemperatureStateData> {

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
    public SensorTemperatureStateData makeNewCopy(ConnectedNode connectedNode, String frameId,
            SensorTemperatureStateData stateData) {
        SensorTemperatureStateData result = connectedNode.getTopicMessageFactory()
                .newFromType(SensorTemperatureStateData._TYPE);
        result.getHeader().setFrameId(frameId);
        result.getHeader().setStamp(connectedNode.getCurrentTime());
        result.setState(stateData.getState());
        result.setTemperatureAmbiant(makeNewCopy(connectedNode, frameId, stateData.getTemperatureAmbiant()));
        result.setTemperatureObject(makeNewCopy(connectedNode, frameId, stateData.getTemperatureObject()));

        return result;
    }

    public static Temperature makeNewCopy(
            ConnectedNode connectedNode,
            String frameId,
            Temperature temperature) {

        Temperature result = connectedNode.getTopicMessageFactory()
                .newFromType(Temperature._TYPE);

        result.getHeader().setStamp(connectedNode.getCurrentTime());
        result.getHeader().setFrameId(frameId);
        result.setTemperature(temperature.getTemperature());
        result.setVariance(temperature.getVariance());

        return result;
    }

}

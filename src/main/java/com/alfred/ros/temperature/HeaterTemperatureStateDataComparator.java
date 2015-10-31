package com.alfred.ros.temperature;

import sensor_msgs.Temperature;

import org.ros.node.ConnectedNode;

import com.alfred.ros.core.StateDataComparator;

import heater_msgs.HeatingStateData;
import heater_msgs.HeatingTemperatureSlot;

public class HeaterTemperatureStateDataComparator implements StateDataComparator<HeatingStateData> {

    @Override
    public boolean isEquals(HeatingStateData stateData1, HeatingStateData stateData2) {
        boolean result = (stateData1 == null && stateData2 == null)
                || (stateData1 != null && stateData2 != null
                        && stateData1.getProportional()
                                == stateData2.getProportional()
                        && stateData1.getIntegral()
                                == stateData2.getIntegral()
                        && stateData1.getDerivative()
                                == stateData2.getDerivative()
                        && isEquals(
                                stateData1.getTemperatureGoal(),
                                stateData2.getTemperatureGoal())
                        && isEquals(
                                stateData1.getTemperatureReal(),
                                stateData2.getTemperatureReal())
                        && isEquals(
                                stateData1.getTimeSlotCurrent(),
                                stateData2.getTimeSlotCurrent()));

        return result;
    }

    public boolean isEquals(HeatingTemperatureSlot stateData1, HeatingTemperatureSlot stateData2) {

        boolean result = (stateData1 == null && stateData2 == null)
                || (stateData1 != null && stateData2 != null
                && stateData1.getDateBegin()
                == stateData2.getDateBegin()
                && stateData1.getDateEnd()
                == stateData2.getDateEnd()
                && stateData1.getFrequency()
                == stateData2.getFrequency()
                && stateData1.getFrequencyUnit()
                == stateData2.getFrequencyUnit()
                && stateData1.getPriority()
                == stateData2.getPriority()
                && stateData1.getTemperature()
                == stateData2.getTemperature());

        return result;
    }

    public boolean isEquals(Temperature stateData1, Temperature stateData2) {

        boolean result = (stateData1 == null && stateData2 == null)
                || (stateData1 != null && stateData2 != null
                        && stateData1.getTemperature()
                                == stateData2.getTemperature()
                        && stateData1.getVariance()
                                == stateData2.getVariance());

        return result;
    }

    @Override
    public HeatingStateData makeNewCopy(
            ConnectedNode connectedNode,
            String frameId,
            HeatingStateData stateData) {

        HeatingStateData result = connectedNode.getTopicMessageFactory()
                .newFromType(HeatingStateData._TYPE);

        result.getHeader().setFrameId(frameId);
        result.getHeader().setStamp(connectedNode.getCurrentTime());
        result.setProportional(stateData.getProportional());
        result.setIntegral(stateData.getIntegral());
        result.setDerivative(stateData.getDerivative());
        result.setTemperatureGoal(SensorTemperatureStateDataComparator.makeNewCopy(connectedNode, frameId, stateData.getTemperatureGoal()));
        result.setTemperatureReal(SensorTemperatureStateDataComparator.makeNewCopy(connectedNode, frameId, stateData.getTemperatureReal()));
        result.setTimeSlotCurrent(makeNewCopy(connectedNode, frameId, stateData.getTimeSlotCurrent()));

        return result;
    }

    public static HeatingTemperatureSlot makeNewCopy(
            ConnectedNode connectedNode,
            String frameId,
            HeatingTemperatureSlot data) {

        HeatingTemperatureSlot result = connectedNode.getTopicMessageFactory()
                .newFromType(HeatingTemperatureSlot._TYPE);

        result.getHeader().setFrameId(frameId);
        result.getHeader().setStamp(connectedNode.getCurrentTime());

        result.setDateBegin(data.getDateBegin());
        result.setDateEnd(data.getDateEnd());
        result.setFrequency(data.getFrequency());
        result.setFrequencyUnit(data.getFrequencyUnit());
        result.setPriority(data.getPriority());
        result.setTemperature(data.getTemperature());

        return result;
    }
}

package org.rosbuilding.common.light;

import std_msgs.msg.Header;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.StateDataComparator;

import geometry_msgs.msg.Point;
import geometry_msgs.msg.Pose;
import geometry_msgs.msg.Quaternion;
import sensor_msgs.msg.Illuminance;
import smarthome_light_msgs.msg.Hsb;
import smarthome_light_msgs.msg.StateData;
import smarthome_msgs.msg.NodeState;

public class LightStateDataComparator extends StateDataComparator<StateData> {

    @Override
    public boolean isEquals(StateData state1, StateData state2) {
        boolean result = (state1 != null && state2 != null);
        if (result) {
            result &= state1.getState() == state2.getState();
            result &= isEqual(state1.getIlluminance(), state2.getIlluminance());
            result &= isEqual(state1.getHsb(), state2.getHsb());
            result &= isEqual(state1.getState(),  state2.getState());
            result &= isEqual(state1.getPose(),  state2.getPose());
            //result &= isEqual(state1.getDescriptor(),  state2.getDescriptor());
        }
        return result;
    }

    @Override
    public StateData makeNewCopy(Node conectedNode, String frameId,
            StateData stateData) {
        StateData result = new StateData();
        result.setHeader(new Header());
        result.getHeader().setStamp(conectedNode.now());
        result.setState(stateData.getState());
        //result.setDescriptor();
        result.setHsb(makeNewCopy(conectedNode, stateData.getHsb()));
        result.setState(makeNewCopy(conectedNode, stateData.getState()));
        result.setPose(makeNewCopy(conectedNode, stateData.getPose()));
        result.setIlluminance(makeNewCopy(conectedNode, stateData.getIlluminance()));

        return result;
    }

    private static Illuminance makeNewCopy(Node conectedNode, Illuminance illuminance) {
        Illuminance result = new Illuminance();
        if (illuminance != null) {
            result.setHeader(new Header());
            result.getHeader().setStamp(conectedNode.now());
            result.setIlluminance(illuminance.getIlluminance());
            result.setVariance(illuminance.getVariance());
        }
        return result;
    }

    private static NodeState makeNewCopy(Node conectedNode, NodeState state) {
        NodeState result = new NodeState();
        if (state != null) {
            result.setVal(state.getVal());
        }
        return result;
    }

    private static Hsb makeNewCopy(Node conectedNode, Hsb hsb) {
        Hsb result = new Hsb();
        if (hsb != null) {
            result.setBrightness(hsb.getBrightness());
            result.setHue(hsb.getHue());
            result.setSaturation(hsb.getSaturation());
        }
        return result;
    }

    private static Pose makeNewCopy(Node conectedNode, Pose pose) {
        Pose result = new Pose();
        if (pose != null) {
            result.setOrientation(new Quaternion());
            result.getOrientation().setW(pose.getOrientation().getW());
            result.getOrientation().setX(pose.getOrientation().getX());
            result.getOrientation().setY(pose.getOrientation().getY());
            result.getOrientation().setZ(pose.getOrientation().getZ());
            result.setPosition(new Point());
            result.getPosition().setX(pose.getPosition().getX());
            result.getPosition().setY(pose.getPosition().getY());
            result.getPosition().setZ(pose.getPosition().getZ());
        }
        return result;
    }

    private static boolean isEqual(NodeState state1, NodeState state2) {
        boolean result = (state1 != null && state2 != null);
        if (result) {
            result &= state1.getVal() == state2.getVal();
        }
        return result;
    }

    private static boolean isEqual(Pose pose1, Pose pose2) {
        boolean result = (pose1 != null && pose2 != null);
        if (result) {
            result &= pose1.getOrientation().getW() == pose2.getOrientation().getW();
            result &= pose1.getOrientation().getX() == pose2.getOrientation().getX();
            result &= pose1.getOrientation().getY() == pose2.getOrientation().getY();
            result &= pose1.getOrientation().getZ() == pose2.getOrientation().getZ();
        }
        return result;
    }

    private static boolean isEqual(Hsb hsb1, Hsb hsb2) {
        boolean result = (hsb1 != null && hsb2 != null);
        if (result) {
            result &= hsb1.getHue() == hsb2.getHue();
            result &= hsb1.getSaturation() == hsb2.getSaturation();
            result &= hsb1.getBrightness() == hsb2.getBrightness();
        }
        return result;
    }

    private static boolean isEqual(Illuminance illuminance1, Illuminance illuminance2) {
        boolean result = (illuminance1 != null && illuminance2 != null);
        if (result) {
            result &= illuminance1.getIlluminance() == illuminance2.getIlluminance();
            result &= illuminance1.getVariance() == illuminance2.getVariance();
        }
        return result;
    }

}

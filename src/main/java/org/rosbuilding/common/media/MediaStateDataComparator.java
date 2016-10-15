package org.rosbuilding.common.media;

import smarthome_media_msgs.msg.MonitorInfo;
import smarthome_media_msgs.msg.PlayerInfo;
import smarthome_media_msgs.msg.SpeakerInfo;
import smarthome_media_msgs.msg.StateData;
import std_msgs.msg.Header;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.StateDataComparator;

import com.google.common.base.Strings;

public class MediaStateDataComparator implements StateDataComparator<StateData> {

    @Override
    public boolean isEquals(StateData state1, StateData state2) {
        boolean result = (state1 != null && state2 != null);
        if (result) {
            result &= state1.getState() == state2.getState();
            result &= isEqual(state1.getMonitor(),  state2.getMonitor());
            result &= isEqual(state1.getPlayer(),   state2.getPlayer());
            result &= isEqual(state1.getSpeaker(),  state2.getSpeaker());
        }
        return result;
    }

    @Override
    public StateData makeNewCopy(Node conectedNode, String frameId,
            StateData stateData) {
        StateData result = new StateData(); //conectedNode.getTopicMessageFactory().newFromType(StateData._TYPE);
//        result.getHeader().setFrameId(frameId); // Removed on ROS2
        result.setHeader(new Header());
        result.getHeader().setStamp(conectedNode.getCurrentTime());
        result.setState(stateData.getState());
        result.setMonitor(makeNewCopy(conectedNode, stateData.getMonitor()));
        result.setPlayer(makeNewCopy(conectedNode, stateData.getPlayer()));
        result.setSpeaker(makeNewCopy(conectedNode, stateData.getSpeaker()));

        return result;
    }

    private static SpeakerInfo makeNewCopy(Node conectedNode,
            SpeakerInfo speaker) {
        SpeakerInfo result = new SpeakerInfo(); //conectedNode.getTopicMessageFactory().newFromType(SpeakerInfo._TYPE);
        if (speaker != null) {
            result.setChannel(speaker.getChannel());
            result.setLevel(speaker.getLevel());
            result.setMuted(speaker.getMuted());
            result.setOutput(speaker.getOutput());
            result.setSource(speaker.getSource());
        }
        return result;
    }

    private static PlayerInfo makeNewCopy(Node conectedNode,
            PlayerInfo player) {
        PlayerInfo result = new PlayerInfo(); //conectedNode.getTopicMessageFactory().newFromType(PlayerInfo._TYPE);
        if (player != null) {
            result.setCanseek(player.getCanseek());
            result.setFile(player.getFile());
            result.setMediaid(player.getMediaid());
            result.getMediatype().setValue(player.getMediatype().getValue());;
            result.setSpeed(player.getSpeed());
            result.setStamp(player.getStamp());
            result.setState(player.getState());
            result.setSubtitleenabled(player.getSubtitleenabled());
            result.setThumbnail(player.getThumbnail());
            result.setTitle(player.getTitle());
            result.setTotaltime(player.getTotaltime());
        }
        return result;
    }

    private static MonitorInfo makeNewCopy(Node conectedNode,
            MonitorInfo monitor) {
        MonitorInfo result = new MonitorInfo(); //conectedNode.getTopicMessageFactory().newFromType(MonitorInfo._TYPE);
        if (monitor != null) {
            result.setHeight(monitor.getHeight());
            result.setWigth(monitor.getWigth());
            result.setSource(monitor.getSource());
        }
        return result;
    }

    private static boolean isEqual(SpeakerInfo speaker1, SpeakerInfo speaker2) {
        boolean result = (speaker1 != null && speaker2 != null);
        if (result) {
            result &= speaker1.getChannel().equals(speaker2.getChannel());
            result &= speaker1.getLevel() == speaker2.getLevel();
            result &= speaker1.getMuted() == speaker2.getMuted();
            result &= speaker1.getOutput() == speaker2.getOutput();
            result &= speaker1.getSource().equals(speaker1.getSource());
        }
        return result;
    }

    private static boolean isEqual(PlayerInfo player1, PlayerInfo player2) {
        boolean result = (player1 != null && player2 != null);
        if (result) {
            result &= player1.getCanseek() == player2.getCanseek();
            result &= player1.getSubtitleenabled() == player2.getSubtitleenabled();
            result &= player1.getFile().equals(player2.getFile());
            result &= player1.getMediaid() == player2.getMediaid();
            result &= player1.getMediatype().getValue().equals(player2.getMediatype().getValue());
            result &= player1.getSpeed() == player2.getSpeed();
            result &= player1.getStamp().equals(player2.getStamp());
            result &= player1.getState() == player2.getState();
            result &= player1.getThumbnail().equals(player2.getThumbnail());
            result &= player1.getTitle().equals(player2.getTitle());
            result &= player1.getTotaltime().equals(player2.getTotaltime());
        }
        return result;
    }

    private static boolean isEqual(MonitorInfo monitor1, MonitorInfo monitor2) {
        boolean result = (monitor1 != null && monitor2 != null);
        if (result) {
            result &= monitor1.getHeight() == monitor2.getHeight();
            result &= monitor1.getWigth() == monitor2.getWigth();
            result &= monitor1.getSource().equals(monitor2.getSource());
        }
        return result;
    }

}

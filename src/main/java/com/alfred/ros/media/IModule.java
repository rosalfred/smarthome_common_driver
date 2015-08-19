/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.media;

import java.io.IOException;

import media_msgs.MediaAction;
import media_msgs.StateData;

import org.ros.internal.message.Message;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface IModule<T extends Message> {

    public static final String SEP = "::";
    public static final String URI_DATA = "data://";

    /**
     * Refresh stateDate info.
     * @param stateData Current {@link StateData}
     */
    void load(T stateData);

    /**
     * Method callback for publisher.
     * @param message
     * @param stateData
     * @throws InterruptedException
     * @throws IOException
     */
    void callbackCmdAction(MediaAction message, StateData stateData)
            throws IOException, InterruptedException;
}

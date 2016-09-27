/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 */
package org.rosbuilding.common.media;

import org.rosbuilding.common.IModule;

import smarthome_media_msgs.msg.MediaAction;
import smarthome_media_msgs.msg.StateData;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface IMonitor extends IModule<StateData, MediaAction> {

    public static final String OP_CHANNEL      = "channel";

}

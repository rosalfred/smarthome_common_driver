/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common;

import org.ros2.rcljava.internal.message.Message;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public abstract class System<TStateData extends Message, TMessage extends Message> extends Module<TStateData, TMessage> {

    public static final String OP_POWER    = "power";
    public static final String OP_SHUTDOWN = "shutdown";

}

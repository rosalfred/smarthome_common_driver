/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.core;

import org.ros.internal.message.Message;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface ISystem<TStateData extends Message, TMessage extends Message> extends IModule<TStateData, TMessage> {

    public static final String OP_POWER    = "power";
    public static final String OP_SHUTDOWN = "shutdown";

}

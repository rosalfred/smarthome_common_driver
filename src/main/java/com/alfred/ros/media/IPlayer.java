/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.media;

import media_msgs.PlayerInfo;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface IPlayer extends IModule<PlayerInfo> {
    // Media
    public static final String OP_PAUSE        = "pause";
    public static final String OP_PLAY         = "play";
    public static final String OP_PLAYPAUSE    = "playpause";
    public static final String OP_STOP         = "stop";
    public static final String OP_SPEED        = "speed";
    public static final String OP_SPEED_UP     = "speedup";
    public static final String OP_SPEED_DOWN   = "speeddown";
    public static final String OP_OPEN         = "open";
    public static final String OP_SEEK         = "seek";
    public static final String OP_NEXT         = "next";
    public static final String OP_PREVIOUS     = "previous";

    // Playlist
    public static final String OP_ADD_PLAYLIST = "addplay";
    public static final String OP_INS_PLAYLIST = "insplay";
    public static final String OP_REM_PLAYLIST = "remplay";
    public static final String OP_CLR_PLAYLIST = "clrplay";

    // Generic Control
    public static final String OP_BACK         = "back";
    public static final String OP_HOME         = "home";
    public static final String OP_INFO         = "info";
    public static final String OP_DISPLAY      = "display";
    public static final String OP_SELECT       = "select";
    public static final String OP_CONTEXT      = "context";
    public static final String OP_UP           = "up";
    public static final String OP_DOWN         = "down";
    public static final String OP_LEFT         = "left";
    public static final String OP_RIGHT        = "right";
    public static final String OP_TXT          = "text";

    public static final String URI_MEDIA_YOUTUBE = "youtube://";
    public static final String URI_MEDIA_IMDB = "imdb://";
    public static final String URI_MEDIA = "media://";
}

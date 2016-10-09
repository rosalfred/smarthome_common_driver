/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.common.media;

import smarthome_media_msgs.srv.MediaGetItem_Request;
import smarthome_media_msgs.srv.MediaGetItem_Response;
import smarthome_media_msgs.srv.MediaGetItems_Request;
import smarthome_media_msgs.srv.MediaGetItems_Response;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface ILibrary {
    /**
     * Method callback for get_item.
     * @param request {@link MediaGetItem_Request}
     * @param response {@link MediaGetItem_Response}
     */
    void handleMediaGetItem(MediaGetItem_Request request,
            MediaGetItem_Response response);
    /**
     * Method callback for get_items.
     * @param request {@link MediaGetItems_Request}
     * @param response {@link MediaGetItems_Response}
     */
    void handleMediaGetItems(MediaGetItems_Request request,
            MediaGetItems_Response response);
}

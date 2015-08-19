/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.alfred.ros.media;

import media_msgs.MediaGetItemRequest;
import media_msgs.MediaGetItemResponse;
import media_msgs.MediaGetItemsRequest;
import media_msgs.MediaGetItemsResponse;

/**
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 *
 */
public interface ILibrary {
	/**
	 * Method callback for get_item.
	 * @param request {@link MediaGetItemRequest}
	 * @param response {@link MediaGetItemResponse}
	 */
	void handleMediaGetItem(MediaGetItemRequest request,
			MediaGetItemResponse response);
	/**
	 * Method callback for get_items.
	 * @param request {@link MediaGetItemsRequest}
	 * @param response {@link MediaGetItemsResponse}
	 */
	void handleMediaGetItems(MediaGetItemsRequest request,
			MediaGetItemsResponse response);
}

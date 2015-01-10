package com.alfred.ros.media;

import media_msgs.MediaGetItemRequest;
import media_msgs.MediaGetItemResponse;
import media_msgs.MediaGetItemsRequest;
import media_msgs.MediaGetItemsResponse;

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

package com.mendeley.api.network.components;

/**
 * This class represents parameters for folder SDK requests
 * Uninitialised properties will be ignored.
 *
 */
public class FolderRequestParameters extends MendeleyRequest {
	/**
	 * Group ID. If not supplied, returns user folders.
	 */
	public String groupId;

}

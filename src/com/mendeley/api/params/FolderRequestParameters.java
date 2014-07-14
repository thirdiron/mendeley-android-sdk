package com.mendeley.api.params;

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
	
	/**
	 * The maximum number of items on the page. If not supplied, the default is 20. The largest allowable value is 500.
	 */
	public Integer limit;
	

}

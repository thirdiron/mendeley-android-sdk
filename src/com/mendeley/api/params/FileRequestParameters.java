package com.mendeley.api.params;

/**
 * This class represents parameters for file SDK requests
 * Uninitialised properties will be ignored.
 *
 */
public class FileRequestParameters extends MendeleyRequest {
	
	/**
	 * The document ID.
	 */
	public String documentId;
	
	/**
	 * The group ID.
	 */
	public String groupId;
	
	/**
	 * Returns only files added since this timestamp. Should be supplied in ISO 8601 format.
	 */
	public String addedSince;
	
	/**
	 * Returns files deleted since this timestamp. Should be supplied in ISO 8601 format.
	 */
	public String deletedSince;
	
	/**
	 * The maximum number of items on the page. If not supplied, the default is 20. The largest allowable value is 500.
	 */
	public Integer limit;
	
	/**
	 * A marker for the last key in the previous page
	 */
	public String marker;
	
	
	/**
	 * The catalog ID.
	 */
	public String catalogId;
}

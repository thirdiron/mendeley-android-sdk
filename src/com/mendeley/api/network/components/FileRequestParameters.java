package com.mendeley.api.network.components;

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
}

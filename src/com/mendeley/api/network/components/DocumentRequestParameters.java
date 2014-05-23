package com.mendeley.api.network.components;

/**
 * This class represents parameters for document SDK requests
 * Uninitialised properties will be ignored.
 * 
 * @author Elad
 *
 */
public class DocumentRequestParameters extends MendeleyRequest {
	
	/**
	 * The required document view.
	 * {@link #view}
	 */
	public View view;
	
	/**
	 * Group ID. If not supplied, returns user documents.
	 */
	public String groupId;
	
	/**
	 * Returns only documents modified since this timestamp. Should be supplied in ISO 8601 format.
	 */
	public String modifiedSince;
	
	/**
	 * Returns only documents deleted since this timestamp. Should be supplied in ISO 8601 format.
	 */
	public String deletedSince;
	
	/**
	 * The maximum number of items on the page. If not supplied, the default is 20. The largest allowable value is 500.
	 */
	public Integer limit;
	
	/**
	 * A marker for the last key in the previous page.
	 */
	public String marker;
	
	/**
	 * A flag to indicate that the scrolling direction has switched.
	 */
	public Boolean reverse;
	
	/**
	 * The sort order
	 * {@link #order}
	 */
	public Order order;
	
	/**
	 * The field to sort on
	 * {@link #sort}
	 */
	public Sort sort;
	
	public DocumentRequestParameters() {}

}

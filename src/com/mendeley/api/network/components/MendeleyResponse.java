package com.mendeley.api.network.components;

import com.mendeley.api.exceptions.MendeleyException;

/**
 * A MendeleyResponse object will be sent to the application together with a data object via callbacks from a network call.
 * An object of this class holds response header parameters and a MendeleyException.
 * The MendeleyException object will hold exception data in case a call failed.
 *
 */
public class MendeleyResponse {
	
	public int responseCode;
	public MendeleyException mendeleyException;
	
	public String header;
	public String date;
	public String contentEncoding;
	public String contentType;
	public String vary;
	public String traceId;
	public String connection;
	public String contentLength;
	public String location;
	
	@Override
	public String toString() {
		return "header: "+header+
			   ", date: "+date+
			   ", contentEncoding: "+contentEncoding+
			   ", contentType: "+contentType+
			   ", vary: "+vary+
			   ", traceId: "+traceId+
			   ", connection: "+connection+
			   ", contentLength: "+contentLength+
			   ", responseCode: "+responseCode+
			   ", mendeleyException: "+mendeleyException;
	}
}

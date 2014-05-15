package com.mendeley.api.network.components;

public class MendeleyResponse {
	
	public String header;
	public String date;
	public String contentEncoding;
	public String contentType;
	public String vary;
	public String traceId;
	public String connection;
	public String link;
	public String contentLength;
	public int responseCode;
	
	public MendeleyResponse(int responseCode) {
		this.responseCode = responseCode;
	}
	
	@Override
	public String toString() {
		return "header: "+header+
			   ", date: "+date+
			   ", contentEncoding: "+contentEncoding+
			   ", contentType: "+contentType+
			   ", vary: "+vary+
			   ", traceId: "+traceId+
			   ", connection: "+connection+
			   ", link: "+link+
			   ", contentLength: "+contentLength+
			   ", responseCode: "+responseCode;
	}
}

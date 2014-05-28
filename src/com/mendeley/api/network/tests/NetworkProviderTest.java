package com.mendeley.api.network.tests;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.mendeley.api.network.NetworkProvider;
import com.mendeley.api.network.components.MendeleyResponse;

public class NetworkProviderTest extends TestCase {

	NetworkProvider provider;
	
	@Override
	protected void setUp() {
		provider = new NetworkProvider();
    }
	
	private MendeleyResponse getTestResponse() {
		MendeleyResponse response = new MendeleyResponse();
		response.date = "Mon, 26 May 2014 14:46:18 GMT";
		response.header = "HTTP/1.1 200 OK";
		response.connection = "keep-alive";
		response.contentType = "application/vnd.mendeley-document.1+json";
		response.mendeleyCount = 14;
		response.vary = "Accept-Encoding, User-Agent";
		response.traceId = "_ZqEYtC9vmI";
		response.linkNext = "https://mix.mendeley.com/documents/?limit=3&reverse=false&order=asc&marker=7320b137-7499-3274-b0dc-bb831d29a0a4";
		response.linkLast = "https://mix.mendeley.com/documents/?limit=3&reverse=true&order=asc";

		return response;
	}
	
	private Map<String, List<String>> getHeadersMap() {
		Map<String, List<String>> headersMap = new HashMap<String, List<String>>();
		
		List<String> headers = new ArrayList<String>();
		headers.add("Mon, 26 May 2014 14:46:18 GMT");
		headersMap.put("Date", headers);
		headers = new ArrayList<String>();
		
		headers.add("HTTP/1.1 200 OK");
		headersMap.put(null, headers);
		headers = new ArrayList<String>();
		
		headers.add("keep-alive");
		headersMap.put("Connection", headers);
		headers = new ArrayList<String>();
		
		headers.add("application/vnd.mendeley-document.1+json");
		headersMap.put("Content-Type", headers);
		headers = new ArrayList<String>();
		
		headers.add("14");
		headersMap.put("Mendeley-Count", headers);
		headers = new ArrayList<String>();
		
		headers.add("Accept-Encoding, User-Agent");
		headersMap.put("Vary", headers);
		headers = new ArrayList<String>();
		
		headers.add("_ZqEYtC9vmI");
		headersMap.put("X-Mendeley-Trace-Id", headers);
		headers = new ArrayList<String>();
		
		headers.add("<https://mix.mendeley.com/documents/?limit=3&reverse=true&order=asc>; rel=\"last\"");
		headers.add("<https://mix.mendeley.com/documents/?limit=3&reverse=false&order=asc&marker=7320b137-7499-3274-b0dc-bb831d29a0a4>; rel=\"next\"");
		headersMap.put("Link", headers);

		return headersMap;
	}
	
	@Test
	public void test_getResponseHeaders() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		MendeleyResponse expectedResponse = getTestResponse();
		Map<String, List<String>> headersMap = getHeadersMap();
		MendeleyResponse response = new MendeleyResponse();
		
		String methodName = "getResponseHeaders";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(headersMap);
		values.add(response);		
		provider.getResultFromMethod(methodName, values);
		
		boolean equal = response.date.equals(expectedResponse.date) &&
				response.date.equals(expectedResponse.date) &&
				response.header.equals(expectedResponse.header) &&
				response.connection.equals(expectedResponse.connection) &&
				response.contentType.equals(expectedResponse.contentType) &&
				response.mendeleyCount.equals(expectedResponse.mendeleyCount) &&
				response.vary.equals(expectedResponse.vary) &&
				response.traceId.equals(expectedResponse.traceId) &&
				response.linkNext.equals(expectedResponse.linkNext) &&
				response.linkLast.equals(expectedResponse.linkLast);
		
		assertTrue("Headers are missing or incorrect in MendeleyResponse object", equal);
	}

}

package com.mendeley.api.network.tests;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.mendeley.api.network.DocumentNetworkProvider;
import com.mendeley.api.network.NetworkProvider;
import com.mendeley.api.network.components.DocumentRequestParameters;
import com.mendeley.api.network.components.MendeleyRequest;

public class DocumentNetworkProviderTest  extends TestCase {

	DocumentNetworkProvider provider;
	String documentsUrl;
	final String documentId = "test-document_id";
	
	@Override
	protected void setUp() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		provider = new DocumentNetworkProvider();
		
		NetworkProvider networkProvider = new NetworkProvider();	
		ArrayList<Object> values = new ArrayList<Object>();			
		String methodName = "getApiUrl";
		String apiUrl = (String) networkProvider.getResultFromMethod(methodName, values);
		
		documentsUrl = apiUrl+"documents";
    }
	
	@Test
	public void test_getDeleteDocumentUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = documentsUrl+"/"+documentId;
		
		String methodName = "getDeleteDocumentUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(documentId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Documents url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getTrashDocumentUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = documentsUrl+"/"+documentId+"/trash";
		
		String methodName = "getTrashDocumentUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(documentId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Post trash url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getGetDocumentUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String paramsString = "?view=client";
		String expectedUrl = documentsUrl+"/"+documentId+paramsString;

		DocumentRequestParameters params = new DocumentRequestParameters();
		params.view = MendeleyRequest.View.CLIENT;		
		String methodName = "getGetDocumentUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(documentId);	
		values.add(params);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get document url with parameters is wrong", expectedUrl, url);
		
		expectedUrl = documentsUrl+"/"+documentId;
		params = new DocumentRequestParameters();
		values = new ArrayList<Object>();		
		values.add(documentId);	
		values.add(params);	
		url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get document url without parameters is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getGetDocumentsUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		
		MendeleyRequest.View view = MendeleyRequest.View.CLIENT;
		String groupId = "test-group_id";
		String modifiedSince = "2014-02-28T11:52:30.000Z";
		String deletedSince = "2014-01-21T11:52:30.000Z";
		int limit = 7;
		String marker = "12";
		boolean reverse = true;
		MendeleyRequest.Order order = MendeleyRequest.Order.DESC;
		MendeleyRequest.Sort sort = MendeleyRequest.Sort.MODIFIED;
		
		
		String paramsString = "?view=" + view +
				"&group_id=" + groupId + 
				"&modified_since=" + URLEncoder.encode(modifiedSince, "ISO-8859-1") + 
				"&deleted_since=" + URLEncoder.encode(deletedSince, "ISO-8859-1") + 
				"&limit=" + limit + 
				"&marker=" + marker + 
				"&reverse=" + reverse + 
				"&order=" + order + 
				"&sort=" + sort; 
		String expectedUrl = documentsUrl+paramsString;

		DocumentRequestParameters params = new DocumentRequestParameters();
		params.view = view;		
		params.groupId = groupId;
		params.modifiedSince = modifiedSince;
		params.deletedSince = deletedSince;
		params.limit = 7;
		params.marker = "12";
		params.reverse = true;
		params.order = order;
		params.sort = sort;
		
		String methodName = "getGetDocumentsUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(params);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get documents url with parameters is wrong", expectedUrl, url);
		
		expectedUrl = documentsUrl;
		params = new DocumentRequestParameters();
		values = new ArrayList<Object>();		
		values.add(params);	
		url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get documents url without parameters is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getPatchDocumentUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = documentsUrl+"/"+documentId;
		
		String methodName = "getPatchDocumentUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(documentId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Patch document url is wrong", expectedUrl, url);
	}
}

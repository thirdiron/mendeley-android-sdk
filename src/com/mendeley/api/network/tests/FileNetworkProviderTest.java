package com.mendeley.api.network.tests;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;

import com.mendeley.api.network.FileNetworkProvider;
import com.mendeley.api.network.NetworkProvider;
import com.mendeley.api.network.components.FileRequestParameters;

public class FileNetworkProviderTest extends TestCase {

	FileNetworkProvider provider;
	String filesUrl;
	final String fileId = "test-file_id";
	
	@Override
	protected void setUp() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		provider = new FileNetworkProvider();
		
		NetworkProvider networkProvider = new NetworkProvider();	
		ArrayList<Object> values = new ArrayList<Object>();			
		String methodName = "getApiUrl";
		String apiUrl = (String) networkProvider.getResultFromMethod(methodName, values);
		
		filesUrl = apiUrl+"files";
    }
	
	@Test
	public void test_getGetFilesUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		
		String documentId = "test-document_id";
		String groupId = "test-group_id";
		String addedSince = "2014-02-28T11:52:30.000Z";
		String deletedSince = "2014-01-21T11:52:30.000Z";

		String paramsString = "?document_id=" + documentId +
				"&group_id=" + groupId +
				"&added_since=" +  URLEncoder.encode(addedSince, "ISO-8859-1") + 
				"&deleted_since=" + URLEncoder.encode(deletedSince, "ISO-8859-1");
		
		String expectedUrl = filesUrl+paramsString;

		FileRequestParameters params = new FileRequestParameters();
		params.documentId = documentId;		
		params.groupId = groupId;
		params.addedSince = addedSince;
		params.deletedSince = deletedSince;

		String methodName = "getGetFilesUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(params);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get files url with parameters is wrong", expectedUrl, url);
		
		expectedUrl = filesUrl;
		params = new FileRequestParameters();
		values = new ArrayList<Object>();		
		values.add(params);	
		url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get files url without parameters is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getGetFileUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = filesUrl+"/"+fileId;
		
		String methodName = "getGetFileUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(fileId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get file url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getDeleteFileUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = filesUrl+"/"+fileId;
		
		String methodName = "getDeleteFileUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(fileId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Delete file url is wrong", expectedUrl, url);
	}
}

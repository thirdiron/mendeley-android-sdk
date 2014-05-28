package com.mendeley.api.network.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;

import com.mendeley.api.network.FolderNetworkProvider;
import com.mendeley.api.network.NetworkProvider;
import com.mendeley.api.network.components.FolderRequestParameters;

public class FolderNetworkProviderTest extends TestCase {

	FolderNetworkProvider provider;
	String foldersUrl;
	final String folderId = "test-folder_id";
	
	@Override
	protected void setUp() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		provider = new FolderNetworkProvider();
		
		NetworkProvider networkProvider = new NetworkProvider();	
		ArrayList<Object> values = new ArrayList<Object>();			
		String methodName = "getApiUrl";
		String apiUrl = (String) networkProvider.getResultFromMethod(methodName, values);
		
		foldersUrl = apiUrl+"folders";
    }
	
	@Test
	public void test_getetFoldersUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String groupId = "test-group_id";
				
		String paramsString = "?group_id=" + groupId;; 
		String expectedUrl = foldersUrl+paramsString;

		FolderRequestParameters params = new FolderRequestParameters();
		params.groupId = groupId;
		
		String methodName = "getGetFoldersUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(params);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get folders url with parameters is wrong", expectedUrl, url);
		
		expectedUrl = foldersUrl;
		params = new FolderRequestParameters();
		values = new ArrayList<Object>();		
		values.add(params);	
		url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get folders url without parameters is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getGetFolderUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = foldersUrl+"/"+folderId;
		
		String methodName = "getGetFolderUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get folder url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getGetFolderDocumentIdsUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = foldersUrl+"/"+folderId+"/documents";
		
		String methodName = "getGetFolderDocumentIdsUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Get folder document ids url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getPostDocumentToFolderUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = foldersUrl+"/"+folderId+"/documents";
		
		String methodName = "getPostDocumentToFolderUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Post document to folder url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getDeleteFolderUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = foldersUrl+"/"+folderId;
		
		String methodName = "getDeleteFolderUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Delete folder url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getDeleteDocumentFromFolderUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String documentId = "test-document_id";
		String expectedUrl = foldersUrl+"/"+folderId+"/documents"+documentId;
		
		String methodName = "getDeleteDocumentFromFolderUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		values.add(documentId);
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Delete document from folder url is wrong", expectedUrl, url);
	}
	
	@Test
	public void test_getPatchFolderUrlUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = foldersUrl + "/"+folderId;
		
		String methodName = "getPatchFolderUrl";		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(folderId);	
		String url = (String) provider.getResultFromMethod(methodName, values);
		
		assertEquals("Patch folder url is wrong", expectedUrl, url);
	}
}

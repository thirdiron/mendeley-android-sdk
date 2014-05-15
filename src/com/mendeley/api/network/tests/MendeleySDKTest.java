package com.mendeley.api.network.tests;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.mendeley.api.model.Document;
import com.mendeley.api.network.JasonParser;
import com.mendeley.api.network.MendeleySDK;
import com.mendeley.api.network.NetworkProvider;
import com.mendeley.api.network.components.MendeleyResponse;

public class MendeleySDKTest extends TestCase {

	static String testDocumentId = "test_id";
	
	MendeleySDK sdk; 
	NetworkProvider provider;
	HttpsURLConnection getConnection;
	HttpsURLConnection postConnection;
	HttpsURLConnection deleteConnection;
	
	@Override
	protected void setUp() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sdk = new MendeleySDK();
		provider = new NetworkProvider();
		
		createConnections() ;
    }
	
	@Parameters
	public static Document getTestDocument() {
		Document testDocument = new Document();
	    testDocument.id = "test_document_id";
	    testDocument.title = "test_document_title";
	    
	    return testDocument;
	}

	private void createConnections() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String testUrl = "https://httpbin.org/";
		String getTestUrl = testUrl+"get";
		String postTestUrl =  testUrl+"post";
		String deleteTestUrl = testUrl+"delete";

		getConnection = getHttpsURLConnection(getTestUrl, "GET");
		postConnection = getHttpsURLConnection(postTestUrl, "POST");
		deleteConnection = getHttpsURLConnection(deleteTestUrl, "DELETE");
		
	}
	
	private HttpsURLConnection getHttpsURLConnection(String testUrl, String method) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String methodName = "getConnection";			
		HttpsURLConnection con;		
		String endPoint = method;	
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(testUrl);
		values.add(endPoint);		
		con = (HttpsURLConnection) provider.getMethodToTest(methodName, values);	
		con.setDoOutput(true);
		
		return con;
	}
	
	public void testPreconditions() {
		assertNotNull("MendeleySDK object is null", sdk);
		assertNotNull("NetworkProvider object is null", provider);
		assertNotNull("HttpsURLConnection getConnection object is null", getConnection);
		assertNotNull("HttpsURLConnection postConnection object is null", postConnection);
		assertNotNull("HttpsURLConnection deleteConnection object is null", deleteConnection);
    }	
	
	@Test
	public void test_getResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		
		String testProprty = "Test-Proprty";
		String testValue = "testValue";
		
		getConnection.addRequestProperty(testProprty, testValue);
		getConnection.connect();
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(getConnection);
		String methodName = "getResponse";
		
		MendeleyResponse response = (MendeleyResponse) provider.getMethodToTest(methodName, values);	

		InputStream is = getConnection.getInputStream();
		values = new ArrayList<Object>();		
		values.add(is);
		methodName = "getJsonString";
		
		String responseString = (String) provider.getMethodToTest(methodName, values);	
		
		JSONObject reponseJson = new JSONObject(responseString);
		JSONObject headersJson = reponseJson.getJSONObject("headers");
		
		String responseValue = headersJson.getString(testProprty);
		
		assertEquals("Request propery with wrong value", testValue, responseValue);
		assertEquals("Response code != 200", 200, response.responseCode);
	}

	@Test
	public void test_postResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		
		JasonParser parser = new JasonParser();
		Document testDocument = getTestDocument();
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(testDocument);
		String methodName = "jsonFromDocument";
		String testDocumentString = (String) parser.getMethodToTest(methodName, values);	

		postConnection.connect();		
		OutputStream os = postConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(os, "UTF-8"));
		writer.write(testDocumentString);
		writer.flush();
		writer.close();
		os.close();
		
		values = new ArrayList<Object>();		
		values.add(getConnection);
		methodName = "getResponse";
		
		MendeleyResponse response = (MendeleyResponse) provider.getMethodToTest(methodName, values);	

		InputStream is = postConnection.getInputStream();
		values = new ArrayList<Object>();		
		values.add(is);
		methodName = "getJsonString";
		String responseString = (String) provider.getMethodToTest(methodName, values);	
		is.close();
		
		JSONObject reponseJson = new JSONObject(responseString);
		JSONObject jsonObject = reponseJson.getJSONObject("json");
		
		values = new ArrayList<Object>();		
		values.add(jsonObject.toString());
		methodName = "parseDocument";
		Document responseDocument = (Document) parser.getMethodToTest(methodName, values);	
		
		assertEquals("Response code != 200", 200, response.responseCode);
		assertEquals("Posted and returned documents are not equal", testDocument, responseDocument);
	}
	
	@Test
	public void test_deleteResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		
		deleteConnection.connect();
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(deleteConnection);
		String methodName = "getResponse";

		MendeleyResponse response = (MendeleyResponse) provider.getMethodToTest(methodName, values);	
		
		assertEquals("Response code != 200", 200, response.responseCode);
	}

	
}

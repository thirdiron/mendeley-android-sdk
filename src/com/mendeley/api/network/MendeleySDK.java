package com.mendeley.api.network;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

import com.mendeley.api.model.Document;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyAPICallsInterface;

public class MendeleySDK implements AuthenticationInterface {

	static boolean authenticated = false;
	private static String apiUrl = "https://mix.mendeley.com:443/";
	private static String documentsUrl = apiUrl + "documents/";
	
	Context appContext;
	AuthentictionManager authentictionManager;
	MethodtoInvoke methodToInvoke;
	
	NetworkProvider networkProvider;
	MendeleyAPICallsInterface appInterface;
	
	
	public MendeleySDK(Context appContext) {
		this.appContext = appContext;
		appInterface = (MendeleyAPICallsInterface) appContext;
		authentictionManager = new AuthentictionManager(appContext, this);
		networkProvider = new NetworkProvider(this, appInterface);
	}

	public void getDocuments() throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName());
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doGetDocuments(documentsUrl);
		}
	}
	
	public void getDocument(String documentId) throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doGetDocument(documentsUrl, documentId);
		}
	}
	
	public void trashDocument(String documentId) throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doPostTrashDocument(documentsUrl, documentId);
		}
	}
	
	public void deleteDocument(String documentId) throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doDeleteDocument(documentsUrl, documentId);
		}
	}
	
	public void postDocument(Document document) throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{document.getClass()}, new Object[]{document});
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doPostDocument(documentsUrl, document);
		}
	}

	public void patchDocument(String id, Date date, Document document) throws ClientProtocolException, IOException {
		if (!authenticated) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{id.getClass(), date.getClass(), document.getClass()}, new Object[]{id, date, document});
			authentictionManager.authenticate();
		}
		else {
			networkProvider.doPatchDocument(documentsUrl, id, date, document);
		}
	}

	private void invokeMethod() {
		
		try {
			Method method = null;
			String className = this.getClass().getName();			
			Class<?> clazz = Class.forName(className);  	
			if (methodToInvoke.argumentTypes != null) {
				method = clazz.getMethod(methodToInvoke.methodName, methodToInvoke.argumentTypes);  	
				method.invoke(this, methodToInvoke.arguments);  
			} else {
				method = clazz.getMethod(methodToInvoke.methodName);
				method.invoke(this);
			}
		}
		catch (Exception e) {
			Log.e("", "", e);
		}
	}

	@Override
	public void onAuthenticated() {
		authenticated = true;
		invokeMethod();
	}
	
	protected class MethodtoInvoke {
		
		String methodName;
		@SuppressWarnings("rawtypes")
		Class[] argumentTypes;
		Object[] arguments;
		
		MethodtoInvoke(String methodName) {
			this.methodName = methodName;
			argumentTypes = null;
			arguments = null;
		}
		
		MethodtoInvoke(String methodName, @SuppressWarnings("rawtypes") Class[] argumentTypes, Object[] arguments) {
			this.methodName = methodName;
			this.argumentTypes = argumentTypes;
			this.arguments = arguments;
		}
	}

	@Override
	public void onAuthenticationFail() {
		Log.e("", "onAuthenticationFail");
	}


	@Override
	public void onAPICallFail() {
		Log.e("", "onAPICallFail");
	}

	
	// Testing
	
	public MendeleySDK() {
		
	}
	
}

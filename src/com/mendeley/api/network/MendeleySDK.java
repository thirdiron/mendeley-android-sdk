package com.mendeley.api.network;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

import com.mendeley.api.exceptions.InterfaceNotImplementedException;
import com.mendeley.api.model.Document;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyDocumentsInterface;
import com.mendeley.api.network.interfaces.MendeleyFoldersInterface;
import com.mendeley.api.util.Utils;

public class MendeleySDK implements AuthenticationInterface {

	static boolean authenticated = false;
	private static String apiUrl = "https://mix.mendeley.com:443/";
	private static String documentsUrl = apiUrl + "documents/";
	
	Context appContext;
	AuthentictionManager authentictionManager;
	MethodtoInvoke methodToInvoke;
	
	DocumentsNetworkProvider documentdNetworkProvider;
	
	MendeleyDocumentsInterface documentsInterface;
	MendeleyFoldersInterface foldersInterface;

	public MendeleySDK(Context appContext) {
		this.appContext = appContext;
		
		authentictionManager = new AuthentictionManager(appContext, this);
		initialiseInterfaces(appContext);
		
		hasCredentials();
	}

	public boolean hasCredentials() {
		return authentictionManager.hasCredentials();
	}
	
	public void clearCredentials() {
		authentictionManager.clearCredentials();
	}
	
	private void initialiseInterfaces(Context context) {
		
		if (context instanceof MendeleyDocumentsInterface) {
			documentsInterface = (MendeleyDocumentsInterface) context;
		}
		
		if (context instanceof MendeleyFoldersInterface) {
			foldersInterface = (MendeleyFoldersInterface) context;
		}
		
		documentdNetworkProvider = new DocumentsNetworkProvider(this, documentsInterface);
	} 
	
	private boolean isAuthenticated() {

		boolean isAuthenticated = false;
		
		if (NetworkProvider.accessToken != null && NetworkProvider.expiresAt != null) {
			Date now = new Date();
			Date expires = null;
			try {
				expires = Utils.dateFormat.parse(NetworkProvider.expiresAt);
			} catch (ParseException e) {
				Log.e("", "", e);
				return false;
			}

			long diffInMs = expires.getTime() - now.getTime();
			long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
			
			isAuthenticated = diffInSec > 0;
		}

		return isAuthenticated;
	}

	public void getDocuments() throws ClientProtocolException, IOException, InterfaceNotImplementedException {
		if (documentsInterface == null) {
			throw new InterfaceNotImplementedException(MendeleyDocumentsInterface.class + " is not implemented ");
		}
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName());
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doGetDocuments(documentsUrl);
		}
	}
	
	public void getDocument(String documentId) throws ClientProtocolException, IOException {
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doGetDocument(documentsUrl, documentId);
		}
	}
	
	public void trashDocument(String documentId) throws ClientProtocolException, IOException {
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doPostTrashDocument(documentsUrl, documentId);
		}
	}
	
	public void deleteDocument(String documentId) throws ClientProtocolException, IOException {
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{documentId.getClass()}, new Object[]{documentId});
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doDeleteDocument(documentsUrl, documentId);
		}
	}
	
	public void postDocument(Document document) throws ClientProtocolException, IOException {
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{document.getClass()}, new Object[]{document});
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doPostDocument(documentsUrl, document);
		}
	}

	public void patchDocument(String id, Date date, Document document) throws ClientProtocolException, IOException {
		if (!isAuthenticated()) {
			methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[0].getMethodName(), new Class[]{id.getClass(), date.getClass(), document.getClass()}, new Object[]{id, date, document});
			authentictionManager.authenticate();
		}
		else {
			documentdNetworkProvider.doPatchDocument(documentsUrl, id, date, document);
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

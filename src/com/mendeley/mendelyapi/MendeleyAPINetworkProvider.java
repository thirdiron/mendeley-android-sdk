package com.mendeley.mendelyapi;

import android.content.Context;

public class MendeleyAPINetworkProvider {
	
	public static final String AUTHENTICATEED_FILTER = "com.mendeley.mendelyapi.Authenticate";
	public static final String INTENT_TYPE = "intentType";
	public static final int INTENT_AUTHENTICATED = 0;
	public static final int INTENT_LOGIN = 1;
	public static final int INTENT_REFRESH = 2;
	
	public static String tokenType = "";
	public static String accessToken = "";
	public static String refreshToken;
	public static int expiresIn = 0;
	
	Context context;
		
	AuthentictionManager authentictionManager;
	DocumentsManager documentsManager;
	FolderManager folderManager;
	
	
	public MendeleyAPINetworkProvider(Context context) {
		this.context = context;
	}
	
	
	public DocumentsManager getDocumentsManager() {
		
		if (documentsManager == null) {
			documentsManager = new DocumentsManager(context);
		}
		
		return documentsManager;
	}
	
	
	public AuthentictionManager getAuthentictionManager() {
		
		if (authentictionManager == null) {
			authentictionManager = new AuthentictionManager(context);
		}
		
		return authentictionManager;
	}
	
	public FolderManager getFolderManager() {
		
		if (folderManager == null) {
			folderManager = new FolderManager(context);
		}
		
		return folderManager;
	}
	
}

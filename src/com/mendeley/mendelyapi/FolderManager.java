package com.mendeley.mendelyapi;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.mendeley.mendelyapi.model.MendeleyFolder;

public class FolderManager extends APICallManager {

	String foldersUrl = API_URL + "library/folders/";
	
	
	protected FolderManager(Context context) {
		super(context);
	}

	
	public MendeleyFolder getMendeleyFolder() throws Exception {

		MendeleyFolder mendeleyFolder = null;
		
		Log.e("", "getMendeleyFolder");
		
		try {
            HttpResponse response = doGet(foldersUrl);

            String jsonString = getJsonString(response.getEntity().getContent());
            
            
            Log.e("", "Folders --> " + jsonString);
            
            JSONObject folderObject = new JSONObject(jsonString);
            
            
            
		}
        catch (JSONException e) {
        	
        	Log.e("", "", e);
        	
            throw new Exception(e);
        }
		
		return mendeleyFolder;
	}
}

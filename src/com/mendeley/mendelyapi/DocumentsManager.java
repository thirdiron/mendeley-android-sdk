

package com.mendeley.mendelyapi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mendeley.mendelyapi.model.DocumentFile;
import com.mendeley.mendelyapi.model.MendeleyDocument;
import com.mendeley.mendelyapi.model.MendeleyLibrary;
import com.mendeley.mendelyapi.model.Person;

public class DocumentsManager extends APICallManager {
	
	static final String MENDELEY_FOLDER = "Mendeley";
	static final String DOCUMENTS_FOLDER = MENDELEY_FOLDER+"/"+"Documents";
	
	String documentsUrl = API_URL + "library/";
	String documentUrl = API_URL + "library/documents/";	
	
	protected DocumentsManager(Context context) {
		super(context);
	}
	
    private String getDocumentsFolder() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
	    File folder = new File(filepath, DOCUMENTS_FOLDER);

	    if(!folder.exists()) {
	    	folder.mkdirs();
	    }

	    return (folder.getAbsolutePath());
	}
	

	HttpResponse doGetIds(String url, int page, int items) throws ClientProtocolException, IOException {
		return doGet(url +="?page="+page+"&items="+items);
	}
	
	
	public MendeleyLibrary getMendeleyLibrary(int page, int itemsPerPage) throws Exception {

		MendeleyLibrary mendeleyLibrary;
		ArrayList<String> documentsIds = new ArrayList<String>();
		
		try {
            HttpResponse response = doGetIds(documentsUrl, page, itemsPerPage);

            String jsonString = getJsonString(response.getEntity().getContent());
            
            JSONObject libraryObject = new JSONObject(jsonString);
            
            JSONArray documentIds = libraryObject.getJSONArray("document_ids");
            
            for (int i = 0; i < documentIds.length(); i++) {
            	documentsIds.add(documentIds.getString(i));
            }
            
            mendeleyLibrary = new MendeleyLibrary(
            		libraryObject.getInt("total_results"), 
            		libraryObject.getInt("total_pages"), 
            		libraryObject.getInt("current_page"), 
            		libraryObject.getInt("items_per_page"),
            		documentsIds);
            
            Log.e("", mendeleyLibrary.toString());
            
		}
        catch (JSONException e) {
            throw new Exception(e);
        }
		
		return mendeleyLibrary;
	}
	
	public MendeleyLibrary getMendeleyLibrary() throws Exception {

		MendeleyLibrary mendeleyLibrary;
		
		try {
            HttpResponse response = doGetIds(documentsUrl, 0, MendeleyLibrary.MAX_ITEMS_PER_PAGE);

            String jsonString = getJsonString(response.getEntity().getContent());
            
            JSONObject libraryObject = new JSONObject(jsonString);
            
            mendeleyLibrary = new MendeleyLibrary(
            		libraryObject.getInt("total_results"), 
            		libraryObject.getInt("total_pages"), 
            		libraryObject.getInt("current_page"), 
            		libraryObject.getInt("items_per_page"),
            		new ArrayList<String>(0));            
		}
        catch (JSONException e) {
            throw new Exception(e);
        }
		
		return mendeleyLibrary;
	}
	
	
	public List<MendeleyDocument> getDocuments(List<String> documentIds) throws Exception {
		
		List<MendeleyDocument> documents = new ArrayList<MendeleyDocument>();

		try {
			for (String id : documentIds) {
				String url = documentUrl + id;

	            HttpResponse response = doGet(url);
	            
	            String jsonString = getJsonString(response.getEntity().getContent());

	            documents.add(parseDocument(jsonString));
			}
		}
        catch (JSONException e) {
        	
        	Log.e("", "", e);
        	
            throw new Exception(e);
        }

		return documents;
	}
	
	private MendeleyDocument parseDocument(String jsonString) throws JSONException {
		MendeleyDocument mendeleyDocument = new MendeleyDocument();
		
		Log.e("", jsonString);
		
		JSONObject documentObject = new JSONObject(jsonString);
		 
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {
			  
				case "last_modified":
					mendeleyDocument.lastModified = documentObject.getString(key);
					break;
				case "group_id":
					mendeleyDocument.groupId = documentObject.getString(key);
					break;
				case "profile_id":
					mendeleyDocument.profileId = documentObject.getString(key);
					break;
				case "read":
					mendeleyDocument.read = documentObject.getBoolean(key);
					break;
				case "starred":
					mendeleyDocument.starred = documentObject.getBoolean(key);
					break;
				case "authored":
					mendeleyDocument.authored = documentObject.getBoolean(key);
					break;
				case "confirmed":
					mendeleyDocument.confirmed = documentObject.getBoolean(key);
					break;
				case "hidden":
					mendeleyDocument.hidden = documentObject.getBoolean(key);
					break;
				case "id":
					mendeleyDocument.id = documentObject.getString(key);
					break;
				case "type":
					mendeleyDocument.type = documentObject.getString(key);
					break;
				case "month":
					mendeleyDocument.month = documentObject.getInt(key);
					break;
				case "year":
					mendeleyDocument.year = documentObject.getInt(key);
					break;
				case "day":
					mendeleyDocument.day = documentObject.getInt(key);
					break;
				case "source":
					mendeleyDocument.source = documentObject.getString(key);
					break;
				case "title":
					mendeleyDocument.title = documentObject.getString(key);
					break;
				case "revision":
					mendeleyDocument.revision = documentObject.getString(key);
					break;
				case "abstract":
					mendeleyDocument.abstractString = documentObject.getString(key);
					break;
				case "added":
					mendeleyDocument.added = documentObject.getString(key);
					break;
				case "pages":
					mendeleyDocument.pages = documentObject.getString(key);
					break;
				case "volume":
					mendeleyDocument.volume = documentObject.getString(key);
					break;
				case "issue":
					mendeleyDocument.issue = documentObject.getString(key);
					break;
				case "website":
					mendeleyDocument.website = documentObject.getString(key);
					break;
				case "publisher":
					mendeleyDocument.publisher = documentObject.getString(key);
					break;
				case "city":
					mendeleyDocument.city = documentObject.getString(key);
					break;
				case "edition":
					mendeleyDocument.edition = documentObject.getString(key);
					break;
				case "institution":
					mendeleyDocument.institution = documentObject.getString(key);
					break;
				case "series":
					mendeleyDocument.series = documentObject.getString(key);
					break;
				case "chapter":
					mendeleyDocument.chapter = documentObject.getString(key);
					break;		
				case "authors":
					  
					JSONArray authors = documentObject.getJSONArray(key);
			            
		            for (int i = 0; i < authors.length(); i++) {
		            	Person author = new Person (
		            			authors.getJSONObject(i).getString("forename"),
		            			authors.getJSONObject(i).getString("surname"));
		            	mendeleyDocument.authors.add(author);
		            }
		            break;
				case "editors":
					  
					JSONArray editors = documentObject.getJSONArray(key);
			            
		            for (int i = 0; i < editors.length(); i++) {
		            	Person editor = new Person (
		            			editors.getJSONObject(i).getString("forename"),
		            			editors.getJSONObject(i).getString("surname"));
		            	mendeleyDocument.editors.add(editor);
		            }
		            break;
				case "identifiers":
					  
					Log.e("", "" + documentObject.getString(key));
					
//					JSONObject identifiersObject = new JSONObject (documentObject.getString(key));
//						 
//					 for (@SuppressWarnings("unchecked") Iterator<String> identifierIter = 
//							 identifiersObject.keys(); identifierIter.hasNext();) {
//					  
//						 String identifierKey = identifierIter.next();
//						 mendeleyDocument.identifiers.put(identifierKey, identifiersObject.getString(identifierKey));
//					 }
					 break;
					  
				case "files":
					  
					File docsFolder = new File(getDocumentsFolder());
		            File[] existingFiles = docsFolder.listFiles();
			            
		            JSONArray files = documentObject.getJSONArray(key);
		            for (int i = 0; i < files.length(); i++) {
		            	DocumentFile file = new DocumentFile();
		            	file.fileHash = files.getJSONObject(i).getString("file_hash");
		            	file.fileSize = files.getJSONObject(i).getInt("file_size");
		            	file.fileExtension = files.getJSONObject(i).getString("file_extension");
		            	file.dateAdded = files.getJSONObject(i).getString("date_added");		            	
		            	mendeleyDocument.files.add(file);
			            	
			            for (int j = 0; j < existingFiles.length; j++) {
			            	if (existingFiles[j].getName().equals(file.fileHash+"."+file.fileExtension)) {
			            		mendeleyDocument.downloaded = true;
			            		break;
			            	}
			            }
		            }
		            break;
			  }
		  }
		  
		 return mendeleyDocument;
	}

}




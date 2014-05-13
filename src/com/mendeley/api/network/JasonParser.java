package com.mendeley.api.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mendeley.api.model.Document;
import com.mendeley.api.model.Person;

public class JasonParser {
	
	protected String jsonFromDocument(Document document) throws JSONException {

		JSONArray authors = new JSONArray();
		
		for (int i = 0; i < document.authors.size(); i++) {
			JSONObject author = new JSONObject();
			author.put("forename", document.authors.get(i).forename);
			author.put("surname", document.authors.get(i).surname);
			authors.put(i, author);
		}
		
		JSONObject jDocument = new JSONObject();
		
		jDocument.put("title", document.title);
		jDocument.put("authors", authors);
		jDocument.put("type", document.type);
		
		return jDocument.toString();
		
	}

	protected Document parseDocument(String jsonString) throws JSONException {
		
		Document mendeleyDocument = new Document();

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
					JSONObject identifiersObject = new JSONObject (documentObject.getString(key));
						 
					 for (@SuppressWarnings("unchecked") Iterator<String> identifierIter = 
							 identifiersObject.keys(); identifierIter.hasNext();) {
					  
						 String identifierKey = identifierIter.next();
						 mendeleyDocument.identifiers.put(identifierKey, identifiersObject.getString(identifierKey));
					 }
					 break;
			  }
		  }
		  
		 return mendeleyDocument;
	}
	
	protected List<Document> parseDocumentList(String jsonString) throws JSONException {
		
		List<Document> documents = new ArrayList<Document>();
		
		JSONArray jsonarray = new JSONArray(jsonString);
		
		for (int i = 0; i < jsonarray.length(); i++) {
			documents.add(parseDocument(jsonarray.getString(i)));
		}
		
		return documents;
	}
	
	protected Person parsePerson(String jsonString) {
		
		return null;
	}
	
	protected List<Person> parsePersonList(String jsonString) {
		
		return null;
	}
}

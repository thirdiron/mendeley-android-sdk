package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mendeley.api.model.Discipline;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.model.Education;
import com.mendeley.api.model.Employment;
import com.mendeley.api.model.File;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Person;
import com.mendeley.api.model.Photo;
import com.mendeley.api.model.Profile;

/**
 * This class hold methods to parse json strings to model objects 
 * as well as create json strings from objects that are used by the NetwrokProvider classes.
 *
 */
public class JsonParser {
	/**
	 * Creating a json string from a Document object
	 * @param document the Document object
	 * @return the json string
	 * @throws JSONException
	 */
	protected static String jsonFromDocument(Document document) throws JSONException {

		JSONArray authors = new JSONArray();
		for (int i = 0; i < document.authors.size(); i++) {
			JSONObject author = new JSONObject();
			author.put("first_name", document.authors.get(i).firstName);
			author.put("last_name", document.authors.get(i).lastName);
			authors.put(i, author);
		}
		
		JSONArray editors = new JSONArray();
		for (int i = 0; i < document.editors.size(); i++) {
			JSONObject editor = new JSONObject();
			editor.put("first_name", document.editors.get(i).firstName);
			editor.put("last_name", document.editors.get(i).lastName);
			editors.put(i, editor);
		}
		
		JSONObject identifiers = new JSONObject();
		for (String key : document.identifiers.keySet()) {
			identifiers.put(key, document.identifiers.get(key));
		}

		JSONObject jDocument = new JSONObject();
		
		jDocument.put("title", document.title);
		jDocument.put("authors", authors);
		jDocument.put("type", document.type);
		jDocument.put("id", document.id);
		
		jDocument.put("last_modified", document.lastModified);
		jDocument.put("group_id", document.groupId);
		jDocument.put("profile_id", document.profileId);
		jDocument.put("read", document.read);
		jDocument.put("starred", document.starred);
		jDocument.put("authored", document.authored);
		jDocument.put("confirmed", document.confirmed);
		jDocument.put("hidden", document.hidden);
		jDocument.put("month", document.month);
		jDocument.put("year", document.year);
		jDocument.put("day", document.day);
		jDocument.put("source", document.source);
		jDocument.put("revision", document.revision);
		jDocument.put("abstract", document.abstractString);
		jDocument.put("created", document.created);
		jDocument.put("pages", document.pages);
		jDocument.put("volume", document.volume);
		jDocument.put("issue", document.issue);
		jDocument.put("website", document.website);
		jDocument.put("publisher", document.publisher);
		jDocument.put("city", document.city);
		jDocument.put("edition", document.edition);
		jDocument.put("institution", document.institution);
		jDocument.put("series", document.series);
		jDocument.put("chapter", document.chapter);
		jDocument.put("identifiers", identifiers);
		jDocument.put("editors", editors);
		
		return jDocument.toString();
	}
	
	/**
	 * Creating a json string from a Folder object
	 * 
	 * @param folder the Folder object
	 * @return the json string
	 * @throws JSONException
	 */
	protected static String jsonFromFolder(Folder folder) throws JSONException {

		JSONObject jFolder = new JSONObject();
		
		jFolder.put("name", folder.name);
		jFolder.put("parent_id", folder.parentId);
		jFolder.put("id", folder.id);
		jFolder.put("group_id", folder.groupId);
		jFolder.put("added", folder.added);
		
		return jFolder.toString();
	}
	
	/**
	 * Creating a json string from a document id string
	 * 
	 * @param documentId the document id string
	 * @return the json string
	 * @throws JSONException
	 */
	protected static String jsonFromDocumentId(String documentId) throws JSONException {
		JSONObject jDocument = new JSONObject();		
		jDocument.put("id", documentId);		
		return jDocument.toString();
	}
	
	/**
	 * Creating a list of string document ids from a json string
	 * 
	 * @param jsonString the json string of the document ids
	 * @return the list of string document ids
	 * @throws JSONException
	 */
	protected static List<DocumentId> parseDocumentIds(String jsonString) throws JSONException {
		List<DocumentId> documentIds = new ArrayList<DocumentId>();
		JSONArray jsonArray = new JSONArray(jsonString);
		for (int i = 0; i < jsonArray.length(); i++) {	
			documentIds.add(parseDocumentId(jsonArray.getString(i)));
		}
		
		return documentIds;
	}

    static Map<String, String> parseDocumentTypes(String jsonString) throws JSONException{
		Map<String, String> typesMap = new HashMap<String, String>();
		
		JSONArray jsonarray = new JSONArray(jsonString);
		
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonObject = jsonarray.getJSONObject(i);
			String key = jsonObject.getString("name");
			String value = jsonObject.getString("description");
			typesMap.put(key, value);
		}
		return typesMap;
	}

	/**
	 * Creating a File object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the File object
	 * @throws JSONException
	 */
	protected static File parseFile(String jsonString) throws JSONException {
		File.Builder mendeleyFile = new File.Builder();
		
		JSONObject documentObject = new JSONObject(jsonString);
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {			  
				case "id":
					mendeleyFile.setId(documentObject.getString(key));
					break;
				case "document_id":
					mendeleyFile.setDocumentId(documentObject.getString(key));
					break;
				case "mime_type":
					mendeleyFile.setMimeType(documentObject.getString(key));
					break;
				case "file_name":
					mendeleyFile.setFileName(documentObject.getString(key));
					break;
				case "filehash":
					mendeleyFile.setFileHash(documentObject.getString(key));
					break;
			}
		}
		
		return mendeleyFile.build();
	}
	
	protected static DocumentId parseDocumentId(String jsonString) throws JSONException {
		DocumentId.Builder mendeleyDocumentId = new DocumentId.Builder();
		
		JSONObject documentObject = new JSONObject(jsonString);
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {			  
				case "id":
					mendeleyDocumentId.setDocumentId(documentObject.getString(key));
					break;
			}
		}
		
		return mendeleyDocumentId.build();
	}
	
	/**
	 * Creating a list of Folder objects from a json string
	 * 
	 * @param jsonString the json string
	 * @return the list of Folder objects
	 * @throws JSONException
	 */
	protected static List<Folder> parseFolderList(String jsonString) throws JSONException {
		
		List<Folder> folders = new ArrayList<Folder>();
		
		JSONArray jsonarray = new JSONArray(jsonString);
		
		for (int i = 0; i < jsonarray.length(); i++) {
			folders.add(parseFolder(jsonarray.getString(i)));
		}
		
		return folders;
	}
	
	/**
	 * Creating a Folder object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the Folder object
	 * @throws JSONException
	 */
	protected static Folder parseFolder(String jsonString) throws JSONException {
		
		Folder.Builder mendeleyFolder = new Folder.Builder();
		
		JSONObject folderObject = new JSONObject(jsonString);
		
		mendeleyFolder.setName(folderObject.getString("name"));
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				folderObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {
			  
				case "parent_id":
					mendeleyFolder.setParentId(folderObject.getString(key));
					break;
				case "id":
					mendeleyFolder.setId(folderObject.getString(key));
					break;
				case "group_id":
					mendeleyFolder.setGroupId(folderObject.getString(key));
					break;
				case "added":
					mendeleyFolder.setAdded(folderObject.getString(key));
					break;
			}
		}
		
		return mendeleyFolder.build();
	}
	
	/**
	 * Creating a Profile object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the Profile object
	 * @throws JSONException
	 */
	protected static Profile parseProfile(String jsonString) throws JSONException {
		
		Profile.Builder mendeleyProfile = new Profile.Builder();
		
		JSONObject profileObject = new JSONObject(jsonString);
		 
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				profileObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {
			  
				case "location":
					mendeleyProfile.setLastName(profileObject.getString(key));
					break;
				case "id":
					mendeleyProfile.setId(profileObject.getString(key));
					break;
				case "display_name":
					mendeleyProfile.setDisplayName(profileObject.getString(key));
					break;
				case "user_type":
					mendeleyProfile.setUserType(profileObject.getString(key));
					break;
				case "url":
					mendeleyProfile.setUrl(profileObject.getString(key));
					break;
				case "email":
					mendeleyProfile.setEmail(profileObject.getString(key));
					break;
				case "link":
					mendeleyProfile.setLink(profileObject.getString(key));
					break;
				case "first_name":
					mendeleyProfile.setFirstName(profileObject.getString(key));
					break;
				case "last_name":
					mendeleyProfile.setLastName(profileObject.getString(key));
					break;
				case "research_interests":
					mendeleyProfile.setResearchInterests(profileObject.getString(key));
					break;
				case "academic_status":
					mendeleyProfile.setAcademicStatus(profileObject.getString(key));
					break;
				case "verified":
					mendeleyProfile.setVerified(profileObject.getBoolean(key));
					break;
				case "created_at":
					mendeleyProfile.setCreatedAt(profileObject.getString(key));
					break;
				case "discipline":
					JSONObject disciplineObject = profileObject.getJSONObject(key);
					Discipline discipline = new Discipline();
					if (disciplineObject.has("name")) {
						discipline.name = disciplineObject.getString("name");
					}
					mendeleyProfile.setDiscipline(discipline);
					break;
				case "photo":
					JSONObject photoObject = profileObject.getJSONObject(key);
					Photo photo = new Photo();
					if (photoObject.has("standard")) {
						photo.standard = photoObject.getString("standard");
					}
					if (photoObject.has("square")) {
						photo.square = photoObject.getString("square");
					}
					
					mendeleyProfile.setPhoto(photo);
					break;
				case "education":					  
					JSONArray educationArray = profileObject.getJSONArray(key);
			       	ArrayList<Education> educationList = new ArrayList<Education>();
					
		            for (int i = 0; i < educationArray.length(); i++) {
		            	
		            	JSONObject educationObject = educationArray.getJSONObject(i);		            	
		            	Education.Builder education = new Education.Builder();
		            	
		            	for (@SuppressWarnings("unchecked") Iterator<String> educationIter = 
		            			educationObject.keys(); educationIter.hasNext();) {
		        		  
		        			String educationKey = educationIter.next();
		        			switch (educationKey) {
			        			case "id":
			        				education.setId(educationObject.getInt(educationKey));
			    					break;
			        			case "last_modified":
			        				education.setLastModified(educationObject.getString(educationKey));
			    					break;
			        			case "created":
			        				education.setCreated(educationObject.getString(educationKey));
			    					break;
			        			case "degree":
			        				education.setDegree(educationObject.getString(educationKey));
			    					break;
			        			case "institution":
			        				education.setInstitution(educationObject.getString(educationKey));
			    					break;
			        			case "start_date":
			        				education.setStartDate(educationObject.getString(educationKey));
			    					break;
			        			case "end_date":
			        				education.setEndDate(educationObject.getString(educationKey));
			    					break;
			        			case "website":
			        				education.setWebsite(educationObject.getString(educationKey));
			    					break;
		        			}
		            	}
		            	educationList.add(education.build());
		            	
		            }
		            
		            mendeleyProfile.setEducation(educationList);
		            break;
				case "employment":					  
					JSONArray employmentArray = profileObject.getJSONArray(key);
					ArrayList<Employment> employmentList = new ArrayList<Employment>();

		            for (int i = 0; i < employmentArray.length(); i++) {
		            	
		            	JSONObject employmentObject = employmentArray.getJSONObject(i);		            	
		            	Employment employment = new Employment();

		            	for (@SuppressWarnings("unchecked") Iterator<String> employmentIter = 
		            			employmentObject.keys(); employmentIter.hasNext();) {
		        		  
		        			String employmentKey = employmentIter.next();

		        			switch (employmentKey) {
			        			case "id":
			        				employment.id = employmentObject.getInt(employmentKey);
			    					break;
			        			case "last_modified":
			        				employment.lastModified = employmentObject.getString(employmentKey);
			    					break;
			        			case "position":
			        				employment.position = employmentObject.getString(employmentKey);
			    					break;
			        			case "created":
			        				employment.created = employmentObject.getString(employmentKey);
			    					break;
			        			case "institution":
			        				employment.institution = employmentObject.getString(employmentKey);
			    					break;
			        			case "start_date":
			        				employment.startDate = employmentObject.getString(employmentKey);
			    					break;
			        			case "end_date":
			        				employment.endDate = employmentObject.getString(employmentKey);
			    					break;
			        			case "website":
			        				employment.website = employmentObject.getString(employmentKey);
			    					break;
			        			case "is_main_employment":
			        				employment.isMainEmployment = employmentObject.getBoolean(employmentKey);
			    					break;
			        			case "classes":
			        				JSONArray classesArray = employmentObject.getJSONArray(key);
			        				for (int j= 0; j < classesArray.length(); j++) {
			        					employment.classes.add(classesArray.getString(j));
			        				}
			    					break;
		        			}
		            	}
		            	employmentList.add(employment);		            	
		            }
		            
		            mendeleyProfile.setEmployment(employmentList);
		            break;
			}
		}
		
		return mendeleyProfile.build();
	}

	/**
	 * Creating a Document object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the Document object
	 * @throws JSONException
	 */
	protected static Document parseDocument(String jsonString) throws JSONException {
		Document.Builder mendeleyDocument = new Document.Builder();
		JSONObject documentObject = new JSONObject(jsonString);
		 
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
			switch (key) {
			  
				case "last_modified":
					mendeleyDocument.setLastModified(documentObject.getString(key));
					break;
				case "group_id":
					mendeleyDocument.setGroupId(documentObject.getString(key));
					break;
				case "profile_id":
					mendeleyDocument.setProfileId(documentObject.getString(key));
					break;
				case "read":
					mendeleyDocument.setRead(documentObject.getBoolean(key));
					break;
				case "starred":
					mendeleyDocument.setStarred(documentObject.getBoolean(key));
					break;
				case "authored":
					mendeleyDocument.setAuthored(documentObject.getBoolean(key));
					break;
				case "confirmed":
					mendeleyDocument.setConfirmed(documentObject.getBoolean(key));
					break;
				case "hidden":
					mendeleyDocument.setHidden(documentObject.getBoolean(key));
					break;
				case "id":
					mendeleyDocument.setId(documentObject.getString(key));
					break;
				case "type":
					mendeleyDocument.setType(documentObject.getString(key));
					break;
				case "month":
					mendeleyDocument.setMonth(documentObject.getInt(key));
					break;
				case "year":
					mendeleyDocument.setYear(documentObject.getInt(key));
					break;
				case "day":
					mendeleyDocument.setDay(documentObject.getInt(key));
					break;
				case "source":
					mendeleyDocument.setSource(documentObject.getString(key));
					break;
				case "title":
					mendeleyDocument.setTitle(documentObject.getString(key));
					break;
				case "revision":
					mendeleyDocument.setRevision(documentObject.getString(key));
				case "created":
					mendeleyDocument.setCreated(documentObject.getString(key));
					break;
				case "abstract":
					mendeleyDocument.setAbstractString(documentObject.getString(key));
					break;
				case "pages":
					mendeleyDocument.setPages(documentObject.getString(key));
					break;
				case "volume":
					mendeleyDocument.setVolume(documentObject.getString(key));
					break;
				case "issue":
					mendeleyDocument.setIssue(documentObject.getString(key));
					break;
				case "website":
					mendeleyDocument.setWebsite(documentObject.getString(key));
					break;
				case "publisher":
					mendeleyDocument.setPublisher(documentObject.getString(key));
					break;
				case "city":
					mendeleyDocument.setCity(documentObject.getString(key));
					break;
				case "edition":
					mendeleyDocument.setEdition(documentObject.getString(key));
					break;
				case "institution":
					mendeleyDocument.setInstitution(documentObject.getString(key));
					break;
				case "series":
					mendeleyDocument.setSeries(documentObject.getString(key));
					break;
				case "chapter":
					mendeleyDocument.setChapter(documentObject.getString(key));
					break;		
				case "authors":
					  
					JSONArray authors = documentObject.getJSONArray(key);
					ArrayList<Person> authorsList = new ArrayList<Person>();
			            
		            for (int i = 0; i < authors.length(); i++) {
		            	Person author = new Person (
		            			authors.getJSONObject(i).getString("first_name"),
		            			authors.getJSONObject(i).getString("last_name"));
		            	authorsList.add(author);
		            }
		            
		            mendeleyDocument.setAuthors(authorsList);
		            break;
				case "editors":
					  
					JSONArray editors = documentObject.getJSONArray(key);
					ArrayList<Person> editorsList = new ArrayList<Person>();
			            
		            for (int i = 0; i < editors.length(); i++) {
		            	Person editor = new Person (
		            			editors.getJSONObject(i).getString("first_name"),
		            			editors.getJSONObject(i).getString("last_name"));
		            	editorsList.add(editor);
		            }
		            
		            mendeleyDocument.setEditors(editorsList);
		            break;
				case "identifiers":
					JSONObject identifiersObject = documentObject.getJSONObject(key);
					HashMap<String, String> identifiersMap = new HashMap<String, String>();
						 
					 for (@SuppressWarnings("unchecked") Iterator<String> identifierIter = 
							 identifiersObject.keys(); identifierIter.hasNext();) {
					  
						 String identifierKey = identifierIter.next();
						 identifiersMap.put(identifierKey, identifiersObject.getString(identifierKey));
					 }
					 
					 mendeleyDocument.setIdentifiers(identifiersMap);
					 break;
			  }
		  }
		  
		 return mendeleyDocument.build();
	}
	
	/**
	 * Creating a list of File objects from a json string
	 * 
	 * @param jsonString the json string
	 * @return the list of File objects
	 * @throws JSONException
	 */
	protected static List<File> parseFileList(String jsonString) throws JSONException {
		
		List<File> files = new ArrayList<File>();
		
		JSONArray jsonarray = new JSONArray(jsonString);
		
		for (int i = 0; i < jsonarray.length(); i++) {
			files.add(parseFile(jsonarray.getString(i)));
		}
		
		return files;
	}
	
	/**
	 *  Creating a list of Document objects from a json string
	 * 
	 * @param jsonString the json string
	 * @return the list of Document objects
	 * @throws JSONException
	 */
	protected static List<Document> parseDocumentList(String jsonString) throws JSONException {
		
		List<Document> documents = new ArrayList<Document>();
		
		JSONArray jsonarray = new JSONArray(jsonString);
		
		for (int i = 0; i < jsonarray.length(); i++) {
			documents.add(parseDocument(jsonarray.getString(i)));
		}
		
		return documents;
	}
	
	/**
     * Helper method for getting jeson string from an InputStream object
     * 
     * @param stream the InputStream object
     * @return the json String object
     * @throws IOException
     */
    static String getJsonString(InputStream stream) throws IOException {
		
		StringBuffer data = new StringBuffer();
		InputStreamReader isReader = null;
		BufferedReader br = null;
		
		try {
			
			isReader = new InputStreamReader(stream); 
            br = new BufferedReader(isReader);
            String brl = ""; 
            while ((brl = br.readLine()) != null) {
        	    data.append(brl);
            }
            
		} finally {
			stream.close();
            isReader.close();
            br.close();
		}
		
		return data.toString();
	}
}

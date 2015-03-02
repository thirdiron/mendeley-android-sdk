package com.mendeley.api.network;

import android.graphics.Color;

import com.mendeley.api.model.Annotation;
import com.mendeley.api.model.Box;
import com.mendeley.api.model.Discipline;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.model.Education;
import com.mendeley.api.model.Employment;
import com.mendeley.api.model.File;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Group;
import com.mendeley.api.model.Person;
import com.mendeley.api.model.Photo;
import com.mendeley.api.model.Point;
import com.mendeley.api.model.Profile;
import com.mendeley.api.model.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mendeley.api.model.Annotation.PrivacyLevel;

/**
 * This class hold methods to parse json strings to model objects 
 * as well as create json strings from objects that are used by the NetwrokProvider classes.
 */
public class JsonParser {
    private static final String TAG = JsonParser.class.getCanonicalName();

    public static String jsonFromAnnotation(Annotation annotation) throws JSONException {
        JSONArray positions = new JSONArray();
        for (int i = 0; i < annotation.positions.size(); i++) {
            Box box = annotation.positions.get(i);
            positions.put(i, serializeBox(box));
        }

        JSONObject jAnnotation = new JSONObject();

        jAnnotation.put("id", annotation.id);
        jAnnotation.put("type", annotation.type.name);
        jAnnotation.put("previous_id", annotation.previousId);
        jAnnotation.put("color", serializeColor(annotation.color));
        jAnnotation.put("text", annotation.text);
        jAnnotation.put("profile_id", annotation.profileId);
        jAnnotation.put("positions", positions);
        jAnnotation.put("created", annotation.created);
        jAnnotation.put("last_modified", annotation.lastModified);
        jAnnotation.put("privacy_level", annotation.privacyLevel.name);
        jAnnotation.put("filehash", annotation.fileHash);
        jAnnotation.put("document_id", annotation.documentId);

        return jAnnotation.toString();
    }

    private static JSONObject serializeColor(int color) throws JSONException {
        JSONObject jColor = new JSONObject();
        jColor.put("r", Color.red(color));
        jColor.put("g", Color.green(color));
        jColor.put("b", Color.blue(color));
        return jColor;
    }

    private static JSONObject serializeBox(Box box) throws JSONException {
        JSONObject topLeft = null;
        JSONObject bottomRight = null;

        if (box.topLeft != null) {
            topLeft = new JSONObject();
            topLeft.put("x", box.topLeft.x);
            topLeft.put("y", box.topLeft.y);
        }
        if (box.bottomRight != null) {
            bottomRight = new JSONObject();
            bottomRight.put("x", box.bottomRight.x);
            bottomRight.put("y", box.bottomRight.y);
        }

        JSONObject bbox = new JSONObject();
        bbox.put("top_left", topLeft);
        bbox.put("bottom_right", bottomRight);
        bbox.put("page", box.page);
        return bbox;
    }

    /**
     * Create a json string from a Document object
     * @param document the Document object
     * @return the json string
     * @throws JSONException
     */
	public static String jsonFromDocument(Document document) throws JSONException {
        final JSONObject jDocument = new JSONObject();

        if (!document.websites.isNull()) {
            JSONArray websites = new JSONArray();
            for (int i = 0; i < document.websites.size(); i++) {
                websites.put(i, document.websites.get(i));
            }
            jDocument.put("websites", websites);
        }

        if (!document.keywords.isNull()) {
            JSONArray keywords = new JSONArray();
            for (int i = 0; i < document.keywords.size(); i++) {
                keywords.put(i, document.keywords.get(i));
            }
            jDocument.put("keywords", keywords);
        }

        if (!document.tags.isNull()) {
            JSONArray tags = new JSONArray();
            for (int i = 0; i < document.tags.size(); i++) {
                tags.put(i, document.tags.get(i));
            }
            jDocument.put("tags", tags);
        }

        if (!document.authors.isNull()) {
            JSONArray authors = new JSONArray();
            for (int i = 0; i < document.authors.size(); i++) {
                JSONObject author = new JSONObject();
                author.put("first_name", document.authors.get(i).firstName);
                author.put("last_name", document.authors.get(i).lastName);
                authors.put(i, author);
            }
            jDocument.put("authors", authors);
        }

        if (!document.editors.isNull()) {
            JSONArray editors = new JSONArray();
            for (int i = 0; i < document.editors.size(); i++) {
                JSONObject editor = new JSONObject();
                editor.put("first_name", document.editors.get(i).firstName);
                editor.put("last_name", document.editors.get(i).lastName);
                editors.put(i, editor);
            }
            jDocument.put("editors", editors);
        }

        if (!document.identifiers.isNull()) {
            JSONObject identifiers = new JSONObject();
            for (String key : document.identifiers.keySet()) {
                identifiers.put(key, document.identifiers.get(key));
            }
            jDocument.put("identifiers", identifiers);
        }

		jDocument.put("title", document.title);
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
		jDocument.put("publisher", document.publisher);
		jDocument.put("city", document.city);
		jDocument.put("edition", document.edition);
		jDocument.put("institution", document.institution);
		jDocument.put("series", document.series);
		jDocument.put("chapter", document.chapter);
        jDocument.put("accessed", document.accessed);
        jDocument.put("file_attached", document.fileAttached);
        jDocument.put("client_data", document.clientData);
        jDocument.put("unique_id", document.uniqueId);

		return jDocument.toString();
	}
	
	/**
	 * Creating a json string from a Folder object
	 * 
	 * @param folder the Folder object
	 * @return the json string
	 * @throws JSONException
	 */
    public static String jsonFromFolder(Folder folder) throws JSONException {

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
    public static String jsonFromDocumentId(String documentId) throws JSONException {
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
    public static List<DocumentId> parseDocumentIds(String jsonString) throws JSONException {
		List<DocumentId> documentIds = new ArrayList<DocumentId>();
		JSONArray jsonArray = new JSONArray(jsonString);
		for (int i = 0; i < jsonArray.length(); i++) {	
			documentIds.add(parseDocumentId(jsonArray.get(i).toString()));
		}
		
		return documentIds;
	}

    public static Map<String, String> parseDocumentTypes(String jsonString) throws JSONException{
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
    public static File parseFile(String jsonString) throws JSONException {
		File.Builder mendeleyFile = new File.Builder();
		
		JSONObject documentObject = new JSONObject(jsonString);
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
            if (key.equals("id")) {
                mendeleyFile.setId(documentObject.getString(key));

            } else if (key.equals("document_id")) {
                mendeleyFile.setDocumentId(documentObject.getString(key));

            } else if (key.equals("mime_type")) {
                mendeleyFile.setMimeType(documentObject.getString(key));

            } else if (key.equals("file_name")) {
                mendeleyFile.setFileName(documentObject.getString(key));

            } else if (key.equals("filehash")) {
                mendeleyFile.setFileHash(documentObject.getString(key));

            } else if (key.equals("size")) {
                mendeleyFile.setFileSize(documentObject.getInt(key));
            }
		}
		
		return mendeleyFile.build();
	}

    public static DocumentId parseDocumentId(String jsonString) throws JSONException {
		DocumentId.Builder mendeleyDocumentId = new DocumentId.Builder();
		
		JSONObject documentObject = new JSONObject(jsonString);
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				 documentObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
            if (key.equals("id")) {
                mendeleyDocumentId.setDocumentId(documentObject.getString(key));

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
    public static List<Folder> parseFolderList(String jsonString) throws JSONException {
		
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
    public static Folder parseFolder(String jsonString) throws JSONException {
        JSONObject folderObject = new JSONObject(jsonString);

        Folder.Builder mendeleyFolder = new Folder.Builder(folderObject.getString("name"));
		
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter =
				folderObject.keys(); keysIter.hasNext();) {
			String key = keysIter.next();
            if (key.equals("parent_id")) {
                mendeleyFolder.setParentId(folderObject.getString(key));

            } else if (key.equals("id")) {
                mendeleyFolder.setId(folderObject.getString(key));

            } else if (key.equals("group_id")) {
                mendeleyFolder.setGroupId(folderObject.getString(key));

            } else if (key.equals("added")) {
                mendeleyFolder.setAdded(folderObject.getString(key));

            }
		}
		
		return mendeleyFolder.build();
	}

    /**
     * Creating a UserRole object from a json string
     *
     * @param jsonString the json string
     * @return the UserRole object
     * @throws JSONException
     */
    public static UserRole parseUserRole(String jsonString) throws JSONException {
        JSONObject userRoleObject = new JSONObject(jsonString);

        UserRole.Builder mendeleyUserRole = new UserRole.Builder();

        for (@SuppressWarnings("unchecked") Iterator<String> keysIter =
                     userRoleObject.keys(); keysIter.hasNext();) {
            String key = keysIter.next();
            if (key.equals("profile_id")) {
                mendeleyUserRole.setProfileId(userRoleObject.getString(key));

            } else if (key.equals("joined")) {
                mendeleyUserRole.setJoined(userRoleObject.getString(key));

            } else if (key.equals("role")) {
                mendeleyUserRole.setRole(userRoleObject.getString(key));

            }
        }

        return mendeleyUserRole.build();
    }

    public static Annotation parseAnnotation(String jsonString) throws JSONException {
        Annotation.Builder builder = new Annotation.Builder();

        JSONObject jAnnotation = new JSONObject(jsonString);

        for (Iterator<String> keysIter = jAnnotation.keys(); keysIter.hasNext(); ) {
            String key = keysIter.next();
            if (key.equals("id")) {
                builder.setId(jAnnotation.getString(key));

            } else if (key.equals("type")) {
                builder.setType(Annotation.Type.fromName(jAnnotation.getString(key)));

            } else if (key.equals("previous_id")) {
                builder.setPreviousId(jAnnotation.getString(key));

            } else if (key.equals("color")) {
                builder.setColor(parseColor(jAnnotation.getJSONObject(key)));

            } else if (key.equals("text")) {
                builder.setText(jAnnotation.getString(key));

            } else if (key.equals("profile_id")) {
                builder.setProfileId(jAnnotation.getString(key));

            } else if (key.equals("positions")) {
                JSONArray jPositions = jAnnotation.getJSONArray(key);
                builder.setPositions(parseBoundingBoxes(jPositions));

            } else if (key.equals("created")) {
                builder.setCreated(jAnnotation.getString(key));

            } else if (key.equals("last_modified")) {
                builder.setLastModified(jAnnotation.getString(key));

            } else if (key.equals("privacy_level")) {
                builder.setPrivacyLevel(PrivacyLevel.fromName(jAnnotation.getString(key)));

            } else if (key.equals("filehash")) {
                builder.setFileHash(jAnnotation.getString(key));

            } else if (key.equals("document_id")) {
                builder.setDocumentId(jAnnotation.getString(key));

            }
        }
        return builder.build();
    }

    private static int parseColor(JSONObject jColor) throws JSONException {
        int r = jColor.getInt("r");
        int g = jColor.getInt("g");
        int b = jColor.getInt("b");
        return Color.rgb(r, g, b);
    }

    private static List<Box> parseBoundingBoxes(JSONArray jPositions) throws JSONException {
        List<Box> boxes = new ArrayList<Box>();
        for (int i = 0; i < jPositions.length(); i++) {
            JSONObject jBox = jPositions.getJSONObject(i);

            Point topLeft = null;
            Point bottomRight = null;
            Integer page = null;

            if (jBox.has("page")) {
                page = jBox.getInt("page");
            }
            if (jBox.has("top_left")) {
                JSONObject jTopLeft = jBox.getJSONObject("top_left");
                topLeft = new Point(jTopLeft.getDouble("x"), jTopLeft.getDouble("y"));
            }
            if (jBox.has("bottom_right")) {
                JSONObject jBottomRight = jBox.getJSONObject("bottom_right");
                bottomRight = new Point(jBottomRight.getDouble("x"), jBottomRight.getDouble("y"));
            }

            Box box = new Box(topLeft, bottomRight, page);
            boxes.add(box);
        }
        return boxes;
    }

    /**
	 * Creating a Profile object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the Profile object
	 * @throws JSONException
	 */
    public static Profile parseProfile(String jsonString) throws JSONException {
		Profile.Builder mendeleyProfile = new Profile.Builder();
		
		JSONObject profileObject = new JSONObject(jsonString);
		 
		for (@SuppressWarnings("unchecked") Iterator<String> keysIter = 
				profileObject.keys(); keysIter.hasNext();) {
		  
			String key = keysIter.next();
            if (key.equals("location")) {
                mendeleyProfile.setLocation(profileObject.getString(key));

            } else if (key.equals("id")) {
                mendeleyProfile.setId(profileObject.getString(key));

            } else if (key.equals("display_name")) {
                mendeleyProfile.setDisplayName(profileObject.getString(key));

            } else if (key.equals("user_type")) {
                mendeleyProfile.setUserType(profileObject.getString(key));

            } else if (key.equals("url")) {
                mendeleyProfile.setUrl(profileObject.getString(key));

            } else if (key.equals("email")) {
                mendeleyProfile.setEmail(profileObject.getString(key));

            } else if (key.equals("link")) {
                mendeleyProfile.setLink(profileObject.getString(key));

            } else if (key.equals("first_name")) {
                mendeleyProfile.setFirstName(profileObject.getString(key));

            } else if (key.equals("last_name")) {
                mendeleyProfile.setLastName(profileObject.getString(key));

            } else if (key.equals("research_interests")) {
                mendeleyProfile.setResearchInterests(profileObject.getString(key));

            } else if (key.equals("academic_status")) {
                mendeleyProfile.setAcademicStatus(profileObject.getString(key));

            } else if (key.equals("verified")) {
                mendeleyProfile.setVerified(profileObject.getBoolean(key));

            } else if (key.equals("created_at")) {
                mendeleyProfile.setCreatedAt(profileObject.getString(key));

            } else if (key.equals("discipline")) {
                JSONObject disciplineObject = profileObject.getJSONObject(key);
                Discipline discipline = new Discipline();
                if (disciplineObject.has("name")) {
                    discipline.name = disciplineObject.getString("name");
                }
                mendeleyProfile.setDiscipline(discipline);

            } else if (key.equals("photo")) {
                JSONObject photoObject = profileObject.getJSONObject(key);
                Photo photo = null;
                if (photoObject.has("square")) {
                    photo = new Photo(photoObject.getString("square"));
                }

                mendeleyProfile.setPhoto(photo);

            } else if (key.equals("education")) {
                JSONArray educationArray = profileObject.getJSONArray(key);
                ArrayList<Education> educationList = new ArrayList<Education>();

                for (int i = 0; i < educationArray.length(); i++) {

                    JSONObject educationObject = educationArray.getJSONObject(i);
                    educationList.add(parseEducation(educationObject));

                }

                mendeleyProfile.setEducation(educationList);

            } else if (key.equals("employment")) {
                JSONArray employmentArray = profileObject.getJSONArray(key);
                ArrayList<Employment> employmentList = new ArrayList<Employment>();

                for (int i = 0; i < employmentArray.length(); i++) {
                    JSONObject employmentObject = employmentArray.getJSONObject(i);
                    employmentList.add(parseEmployment(employmentObject));
                }

                mendeleyProfile.setEmployment(employmentList);

            }
		}
		
		return mendeleyProfile.build();
	}

    private static Employment parseEmployment(JSONObject employmentObject) throws JSONException {
        Employment employment = new Employment();

        for (@SuppressWarnings("unchecked") Iterator<String> employmentIter =
                     employmentObject.keys(); employmentIter.hasNext(); ) {

            String employmentKey = employmentIter.next();

            if (employmentKey.equals("id")) {
                employment.id = employmentObject.getInt(employmentKey);

            } else if (employmentKey.equals("last_modified")) {
                employment.lastModified = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("position")) {
                employment.position = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("created")) {
                employment.created = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("institution")) {
                employment.institution = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("start_date")) {
                employment.startDate = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("end_date")) {
                employment.endDate = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("website")) {
                employment.website = employmentObject.getString(employmentKey);

            } else if (employmentKey.equals("is_main_employment")) {
                employment.isMainEmployment = employmentObject.getBoolean(employmentKey);

            } else if (employmentKey.equals("classes")) {
                JSONArray classesArray = employmentObject.getJSONArray(employmentKey);
                for (int j = 0; j < classesArray.length(); j++) {
                    employment.classes.add(classesArray.getString(j));
                }

            }
        }
        return employment;
    }

    private static Education parseEducation(JSONObject educationObject) throws JSONException {
        Education.Builder education = new Education.Builder();

        for (@SuppressWarnings("unchecked") Iterator<String> educationIter =
                     educationObject.keys(); educationIter.hasNext(); ) {

            String educationKey = educationIter.next();
            if (educationKey.equals("id")) {
                education.setId(educationObject.getInt(educationKey));

            } else if (educationKey.equals("last_modified")) {
                education.setLastModified(educationObject.getString(educationKey));

            } else if (educationKey.equals("created")) {
                education.setCreated(educationObject.getString(educationKey));

            } else if (educationKey.equals("degree")) {
                education.setDegree(educationObject.getString(educationKey));

            } else if (educationKey.equals("institution")) {
                education.setInstitution(educationObject.getString(educationKey));

            } else if (educationKey.equals("start_date")) {
                education.setStartDate(educationObject.getString(educationKey));

            } else if (educationKey.equals("end_date")) {
                education.setEndDate(educationObject.getString(educationKey));

            } else if (educationKey.equals("website")) {
                education.setWebsite(educationObject.getString(educationKey));

            }
        }
        return education.build();
    }

    /**
     * Creating a Group object from a json string
     *
     * @param jsonString the json string
     * @return the Group object
     * @throws JSONException
     */
    public static Group parseGroup(String jsonString) throws JSONException {
        JSONObject groupObject = new JSONObject(jsonString);

        Group.Builder mendeleyGroup = new Group.Builder();

        for (@SuppressWarnings("unchecked")
             Iterator<String> keysIter = groupObject.keys(); keysIter.hasNext(); ) {

            String key = keysIter.next();
            if (key.equals("id")) {
                mendeleyGroup.setId(groupObject.getString(key));

            } else if (key.equals("created")) {
                mendeleyGroup.setCreated(groupObject.getString(key));

            } else if (key.equals("owning_profile_id")) {
                mendeleyGroup.setOwningProfileId(groupObject.getString(key));

            } else if (key.equals("link")) {
                mendeleyGroup.setLink(groupObject.getString(key));

            } else if (key.equals("role")) {
                mendeleyGroup.setRole(Group.Role.fromValue(groupObject.getString(key)));

            } else if (key.equals("access_level")) {
                mendeleyGroup.setAccessLevel(Group.AccessLevel.fromValue(groupObject.getString(key)));

            } else if (key.equals("name")) {
                mendeleyGroup.setName(groupObject.getString(key));

            } else if (key.equals("description")) {
                mendeleyGroup.setDescription(groupObject.getString(key));

            } else if (key.equals("tags")) {
                JSONArray tagsJsonArray = groupObject.getJSONArray(key);
                ArrayList<String> tagsArray = new ArrayList<String>();
                for (int i = 0; i < tagsJsonArray.length(); i++) {
                    tagsArray.add(tagsJsonArray.getString(i));
                }
                mendeleyGroup.setTags(tagsArray);

            } else if (key.equals("webpage")) {
                mendeleyGroup.setWebpage(groupObject.getString(key));

            } else if (key.equals("disciplines")) {
                JSONArray disciplinesJsonArray = groupObject.getJSONArray(key);
                ArrayList<String> disciplinesArray = new ArrayList<String>();
                for (int i = 0; i < disciplinesJsonArray.length(); i++) {
                    disciplinesArray.add(disciplinesJsonArray.getString(i));
                }
                mendeleyGroup.setDisciplines(disciplinesArray);

            } else if (key.equals("photo")) {
                JSONObject photoObject = groupObject.getJSONObject(key);
                Photo photo = null;
                if (photoObject.has("square")) {
                    photo = new Photo(photoObject.getString("square"));
                }

                mendeleyGroup.setPhoto(photo);

            }
        }

        return mendeleyGroup.build();
    }

	/**
	 * Creating a Document object from a json string
	 * 
	 * @param jsonString the json string
	 * @return the Document object
	 * @throws JSONException
	 */
    public static Document parseDocument(String jsonString) throws JSONException {
        JSONObject documentObject = new JSONObject(jsonString);

        String title = "";
        if (documentObject.has("title")) {
            title = documentObject.getString("title");
        }

        String type = documentObject.getString("type");
		Document.Builder mendeleyDocument = new Document.Builder();

		for (@SuppressWarnings("unchecked")
             Iterator<String> keysIter = documentObject.keys(); keysIter.hasNext(); ) {

			String key = keysIter.next();
            if (key.equals("title")) {
                mendeleyDocument.setTitle(documentObject.getString(key));

            } else if (key.equals("type")) {
                mendeleyDocument.setType(documentObject.getString(key));

            } else if (key.equals("last_modified")) {
                mendeleyDocument.setLastModified(documentObject.getString(key));

            } else if (key.equals("group_id")) {
                mendeleyDocument.setGroupId(documentObject.getString(key));

            } else if (key.equals("profile_id")) {
                mendeleyDocument.setProfileId(documentObject.getString(key));

            } else if (key.equals("read")) {
                mendeleyDocument.setRead(documentObject.getBoolean(key));

            } else if (key.equals("starred")) {
                mendeleyDocument.setStarred(documentObject.getBoolean(key));

            } else if (key.equals("authored")) {
                mendeleyDocument.setAuthored(documentObject.getBoolean(key));

            } else if (key.equals("confirmed")) {
                mendeleyDocument.setConfirmed(documentObject.getBoolean(key));

            } else if (key.equals("hidden")) {
                mendeleyDocument.setHidden(documentObject.getBoolean(key));

            } else if (key.equals("id")) {
                mendeleyDocument.setId(documentObject.getString(key));

            } else if (key.equals("month")) {
                mendeleyDocument.setMonth(documentObject.getInt(key));

            } else if (key.equals("year")) {
                mendeleyDocument.setYear(documentObject.getInt(key));

            } else if (key.equals("day")) {
                mendeleyDocument.setDay(documentObject.getInt(key));

            } else if (key.equals("source")) {
                mendeleyDocument.setSource(documentObject.getString(key));

            } else if (key.equals("revision")) {
                mendeleyDocument.setRevision(documentObject.getString(key));

            } else if (key.equals("created")) {
                mendeleyDocument.setCreated(documentObject.getString(key));

            } else if (key.equals("abstract")) {
                mendeleyDocument.setAbstractString(documentObject.getString(key));

            } else if (key.equals("pages")) {
                mendeleyDocument.setPages(documentObject.getString(key));

            } else if (key.equals("volume")) {
                mendeleyDocument.setVolume(documentObject.getString(key));

            } else if (key.equals("issue")) {
                mendeleyDocument.setIssue(documentObject.getString(key));

            } else if (key.equals("publisher")) {
                mendeleyDocument.setPublisher(documentObject.getString(key));

            } else if (key.equals("city")) {
                mendeleyDocument.setCity(documentObject.getString(key));

            } else if (key.equals("edition")) {
                mendeleyDocument.setEdition(documentObject.getString(key));

            } else if (key.equals("institution")) {
                mendeleyDocument.setInstitution(documentObject.getString(key));

            } else if (key.equals("series")) {
                mendeleyDocument.setSeries(documentObject.getString(key));

            } else if (key.equals("chapter")) {
                mendeleyDocument.setChapter(documentObject.getString(key));

            } else if (key.equals("client_data")) {
                mendeleyDocument.setClientData(documentObject.getString(key));

            } else if (key.equals("unique_id")) {
                mendeleyDocument.setUniqueId(documentObject.getString(key));

            } else if (key.equals("authors")) {
                final JSONArray authors = documentObject.getJSONArray(key);
                final ArrayList<Person> authorsList = new ArrayList<Person>();

                for (int i = 0; i < authors.length(); i++) {
                    final Person author = new Person(
                            authors.getJSONObject(i).optString("first_name"),
                            authors.getJSONObject(i).getString("last_name"));
                    authorsList.add(author);
                }

                mendeleyDocument.setAuthors(authorsList);

            } else if (key.equals("editors")) {
                JSONArray editors = documentObject.getJSONArray(key);
                ArrayList<Person> editorsList = new ArrayList<Person>();

                for (int i = 0; i < editors.length(); i++) {
                    Person editor = new Person(
                            editors.getJSONObject(i).optString("first_name"),
                            editors.getJSONObject(i).getString("last_name"));
                    editorsList.add(editor);
                }

                mendeleyDocument.setEditors(editorsList);

            } else if (key.equals("identifiers")) {
                JSONObject identifiersObject = documentObject.getJSONObject(key);
                HashMap<String, String> identifiersMap = new HashMap<String, String>();

                for (@SuppressWarnings("unchecked") Iterator<String> identifierIter =
                             identifiersObject.keys(); identifierIter.hasNext(); ) {

                    String identifierKey = identifierIter.next();
                    identifiersMap.put(identifierKey, identifiersObject.getString(identifierKey));
                }

                mendeleyDocument.setIdentifiers(identifiersMap);

            } else if (key.equals("tags")) {
                JSONArray tags = documentObject.getJSONArray(key);
                ArrayList<String> tagsList = new ArrayList<String>();

                for (int i = 0; i < tags.length(); i++) {
                    tagsList.add(tags.getString(i));
                }

                mendeleyDocument.setTags(tagsList);

            } else if (key.equals("accessed")) {
                mendeleyDocument.setAccessed(documentObject.getString(key));

            } else if (key.equals("file_attached")) {
                mendeleyDocument.setFileAttached(documentObject.getBoolean(key));

            } else if (key.equals("keywords")) {
                JSONArray keywords = documentObject.getJSONArray(key);
                ArrayList<String> keywordsList = new ArrayList<String>();

                for (int i = 0; i < keywords.length(); i++) {
                    keywordsList.add(keywords.getString(i));
                }

                mendeleyDocument.setKeywords(keywordsList);

            } else if (key.equals("websites")) {
                JSONArray websites = documentObject.getJSONArray(key);
                ArrayList<String> websitesList = new ArrayList<String>();

                for (int i = 0; i < websites.length(); i++) {
                    websitesList.add(websites.getString(i));
                }
                mendeleyDocument.setWebsites(websitesList);

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
    public static List<File> parseFileList(String jsonString) throws JSONException {
		
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
    public static List<Document> parseDocumentList(String jsonString) throws JSONException {

        List<Document> documents = new ArrayList<Document>();

        JSONArray jsonarray = new JSONArray(jsonString);

        for (int i = 0; i < jsonarray.length(); i++) {
            documents.add(parseDocument(jsonarray.getString(i)));
        }

        return documents;
    }

    public static List<Annotation> parseAnnotationList(String jsonString) throws JSONException {
        List<Annotation> annotations = new ArrayList<Annotation>();
        JSONArray jsonarray = new JSONArray(jsonString);

        for (int i = 0; i < jsonarray.length(); i++) {
            annotations.add(parseAnnotation(jsonarray.getString(i)));
        }
        return annotations;
    }

    /**
     *  Creating a list of UserRole objects from a json string
     *
     * @param jsonString the json string
     * @return the list of UserRole objects
     * @throws JSONException
     */
    public static List<UserRole> parseUserRoleList(String jsonString) throws JSONException {

        List<UserRole> userRoles = new ArrayList<UserRole>();

        JSONArray jsonarray = new JSONArray(jsonString);

        for (int i = 0; i < jsonarray.length(); i++) {
            userRoles.add(parseUserRole(jsonarray.getString(i)));
        }

        return userRoles;
    }

    /**
     *  Creating a list of Group objects from a json string
     *
     * @param jsonString the json string
     * @return the list of Group objects
     * @throws JSONException
     */
    public static List<Group> parseGroupList(String jsonString) throws JSONException {

        List<Group> groups = new ArrayList<Group>();

        JSONArray jsonarray = new JSONArray(jsonString);

        for (int i = 0; i < jsonarray.length(); i++) {
            groups.add(parseGroup(jsonarray.getString(i)));
        }

        return groups;
    }
	
	/**
     * Helper method for getting json string from an InputStream object
     * 
     * @param stream the InputStream object
     * @return the json String object
     * @throws IOException
     */
    public static String getJsonString(InputStream stream) throws IOException {
		
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

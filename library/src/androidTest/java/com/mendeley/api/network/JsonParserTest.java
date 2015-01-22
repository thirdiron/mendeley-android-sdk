package com.mendeley.api.network;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;


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
import com.mendeley.api.model.Profile;
import com.mendeley.api.model.UserRole;
import com.mendeley.integration.TestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JsonParserTest extends InstrumentationTestCase {

	final String documentFile = "test_document.json";
    final String folderFile =  "test_folder.json";
    final String fileFile =  "test_file.json";
    final String profileFile =  "test_profile.json";
    final String documentIdsFile =  "test_document_ids.json";
    final String groupFile =  "test_group.json";
    final String userRoleFile =  "test_user_role.json";

	public Document getTestDocument() {
	    Person author = new Person("test-first_name", "test-last_name");
        Person editor = new Person("test-first_name", "test-last_name");
	    
	    Document.Builder testDocument = new Document.Builder("test-title", "book");
        testDocument.setLastModified("2014-02-28T11:52:30.000Z");
        testDocument.setGroupId("test-group_id");
        testDocument.setProfileId("test-profile_id");
        testDocument.setRead(false);
        testDocument.setStarred(false);
        testDocument.setAuthored(false);
        testDocument.setConfirmed(false);
        testDocument.setHidden(false);
        testDocument.setId("test-id");
        testDocument.setMonth(0);
        testDocument.setYear(2014);
        testDocument.setDay(0);
        testDocument.setSource("test-source");
        testDocument.setRevision("test-revision");
	    testDocument.setCreated("2014-02-20T16:53:25.000Z");
        testDocument.setIdentifiers(null);
        testDocument.setAbstractString("test-abstract");
	    ArrayList<Person> authorsList = new ArrayList<Person>();
	    authorsList.add(author);
	    testDocument.setAuthors(authorsList);
        testDocument.setPages("1-9");
        testDocument.setVolume("1");
        testDocument.setIssue("1");
        testDocument.setPublisher("test-publisher");
        testDocument.setCity("test-city");
        testDocument.setEdition("1");
        testDocument.setInstitution("test-institution");
        testDocument.setSeries("1");
        testDocument.setChapter("1");
        ArrayList<Person>editorsList = new ArrayList<Person>();
        authorsList.add(editor);
        testDocument.setEditors(authorsList);
        testDocument.setAccessed("2014-02-28");
        testDocument.setFileAttached(false);
        testDocument.setFileAttached(false);
        testDocument.setClientData("test-client_data");
        testDocument.setUniqueId("test-unique_id");


        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("test-keyword");
        testDocument.setKeywords(keywords);

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("test-tag");
        testDocument.setTags(tags);

        ArrayList<String> websites = new ArrayList<String>();
        websites.add("test-website1");
        websites.add("test-website2");
        testDocument.setWebsites(websites);


        //"client_data": "test-client_data",
        //"unique_id": "test-unique_id"

	    return testDocument.build();
	}

    public Group getTestGroup() {

        Group.Builder testGroup = new Group.Builder();
        testGroup.setName("test-group-name");
        testGroup.setDescription("test-group-description");
        testGroup.setId("test-group-id");
        testGroup.setCreated("2014-07-29T11:22:55.000Z");
        testGroup.setOwningProfileId("test-group-owing-profile-id");
        testGroup.setAccessLevel(Group.AccessLevel.PUBLIC);
        testGroup.setRole("owner");
        testGroup.setWebpage("test-group-webpage");
        testGroup.setLink("test-group-link");
        Photo testPhoto = new Photo("test-square-photo.png");
        testGroup.setPhoto(testPhoto);
        ArrayList<String> testDisciplines = new ArrayList<String>();
        testDisciplines.add("Computer and Information Science");
        testGroup.setDisciplines(testDisciplines);

        return testGroup.build();
    }

    public UserRole getTestUserRole() {

        UserRole.Builder testUserRole = new UserRole.Builder();
        testUserRole.setProfileId("test-user-role-id");
        testUserRole.setJoined("2014-07-29T11:22:55.000Z");
        testUserRole.setRole("owner");

        return testUserRole.build();
    }

	public Folder getTestFolder() {
		Folder.Builder mendeleyFolder = new Folder.Builder("test-name");
		mendeleyFolder.setId("test-id");
		mendeleyFolder.setAdded("2014-02-20T16:53:25.000Z");
	    
	    return mendeleyFolder.build();
	}
	
	public File getTestFile() {
	    File.Builder testFile = new File.Builder();
	    testFile.setId("test-id");
	    testFile.setDocumentId("test-document_id");
	    testFile.setMimeType("test-mime_type");
	    testFile.setFileName("test-file_name");
	    testFile.setFileHash("test-filehash");
        testFile.setFileSize(1024);
	    
	    return testFile.build();
	}
	
	public Profile getTestProfile() {
		
		Discipline testDiscipline = new Discipline();
		testDiscipline.name = "test-name";
		Photo testPhoto = new Photo("test-square");
		Education.Builder testEducation = new Education.Builder();
		testEducation.setInstitution("test-education_institution");
		testEducation.setStartDate("2002-01-01");
		testEducation.setEndDate("2004-01-01");
		Employment testEmployment = new Employment();
		testEmployment.institution = "test-employment_institution";
		testEmployment.position = "test-position";
		testEmployment.startDate = "2008-06-01";
		testEmployment.isMainEmployment = true;
		
		Profile.Builder testProfile = new Profile.Builder();
		testProfile.setId("test-id");
		testProfile.setFirstName("test-first_name");
		testProfile.setLastName("test-last_name");
		testProfile.setDisplayName("test-display_name");
		testProfile.setEmail("test-email");
		testProfile.setLink("test-link");
		testProfile.setAcademicStatus("test-academic_status");
		testProfile.setVerified(true);
		testProfile.setUserType("test-user_type");
		testProfile.setCreatedAt("2014-04-28T15:37:51.000Z");
		testProfile.setDiscipline(testDiscipline);
		testProfile.setPhoto(testPhoto);
      	ArrayList<Education> educationList = new ArrayList<Education>();
      	educationList.add(testEducation.build());
		testProfile.setEducation(educationList);
		ArrayList<Employment> employmentList = new ArrayList<Employment>();
		employmentList.add(testEmployment);
		testProfile.setEmployment(employmentList);

	    return testProfile.build();
	}

	public String getJsonStringFromAssetsFile(String fileNameName) throws IOException {
	    return TestUtils.getAssetsFileAsString(getInstrumentation().getContext().getAssets(), fileNameName);
	}
	
	@SmallTest
	public void test_parseDocument()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
    	JsonParser parser = new JsonParser();
		Document testDocument = getTestDocument();
		String jsonString = getJsonStringFromAssetsFile(documentFile);
		
		Document parsedDocument = parser.parseDocument(jsonString);

        assertEquals("title", testDocument.title, parsedDocument.title);
        assertEquals("year", testDocument.year, parsedDocument.year);
        assertEquals("type", testDocument.type, parsedDocument.type);
        assertEquals("lastModified", testDocument.lastModified, parsedDocument.lastModified);
        assertEquals("groupId", testDocument.groupId, parsedDocument.groupId);
        assertEquals("profileId", testDocument.profileId, parsedDocument.profileId);
        assertEquals("read", testDocument.read, parsedDocument.read);
        assertEquals("starred", testDocument.starred, parsedDocument.starred);
        assertEquals("authored", testDocument.authored, parsedDocument.authored);
        assertEquals("confirmed", testDocument.confirmed, parsedDocument.confirmed);
        assertEquals("hidden", testDocument.hidden, parsedDocument.hidden);
		assertEquals("id", testDocument.id, parsedDocument.id);
        assertEquals("month", testDocument.month, parsedDocument.month);
        assertEquals("year", testDocument.year, parsedDocument.year);
        assertEquals("day", testDocument.day, parsedDocument.day);
        assertEquals("source", testDocument.source, parsedDocument.source);
        assertEquals("revision", testDocument.revision, parsedDocument.revision);
        assertEquals("created", testDocument.created, parsedDocument.created);
        assertEquals("identifiers", testDocument.identifiers, parsedDocument.identifiers);
        assertEquals("abstract", testDocument.abstractString, parsedDocument.abstractString);
        assertEquals("pages", testDocument.pages, parsedDocument.pages);
        assertEquals("volume", testDocument.volume, parsedDocument.volume);
        assertEquals("issue", testDocument.issue, parsedDocument.issue);
        assertEquals("publisher", testDocument.publisher, parsedDocument.publisher);
        assertEquals("city", testDocument.city, parsedDocument.city);
        assertEquals("edition", testDocument.edition, parsedDocument.edition);
        assertEquals("institution", testDocument.institution, parsedDocument.institution);
        assertEquals("series", testDocument.series, parsedDocument.series);
        assertEquals("chapter", testDocument.chapter, parsedDocument.chapter);
        assertEquals("accessed", testDocument.accessed, parsedDocument.accessed);
        assertEquals("fileAttached", testDocument.fileAttached, parsedDocument.fileAttached);
        assertEquals("keywords", testDocument.keywords.get(0), parsedDocument.keywords.get(0));
        assertEquals("tags", testDocument.tags.get(0), parsedDocument.tags.get(0));

        assertEquals("websites1", testDocument.websites.get(0), parsedDocument.websites.get(0));
        assertEquals("websites2", testDocument.websites.get(1), parsedDocument.websites.get(1));

		assertEquals("author firstName", testDocument.authors.get(0).firstName, parsedDocument.authors.get(0).firstName);
		assertEquals("author lastName", testDocument.authors.get(0).lastName, parsedDocument.authors.get(0).lastName);
        assertEquals("editor firstName", testDocument.editors.get(0).firstName, parsedDocument.editors.get(0).firstName);
        assertEquals("editor lastName", testDocument.editors.get(0).lastName, parsedDocument.editors.get(0).lastName);
	}
	
	@SmallTest
	public void test_parseFolder()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
    	JsonParser parser = new JsonParser();
		Folder testFolder = getTestFolder();
		String jsonString = getJsonStringFromAssetsFile(folderFile);
		
		Folder parsedFolder = parser.parseFolder(jsonString);

		boolean equal = 
				testFolder.id.equals(parsedFolder.id) &&
				testFolder.name.equals(parsedFolder.name) &&
				testFolder.added.equals(parsedFolder.added);

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@SmallTest
	public void test_parseFile()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
    	JsonParser parser = new JsonParser();
		File testFile = getTestFile();
		String jsonString = getJsonStringFromAssetsFile(fileFile);
		
		File parsedFile = parser.parseFile(jsonString);

		boolean equal = 
				testFile.id.equals(parsedFile.id) &&
				testFile.documentId.equals(parsedFile.documentId) &&
				testFile.mimeType.equals(parsedFile.mimeType) &&
				testFile.fileName.equals(parsedFile.fileName) &&
				testFile.fileHash.equals(parsedFile.fileHash) &&
                testFile.fileSize == parsedFile.fileSize;

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@SmallTest
	public void test_parseProfile()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
    	JsonParser parser = new JsonParser();
		Profile testProfile = getTestProfile();
		String jsonString = getJsonStringFromAssetsFile(profileFile);
		
		Profile parsedProfile = parser.parseProfile(jsonString);

		boolean equal = 
				testProfile.id.equals(parsedProfile.id) &&
				testProfile.firstName.equals(parsedProfile.firstName) &&
				testProfile.lastName.equals(parsedProfile.lastName) &&
				testProfile.displayName.equals(parsedProfile.displayName) &&
				testProfile.email.equals(parsedProfile.email) &&
				testProfile.link.equals(parsedProfile.link) &&
				testProfile.academicStatus.equals(parsedProfile.academicStatus) &&
				testProfile.verified.equals(parsedProfile.verified) &&
				testProfile.userType.equals(parsedProfile.userType) &&
				testProfile.createdAt.equals(parsedProfile.createdAt) &&
				testProfile.discipline.name.equals(parsedProfile.discipline.name) &&
				testProfile.photo.photoUrl.equals(parsedProfile.photo.photoUrl) &&
				testProfile.education.get(0).institution.equals(parsedProfile.education.get(0).institution) &&
				testProfile.education.get(0).startDate.equals(parsedProfile.education.get(0).startDate) &&
				testProfile.education.get(0).endDate.equals(parsedProfile.education.get(0).endDate) &&
				testProfile.employment.get(0).institution.equals(parsedProfile.employment.get(0).institution) &&
				testProfile.employment.get(0).position.equals(parsedProfile.employment.get(0).position) &&
				testProfile.employment.get(0).startDate.equals(parsedProfile.employment.get(0).startDate) &&
				testProfile.employment.get(0).isMainEmployment == parsedProfile.employment.get(0).isMainEmployment;
				
		assertTrue("Parsed profile with wrong or missing data", equal);
	}


	@SmallTest
	public void test_jsonFromDocument()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
        JsonParser parser = new JsonParser();
        Document testDocument = getTestDocument();

        String jsonFromDocument = parser.jsonFromDocument(testDocument);
        String jsonStringFromFile = getJsonStringFromAssetsFile(documentFile);
        JSONObject jDocument = new JSONObject(jsonFromDocument);
        JSONObject fDocument = new JSONObject(jsonStringFromFile);

        assertEquals("title", jDocument.getString("title"), fDocument.getString("title"));
        assertEquals("year", jDocument.getInt("year"), fDocument.getInt("year"));
        assertEquals("type", jDocument.getString("type"), fDocument.getString("type"));
        assertEquals("last_modified", jDocument.getString("last_modified"), fDocument.getString("last_modified"));
        assertEquals("group_id", jDocument.getString("group_id"), fDocument.getString("group_id"));
        assertEquals("profile_id", jDocument.getString("profile_id"), fDocument.getString("profile_id"));
        assertEquals("read", jDocument.getBoolean("read"), fDocument.getBoolean("read"));
        assertEquals("starred", jDocument.getBoolean("starred"), fDocument.getBoolean("starred"));
        assertEquals("authored", jDocument.getBoolean("authored"), fDocument.getBoolean("authored"));
        assertEquals("confirmed", jDocument.getBoolean("confirmed"), fDocument.getBoolean("confirmed"));
        assertEquals("hidden", jDocument.getBoolean("hidden"), fDocument.getBoolean("hidden"));
        assertEquals("id", jDocument.getString("id"), fDocument.getString("id"));
        assertEquals("month", jDocument.getInt("month"), fDocument.getInt("month"));
        assertEquals("year", jDocument.getInt("year"), fDocument.getInt("year"));
        assertEquals("day", jDocument.getInt("day"), fDocument.getInt("day"));
        assertEquals("source", jDocument.getString("source"), fDocument.getString("source"));
        assertEquals("revision", jDocument.getString("revision"), fDocument.getString("revision"));
        assertEquals("created", jDocument.getString("created"), fDocument.getString("created"));
        assertEquals("abstract", jDocument.getString("abstract"), fDocument.getString("abstract"));
        assertEquals("pages", jDocument.getString("pages"), fDocument.getString("pages"));
        assertEquals("volume", jDocument.getString("volume"), fDocument.getString("volume"));
        assertEquals("issue", jDocument.getString("issue"), fDocument.getString("issue"));
        assertEquals("publisher", jDocument.getString("publisher"), fDocument.getString("publisher"));
        assertEquals("city", jDocument.getString("city"), fDocument.getString("city"));
        assertEquals("edition", jDocument.getString("edition"), fDocument.getString("edition"));
        assertEquals("institution", jDocument.getString("institution"), fDocument.getString("institution"));
        assertEquals("series", jDocument.getString("series"), fDocument.getString("series"));
        assertEquals("chapter", jDocument.getString("chapter"), fDocument.getString("chapter"));
        assertEquals("accessed", jDocument.getString("accessed"), fDocument.getString("accessed"));
        assertEquals("client_data", jDocument.getString("client_data"), fDocument.getString("client_data"));
        assertEquals("unique_id", jDocument.getString("unique_id"), fDocument.getString("unique_id"));
        assertEquals("file_attached", jDocument.getBoolean("file_attached"), fDocument.getBoolean("file_attached"));

        JSONArray jKeywords = jDocument.getJSONArray("keywords");
        JSONArray fKeywords = fDocument.getJSONArray("keywords");
        assertEquals("keywords", jKeywords.get(0), fKeywords.get(0));

        JSONArray jTags = jDocument.getJSONArray("tags");
        JSONArray fTags = fDocument.getJSONArray("tags");
        assertEquals("tags", jTags.get(0), fTags.get(0));

        JSONArray jWebsites = jDocument.getJSONArray("websites");
        JSONArray fWebsites = fDocument.getJSONArray("websites");
        assertEquals("websites 1", jWebsites.get(0), fWebsites.get(0));
        assertEquals("websites 2", jWebsites.get(1), fWebsites.get(1));

        JSONArray jAuthors = jDocument.getJSONArray("authors");
        JSONArray fAuthors = jDocument.getJSONArray("authors");
        assertEquals("authors", jAuthors.get(0), fAuthors.get(0));

        JSONArray jEditors = jDocument.getJSONArray("authors");
        JSONArray fEditors = jDocument.getJSONArray("authors");
        assertEquals("editors", jEditors.get(0), fEditors.get(0));
    }

	@SmallTest
	public void test_jsonFromFolder()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
    	JsonParser parser = new JsonParser();
		Folder testFolder = getTestFolder();
		
		String jsonFromFolder = parser.jsonFromFolder(testFolder);
		String jsonStringFromFile = getJsonStringFromAssetsFile(folderFile);
		
		boolean equal = jsonFromFolder.equals(jsonStringFromFile);

		assertTrue("Json string from folder with wrong or missing data", equal);
	}
	
	@SmallTest
	public void test_jsonFromDocumentId()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
    	JsonParser parser = new JsonParser();
    	String documentId = "test-document_id";
    	String expectedString = "{\"id\":\"test-document_id\"}";
		
		String jsonString = parser.jsonFromDocumentId(documentId);

		assertEquals(expectedString, jsonString);
	}
	
	@SmallTest
	public void test_parseDocumentIds()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
    	JsonParser parser = new JsonParser();
		String jsonString = getJsonStringFromAssetsFile(documentIdsFile);
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("test-document_id_1");
		expectedList.add("test-document_id_2");
		expectedList.add("test-document_id_3");
		
		List<DocumentId> list = parser.parseDocumentIds(jsonString);

		boolean equal = true;
		
		for (int i = 0; i < list.size(); i++) {
			equal = list.get(i).id.equals(expectedList.get(i));
			if (!equal) {
				break;
			}
		}
		
		assertTrue("Json document ids list with wrong or missing data", equal);
	}

    @SmallTest
    public void test_parseGroup()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
        JsonParser parser = new JsonParser();
        Group testGroup = getTestGroup();
        String jsonString = getJsonStringFromAssetsFile(groupFile);

        Group parsedGroup = parser.parseGroup(jsonString);

        assertEquals("id", testGroup.id, parsedGroup.id);
        assertEquals("name", testGroup.name, parsedGroup.name);
        assertEquals("description", testGroup.description, parsedGroup.description);
        assertEquals("created", testGroup.created, parsedGroup.created);
        assertEquals("owing_profile_id", testGroup.owningProfileId, parsedGroup.owningProfileId);
        assertEquals("access_level", testGroup.accessLevel, parsedGroup.accessLevel);
        assertEquals("role", testGroup.role, parsedGroup.role);
        assertEquals("webpage", testGroup.webpage, parsedGroup.webpage);
        assertEquals("link", testGroup.link, parsedGroup.link);
        assertEquals("photo_square", testGroup.photo.photoUrl, parsedGroup.photo.photoUrl);
        assertEquals("disciplines", testGroup.disciplines.get(0), parsedGroup.disciplines.get(0));
    }

    @SmallTest
    public void test_parseUserRole()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
        JsonParser parser = new JsonParser();
        UserRole userRole = getTestUserRole();
        String jsonString = getJsonStringFromAssetsFile(userRoleFile);

        UserRole parsedUserRole = parser.parseUserRole(jsonString);

        assertEquals("profile_id", userRole.profileId, parsedUserRole.profileId);
        assertEquals("joined", userRole.joined, parsedUserRole.joined);
        assertEquals("role", userRole.role, parsedUserRole.role);
    }
	
}

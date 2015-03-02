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

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParserTest extends InstrumentationTestCase {

	final String documentWithNotNullCollectionsFile = "test_document_not_null_collections.json";
    final String documentWithNullCollectionsFile = "test_document_null_collections.json";
    final String folderFile =  "test_folder.json";
    final String fileFile =  "test_file.json";
    final String profileFile =  "test_profile.json";
    final String documentIdsFile =  "test_document_ids.json";
    final String groupFile =  "test_group.json";
    final String userRoleFile =  "test_user_role.json";

	public Document getTestDocumentWithNonNotNullCollections() {
        HashMap<String,String> identifiers = new HashMap<String, String>();

        Person author = new Person("test-first_name", "test-last_name");
        ArrayList<Person> authorsList = new ArrayList<Person>();
        authorsList.add(author);

        Person editor = new Person("test-first_name", "test-last_name");
        ArrayList<Person>editorsList = new ArrayList<Person>();
        editorsList.add(editor);

        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("test-keyword");

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("test-tag");

        ArrayList<String> websites = new ArrayList<String>();
        websites.add("test-website1");
        websites.add("test-website2");

	    return getTestDocument(authorsList, editorsList, keywords, tags, websites, identifiers);
	}

    public Document getTestDocument(ArrayList<Person> authorsList, ArrayList<Person> editorsList, ArrayList<String> keywords, ArrayList<String> tags, ArrayList<String> websites, HashMap<String, String> identifiers) {
        Document.Builder testDocument = new Document.Builder();

        testDocument.setTitle("test-title");
        testDocument.setType("book");
        testDocument.setAuthors(authorsList);
        testDocument.setEditors(editorsList);
        testDocument.setKeywords(keywords);
        testDocument.setTags(tags);
        testDocument.setWebsites(websites);
        testDocument.setIdentifiers(identifiers);

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
        testDocument.setAbstractString("test-abstract");
        testDocument.setPages("1-9");
        testDocument.setVolume("1");
        testDocument.setIssue("1");
        testDocument.setPublisher("test-publisher");
        testDocument.setCity("test-city");
        testDocument.setEdition("1");
        testDocument.setInstitution("test-institution");
        testDocument.setSeries("1");
        testDocument.setChapter("1");
        testDocument.setAccessed("2014-02-28");
        testDocument.setFileAttached(false);
        testDocument.setFileAttached(false);
        testDocument.setClientData("test-client_data");
        testDocument.setUniqueId("test-unique_id");

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
        testGroup.setRole(Group.Role.OWNER);
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
    public void test_parseDocument_withNotNullCollections()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

        // GIVEN the JSON representation of a document where its collections (authors, editors...) are NOT null
        Document expectedDocument = getTestDocumentWithNonNotNullCollections();
        String parsingString = getJsonStringFromAssetsFile(documentWithNotNullCollectionsFile);

        // WHEN we parse the JSON
        Document actualDocument = JsonParser.parseDocument(parsingString);

        // THEN the parsed document matches the expected one
        assertDocumentsAreEqual(expectedDocument, actualDocument);

        // ...AND the collections are NOT null
        assertFalse(actualDocument.authors.isNull());
        assertFalse(actualDocument.editors.isNull());
        assertFalse(actualDocument.websites.isNull());
        assertFalse(actualDocument.tags.isNull());
        assertFalse(actualDocument.keywords.isNull());
        assertFalse(actualDocument.identifiers.isNull());
    }



    @SmallTest
    public void test_parseDocument_withNullCollections()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

        // GIVEN the JSON representation of a document where its collections (authors, editors...) ARE null
        Document expectedDocument = getTestDocument(null, null, null, null, null, null);
        String parsingString = getJsonStringFromAssetsFile(documentWithNullCollectionsFile);

        // WHEN we parse the JSON
        Document actualDocument = JsonParser.parseDocument(parsingString);

        // THEN the parsed document matches the expected one
        assertDocumentsAreEqual(expectedDocument, actualDocument);

        // ...AND the collections are null
        assertTrue(actualDocument.authors.isEmpty());
        assertTrue(actualDocument.authors.isNull());

        assertTrue(actualDocument.editors.isEmpty());
        assertTrue(actualDocument.editors.isNull());

        assertTrue(actualDocument.websites.isEmpty());
        assertTrue(actualDocument.websites.isNull());

        assertTrue(actualDocument.tags.isEmpty());
        assertTrue(actualDocument.tags.isNull());

        assertTrue(actualDocument.keywords.isEmpty());
        assertTrue(actualDocument.keywords.isNull());

        assertTrue(actualDocument.identifiers.isEmpty());
        assertTrue(actualDocument.identifiers.isNull());

    }

	@SmallTest
	public void test_parseFolder()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		Folder expectedFolder = getTestFolder();
		String parsingString = getJsonStringFromAssetsFile(folderFile);

		Folder actualFolder = JsonParser.parseFolder(parsingString);

		boolean equal =
				expectedFolder.id.equals(actualFolder.id) &&
                        expectedFolder.name.equals(actualFolder.name) &&
                        expectedFolder.added.equals(actualFolder.added);

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@SmallTest
	public void test_parseFile()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		File expectedFile = getTestFile();
		String parsingString = getJsonStringFromAssetsFile(fileFile);
		
		File actualFile = JsonParser.parseFile(parsingString);

		boolean equal = 
				expectedFile.id.equals(actualFile.id) &&
				expectedFile.documentId.equals(actualFile.documentId) &&
				expectedFile.mimeType.equals(actualFile.mimeType) &&
				expectedFile.fileName.equals(actualFile.fileName) &&
				expectedFile.fileHash.equals(actualFile.fileHash) &&
                expectedFile.fileSize == actualFile.fileSize;

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@SmallTest
	public void test_parseProfile()
			throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		Profile expectedProfile = getTestProfile();
		String parsingString = getJsonStringFromAssetsFile(profileFile);
		
		Profile actualProfile = JsonParser.parseProfile(parsingString);

		boolean equal = 
				expectedProfile.id.equals(actualProfile.id) &&
				expectedProfile.firstName.equals(actualProfile.firstName) &&
				expectedProfile.lastName.equals(actualProfile.lastName) &&
				expectedProfile.displayName.equals(actualProfile.displayName) &&
				expectedProfile.email.equals(actualProfile.email) &&
				expectedProfile.link.equals(actualProfile.link) &&
				expectedProfile.academicStatus.equals(actualProfile.academicStatus) &&
				expectedProfile.verified.equals(actualProfile.verified) &&
				expectedProfile.userType.equals(actualProfile.userType) &&
				expectedProfile.createdAt.equals(actualProfile.createdAt) &&
				expectedProfile.discipline.name.equals(actualProfile.discipline.name) &&
				expectedProfile.photo.photoUrl.equals(actualProfile.photo.photoUrl) &&
				expectedProfile.education.get(0).institution.equals(actualProfile.education.get(0).institution) &&
				expectedProfile.education.get(0).startDate.equals(actualProfile.education.get(0).startDate) &&
				expectedProfile.education.get(0).endDate.equals(actualProfile.education.get(0).endDate) &&
				expectedProfile.employment.get(0).institution.equals(actualProfile.employment.get(0).institution) &&
				expectedProfile.employment.get(0).position.equals(actualProfile.employment.get(0).position) &&
				expectedProfile.employment.get(0).startDate.equals(actualProfile.employment.get(0).startDate) &&
				expectedProfile.employment.get(0).isMainEmployment == actualProfile.employment.get(0).isMainEmployment;
				
		assertTrue("Parsed profile with wrong or missing data", equal);
	}


	@SmallTest
	public void test_jsonFromDocument_withNotNullCollections()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {

        // GIVEN a document where its collections (authors, editors...) are NOT null
        String expectedJson = getJsonStringFromAssetsFile(documentWithNotNullCollectionsFile);
        Document formattingDocument = getTestDocumentWithNonNotNullCollections();

        // WHEN we format it
        String actualJson = JsonParser.jsonFromDocument(formattingDocument);

        // THEN the obtained JSON matches the expected one
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @SmallTest
    public void test_jsonFromDocument_withNullCollections()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {

        // GIVEN a document where its collections (authors, editors...) ARE null
        String expectedJson = getJsonStringFromAssetsFile(documentWithNullCollectionsFile);
        Document formattingDocument = getTestDocument(null, null, null, null, null, null);

        // WHEN we format it
        String actualJson = JsonParser.jsonFromDocument(formattingDocument);

        // THEN the obtained JSON matches the expected one
        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // ...AND the collections are empty, non-null objects
        JSONObject jsonObject = new JSONObject(actualJson);
        assertFalse(jsonObject.has("authors"));

        assertFalse(jsonObject.has("editors"));

        assertFalse(jsonObject.has("websites"));

        assertFalse(jsonObject.has("identifiers"));

        assertFalse(jsonObject.has("tags"));

        assertFalse(jsonObject.has("keywords"));
    }


	@SmallTest
	public void test_jsonFromFolder()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
		Folder parsingFolder = getTestFolder();
		
		String actualJson = JsonParser.jsonFromFolder(parsingFolder);
		String expectedJson = getJsonStringFromAssetsFile(folderFile);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
	
	@SmallTest
	public void test_jsonFromDocumentId()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {
    	String documentId = "test-document_id";
    	String expectedString = "{\"id\":\"test-document_id\"}";
		
		String actualString = JsonParser.jsonFromDocumentId(documentId);

        JSONAssert.assertEquals(expectedString, actualString, false);
    }
	
	@SmallTest
	public void test_parseDocumentIds()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, JSONException {

        String jsonString = getJsonStringFromAssetsFile(documentIdsFile);
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("test-document_id_1");
		expectedList.add("test-document_id_2");
		expectedList.add("test-document_id_3");
		
		List<DocumentId> actualList = JsonParser.parseDocumentIds(jsonString);


        assertEquals("Wrong list size", expectedList.size(), actualList.size());
		for (int i = 0; i < actualList.size(); i++) {
			assertEquals("Wrong list item ", actualList.get(i).id, (expectedList.get(i)));
		}
	}

    @SmallTest
    public void test_parseGroup()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

        Group expectedGroup = getTestGroup();
        String parsingString = getJsonStringFromAssetsFile(groupFile);

        Group actualGroup = JsonParser.parseGroup(parsingString);

        assertEquals("id", expectedGroup.id, actualGroup.id);
        assertEquals("name", expectedGroup.name, actualGroup.name);
        assertEquals("description", expectedGroup.description, actualGroup.description);
        assertEquals("created", expectedGroup.created, actualGroup.created);
        assertEquals("owing_profile_id", expectedGroup.owningProfileId, actualGroup.owningProfileId);
        assertEquals("access_level", expectedGroup.accessLevel, actualGroup.accessLevel);
        assertEquals("role", expectedGroup.role, actualGroup.role);
        assertEquals("webpage", expectedGroup.webpage, actualGroup.webpage);
        assertEquals("link", expectedGroup.link, actualGroup.link);
        assertEquals("photo_square", expectedGroup.photo.photoUrl, actualGroup.photo.photoUrl);
        assertEquals("disciplines", expectedGroup.disciplines.get(0), actualGroup.disciplines.get(0));
    }

    @SmallTest
    public void test_parseUserRole()
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
        UserRole expectedUserRole = getTestUserRole();
        String parsingJsonString = getJsonStringFromAssetsFile(userRoleFile);

        UserRole actualUserRole = JsonParser.parseUserRole(parsingJsonString);

        assertEquals("profile_id", expectedUserRole.profileId, actualUserRole.profileId);
        assertEquals("joined", expectedUserRole.joined, actualUserRole.joined);
        assertEquals("role", expectedUserRole.role, actualUserRole.role);
    }


    private void assertDocumentsAreEqual(Document doc1, Document doc2)
            throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

        assertEquals("title", doc1.title, doc2.title);
        assertEquals("year", doc1.year, doc2.year);
        assertEquals("type", doc1.type, doc2.type);
        assertEquals("lastModified", doc1.lastModified, doc2.lastModified);
        assertEquals("groupId", doc1.groupId, doc2.groupId);
        assertEquals("profileId", doc1.profileId, doc2.profileId);
        assertEquals("read", doc1.read, doc2.read);
        assertEquals("starred", doc1.starred, doc2.starred);
        assertEquals("authored", doc1.authored, doc2.authored);
        assertEquals("confirmed", doc1.confirmed, doc2.confirmed);
        assertEquals("hidden", doc1.hidden, doc2.hidden);
        assertEquals("id", doc1.id, doc2.id);
        assertEquals("month", doc1.month, doc2.month);
        assertEquals("year", doc1.year, doc2.year);
        assertEquals("day", doc1.day, doc2.day);
        assertEquals("source", doc1.source, doc2.source);
        assertEquals("revision", doc1.revision, doc2.revision);
        assertEquals("created", doc1.created, doc2.created);

        assertEquals("abstract", doc1.abstractString, doc2.abstractString);
        assertEquals("pages", doc1.pages, doc2.pages);
        assertEquals("volume", doc1.volume, doc2.volume);
        assertEquals("issue", doc1.issue, doc2.issue);
        assertEquals("publisher", doc1.publisher, doc2.publisher);
        assertEquals("city", doc1.city, doc2.city);
        assertEquals("edition", doc1.edition, doc2.edition);
        assertEquals("institution", doc1.institution, doc2.institution);
        assertEquals("series", doc1.series, doc2.series);
        assertEquals("chapter", doc1.chapter, doc2.chapter);
        assertEquals("accessed", doc1.accessed, doc2.accessed);
        assertEquals("fileAttached", doc1.fileAttached, doc2.fileAttached);

        assertEquals("identifiers size", doc1.identifiers.size(), doc2.identifiers.size());
        for (String key : doc1.identifiers.keySet()) {
            assertEquals("identifier " + key, doc1.identifiers.get(key), doc1.identifiers.get(key));
        }

        assertEquals("keywords size", doc1.keywords.size(), doc2.keywords.size());
        for (int i = 0; i < doc1.identifiers.size(); i++) {
            assertEquals("keyword " + i, doc1.keywords.get(i), doc2.keywords.get(i));
        }

        assertEquals("tags size", doc1.tags.size(), doc2.tags.size());
        for (int i = 0; i < doc1.tags.size(); i++) {
            assertEquals("tag " + i, doc1.tags.get(i), doc2.tags.get(i));
        }

        assertEquals("websites size", doc1.websites.size(), doc2.websites.size());
        for (int i = 0; i < doc1.websites.size(); i++) {
            assertEquals("website " + i, doc1.websites.get(i), doc2.websites.get(i));
        }

        assertEquals("author size", doc1.authors.size(), doc2.authors.size());
        for (int i = 0; i < doc1.authors.size(); i++) {
            assertEquals("author " + i, doc1.authors.get(i), doc2.authors.get(i));
        }

        assertEquals("editors size", doc1.editors.size(), doc2.editors.size());
        for (int i = 0; i < doc1.editors.size(); i++) {
            assertEquals("editor " + i, doc1.editors.get(i), doc2.editors.get(i));
        }
    }

}

package com.mendeley.api.network.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import com.mendeley.api.model.Discipline;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Education;
import com.mendeley.api.model.Employment;
import com.mendeley.api.model.File;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Person;
import com.mendeley.api.model.Photo;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.JasonParser;

public class JsonParserTest extends TestCase {

	String documentFile = "test_document.json";
	String folderFile = "test_folder.json";
	String fileFile = "test_file.json";
	String profileFile = "test_profile.json";
	String documentIdsFile = "test_document_ids.json";

	public Document getTestDocument() {
	    Person author = new Person("test-forename", "test-surname");
		Document testDocument = new Document();
	    testDocument.id = "test-id";
	    testDocument.title = "test-title";
	    testDocument.year = 2014;
	    testDocument.added = "2014-02-20T16:53:25.000Z";
	    testDocument.profileId = "test-profile_id";
	    testDocument.lastModified = "2014-02-28T11:52:30.000Z";
	    testDocument.authors.add(author);

	    return testDocument;
	}

	public Folder getTestFolder() {
	    Folder testFolder = new Folder("test-name");
	    testFolder.id = "test-id";
	    testFolder.added = "2014-02-20T16:53:25.000Z";
	    
	    return testFolder;
	}
	
	public File getTestFile() {
	    File testFile = new File();
	    testFile.id = "test-id";
	    testFile.documentId = "test-document_id";
	    testFile.mimeType = "test-mime_type";
	    testFile.fileName = "test-file_name";
	    testFile.fileHash = "test-filehash";
	    
	    return testFile;
	}
	
	public Profile getTestProfile() {
		
		Discipline testDiscipline = new Discipline();
		testDiscipline.name = "test-name";
		Photo testPhoto = new Photo();
		testPhoto.standard = "test-standard";
		testPhoto.square = "test-square";
		Education testEducation = new Education();
		testEducation.institution = "test-education_institution";
		testEducation.startDate = "2002-01-01";
		testEducation.endDate = "2004-01-01";
		Employment testEmployment = new Employment();
		testEmployment.institution = "test-employment_institution";
		testEmployment.position = "test-position";
		testEmployment.startDate = "2008-06-01";
		testEmployment.isMainEmployment = true;
		
		Profile testProfile = new Profile();
		testProfile.id = "test-id";
		testProfile.firstName = "test-first_name";
		testProfile.lastName = "test-last_name";
		testProfile.displayName = "test-display_name";
		testProfile.email = "test-email";
		testProfile.link = "test-link";
		testProfile.academicStatus = "test-academic_status";
		testProfile.verified = true;
		testProfile.userType = "test-user_type";
		testProfile.createdAt = "2014-04-28T15:37:51.000Z";
		testProfile.discipline = testDiscipline;
		testProfile.photo = testPhoto;
		testProfile.education.add(testEducation);
		testProfile.employment.add(testEmployment);

	    return testProfile;
	}

	public String getJsonStringFromFile(String fileName) throws IOException {
		
		StringBuilder jsonString = new StringBuilder();
	    InputStream is = getClass().getResourceAsStream(fileName);
	    
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
	    String str;

	    while ((str=in.readLine()) != null) {
	    	jsonString.append(str);
	    }

	    in.close();
	    
	    return jsonString.toString();
	}
	
	@Test
	public void test_parseDocument() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	JasonParser parser = new JasonParser();
		Document testDocument = getTestDocument();
		String jsonString = getJsonStringFromFile(documentFile);
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(jsonString);
		String methodName = "parseDocument";
		Document parsedDocument = (Document) parser.getMethodToTest(methodName, values);	

		boolean equal = 
				testDocument.id.equals(parsedDocument.id) &&
				testDocument.title.equals(parsedDocument.title) &&
				testDocument.year.equals(parsedDocument.year) &&
				testDocument.added.equals(parsedDocument.added) &&
				testDocument.profileId.equals(parsedDocument.profileId) &&
				testDocument.lastModified.equals(parsedDocument.lastModified) &&
				testDocument.id.equals(parsedDocument.id) &&
				testDocument.authors.get(0).forename.equals(parsedDocument.authors.get(0).forename) &&
				testDocument.authors.get(0).surname.equals(parsedDocument.authors.get(0).surname);

		assertTrue("Parsed document with wrong or missing data", equal);
	}
	
	@Test
	public void test_parseFolder() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	JasonParser parser = new JasonParser();
		Folder testFolder = getTestFolder();
		String jsonString = getJsonStringFromFile(folderFile);
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(jsonString);
		String methodName = "parseFolder";
		Folder parsedFolder = (Folder) parser.getMethodToTest(methodName, values);	

		boolean equal = 
				testFolder.id.equals(parsedFolder.id) &&
				testFolder.name.equals(parsedFolder.name) &&
				testFolder.added.equals(parsedFolder.added);

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@Test
	public void test_parseFile() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	JasonParser parser = new JasonParser();
		File testFile = getTestFile();
		String jsonString = getJsonStringFromFile(fileFile);
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(jsonString);
		String methodName = "parseFile";
		File parsedFile = (File) parser.getMethodToTest(methodName, values);	

		boolean equal = 
				testFile.id.equals(parsedFile.id) &&
				testFile.documentId.equals(parsedFile.documentId) &&
				testFile.mimeType.equals(parsedFile.mimeType) &&
				testFile.fileName.equals(parsedFile.fileName) &&
				testFile.fileHash.equals(parsedFile.fileHash);

		assertTrue("Parsed folder with wrong or missing data", equal);
	}
	
	@Test
	public void test_parseProfile() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	JasonParser parser = new JasonParser();
		Profile testProfile = getTestProfile();
		String jsonString = getJsonStringFromFile(profileFile);
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(jsonString);
		String methodName = "parseProfile";
		Profile parsedProfile = (Profile) parser.getMethodToTest(methodName, values);	

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
				testProfile.photo.standard.equals(parsedProfile.photo.standard) &&
				testProfile.photo.square.equals(parsedProfile.photo.square) &&
				testProfile.education.get(0).institution.equals(parsedProfile.education.get(0).institution) &&
				testProfile.education.get(0).startDate.equals(parsedProfile.education.get(0).startDate) &&
				testProfile.education.get(0).endDate.equals(parsedProfile.education.get(0).endDate) &&
				testProfile.employment.get(0).institution.equals(parsedProfile.employment.get(0).institution) &&
				testProfile.employment.get(0).position.equals(parsedProfile.employment.get(0).position) &&
				testProfile.employment.get(0).startDate.equals(parsedProfile.employment.get(0).startDate) &&
				testProfile.employment.get(0).isMainEmployment == parsedProfile.employment.get(0).isMainEmployment;
				
		assertTrue("Parsed profile with wrong or missing data", equal);
	}
	
	@Test
	public void test_jsonFromDocument() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
    	JasonParser parser = new JasonParser();
		Document testDocument = getTestDocument();
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(testDocument);
		String methodName = "jsonFromDocument";
		String jsonFromDocument = (String) parser.getMethodToTest(methodName, values);	
		String jsonStringFromFile = getJsonStringFromFile(documentFile);

		boolean equal = jsonFromDocument.equals(jsonStringFromFile);

		assertTrue("Json string from document with wrong or missing data", equal);
	}

	@Test
	public void test_jsonFromFolder() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
    	JasonParser parser = new JasonParser();
		Folder testFolder = getTestFolder();
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(testFolder);
		String methodName = "jsonFromFolder";
		String jsonFromFolder = (String) parser.getMethodToTest(methodName, values);	
		String jsonStringFromFile = getJsonStringFromFile(folderFile);
		
		boolean equal = jsonFromFolder.equals(jsonStringFromFile);

		assertTrue("Json string from folder with wrong or missing data", equal);
	}
	
	@Test
	public void test_jsonFromDocumentId() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
    	JasonParser parser = new JasonParser();
    	String documentId = "test-document_id";
    	String expectedString = "{\"document\":\"test-document_id\"}";
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(documentId);
		String methodName = "jsonFromDocumentId";
		String jsonString = (String) parser.getMethodToTest(methodName, values);	

		boolean equal = jsonString.equals(expectedString);

		assertTrue("Json string from document id with wrong or missing data", equal);
	}
	
	@Test
	public void test_parseDocumentIds() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
    	JasonParser parser = new JasonParser();
		String jsonString = getJsonStringFromFile(documentIdsFile);
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("test-document_id_1");
		expectedList.add("test-document_id_2");
		expectedList.add("test-document_id_3");
		
		ArrayList<Object> values = new ArrayList<Object>();		
		values.add(jsonString);
		String methodName = "parseDocumentIds";
		@SuppressWarnings("unchecked")
		List<String> list = (ArrayList<String>) parser.getMethodToTest(methodName, values);	

		boolean equal = true;
		
		for (int i = 0; i < list.size(); i++) {
			equal = list.get(i).equals(expectedList.get(i));
			if (!equal) {
				break;
			}
		}
		
		assertTrue("Json document ids list with wrong or missing data", equal);
	}
	
	
}

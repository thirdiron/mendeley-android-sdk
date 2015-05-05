package com.mendeley.integration;

import android.test.AndroidTestCase;

import com.mendeley.api.BlockingSdk;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.callbacks.file.FileList;
import com.mendeley.api.callbacks.folder.FolderList;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.File;
import com.mendeley.api.model.Folder;
import com.mendeley.api.params.DocumentRequestParameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public class FileNetworkBlockingTest extends AndroidTestCase {

    private static final Document[] DOCUMENTS = {
            new Document.Builder()
                    .setTitle("Les Chants de Maldoror")
                    .setType("book")
                    .setYear(1868).build()
    };

    private BlockingSdk sdk;

    @Override
    protected void setUp() throws InterruptedException, SignInException {
        sdk = TestUtils.signIn(getContext().getAssets());
    }

    public void testPostFile() throws MendeleyException, IOException {
        ensureCorrectDocumentsExist();

        DocumentRequestParameters params = new DocumentRequestParameters();
        params.limit = 1;
        List<Document> docs = sdk.getDocuments(params).documents;

        assertEquals("expected to get first document", 1, docs.size());

        String fileName ="android.pdf";
        Document doc = docs.get(0);

        InputStream inputStream = getContext().getAssets().open(fileName);

        File receivedFile = sdk.postFile("application/pdf", doc.id, inputStream, fileName);

        assertNotNull("received file meta data is null", receivedFile);
        assertEquals("received file document id", doc.id, receivedFile.documentId);

        sdk.deleteDocument(doc.id);
    }


    public void testDeleteFile() throws MendeleyException, IOException {
        final int expectedReturnCode = 404;
        ensureCorrectDocumentsExist();

        DocumentRequestParameters params = new DocumentRequestParameters();
        params.limit = 1;
        List<Document> docs = sdk.getDocuments(params).documents;

        assertEquals("expected to get first document", 1, docs.size());

        String fileName ="android.pdf";
        Document doc = docs.get(0);

        InputStream inputStream = getContext().getAssets().open(fileName);

        File receivedFile = sdk.postFile("application/pdf", doc.id, inputStream, fileName);

        sdk.deleteFile(receivedFile.id);

        try {
            sdk.deleteFile(receivedFile.id);
            fail("file was not deleted on first call to deleteFile()");
        } catch (HttpResponseException httpResponseException) {
            assertEquals("file should have been deleted already", expectedReturnCode, httpResponseException.httpReturnCode);
        }
    }

    /**
     * Verify we have the correct set of documents to run the tests, and substitute any that
     * are incorrect. Don't post anything if they are already OK.
     *
     * This method has quadratic logic complexity, but the number of documents is small.
     */
    protected final void ensureCorrectDocumentsExist() throws MendeleyException {
        DocumentList docList = sdk.getDocuments();

        // Delete any incorrect documents:
        for (Document doc: docList.documents) {
            if (!isInFixture(doc)) {
                sdk.deleteDocument(doc.id);
            }
        }

        // Post any missing documents:
        for (Document doc : DOCUMENTS) {
            if (!wasReceived(doc, docList)) {
                sdk.postDocument(doc);
            }
        }
    }

    private boolean isInFixture(Document doc) {
        for (Document fixture : DOCUMENTS) {
            if (documentsEqual(doc, fixture)) {
                return true;
            }
        }
        return false;
    }

    private boolean wasReceived(Document doc, DocumentList docList) {
        for (Document rcvd : docList.documents) {
            if (documentsEqual(doc, rcvd)) {
                return true;
            }
        }
        return false;
    }

    private boolean documentsEqual(Document doc1, Document doc2) {
        return doc1.title.equals(doc2.title)
                && doc1.type.equals(doc2.title)
                && doc1.year.equals(doc2.year);
    }
}

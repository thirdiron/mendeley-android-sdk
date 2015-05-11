package com.mendeley.integration;

import android.test.AndroidTestCase;

import com.mendeley.api.BlockingSdk;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.CatalogDocumentRequestParameters;

import java.io.IOException;

public class CatalogBlockingTest extends AndroidTestCase {

    private static final Document[] DOCUMENTS = {
            new Document.Builder()
                    .setTitle("Les Chants de Maldoror")
                    .setType("book")
                    .setYear(1868).build(),
            new Document.Builder()
                    .setTitle("Les Fleurs du mal")
                    .setType("book")
                    .setYear(1857).build(),
            new Document.Builder()
                    .setTitle("Naked Lunch")
                    .setType("book")
                    .setYear(1959).build()
    };

    private BlockingSdk sdk;

    @Override
    protected void setUp() throws InterruptedException, SignInException {
        sdk = TestUtils.signIn(getContext().getAssets());
    }

    public void testGetCatalogDocuments() throws MendeleyException, IOException {
        //GIVEN a file hash
        final String fileHash = "04f76611735043e1984399109e0891c297194bea";
        final String catalogDocumentId = "bce09cae-58fc-388e-9021-f1923b988d44";
        CatalogDocumentRequestParameters catalogueParams = new CatalogDocumentRequestParameters();
        catalogueParams.filehash = fileHash;

        //WHEN getting a catalog document with this file hash
        DocumentList receivedDocs = sdk.getCatalogDocuments(catalogueParams);

        //THEN the correct catalog document received
        Document catalogDocument = receivedDocs.documents.get(0);
        assertEquals("wrong catalog document", catalogDocumentId, catalogDocument.id);
    }

    public void testGetCatalogDocument() throws MendeleyException, IOException {
        //GIVEN a catalog document id
        final String catalogDocumentId = "bce09cae-58fc-388e-9021-f1923b988d44";

        //WHEN getting a catalog document with this id
        Document receivedDoc = sdk.getCatalogDocument(catalogDocumentId, null);

        //THEN the correct catalog document received
        assertEquals("wrong catalog document", catalogDocumentId, receivedDoc.id);
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

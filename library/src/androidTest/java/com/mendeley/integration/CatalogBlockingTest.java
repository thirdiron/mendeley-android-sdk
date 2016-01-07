package com.mendeley.integration;

import android.test.AndroidTestCase;

import com.mendeley.api.BlockingSdk;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.CatalogDocumentRequestParameters;

import java.io.IOException;

/**
 * These tests are using a file hash of an existing document file, MENDELEY: Getting Started with Mendeley
 * If this file will change the file hash will not return the catalog document id and the test will fail.
 */
public class CatalogBlockingTest extends AndroidTestCase {

    private BlockingSdk sdk;

    @Override
    protected void setUp() throws InterruptedException, SignInException {
        sdk = TestUtils.signIn(getContext().getAssets());
    }

    public void testGetCatalogDocuments() throws MendeleyException, IOException {
        //GIVEN a file hash
        final String fileHash = "2064e86683343709cc3ff535587a4580bbb1b251";
        final String catalogDocumentId = "6f4c827d-f303-3647-98fe-e51ea19700ed";
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
        final String catalogDocumentId = "6f4c827d-f303-3647-98fe-e51ea19700ed";

        //WHEN getting a catalog document with this id
        Document receivedDoc = sdk.getCatalogDocument(catalogDocumentId, null);

        //THEN the correct catalog document received
        assertEquals("wrong catalog document", catalogDocumentId, receivedDoc.id);
    }

}

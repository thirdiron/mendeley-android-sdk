package com.mendeley.integration;

import android.test.AndroidTestCase;

import com.mendeley.api.BlockingSdk;
import com.mendeley.api.callbacks.document.DocumentIdList;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.params.Sort;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class DocumentNetworkBlockingTest extends AndroidTestCase {
    private static final Document[] DOCUMENTS = {
            new Document.Builder("How to avoid huge ships", "book")
                    .setYear(1993).build(),
            new Document.Builder("Cheese problems solved", "book")
                    .setYear(2007).build(),
            new Document.Builder("Across Europe by Kangaroo", "book")
                    .setYear(2003).build(),
            new Document.Builder("How to Be Pope: What to Do and Where to Go Once You're in the Vatican", "book")
                    .setYear(2005).build(),
            new Document.Builder("Be Bold with Bananas", "book")
                    .setYear(1972).build()
    };

    private BlockingSdk sdk;

    @Override
    protected void setUp() throws InterruptedException, SignInException {
        sdk = TestUtils.signIn(getContext().getAssets());
    }

    public void testGetDocuments() throws MendeleyException {
        ensureCorrectDocumentsExist();

        DocumentList docList = getSortedDocuments();

        assertEquals("expected five documents", 5, docList.documents.size());
        assertEquals("title incorrect",
                "Across Europe by Kangaroo", docList.documents.get(0).title);
    }

    public void testGetDocumentsInPages() throws MendeleyException {
        DocumentRequestParameters params = new DocumentRequestParameters();
        params.limit = 3;
        params.sort = Sort.TITLE;

        DocumentList docList = sdk.getDocuments(params);

        assertEquals("expected three documents", 3, docList.documents.size());
        assertTrue("page must be valid", Page.isValidPage(docList.next));

        docList = sdk.getDocuments(docList.next);

        assertEquals("expected two documents", 2, docList.documents.size());
        assertTrue("page must be invalid", !Page.isValidPage(docList.next));
    }

    public void testPostAndDeleteDocument() throws MendeleyException {
        Document doc = new Document.Builder("abc", "book").build();

        Document rcvd = sdk.postDocument(doc);

        assertEquals("abc", rcvd.title);

        sdk.deleteDocument(rcvd.id);
    }

    public void testPatchDocument() throws MendeleyException {
        DocumentList docList = getSortedDocuments();

        assertEquals("incorrect year", docList.documents.get(0).year.intValue(), 2003);

        Document doc = new Document.Builder(docList.documents.get(0)).setYear(1066).build();

        patchDocument(doc);
        Document docRcvd = sdk.getDocument(doc.id, null);

        assertEquals("incorrect year", docRcvd.year.intValue(), 1066);

        doc = new Document.Builder(docList.documents.get(0)).setYear(2003).build();

        patchDocument(doc);
        docRcvd = getDocumentById(doc.id);

        assertEquals("incorrect year", docRcvd.year.intValue(), 2003);
    }

    public void testGetDocumentTypes() throws MendeleyException {
        Map<String, String> types = sdk.getDocumentTypes();

        Set<String> keys = types.keySet();
        assertTrue("document types must contain journal", keys.contains("journal"));
        assertTrue("document types must contain book", keys.contains("book"));
    }

    public void testGetDeletedDocuments() throws MendeleyException {
        ensureCorrectDocumentsExist();

        DocumentList docList = sdk.getDocuments();

        DocumentRequestParameters params = new DocumentRequestParameters();
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar date = Calendar.getInstance();
        String deletedSince = dateFormat.format(date.getTime());

        List<DocumentId> expectedDocumentIds = new ArrayList<DocumentId>();

        for (Document document : docList.documents) {
            DocumentId.Builder docBuilder = new DocumentId.Builder();
            docBuilder.setDocumentId(document.id);
            DocumentId documentId = docBuilder.build();

            expectedDocumentIds.add(documentId);
            sdk.deleteDocument(documentId.id);
        }

        DocumentIdList docIdList = sdk.getDeletedDocuments(deletedSince, params);

        assertTrue("deleted document id not received", docIdList.documentIds.size() >= expectedDocumentIds.size() );

        for (DocumentId expectedDocumentId : expectedDocumentIds) {
            assertTrue("deleted document id not received", docIdList.documentIds.contains(expectedDocumentId));
        }
    }


    /**
     * Verify we have the correct set of documents to run the tests, and substitute any that
     * are incorrect. Don't post anything if they are already OK.
     *
     * This method has quadratic logic complexity, but the number of documents is small.
     */
    private void ensureCorrectDocumentsExist() throws MendeleyException {
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

    private DocumentList getSortedDocuments() throws MendeleyException {
        DocumentRequestParameters params = new DocumentRequestParameters();
        params.sort = Sort.TITLE;
        return sdk.getDocuments(params);
    }

    private void patchDocument(Document doc) throws MendeleyException {
        sdk.patchDocument(doc.id, null, doc);
    }

    private Document getDocumentById(String id) throws MendeleyException {
        return sdk.getDocument(id, null);
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

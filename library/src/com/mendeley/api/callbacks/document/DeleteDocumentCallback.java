package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;

public interface DeleteDocumentCallback {
    public void onDocumentDeleted(String documentId);
    public void onDocumentNotDeleted(MendeleyException mendeleyException);
}

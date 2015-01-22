package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;

public interface TrashDocumentCallback {
    public void onDocumentTrashed(String documentId);
    public void onDocumentNotTrashed(MendeleyException mendeleyException);
}

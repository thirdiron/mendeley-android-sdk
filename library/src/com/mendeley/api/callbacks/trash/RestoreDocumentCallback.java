package com.mendeley.api.callbacks.trash;

import com.mendeley.api.exceptions.MendeleyException;

public interface RestoreDocumentCallback {
    public void onDocumentRestored(String documentId);
    public void onDocumentNotRestored(MendeleyException mendeleyException);
}

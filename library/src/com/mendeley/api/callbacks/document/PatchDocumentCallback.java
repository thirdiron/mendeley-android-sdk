package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;

public interface PatchDocumentCallback {
    public void onDocumentPatched(String documentId);
    public void onDocumentNotPatched(MendeleyException mendeleyException);
}

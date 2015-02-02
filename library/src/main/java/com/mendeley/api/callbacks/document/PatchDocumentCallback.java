package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;

public interface PatchDocumentCallback {
    public void onDocumentPatched(Document document);
    public void onDocumentNotPatched(MendeleyException mendeleyException);
}

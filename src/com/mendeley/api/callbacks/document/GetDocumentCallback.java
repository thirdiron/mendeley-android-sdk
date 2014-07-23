package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;

public interface GetDocumentCallback {
    public void onDocumentReceived(Document document);
    public void onDocumentNotReceived(MendeleyException mendeleyException);
}

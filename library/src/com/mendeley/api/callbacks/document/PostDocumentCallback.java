package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;

public interface PostDocumentCallback {
    public void onDocumentPosted(Document document);
    public void onDocumentNotPosted(MendeleyException mendeleyException);
}

package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetDocumentsCallback {
    public void onDocumentsReceived(List<Document> documents, Page next);
    public void onDocumentsNotReceived(MendeleyException mendeleyException);
}

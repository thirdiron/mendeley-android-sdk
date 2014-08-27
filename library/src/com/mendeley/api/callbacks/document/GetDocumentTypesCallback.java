package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;

import java.util.Map;

public interface GetDocumentTypesCallback {
    public void onDocumentTypesReceived(Map<String, String> typesMap);
    public void onDocumentTypesNotReceived(MendeleyException mendeleyException);
}

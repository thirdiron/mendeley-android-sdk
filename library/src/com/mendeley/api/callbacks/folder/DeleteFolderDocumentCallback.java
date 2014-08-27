package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;

public interface DeleteFolderDocumentCallback {
    public void onFolderDocumentDeleted(String documentId);
    public void onFolderDocumentNotDeleted(MendeleyException mendeleyException);
}

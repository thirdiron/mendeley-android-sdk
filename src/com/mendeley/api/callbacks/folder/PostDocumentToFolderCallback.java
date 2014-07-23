package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;

public interface PostDocumentToFolderCallback {
    public void onDocumentPostedToFolder(String folderId);
    public void onDocumentNotPostedToFolder(MendeleyException mendeleyException);
}

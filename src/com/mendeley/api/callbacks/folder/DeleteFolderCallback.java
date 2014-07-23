package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;

public interface DeleteFolderCallback {
    public void onFolderDeleted(String folderId);
    public void onFolderNotDeleted(MendeleyException mendeleyException);
}

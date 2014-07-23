package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;

public interface PatchFolderCallback {
    public void onFolderPatched(String folderId);
    public void onFolderNotPatched(MendeleyException mendeleyException);
}

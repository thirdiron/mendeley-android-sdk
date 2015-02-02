package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;

public interface PatchFolderCallback {
    public void onFolderPatched(Folder folder);
    public void onFolderNotPatched(MendeleyException mendeleyException);
}

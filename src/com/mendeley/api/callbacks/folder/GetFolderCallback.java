package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;

public interface GetFolderCallback {
    public void onFolderReceived(Folder folder);
    public void onFolderNotReceived(MendeleyException mendeleyException);
}

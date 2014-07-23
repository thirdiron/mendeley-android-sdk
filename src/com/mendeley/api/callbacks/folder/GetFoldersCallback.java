package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetFoldersCallback {
    public void onFoldersReceived(List<Folder> folders, Page next);
    public void onFoldersNotReceived(MendeleyException mendeleyException);
}

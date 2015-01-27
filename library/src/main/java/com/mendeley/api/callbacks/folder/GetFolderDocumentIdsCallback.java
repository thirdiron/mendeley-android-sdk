package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetFolderDocumentIdsCallback {
    public void onFolderDocumentIdsReceived(String folderId, List<DocumentId> documentIds, Page next);
    public void onFolderDocumentIdsNotReceived(MendeleyException mendeleyException);
}

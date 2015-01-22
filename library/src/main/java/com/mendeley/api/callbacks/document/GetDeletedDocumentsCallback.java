package com.mendeley.api.callbacks.document;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public interface GetDeletedDocumentsCallback {
    public void onDeletedDocumentsReceived(List<DocumentId> documentIds, Page next, Date serverDate);
    public void onDeletedDocumentsNotReceived(MendeleyException mendeleyException);
}

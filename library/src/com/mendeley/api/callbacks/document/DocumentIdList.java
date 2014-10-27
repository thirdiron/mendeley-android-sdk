package com.mendeley.api.callbacks.document;

import com.mendeley.api.model.Document;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class DocumentIdList {
    public List<DocumentId> documentIds;
    public Page next;
    public Date serverDate;
}

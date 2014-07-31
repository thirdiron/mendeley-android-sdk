package com.mendeley.api.model;

/**
 * Model class representing file json object.
 *
 */
public class DocumentId {

    public final String id;

    private DocumentId(String id) {
        this.id = id;
    }

    public static class Builder {
        private String id;

        public Builder() {}

        public Builder setDocumentId(String id) {
            this.id = id;
            return this;
        }

        public DocumentId build() {
            return new DocumentId(id);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DocumentId) {
            DocumentId other = (DocumentId) object;
            return this.id.equals(other.id);
        }
        return false;
    }
}

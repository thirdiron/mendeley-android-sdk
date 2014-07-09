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
}

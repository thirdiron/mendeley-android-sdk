package com.mendeley.api;

import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.document.DeleteDocumentCallback;
import com.mendeley.api.callbacks.document.DocumentIdList;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.callbacks.document.GetDocumentsCallback;
import com.mendeley.api.callbacks.document.PatchDocumentCallback;
import com.mendeley.api.callbacks.document.PostDocumentCallback;
import com.mendeley.api.callbacks.document.TrashDocumentCallback;
import com.mendeley.api.callbacks.group.GetGroupCallback;
import com.mendeley.api.callbacks.group.GetGroupMembersCallback;
import com.mendeley.api.callbacks.group.GetGroupsCallback;
import com.mendeley.api.callbacks.group.GroupList;
import com.mendeley.api.callbacks.group.GroupMembersList;
import com.mendeley.api.callbacks.trash.RestoreDocumentCallback;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Group;
import com.mendeley.api.model.Profile;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.params.View;

import java.util.Date;
import java.util.Map;

public interface BlockingSdk {
    /* DOCUMENTS */

    /**
     * Retrieve a list of documents in the user's library.
     */
    DocumentList getDocuments(DocumentRequestParameters parameters) throws MendeleyException;

    /**
     * Retrieve a list of documents in the user's library.
     */
    DocumentList getDocuments() throws MendeleyException;

    /**
     * Retrieve subsequent pages of documents in the user's library.
     *
     * @next reference to next page returned in a previous DocumentList.
     */
    DocumentList getDocuments(Page next) throws MendeleyException;

    /**
     * Retrieve a single document, specified by ID.
     *
     * @param documentId the document id to get.
     * @param view extended document view. If null, only core fields are returned.
     */
    Document getDocument(String documentId, View view) throws MendeleyException;

    /**
     * Retrieve a list of deleted documents in the user's library.
     *
     * @param deletedSince only return documents deleted since this timestamp. Should be supplied in ISO 8601 format.
     * @param parameters holds optional query parameters, will be ignored if null
     */
    DocumentIdList getDeletedDocuments(String deletedSince, DocumentRequestParameters parameters) throws MendeleyException;

    /**
     * Retrieve subsequent pages of deleted documents in the user's library.
     *
     * @next reference to next page returned in a previous DocumentIdList.
     */
    DocumentIdList getDeletedDocuments(Page next) throws MendeleyException;

    /**
     * Add a new document to the user's library.
     *
     * @param document the document object to be added.
     */
    Document postDocument(Document document) throws MendeleyException;

    /**
     * Modify an existing document in the user's library.
     *
     * @param documentId the id of the document to be modified.
     * @param date sets an optional "if unmodified since" condition on the request. Ignored if null.
     * @param document a document object containing the fields to be updated.
     *                 Missing fields are left unchanged (not cleared).
     */
    void patchDocument(String documentId, Date date, Document document) throws MendeleyException;

    /**
     * Move an existing document into the user's trash collection.
     *
     * @param documentId id of the document to be trashed.
     */
    void trashDocument(String documentId) throws MendeleyException;

    /**
     * Delete a document.
     *
     * @param documentId id of the document to be deleted.
     */
    void deleteDocument(String documentId) throws MendeleyException;

    /**
     * Return a list of valid document types.
     */
    Map<String, String> getDocumentTypes() throws MendeleyException;

    /* GROUPS */

    /**
     * Return metadata for all the user's groups.
     */
    GroupList getGroups(GroupRequestParameters parameters) throws MendeleyException;

    /**
     * Returns the next page of group metadata entries.
     *
     * @param next returned from a previous getGroups() call.
     */
    GroupList getGroups(Page next) throws MendeleyException;

    /**
     * Returns metadata for a single group, specified by ID.
     *
     * @param groupId ID of the group to retrieve metadata for.
     */
    Group getGroup(String groupId) throws MendeleyException;

    /**
     * Return a list of members user roles of a particular group.
     *
     * @param groupId ID of the group to inspect.
     */
    GroupMembersList getGroupMembers(GroupRequestParameters parameters, String groupId) throws MendeleyException;

    /**
     * Return a list of members user roles of a particular group.
     *
     * @param next returned from a previous getGroupMembers() call.
     */
    GroupMembersList getGroupMembers(Page next) throws MendeleyException;

    /* TRASH */

    /**
     * Retrieve a list of documents in the user's trash.
     */
    DocumentList getTrashedDocuments(DocumentRequestParameters parameters) throws MendeleyException;

    /**
     * Retrieve a list of documents in the user's trash.
     */
    DocumentList getTrashedDocuments() throws MendeleyException;

    /**
     * Retrieve subsequent pages of documents from the user's trash.
     *
     * @next reference to next page returned by a previous DocumentList from getTrashedDocuments().
     */
    DocumentList getTrashedDocuments(Page next) throws MendeleyException;

    /**
     * Move a document from trash into the user's library.
     *
     * @param documentId id of the document to restore.
     */
    void restoreDocument(String documentId) throws MendeleyException;

    /* PROFILES */

    /**
     * Return the user's profile information.
     */
    Profile getMyProfile() throws MendeleyException;

    /**
     * Return profile information for another user.
     *
     * @param  profileId ID of the profile to be fetched.
     */
    Profile getProfile(String profileId) throws MendeleyException;
}

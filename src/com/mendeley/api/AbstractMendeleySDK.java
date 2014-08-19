package com.mendeley.api;

import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.document.DeleteDocumentCallback;
import com.mendeley.api.callbacks.document.GetDeletedDocumentsCallback;
import com.mendeley.api.callbacks.document.GetDocumentCallback;
import com.mendeley.api.callbacks.document.GetDocumentTypesCallback;
import com.mendeley.api.callbacks.document.GetDocumentsCallback;
import com.mendeley.api.callbacks.document.PatchDocumentCallback;
import com.mendeley.api.callbacks.document.PostDocumentCallback;
import com.mendeley.api.callbacks.document.TrashDocumentCallback;
import com.mendeley.api.callbacks.file.DeleteFileCallback;
import com.mendeley.api.callbacks.file.GetFileCallback;
import com.mendeley.api.callbacks.file.GetFilesCallback;
import com.mendeley.api.callbacks.file.PostFileCallback;
import com.mendeley.api.callbacks.folder.DeleteFolderCallback;
import com.mendeley.api.callbacks.folder.DeleteFolderDocumentCallback;
import com.mendeley.api.callbacks.folder.GetFolderCallback;
import com.mendeley.api.callbacks.folder.GetFolderDocumentIdsCallback;
import com.mendeley.api.callbacks.folder.GetFoldersCallback;
import com.mendeley.api.callbacks.folder.PatchFolderCallback;
import com.mendeley.api.callbacks.folder.PostDocumentToFolderCallback;
import com.mendeley.api.callbacks.folder.PostFolderCallback;
import com.mendeley.api.callbacks.group.GetGroupCallback;
import com.mendeley.api.callbacks.group.GetGroupMembersCallback;
import com.mendeley.api.callbacks.group.GetGroupsCallback;
import com.mendeley.api.callbacks.profile.GetProfileCallback;
import com.mendeley.api.callbacks.utils.GetImageCallback;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.params.View;

import java.io.InputStream;
import java.util.Date;

public interface AbstractMendeleySDK {
    RequestHandle getDocuments(DocumentRequestParameters parameters, GetDocumentsCallback callback);

    RequestHandle getDocuments(GetDocumentsCallback callback);

    RequestHandle getDocuments(Page next, GetDocumentsCallback callback);

    void getDocument(String documentId, View view, GetDocumentCallback callback);

    RequestHandle getDeletedDocuments(String deletedSince, DocumentRequestParameters parameters, GetDeletedDocumentsCallback callback);

    RequestHandle getDeletedDocuments(Page next, GetDeletedDocumentsCallback callback);

    void postDocument(Document document, PostDocumentCallback callback);

    void patchDocument(String documentId, Date date, Document document, PatchDocumentCallback callback);

    void trashDocument(String documentId, TrashDocumentCallback callback);

    void deleteDocument(String documentId, DeleteDocumentCallback callback);

    RequestHandle getDocumentTypes(GetDocumentTypesCallback callback);

    RequestHandle getFiles(FileRequestParameters parameters, GetFilesCallback callback);

    RequestHandle getFiles(GetFilesCallback callback);

    RequestHandle getFiles(Page next, GetFilesCallback callback);

    void getFile(String fileId, String documentId, String folderPath, GetFileCallback callback);

    void cancelDownload(String fileId);

    void postFile(String contentType, String documentId, String filePath, PostFileCallback callback);

    void postFile(String contentType, String documentId, InputStream inputStream, String fileName, PostFileCallback callback);

    void deleteFile(String fileId, DeleteFileCallback callback);

    void getMyProfile(GetProfileCallback callback);

    void getProfile(String profileId, GetProfileCallback callback);

    RequestHandle getFolders(FolderRequestParameters parameters, GetFoldersCallback callback);

    RequestHandle getFolders(GetFoldersCallback callback);

    RequestHandle getFolders(Page next, GetFoldersCallback callback);

    void getFolderDocumentIds(Page next, String groupId, GetGroupMembersCallback callback);

    void getFolder(String folderId, GetFolderCallback callback);

    void postFolder(Folder folder, PostFolderCallback callback);

    void patchFolder(String folderId, Folder folder, PatchFolderCallback callback);

    void getFolderDocumentIds(FolderRequestParameters parameters, String folderId, GetFolderDocumentIdsCallback callback);

    void getFolderDocumentIds(Page next, String folderId, GetFolderDocumentIdsCallback callback);

    void postDocumentToFolder(String folderId, String documentId, PostDocumentToFolderCallback callback);

    void deleteFolder(String folderId, DeleteFolderCallback callback);

    void deleteDocumentFromFolder(String folderId, String documentId, DeleteFolderDocumentCallback callback);

    void getImage(String url, GetImageCallback callback);

    RequestHandle getGroups(GroupRequestParameters parameters, GetGroupsCallback callback);

    RequestHandle getGroups(Page next, GetGroupsCallback callback);

    void getGroup(String groupId, GetGroupCallback callback);

    void getGroupMembers(GroupRequestParameters parameters, String groupId, GetGroupMembersCallback callback);

    void clearCredentials();
}

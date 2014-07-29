package com.mendeley.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.mendeley.api.auth.AuthenticationInterface;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.auth.UserCredentials;
import com.mendeley.api.callbacks.MendeleySignInInterface;
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
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.DocumentNetworkProvider;
import com.mendeley.api.network.FileNetworkProvider;
import com.mendeley.api.network.FolderNetworkProvider;
import com.mendeley.api.network.GroupNetworkProvider;
import com.mendeley.api.network.NullRequest;
import com.mendeley.api.network.ProfileNetworkProvider;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Main entry points for making calls to the Mendeley SDK.
 */
public class MendeleySDK {
    public static class ClientCredentials {
        public final String clientId;
        public final String clientSecret;
        public final String redirectUri;

        public ClientCredentials(String clientId, String clientSecret, String redirectUri) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
        }
    }

    private static final String TAG = MendeleySDK.class.getSimpleName();

    private AuthenticationManager authenticationManager;
	private MethodtoInvoke methodToInvoke;

    private DocumentNetworkProvider documentNetworkProvider;
    private FileNetworkProvider fileNetworkProvider;
    private ProfileNetworkProvider profileNetworkProvider;
    private FolderNetworkProvider folderNetworkProvider;
    private GroupNetworkProvider groupNetworkProvider;
	
	private MendeleySignInInterface mendeleySignInInterface;

    /**
     * Obtain a handle to the SDK.
     * <p>
     * It is recommended that applications only call this once, and then use the same handle
     * for all requests.
     *
     * @param context used for creating the sign-in activity
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process
     */
	public MendeleySDK(Context context, ClientCredentials clientCredentials)  {
        initWithWebSignIn(context, clientCredentials);
	}

    /**
     * Obtain a handle to the SDK.
     * <p>
     * It is recommended that applications only call this once, and then use the same handle
     * for all requests.
     *
     * @param context used for creating the sign-in activity
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process
     * @param signInCallback used to receive sign in/out events.
     */
	public MendeleySDK(Context context, MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials) {
        this.mendeleySignInInterface = signInCallback;
        initWithWebSignIn(context, clientCredentials);
    }

    /**
     * Obtain a handle to the SDK (internal use only).
     * <p>
     * Developer applications should not use this constructor; this is only required for unit
     * testing the SDK.
     */
    public MendeleySDK(ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        initWithPasswordSignIn(userCredentials, clientCredentials);
    }

    /**
     * Obtain a handle to the SDK (internal use only).
     * <p>
     * Developer applications should not use this constructor; this is only required for unit
     * testing the SDK.
     */
    public MendeleySDK(MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        this.mendeleySignInInterface = signInCallback;
        initWithPasswordSignIn(userCredentials, clientCredentials);
    }

    /**
     * Return the SDK version name.
     */
    public static String getVersion(Context context) {
        Resources resources = context.getResources();
        return resources.getString(R.string.version_name);
    }

    private void initWithWebSignIn(Context context, ClientCredentials clientCredentials) {
        AuthenticationManager.configure(
                context,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        init();
    }

    private void initWithPasswordSignIn(UserCredentials userCredentials, ClientCredentials clientCredentials) {
        AuthenticationManager.configure(
                userCredentials.username,
                userCredentials.password,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        init();
    }

    private void init() {
        authenticationManager = AuthenticationManager.getInstance();
        initialiseInterfaces();
        if (!hasCredentials()) {
            authenticationManager.authenticate();
        }
    }

    private AuthenticationInterface createAuthenticationInterface() {
        return new AuthenticationInterface() {
            @Override
            public void onAuthenticated() {
                if (mendeleySignInInterface != null) {
                    mendeleySignInInterface.onSignedIn();
                }
                invokeMethod();
            }

            @Override
            public void onAuthenticationFail() {
                authenticationManager.authenticate();
            }
        };
    }

    /* DOCUMENTS */

    /**
     * Retrieve a list of documents in the user's library.
     */
    public RequestHandle getDocuments(DocumentRequestParameters parameters, GetDocumentsCallback callback) {
        if (checkNetworkCall(
                new Class[] { DocumentRequestParameters.class, GetDocumentsCallback.class },
                new Object[] { parameters, callback })) {
            return documentNetworkProvider.doGetDocuments(parameters, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Retrieve a list of documents in the user's library.
     */
    public RequestHandle getDocuments(GetDocumentsCallback callback) {
        return getDocuments((DocumentRequestParameters) null, callback);
    }

    /**
     * Retrieve subsequent pages of documents in the user's library.
     *
     * @next reference to next page returned by a previous onDocumentsReceived() callback.
     */
    public RequestHandle getDocuments(Page next, GetDocumentsCallback callback) {
        if (checkNetworkCall(
                new Class[] { DocumentRequestParameters.class, GetDocumentsCallback.class },
                new Object[] { next, callback })) {
            return documentNetworkProvider.doGetDocuments(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Retrieve a single document, specified by ID.
     *
     * @param documentId the document id to get
     * @param parameters holds optional query parameters, will be ignored if null
     */
    public void getDocument(String documentId, DocumentRequestParameters parameters, GetDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, DocumentRequestParameters.class, GetDocumentCallback.class },
                new Object[] { documentId, parameters, callback })) {
            documentNetworkProvider.doGetDocument(documentId, parameters, callback);
        }
    }

    /**
     * Retrieve a list of deleted documents in the user's library.
     *
     * @param parameters holds optional query parameters, will be ignored if null
     */
    public RequestHandle getDeletedDocuments(DocumentRequestParameters parameters, GetDeletedDocumentsCallback callback) {
        if (checkNetworkCall(
                new Class[] { DocumentRequestParameters.class, GetDeletedDocumentsCallback.class },
                new Object[] { parameters, callback })) {
            return documentNetworkProvider.doGetDeletedDocuments(parameters, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Retrieve subsequent pages of deleted documents in the user's library.
     *
     * @next reference to next page returned by a previous onDeletedDocumentsReceived() callback.
     */
    public RequestHandle getDeletedDocuments(Page next, GetDeletedDocumentsCallback callback) {
        if (checkNetworkCall(
                new Class[] { DocumentRequestParameters.class, GetDeletedDocumentsCallback.class },
                new Object[] { next, callback })) {
            return documentNetworkProvider.doGetDeletedDocuments(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Add a new document to the user's library.
     *
     * @param document the document object to be added.
     */
    public void postDocument(Document document, PostDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { Document.class, PostDocumentCallback.class },
                new Object[] { document, callback })) {
            documentNetworkProvider.doPostDocument(document, callback);
        }
    }


    /**
     * Modify an existing document in the user's library.
     *
     * @param documentId the id of the document to be modified.
     * @param date sets an optional "if unmodified since" condition on the request. Ignored if null.
     * @param document a document object containing the fields to be updated.
     *                 Missing fields are left unchanged (not cleared).
     */
    public void patchDocument(String documentId, Date date, Document document, PatchDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, Date.class, Document.class, PatchDocumentCallback.class },
                new Object[] { documentId, date, document, callback })) {
            documentNetworkProvider.doPatchDocument(documentId, date, document, callback);
        }
    }

    /**
     * Move an existing document into the user's trash collection.
     *
     * @param documentId id of the document to be trashed.
     */
    public void trashDocument(String documentId, TrashDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, TrashDocumentCallback.class },
                new Object[] { documentId, callback })) {
            documentNetworkProvider.doPostTrashDocument(documentId, callback);
        }
    }

    /**
     * Delete a document.
     *
     * @param documentId id of the document to be deleted.
     */
    public void deleteDocument(String documentId, DeleteDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, DeleteDocumentCallback.class },
                new Object[] { documentId, callback })) {
            documentNetworkProvider.doDeleteDocument(documentId, callback);
        }
    }

    /**
     * Return a list of valid document types.
     */
    public RequestHandle getDocumentTypes(GetDocumentTypesCallback callback) {
        if (checkNetworkCall(
                new Class[] { GetDocumentTypesCallback.class },
                new Object[] { callback })) {
            return documentNetworkProvider.doGetDocumentTypes(callback);
        } else {
            return NullRequest.get();
        }
    }

    /* FILES */

    /**
     * Return metadata for a user's files, subject to specified query parameters.
     */
    public RequestHandle getFiles(FileRequestParameters parameters, GetFilesCallback callback) {
        if (checkNetworkCall(
                new Class[] { FileRequestParameters.class, GetFilesCallback.class },
                new Object[] { parameters, callback })) {
            return fileNetworkProvider.doGetFiles(parameters, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Return metadata for all files associated with all of the user's documents.
     */
    public RequestHandle getFiles(GetFilesCallback callback) {
        return getFiles((FileRequestParameters) null, callback);
    }

    /**
     * Return the next page of file metadata entries.
     *
     * @param next returned from previous getFiles() call.
     */
    public RequestHandle getFiles(Page next, GetFilesCallback callback) {
        if (checkNetworkCall(
                new Class[] { FileRequestParameters.class, GetFilesCallback.class },
                new Object[] { next, callback })) {
            return fileNetworkProvider.doGetFiles(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Download the content of a file.
     *
     * @param fileId the id of the file to download.
     * @param folderPath the local filesystem path in which to save the file.
     */
    public void getFile(String fileId, String documentId, String folderPath, GetFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, String.class, GetFileCallback.class },
                new Object[] { fileId, documentId, folderPath, callback })) {
            fileNetworkProvider.doGetFile(fileId, documentId, folderPath, callback);
        }
    }

    /**
     * Cancel an in-progress file download.
     *
     * @param fileId the id of the file being downloaded.
     */
    public void cancelDownload(String fileId) {
        if (checkNetworkCall(
                new Class[] { String.class },
                new Object[] { fileId })) {
            fileNetworkProvider.cancelDownload(fileId);
        }
    }

    /**
     * Upload a new file.
     *
     * @param contentType MIME type of the file, e.g "application/pdf".
     * @param documentId the ID of the document the file should be attached to.
     * @param filePath the absolute local filesystem path where the file can be found.
     */
    public void postFile(String contentType, String documentId, String filePath, PostFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, String.class, PostFileCallback.class },
                new Object[] { contentType, documentId, filePath, callback })) {
            fileNetworkProvider.doPostFile(contentType, documentId, filePath, callback);
        }
    }

    /**
     * Upload a new file.
     *
     * @param contentType MIME type of the file, e.g "application/pdf".
     * @param documentId the ID of the document the file should be attached to.
     * @param inputStream stream providing the data to be uploaded.
     * @param fileName the name to be used for the file.
     */
    public void postFile(String contentType, String documentId, InputStream inputStream, String fileName, PostFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, InputStream.class, String.class, PostFileCallback.class },
                new Object[] { contentType, documentId, inputStream, fileName, callback })) {
            fileNetworkProvider.doPostFile(contentType, documentId, inputStream, fileName, callback);
        }
    }

    /**
     * Delete a file.
     *
     * @param fileId the ID of the file to be deleted.
     */
    public void deleteFile(String fileId, DeleteFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, DeleteFileCallback.class },
                new Object[] { fileId, callback })) {
            fileNetworkProvider.doDeleteFile(fileId, callback);
        }
    }

    /* PROFILES */

    /**
     * Return the user's profile information.
     */
    public void getMyProfile(GetProfileCallback callback) {
        if (checkNetworkCall(new Class[] { GetProfileCallback.class },
                             new Object[] { callback })) {
            profileNetworkProvider.doGetMyProfile(callback);
        }
    }


    /**
     * Return profile information for another user.
     *
     * @param  profileId ID of the profile to be fetched.
     */
    public void getProfile(String profileId, GetProfileCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, GetProfileCallback.class },
                             new Object[] { profileId, callback })) {
            profileNetworkProvider.doGetProfile(profileId, callback);
        }
    }

    /* FOLDERS */

    /**
     * Return metadata for all the user's folders.
	 */
	public RequestHandle getFolders(FolderRequestParameters parameters, GetFoldersCallback callback) {
		if (checkNetworkCall(new Class[] { FolderRequestParameters.class, GetFoldersCallback.class },
			 				 new Object[] { parameters, callback })) {
			return folderNetworkProvider.doGetFolders(parameters, callback);
		} else {
            return NullRequest.get();
        }
	}

    /**
     * Return metadata for all the user's folders.
     */
    public RequestHandle getFolders(GetFoldersCallback callback) {
        return getFolders((FolderRequestParameters) null, callback);
    }

    /**
     * Returns the next page of folder metadata entries.
     *
     * @param next returned from a previous getFolders() call.
     */
    public RequestHandle getFolders(Page next, GetFoldersCallback callback) {
        if (checkNetworkCall(new Class[] { Page.class, GetFolderCallback.class },
                             new Object[] { next, callback })) {
            return folderNetworkProvider.doGetFolders(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Return metadata for all the user's groups.
     */
    public RequestHandle getGroups(GroupRequestParameters parameters, GetGroupsCallback callback) {
        if (checkNetworkCall(new Class[] { GroupRequestParameters.class, GetGroupsCallback.class },
                new Object[] { parameters, callback })) {
            return groupNetworkProvider.doGetGroups(parameters, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Returns the next page of group metadata entries.
     *
     * @param next returned from a previous getGroups() call.
     */
    public RequestHandle getGroups(Page next, GetGroupsCallback callback) {
        if (checkNetworkCall(new Class[] { Page.class, GetGroupsCallback.class },
                new Object[] { next, callback })) {
            return groupNetworkProvider.doGetGroups(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Returns metadata for a single group, specified by ID.
     *
     * @param groupId ID of the group to retrieve metadata for.
     */
    public void getGroup(String groupId, GetGroupCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, GetGroupCallback.class },
                new Object[] { groupId, callback })) {
            groupNetworkProvider.doGetGroup(groupId, callback);
        }
    }

    /**
     * Return a list of members user roles of a particular group.
     *
     * @param groupId ID of the group to inspect.
     */
    public void getGroupMembers(GroupRequestParameters parameters, String groupId, GetGroupMembersCallback callback) {
        if (checkNetworkCall(new Class[] { GroupRequestParameters.class, String.class, GetGroupMembersCallback.class },
                new Object[] { parameters, groupId, callback })) {
            groupNetworkProvider.doGetGroupMembers(parameters, groupId, callback);
        }
    }

    /**
     * Returns the next page of members user roles of a group.
     *
     * @param next returned by a previous call to getGroupMembers().
     * @param groupId provides an ID to return in the callback (the value is not actually
     *                 checked by this call).
     */
    public void getFolderDocumentIds(Page next, String groupId, GetGroupMembersCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, String.class, GetGroupMembersCallback.class },
                new Object[] { next, groupId, callback })) {
            groupNetworkProvider.doGetGroupMembers(next, groupId, callback);
        }
    }


    /**
	 * Returns metadata for a single folder, specified by ID.
	 * 
	 * @param folderId ID of the folder to retrieve metadata for.
	 */
	public void getFolder(String folderId, GetFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, GetFolderCallback.class },
			 				 new Object[] { folderId, callback })) {
			folderNetworkProvider.doGetFolder(folderId, callback);
		}
	}
	
    /**
	 * Create a new folder.
	 * 
	 * @param folder metadata for the folder to create.
	 */
	public void postFolder(Folder folder, PostFolderCallback callback) {
		if (checkNetworkCall(new Class[] { Folder.class, PostFolderCallback.class },
			 				 new Object[] { folder, callback })) {
			folderNetworkProvider.doPostFolder(folder, callback);
		}
	}

    /**
     * Update a folder's metadata.
     * <p>
     * This can be used to rename the folder, and/or to move it to a new parent.
     *
     * @param folderId the id of the folder to modify.
     * @param folder metadata object that provides the new name and parentId.
     */
    public void patchFolder(String folderId, Folder folder, PatchFolderCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, Folder.class, PatchFolderCallback.class },
                             new Object[] { folderId, folder, callback })) {
            folderNetworkProvider.doPatchFolder(folderId, folder, callback);
        }
    }

    /**
     * Return a list of IDs of the documents stored in a particular folder.
     *
     * @param folderId ID of the folder to inspect.
     */
    public void getFolderDocumentIds(FolderRequestParameters parameters, String folderId, GetFolderDocumentIdsCallback callback) {
        if (checkNetworkCall(new Class[] { FolderRequestParameters.class, String.class, GetFolderDocumentIdsCallback.class },
                             new Object[] { parameters, folderId, callback })) {
            folderNetworkProvider.doGetFolderDocumentIds(parameters, folderId, callback);
        }
    }

    /**
     * Returns the next page of document IDs stored in a particular folder.
     *
     * @param next returned by a previous call to getFolderDocumentIds().
     * @param folderId provides an ID to return in the callback (the value is not actually
     *                 checked by this call).
     */
    public void getFolderDocumentIds(Page next, String folderId, GetFolderDocumentIdsCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, String.class, GetFolderDocumentIdsCallback.class },
                             new Object[] { next, folderId, callback })) {
            folderNetworkProvider.doGetFolderDocumentIds(next, folderId, callback);
        }
    }

    /**
     * Add a document to a folder.
	 *
	 * @param folderId the ID the folder.
	 * @param documentId the ID of the document to add to the folder.
	 */
	public void postDocumentToFolder(String folderId, String documentId, PostDocumentToFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, String.class, PostDocumentToFolderCallback.class },
			 				 new Object[] { folderId, documentId, callback })) {
			folderNetworkProvider.doPostDocumentToFolder(folderId, documentId, callback);
		}
	}
	
	/**
     * Delete a folder.
     * <p>
     * This does not delete the documents inside the folder.
	 *
	 * @param folderId the ID of the folder to delete.
	 */
	public void deleteFolder(String folderId, DeleteFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, DeleteFolderCallback.class },
			 				 new Object[] { folderId, callback })) {
			folderNetworkProvider.doDeleteFolder(folderId, callback);
		}
	}
	
	/**
     * Remove a document from a folder.
     * <p>
     * This does not delete the documents itself.
	 *
	 * @param folderId the ID of the folder.
	 * @param documentId the ID of the document to remove.
	 */
	public void deleteDocumentFromFolder(String folderId, String documentId, DeleteFolderDocumentCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, String.class, DeleteFolderDocumentCallback.class },
			 				 new Object[] { folderId, documentId, callback })) {
			folderNetworkProvider.doDeleteDocumentFromFolder(folderId, documentId, callback);
		}
	}
	
	/**
	 * public method to call clearCredentials method on the protected AuthenticationManager
	 */
	public void clearCredentials() {
		authenticationManager.clearCredentials();
	}
	
	/**
	 * Call checkCredentialsAndCopyToNetworkProvider method on the protected AuthenticationManager
	 * 
	 * @return true if credentials are stored already in SharedPreferences.
	 */
	private boolean hasCredentials() {
		return authenticationManager.checkCredentialsAndCopyToNetworkProvider();
	}
	
	/**
	 * Checking the given context to see which interfaces are implemented by the calling activity
	 * and initialising the relevant objects for sending callbacks.
	 */
	private void initialiseInterfaces() {
		documentNetworkProvider = new DocumentNetworkProvider();
        fileNetworkProvider = new FileNetworkProvider();
		profileNetworkProvider = new ProfileNetworkProvider();
		folderNetworkProvider = new FolderNetworkProvider();
        groupNetworkProvider = new GroupNetworkProvider();
	} 
		
    /**
     * Checks if client is authenticated, if false initialises the MethodToInvoke object with the calling method name and its arguments
     * and calls authenticate on the AuthenticationManager, else returns true.
     *
     * @param classes of the arguments of the calling method
     * @param values of the arguments of the calling method
     * @return true if network call can be executed
     */
    private boolean checkNetworkCall(@SuppressWarnings("rawtypes") Class[] classes, Object[] values) {
        if (!authenticationManager.isAuthenticated()) {
            if (classes == null) {
                methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[1].getMethodName());
                authenticationManager.authenticate();
            } else {
                methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[1].getMethodName(), classes, values);
                authenticationManager.authenticate();
            }
            return false;
        }
        return true;
    }

    /**
	 * Invoking the method stored in the MethodToInvoke object.
	 */
	private void invokeMethod() {
		
		if (methodToInvoke != null) {
			try {
				Method method = null;
				String className = this.getClass().getName();			
				Class<?> clazz = Class.forName(className);  	
				if (methodToInvoke.argumentTypes != null) {
					method = clazz.getMethod(methodToInvoke.methodName, methodToInvoke.argumentTypes);  	
					method.invoke(this, methodToInvoke.arguments);  
				} else {
					method = clazz.getMethod(methodToInvoke.methodName);
					method.invoke(this);
				}
			}
			catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}

	/**
	 * Inner class that holds details of the method and arguments to be called once the client has been authenticated.
	 */
	protected class MethodtoInvoke {
		
		String methodName;
		@SuppressWarnings("rawtypes")
		Class[] argumentTypes;
		Object[] arguments;
		
		MethodtoInvoke(String methodName) {
			this.methodName = methodName;
			argumentTypes = null;
			arguments = null;
		}
		
		MethodtoInvoke(String methodName, @SuppressWarnings("rawtypes") Class[] argumentTypes, Object[] arguments) {
			this.methodName = methodName;
			this.argumentTypes = argumentTypes;
			this.arguments = arguments;
		}
	}
	
	/**
	 * public default constructor for junit testing.
	 */
	public MendeleySDK() {
	}
}

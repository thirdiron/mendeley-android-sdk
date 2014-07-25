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
import com.mendeley.api.callbacks.profile.GetProfileCallback;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.DocumentNetworkProvider;
import com.mendeley.api.network.FileNetworkProvider;
import com.mendeley.api.network.FolderNetworkProvider;
import com.mendeley.api.network.NullRequest;
import com.mendeley.api.network.ProfileNetworkProvider;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.Page;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * This class should be instantiated with the calling activity context.
 * The class provides public methods for network calls which are forwarded to the relevant network providers.
 * It also calls the AuthenticationManager for retrieving a valid access token and store the credentials. 
 * The context is used for displaying the authentication WebView, storing credentials in SharedPreferences 
 * and is also checked to see which interfaces the activity implements for sending callbacks once a network task has finished.
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

	protected AuthenticationManager authenticationManager;
	protected MethodtoInvoke methodToInvoke;
	
	protected DocumentNetworkProvider documentNetworkProvider;
	protected FileNetworkProvider fileNetworkProvider;
	protected ProfileNetworkProvider profileNetworkProvider;
	protected FolderNetworkProvider folderNetworkProvider;
	
	private MendeleySignInInterface mendeleySignInInterface;

	private static final String TAG = MendeleySDK.class.getSimpleName();
    /**
     * Obtain a handle to the SDK.
     *
     * @param context used for creating the sign-in activity
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process
     */
	public MendeleySDK(Context context, ClientCredentials clientCredentials)  {
        initWithWebSignIn(context, clientCredentials);
	}

    /**
     * Obtain a handle to the SDK.
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
     * Developer applications should not use this constructor, instead they should pass a context.
     * This constructor is intended for unit testing the SDK.
     */
    public MendeleySDK(ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        initWithPasswordSignIn(userCredentials, clientCredentials);
    }

    /**
     * Obtain a handle to the SDK (internal use only).
     * <p>
     * Developer applications should not use this constructor, instead they should pass a context.
     * This constructor is intended for unit testing the SDK.
     */
    public MendeleySDK(MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        this.mendeleySignInInterface = signInCallback;
        initWithPasswordSignIn(userCredentials, clientCredentials);
    }

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
     *
     * @param parameters holds optional query parameters, will be ignored if null
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
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
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
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param document the document object to be posted
     */
    public void postDocument(Document document, PostDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { Document.class, PostDocumentCallback.class },
                new Object[] { document, callback })) {
            documentNetworkProvider.doPostDocument(document, callback);
        }
    }


    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the id of the document to be patched
     * @param date for the api condition if unmodified since.
     * @param document the document object
     */
    public void patchDocument(String documentId, Date date, Document document, PatchDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, Date.class, Document.class, PatchDocumentCallback.class },
                new Object[] { documentId, date, document, callback })) {
            documentNetworkProvider.doPatchDocument(documentId, date, document, callback);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the document id to be trashed
     */
    public void trashDocument(String documentId, TrashDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, TrashDocumentCallback.class },
                new Object[] { documentId, callback })) {
            documentNetworkProvider.doPostTrashDocument(documentId, callback);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the document id to be deleted
     */
    public void deleteDocument(String documentId, DeleteDocumentCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, DeleteDocumentCallback.class },
                new Object[] { documentId, callback })) {
            documentNetworkProvider.doDeleteDocument(documentId, callback);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
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
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param parameters holds optional query parameters, will be ignored if null
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
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     */
    public RequestHandle getFiles(GetFilesCallback callback) {
        return getFiles((FileRequestParameters) null, callback);
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param next returned from previous getFiles() call
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
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param fileId the id of the file to get
     * @param folderPath the path in which to save the file
     */
    public void getFile(String fileId, String documentId, String folderPath, GetFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, String.class, GetFileCallback.class },
                new Object[] { fileId, documentId, folderPath, callback })) {
            fileNetworkProvider.doGetFile(fileId, documentId, folderPath, callback);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param fileId the id of the file
     */
    public void cancelDownload(String fileId) {
        if (checkNetworkCall(
                new Class[] { String.class },
                new Object[] { fileId })) {
            fileNetworkProvider.cancelDownload(fileId);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param contentType the content type of the file
     * @param documentId the id of the document the file is related to
     * @param filePath the absolute file path
     */
    public void postFile(String contentType, String documentId, String filePath, PostFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, String.class, PostFileCallback.class },
                new Object[] { contentType, documentId, filePath, callback })) {
            fileNetworkProvider.doPostFile(contentType, documentId, filePath, callback);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param contentType the content type of the file
     * @param documentId the id of the document the file is related to
     * @param inputStream provides the data to be uploaded
     */
    public void postFile(String contentType, String documentId, InputStream inputStream, String fileName, PostFileCallback callback) {
        if (checkNetworkCall(
                new Class[] { String.class, String.class, InputStream.class, String.class, PostFileCallback.class },
                new Object[] { contentType, documentId, inputStream, fileName, callback })) {
            fileNetworkProvider.doPostFile(contentType, documentId, inputStream, fileName, callback);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
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
     * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
     */
    public void getMyProfile(GetProfileCallback callback) {
        if (checkNetworkCall(new Class[] { GetProfileCallback.class },
                             new Object[] { callback })) {
            profileNetworkProvider.doGetMyProfile(callback);
        }
    }


    /**
     * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
     */
    public void getProfile(String profileId, GetProfileCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, GetProfileCallback.class },
                             new Object[] { profileId, callback })) {
            profileNetworkProvider.doGetProfile(profileId, callback);
        }
    }

    /* FOLDERS */

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param parameters holds optional query parameters, will be ignored if null
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
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     */
    public RequestHandle getFolders(GetFoldersCallback callback) {
        return getFolders((FolderRequestParameters) null, callback);
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param next reference to next page
     */
    public RequestHandle getFolders(Page next, GetFoldersCallback callback) {
        if (checkNetworkCall(new Class[] { FolderRequestParameters.class, GetFolderCallback.class },
                             new Object[] { next, callback })) {
            return folderNetworkProvider.doGetFolders(next, callback);
        } else {
            return NullRequest.get();
        }
    }

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId id of the folder to get
	 */
	public void getFolder(String folderId, GetFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, GetFolderCallback.class },
			 				 new Object[] { folderId, callback })) {
			folderNetworkProvider.doGetFolder(folderId, callback);
		}
	}
	
    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folder the folder object to post
	 */
	public void postFolder(Folder folder, PostFolderCallback callback) {
		if (checkNetworkCall(new Class[] { Folder.class, PostFolderCallback.class },
			 				 new Object[] { folder, callback })) {
			folderNetworkProvider.doPostFolder(folder, callback);
		}
	}

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param folderId the id of the folder to patch
     * @param folder the folder object that holds the new name and parentId data
     */
    public void patchFolder(String folderId, Folder folder, PatchFolderCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, Folder.class, PatchFolderCallback.class },
                             new Object[] { folderId, folder, callback })) {
            folderNetworkProvider.doPatchFolder(folderId, folder, callback);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param folderId id of the folder for which to get the document ids
     */
    public void getFolderDocumentIds(FolderRequestParameters parameters, String folderId, GetFolderDocumentIdsCallback callback) {
        if (checkNetworkCall(new Class[] { FolderRequestParameters.class, String.class, GetFolderDocumentIdsCallback.class },
                             new Object[] { parameters, folderId, callback })) {
            folderNetworkProvider.doGetFolderDocumentIds(parameters, folderId, callback);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param next reference to next results page
     */
    public void getFolderDocumentIds(Page next, String folderId, GetFolderDocumentIdsCallback callback) {
        if (checkNetworkCall(new Class[] { String.class, String.class, GetFolderDocumentIdsCallback.class },
                             new Object[] { next, folderId, callback })) {
            folderNetworkProvider.doGetFolderDocumentIds(next, folderId, callback);
        }
    }

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to add to the folder
	 */
	public void postDocumentToFolder(String folderId, String documentId, PostDocumentToFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, String.class, PostDocumentToFolderCallback.class },
			 				 new Object[] { folderId, documentId, callback })) {
			folderNetworkProvider.doPostDocumentToFolder(folderId, documentId, callback);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder to delete
	 */
	public void deleteFolder(String folderId, DeleteFolderCallback callback) {
		if (checkNetworkCall(new Class[] { String.class, DeleteFolderCallback.class },
			 				 new Object[] { folderId, callback })) {
			folderNetworkProvider.doDeleteFolder(folderId, callback);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
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
	 *
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

package com.mendeley.api;

import java.lang.reflect.Method;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.auth.UserCredentials;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.profile.GetProfileCallback;
import com.mendeley.api.exceptions.InterfaceNotImplementedException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.DocumentNetworkProvider;
import com.mendeley.api.network.FileNetworkProvider;
import com.mendeley.api.network.FolderNetworkProvider;
import com.mendeley.api.network.NullRequest;
import com.mendeley.api.network.ProfileNetworkProvider;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;
import com.mendeley.api.network.interfaces.MendeleyFileInterface;
import com.mendeley.api.network.interfaces.MendeleyFolderInterface;
import com.mendeley.api.network.interfaces.MendeleyInterface;
import com.mendeley.api.network.interfaces.MendeleySignInInterface;

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
	
	protected MendeleyDocumentInterface documentInterface;
	protected MendeleyFolderInterface folderInterface;
	protected MendeleyFileInterface fileInterface;
	private MendeleySignInInterface mendeleySignInInterface;

	private static final String TAG = MendeleySDK.class.getSimpleName();
    /**
     * Obtain a handle to the SDK.
     *
     * @param context used for creating the sign-in activity
     * @param callbacks object which implements one or more Mendeley*Interfaces
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process
     */
	public MendeleySDK(Context context, Object callbacks, ClientCredentials clientCredentials)  {
        initWithWebSignIn(context, callbacks, clientCredentials);
	}

    /**
     * Obtain a handle to the SDK.
     *
     * @param context used for creating the sign-in activity
     * @param callbacks object which implements one or more Mendeley*Interfaces
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process
     * @param signInCallback used to receive sign in/out events.
     */
	public MendeleySDK(Context context, Object callbacks, MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials) {
        this.mendeleySignInInterface = signInCallback;
        initWithWebSignIn(context, callbacks, clientCredentials);
    }

    /**
     * Obtain a handle to the SDK (internal use only).
     * <p>
     * Developer applications should not use this constructor, instead they should pass a context.
     * This constructor is intended for unit testing the SDK.
     */
    public MendeleySDK(Object callbacks, ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        initWithPasswordSignIn(userCredentials, callbacks, clientCredentials);
    }

    /**
     * Obtain a handle to the SDK (internal use only).
     * <p>
     * Developer applications should not use this constructor, instead they should pass a context.
     * This constructor is intended for unit testing the SDK.
     */
    public MendeleySDK(Object callbacks, MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        this.mendeleySignInInterface = signInCallback;
        initWithPasswordSignIn(userCredentials, callbacks, clientCredentials);
    }

    public static String getVersion(Context context) {
        Resources resources = context.getResources();
        return resources.getString(R.string.version_name);
    }

    private void initWithWebSignIn(Context context, Object callbacks, ClientCredentials clientCredentials) {
        AuthenticationManager.configure(
                context,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        init(callbacks);
    }

    private void initWithPasswordSignIn(UserCredentials userCredentials, Object callbacks, ClientCredentials clientCredentials) {
        AuthenticationManager.configure(
                userCredentials.username,
                userCredentials.password,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        init(callbacks);
    }

    private void init(Object callbacks) {
        authenticationManager = AuthenticationManager.getInstance();
        initialiseInterfaces(callbacks);
        if (!hasCredentials()) {
            authenticationManager.authenticate();
        }
    }

    private AuthenticationInterface createAuthenticationInterface() {
        return new AuthenticationInterface() {
            @Override
            public void onAuthenticated() {
                if (mendeleySignInInterface != null) {
                    mendeleySignInInterface.isSignedIn(true);
                }
                invokeMethod();
            }

            @Override
            public void onAuthenticationFail() {
                if (mendeleySignInInterface != null) {
                    mendeleySignInInterface.isSignedIn(false);
                }
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
    public RequestHandle getDocuments(DocumentRequestParameters parameters) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{DocumentRequestParameters.class},
                new Object[]{parameters})) {
            return documentNetworkProvider.doGetDocuments(parameters);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Retrieve a list of documents in the user's library.
     */
    public RequestHandle getDocuments() throws InterfaceNotImplementedException {
        return getDocuments((DocumentRequestParameters) null);
    }

    /**
     * Retrieve subsequent pages of documents in the user's library.
     *
     * @next reference to next page returned by a previous onDocumentsReceived() callback.
     */
    public RequestHandle getDocuments(Page next) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{DocumentRequestParameters.class},
                new Object[]{next})) {
            return documentNetworkProvider.doGetDocuments(next);
        } else {
            return NullRequest.get();
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the document id to get
     * @param parameters holds optional query parameters, will be ignored if null
     * @throws InterfaceNotImplementedException
     */
    public void getDocument(String documentId, DocumentRequestParameters parameters) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{String.class,DocumentRequestParameters.class},
                new Object[]{documentId, parameters})) {
            documentNetworkProvider.doGetDocument(documentId, parameters);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param document the document object to be posted
     * @throws InterfaceNotImplementedException
     */
    public void postDocument(Document document) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{Document.class},
                new Object[]{document})) {
            documentNetworkProvider.doPostDocument(document);
        }
    }


    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the id of the document to be patched
     * @param date for the api condition if unmodified since.
     * @param document the document object
     * @throws InterfaceNotImplementedException
     */
    public void patchDocument(String documentId, Date date, Document document) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{String.class, Date.class, Document.class},
                new Object[]{documentId, date, document})) {
            documentNetworkProvider.doPatchDocument(documentId, date, document);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the document id to be trashed
     * @throws InterfaceNotImplementedException
     */
    public void trashDocument(String documentId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{String.class},
                new Object[]{documentId})) {
            documentNetworkProvider.doPostTrashDocument(documentId);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @param documentId the document id to be deleted
     * @throws InterfaceNotImplementedException
     */
    public void deleteDocument(String documentId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                new Class[]{String.class},
                new Object[]{documentId})) {
            documentNetworkProvider.doDeleteDocument(documentId);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
     *
     * @throws InterfaceNotImplementedException
     */
    public RequestHandle getDocumentTypes() throws InterfaceNotImplementedException {
        if (checkNetworkCall(documentInterface,
                null,
                null)) {
            return documentNetworkProvider.doGetDocumentTypes();
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
    public RequestHandle getFiles(FileRequestParameters parameters) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{FileRequestParameters.class},
                new Object[]{parameters})) {
            return fileNetworkProvider.doGetFiles(parameters);
        } else {
            return NullRequest.get();
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     */
    public RequestHandle getFiles() throws InterfaceNotImplementedException {
        return getFiles((FileRequestParameters) null);
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param next returned from previous getFiles() call
     */
    public RequestHandle getFiles(Page next) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{FileRequestParameters.class},
                new Object[]{next})) {
            return fileNetworkProvider.doGetFiles(next);
        } else {
            return NullRequest.get();
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param fileId the id of the file to get
     * @param folderPath the path in which to save the file
     * @throws InterfaceNotImplementedException
     */
    public void getFile(String fileId, String documentId, String folderPath) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{String.class, String.class, String.class},
                new Object[]{fileId, documentId, folderPath})) {
            fileNetworkProvider.doGetFile(fileId, documentId, folderPath);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param fileId the id of the file
     */
    public void cancelDownload(String fileId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{String.class},
                new Object[]{fileId})) {
            fileNetworkProvider.cancelDownload(fileId);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @param contentType the content type of the file
     * @param documentId the id of the document the file is related to
     * @param filePath the absolute file path
     * @throws InterfaceNotImplementedException
     */
    public void postFile(String contentType, String documentId, String filePath) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{String.class, String.class, String.class},
                new Object[]{contentType, documentId, filePath})) {
            fileNetworkProvider.doPostFile(contentType, documentId, filePath);
        }
    }

    /**
     *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
     *
     * @throws InterfaceNotImplementedException
     */
    public void deleteFile(String fileId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(fileInterface,
                new Class[]{String.class},
                new Object[]{fileId})) {
            fileNetworkProvider.doDeleteFile(fileId);
        }
    }

    /* PROFILES */

    /**
     * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
     *
     * @throws InterfaceNotImplementedException
     */
    public void getMyProfile(GetProfileCallback callback) throws InterfaceNotImplementedException {
        if (checkNetworkCall(null, null)) {
            profileNetworkProvider.doGetMyProfile(callback);
        }
    }


    /**
     * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
     *
     * @throws InterfaceNotImplementedException
     */
    public void getProfile(String profileId, GetProfileCallback callback) throws InterfaceNotImplementedException {
        if (checkNetworkCall(new Class[]{String.class}, new Object[]{profileId})) {
            profileNetworkProvider.doGetProfile(profileId, callback);
        }
    }

    /* FOLDERS */

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param parameters holds optional query parameters, will be ignored if null
	 * @throws InterfaceNotImplementedException
	 */
	public RequestHandle getFolders(FolderRequestParameters parameters) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{FolderRequestParameters.class}, 
			 				 new Object[]{parameters})) {
			return folderNetworkProvider.doGetFolders(parameters);
		} else {
            return NullRequest.get();
        }
	}

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     */
    public RequestHandle getFolders() throws InterfaceNotImplementedException {
        return getFolders((FolderRequestParameters) null);
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param next reference to next page
     * @throws InterfaceNotImplementedException
     */
    public RequestHandle getFolders(Page next) throws InterfaceNotImplementedException {
        if (checkNetworkCall(folderInterface,
                new Class[]{FolderRequestParameters.class},
                new Object[]{next})) {
            return folderNetworkProvider.doGetFolders(next);
        } else {
            return NullRequest.get();
        }
    }

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId id of the folder to get
	 * @throws InterfaceNotImplementedException
	 */
	public void getFolder(String folderId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{folderId})) {
			folderNetworkProvider.doGetFolder(folderId);
		}
	}
	
    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folder the folder object to post
	 * @throws InterfaceNotImplementedException
	 */
	public void postFolder(Folder folder) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{Folder.class}, 
			 				 new Object[]{folder})) {
			folderNetworkProvider.doPostFolder(folder);
		}
	}

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param folderId the id of the folder to patch
     * @param folder the folder object that holds the new name and parentId data
     * @throws InterfaceNotImplementedException
     */
    public void patchFolder(String folderId, Folder folder) throws InterfaceNotImplementedException {
        if (checkNetworkCall(folderInterface,
                new Class[]{String.class, Folder.class},
                new Object[]{folderId, folder})) {
            folderNetworkProvider.doPatchFolder(folderId, folder);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param folderId id of the folder for which to get the document ids
     */
    public void getFolderDocumentIds(FolderRequestParameters parameters, String folderId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(folderInterface,
                new Class[]{FolderRequestParameters.class, String.class},
                new Object[]{parameters, folderId})) {
            folderNetworkProvider.doGetFolderDocumentIds(parameters, folderId);
        }
    }

    /**
     * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
     *
     * @param next reference to next results page
     */
    public void getFolderDocumentIds(Page next, String folderId) throws InterfaceNotImplementedException {
        if (checkNetworkCall(folderInterface,
                new Class[]{String.class, String.class},
                new Object[]{next, folderId})) {
            folderNetworkProvider.doGetFolderDocumentIds(next, folderId);
        }
    }

    /**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to add to the folder
	 * @throws InterfaceNotImplementedException
	 */
	public void postDocumentToFolder(String folderId, String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class,String.class}, 
			 				 new Object[]{folderId, documentId})) {
			folderNetworkProvider.doPostDocumentToFolder(folderId, documentId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder to delete
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteFolder(String folderId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{folderId})) {
			folderNetworkProvider.doDeleteFolder(folderId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteDocumentFromFolder(String folderId, String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class, String.class}, 
			 				 new Object[]{folderId, documentId})) {
			folderNetworkProvider.doDeleteDocumentFromFolder(folderId, documentId);
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
	 * 
	 * @param callbacks the requester's callbacks.
	 */
	private void initialiseInterfaces(Object callbacks) {
		
		if (callbacks instanceof MendeleyDocumentInterface) {
			documentInterface = (MendeleyDocumentInterface) callbacks;
		}
		
		if (callbacks instanceof MendeleyFolderInterface) {
			folderInterface = (MendeleyFolderInterface) callbacks;
		}
		
		if (callbacks instanceof MendeleyFileInterface) {
			fileInterface = (MendeleyFileInterface) callbacks;
		}
		
		documentNetworkProvider = new DocumentNetworkProvider(documentInterface);
		fileNetworkProvider = new FileNetworkProvider(fileInterface);
		profileNetworkProvider = new ProfileNetworkProvider();
		folderNetworkProvider = new FolderNetworkProvider(folderInterface);
	} 
		
	/**
	 * First checks that the MendeleyInterface has been instantiated for sending callbacks to the application; if not throws InterfaceNotImplementedException.
	 * Then checks if client is authenticated, if false initialises the MethodToInvoke object with the calling method name and its arguments
	 * and calls authenticate on the AuthenticationManager, else returns true.
	 * 
	 * @param mendeleyInterface the instance of MendeleyInterface that will be used for the callbacks
	 * @param classes of the arguments of the calling method
	 * @param values of the arguments of the calling method
	 * @return true if network call can be executed
	 * @throws InterfaceNotImplementedException
	 */
	private boolean checkNetworkCall(MendeleyInterface mendeleyInterface, @SuppressWarnings("rawtypes") Class[] classes, Object[] values) throws InterfaceNotImplementedException {
		if (mendeleyInterface == null) {
			throw new InterfaceNotImplementedException("The required MendeleyInterface is not implemented by the calling class");
		}
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
     * Checks if client is authenticated, if false initialises the MethodToInvoke object with the calling method name and its arguments
     * and calls authenticate on the AuthenticationManager, else returns true.
     *
     * @param classes of the arguments of the calling method
     * @param values of the arguments of the calling method
     * @return true if network call can be executed
     * @throws InterfaceNotImplementedException
     */
    private boolean checkNetworkCall(@SuppressWarnings("rawtypes") Class[] classes, Object[] values) throws InterfaceNotImplementedException {
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

package com.mendeley.api.impl;

import android.os.AsyncTask;

import com.mendeley.api.BlockingSdk;
import com.mendeley.api.MendeleySdk;
import com.mendeley.api.auth.AuthenticationInterface;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.MendeleySignInInterface;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.document.DocumentIdList;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.callbacks.group.GetGroupMembersCallback;
import com.mendeley.api.callbacks.group.GroupList;
import com.mendeley.api.callbacks.group.GroupMembersList;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.NotSignedInException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Group;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.JsonParser;
import com.mendeley.api.network.NullRequest;
import com.mendeley.api.network.procedure.DeleteNetworkProcedure;
import com.mendeley.api.network.procedure.PatchNetworkProcedure;
import com.mendeley.api.network.procedure.PostNoBodyNetworkProcedure;
import com.mendeley.api.network.procedure.Procedure;
import com.mendeley.api.network.provider.DocumentNetworkProvider;
import com.mendeley.api.network.Environment;
import com.mendeley.api.network.provider.FileNetworkProvider;
import com.mendeley.api.network.provider.FolderNetworkProvider;
import com.mendeley.api.network.provider.GroupNetworkProvider;
import com.mendeley.api.network.provider.ProfileNetworkProvider;
import com.mendeley.api.network.provider.TrashNetworkProvider;
import com.mendeley.api.network.provider.UtilsNetworkProvider;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.params.View;

import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.mendeley.api.network.provider.DocumentNetworkProvider.DOCUMENTS_BASE_URL;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.DOCUMENT_TYPES_BASE_URL;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.GetDeletedDocumentsProcedure;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.GetDocumentProcedure;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.GetDocumentTypesProcedure;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.GetDocumentsProcedure;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.PatchDocumentProcedure;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.getDeleteDocumentUrl;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.getGetDocumentUrl;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.getGetDocumentsUrl;
import static com.mendeley.api.network.provider.DocumentNetworkProvider.getTrashDocumentUrl;
import static com.mendeley.api.network.provider.GroupNetworkProvider.GetGroupMembersProcedure;
import static com.mendeley.api.network.provider.GroupNetworkProvider.GetGroupsProcedure;
import static com.mendeley.api.network.provider.GroupNetworkProvider.getGetGroupMembersUrl;
import static com.mendeley.api.network.provider.GroupNetworkProvider.getGetGroupsUrl;
import static com.mendeley.api.network.provider.ProfileNetworkProvider.GetProfileProcedure;
import static com.mendeley.api.network.provider.ProfileNetworkProvider.PROFILES_URL;

/**
 * Implementation of the blocking API calls.
 */
public abstract class BaseMendeleySdk implements BlockingSdk, Environment {
    private static final String TAG = MendeleySdk.class.getSimpleName();

    protected AuthenticationManager authenticationManager;
    protected MendeleySignInInterface mendeleySignInInterface;

    protected DocumentNetworkProvider documentNetworkProvider;
    protected FileNetworkProvider fileNetworkProvider;
    protected ProfileNetworkProvider profileNetworkProvider;
    protected FolderNetworkProvider folderNetworkProvider;
    protected GroupNetworkProvider groupNetworkProvider;
    protected UtilsNetworkProvider utilsNetworkProvider;
    protected TrashNetworkProvider trashNetworkProvider;

    protected void initProviders() {
        documentNetworkProvider = new DocumentNetworkProvider(this, authenticationManager);
        fileNetworkProvider = new FileNetworkProvider(this, authenticationManager);
        profileNetworkProvider = new ProfileNetworkProvider(this, authenticationManager);
        folderNetworkProvider = new FolderNetworkProvider(this, authenticationManager);
        groupNetworkProvider = new GroupNetworkProvider(this, authenticationManager);
        utilsNetworkProvider = new UtilsNetworkProvider(this, authenticationManager);
        trashNetworkProvider = new TrashNetworkProvider(this, authenticationManager);
    }

    protected AuthenticationInterface createAuthenticationInterface() {
        return new AuthenticationInterface() {
            @Override
            public void onAuthenticated(boolean manualSignIn) {
                if (mendeleySignInInterface != null && manualSignIn) {
                    mendeleySignInInterface.onSignedIn();
                }
            }

            @Override
            public void onAuthenticationFail() {
                if (mendeleySignInInterface != null) {
                    mendeleySignInInterface.onSignInFailure();
                }
            }
        };
    }

    public interface Command {
        RequestHandle exec();
    }

    /* DOCUMENTS BLOCKING */

    @Override
    public DocumentList getDocuments(DocumentRequestParameters parameters) throws MendeleyException {
        try {
            String url = getGetDocumentsUrl(DOCUMENTS_BASE_URL, parameters, null);
            Procedure<DocumentList> proc = new GetDocumentsProcedure(url, authenticationManager);
            return proc.checkedRun();
        } catch (UnsupportedEncodingException e) {
            throw new MendeleyException(e.getMessage());
        }
    }

    @Override
    public DocumentList getDocuments() throws MendeleyException {
        return getDocuments((DocumentRequestParameters) null);
    }

    @Override
    public DocumentList getDocuments(Page next) throws MendeleyException {
        if (!Page.isValidPage(next)) {
            throw new NoMorePagesException();
        }
        Procedure<DocumentList> proc = new GetDocumentsProcedure(next.link, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public Document getDocument(String documentId, View view) throws MendeleyException {
        String url = getGetDocumentUrl(documentId, view);
        Procedure<Document> proc = new GetDocumentProcedure(url, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public DocumentIdList getDeletedDocuments(String deletedSince, DocumentRequestParameters parameters) throws MendeleyException {
        try {
            String url = getGetDocumentsUrl(DOCUMENTS_BASE_URL, parameters, deletedSince);
            Procedure<DocumentIdList> proc = new GetDeletedDocumentsProcedure(url, authenticationManager);
            return proc.checkedRun();
        } catch (UnsupportedEncodingException e) {
            throw new MendeleyException(e.getMessage());
        }
    }

    @Override
    public DocumentIdList getDeletedDocuments(Page next) throws MendeleyException {
        if (!Page.isValidPage(next)) {
            throw new NoMorePagesException();
        }
        Procedure<DocumentIdList> proc =
                new GetDeletedDocumentsProcedure(next.link, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public Document postDocument(Document document) throws MendeleyException {
        try {
            Procedure<Document> proc
                    = new DocumentNetworkProvider.PostDocumentProcedure(document, authenticationManager);
            return proc.checkedRun();
        } catch (JSONException e) {
            throw new JsonParsingException(e.getMessage());
        }
    }

    @Override
    public void patchDocument(String documentId, Date date, Document document) throws MendeleyException {
        try {
            String json = JsonParser.jsonFromDocument(document);
            Procedure proc = new PatchDocumentProcedure(documentId, json, date, authenticationManager);
            proc.checkedRun();
        } catch (JSONException e) {
            throw new JsonParsingException(e.getMessage());
        }
    }

    @Override
    public void trashDocument(String documentId) throws MendeleyException {
        Procedure proc = new PostNoBodyNetworkProcedure(getTrashDocumentUrl(documentId), authenticationManager);
        proc.checkedRun();
    }

    @Override
    public void deleteDocument(String documentId) throws MendeleyException {
        Procedure proc = new DeleteNetworkProcedure(getDeleteDocumentUrl(documentId), authenticationManager);
        proc.checkedRun();
    }

    @Override
    public Map<String, String> getDocumentTypes() throws MendeleyException {
        Procedure<Map<String, String>> proc =
                new GetDocumentTypesProcedure(DOCUMENT_TYPES_BASE_URL, authenticationManager);
        return proc.checkedRun();
    }

    /* PROFILES BLOCKING */

    @Override
    public Profile getMyProfile() throws MendeleyException {
        Procedure<Profile> proc = new GetProfileProcedure(PROFILES_URL + "me", authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public Profile getProfile(final String profileId) throws MendeleyException {
        Procedure<Profile> proc = new GetProfileProcedure(PROFILES_URL + profileId, authenticationManager);
        return proc.checkedRun();
    }

    /* GROUPS BLOCKING */

    @Override
    public GroupList getGroups(GroupRequestParameters parameters) throws MendeleyException {
        String url = getGetGroupsUrl(parameters);
        Procedure<GroupList> proc = new GetGroupsProcedure(url, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public GroupList getGroups(Page next) throws MendeleyException {
        if (!Page.isValidPage(next)) {
            throw new NoMorePagesException();
        }
        Procedure<GroupList> proc = new GetGroupsProcedure(next.link, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public Group getGroup(String groupId) throws MendeleyException {
        String url = GroupNetworkProvider.getGetGroupUrl(groupId);
        Procedure<Group> proc = new GroupNetworkProvider.GetGroupProcedure(url, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public GroupMembersList getGroupMembers(GroupRequestParameters parameters, String groupId) throws MendeleyException {
        String url = getGetGroupsUrl(parameters, getGetGroupMembersUrl(groupId));
        Procedure<GroupMembersList> proc = new GetGroupMembersProcedure(url, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public GroupMembersList getGroupMembers(Page next) throws MendeleyException {
        if (!Page.isValidPage(next)) {
            throw new NoMorePagesException();
        }
        Procedure<GroupMembersList> proc = new GetGroupMembersProcedure(next.link, authenticationManager);
        return proc.checkedRun();
    }

    /* TRASH BLOCKING */

    @Override
    public DocumentList getTrashedDocuments(DocumentRequestParameters parameters) throws MendeleyException {
        try {
            String url = getGetDocumentsUrl(TrashNetworkProvider.BASE_URL, parameters, null);
            Procedure<DocumentList> proc = new GetDocumentsProcedure(url, authenticationManager);
            return proc.checkedRun();
        } catch (UnsupportedEncodingException e) {
            throw new MendeleyException(e.getMessage());
        }
    }

    @Override
    public DocumentList getTrashedDocuments() throws MendeleyException {
        return getTrashedDocuments((DocumentRequestParameters) null);
    }

    @Override
    public DocumentList getTrashedDocuments(Page next) throws MendeleyException {
        if (!Page.isValidPage(next)) {
            throw new NoMorePagesException();
        }
        Procedure<DocumentList> proc = new GetDocumentsProcedure(next.link, authenticationManager);
        return proc.checkedRun();
    }

    @Override
    public void restoreDocument(String documentId) throws MendeleyException {
        String url = TrashNetworkProvider.getRecoverUrl(documentId);
        Procedure<Void> proc = new PostNoBodyNetworkProcedure(url, authenticationManager);
        proc.checkedRun();
    }

    /**
     * Internal use only.
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected RequestHandle run(Command command) {
        if (authenticationManager == null || !authenticationManager.isSignedIn()) {
            // Must call signIn first - caller error!
            throw new NotSignedInException();
        }
        if (authenticationManager.willExpireSoon()) {
            return authenticationManager.refreshToken(command);
        } else {
            /*
                If the expiry time check above hasn't worked for some reason, and the server
                believes the credentials have expired anyway, we will get 401 Unauthorized,
                message "Could not access resource because: Token has expired".
                Currently we do not catch this.
                In future we intend to do so, refresh the token and run the command.
                The 401 error code and message is under review by the Platform team.
            */
            return command.exec();
        }
    }
}

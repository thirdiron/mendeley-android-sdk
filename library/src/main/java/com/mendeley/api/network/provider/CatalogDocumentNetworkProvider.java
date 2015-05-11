package com.mendeley.api.network.provider;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.document.DocumentList;
import com.mendeley.api.model.Document;
import com.mendeley.api.network.Environment;
import com.mendeley.api.network.JsonParser;
import com.mendeley.api.network.procedure.GetNetworkProcedure;
import com.mendeley.api.network.procedure.Procedure;
import com.mendeley.api.params.CatalogDocumentRequestParameters;
import com.mendeley.api.params.View;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import static com.mendeley.api.network.NetworkUtils.API_URL;

/**
 * NetworkProvider class for Documents API calls
 */
public class CatalogDocumentNetworkProvider {
    public static String CATALOG_BASE_URL = API_URL + "catalog";

    public static SimpleDateFormat patchDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT' Z");

    private final Environment environment;
    private final AccessTokenProvider accessTokenProvider;

    public CatalogDocumentNetworkProvider(Environment environment, AccessTokenProvider accessTokenProvider) {
        this.environment = environment;
        this.accessTokenProvider = accessTokenProvider;
    }

    public static String getGetCatalogDocumentUrl(String catalogId, View view) {
        StringBuilder url = new StringBuilder();
        url.append(CATALOG_BASE_URL);
        url.append("/").append(catalogId);

        if (view != null) {
            url.append("?").append("view=" + view);
        }

        return url.toString();
    }


    /**
     * Building the url for get catalog document
     *
     * @return the url string
     * @throws UnsupportedEncodingException
     */
    public static String getGetCatalogDocumentsUrl(CatalogDocumentRequestParameters params) throws UnsupportedEncodingException {
        return getCatalogGetDocumentsUrl(CATALOG_BASE_URL, params);
    }

    private static String getCatalogGetDocumentsUrl(String baseUrl, CatalogDocumentRequestParameters params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        StringBuilder paramsString = new StringBuilder();

        if (params != null) {
            boolean firstParam = true;
            if (params.view != null) {
                paramsString.append(firstParam ? "?" : "&").append("view=" + params.view);
                firstParam = false;
            }
            if (params.arxiv != null) {
                paramsString.append(firstParam ? "?" : "&").append("arxiv=" + params.arxiv);
                firstParam = false;
            }
            if (params.doi != null) {
                paramsString.append(firstParam ? "?" : "&").append("doi=" + params.doi);
                firstParam = false;
            }
            if (params.isbn != null) {
                paramsString.append(firstParam ? "?" : "&").append("isbn=" + params.isbn);
                firstParam = false;
            }
            if (params.issn != null) {
                paramsString.append(firstParam ? "?" : "&").append("issn=" + params.issn);
                firstParam = false;
            }
            if (params.pmid != null) {
                paramsString.append(firstParam ? "?" : "&").append("pmid=" + params.pmid);
            }
            if (params.scopus != null) {
                paramsString.append(firstParam ? "?" : "&").append("scopus=" + params.scopus);
            }
            if (params.filehash != null) {
                paramsString.append(firstParam ? "?" : "&").append("filehash=" + params.filehash);
            }
        }

        url.append(paramsString.toString());
        return url.toString();
    }

    public static class GetCatalogDocumentsProcedure extends GetNetworkProcedure<DocumentList> {
        public GetCatalogDocumentsProcedure(String url, AuthenticationManager authenticationManager) {
            super(url, "application/vnd.mendeley-document.1+json", authenticationManager);
        }

        @Override
        protected DocumentList processJsonString(String jsonString) throws JSONException {
            return new DocumentList(JsonParser.parseDocumentList(jsonString), next, serverDate);
        }
    }

    public static class GetCatalogDocumentProcedure extends GetNetworkProcedure<Document> {
        public GetCatalogDocumentProcedure(String url, AuthenticationManager authenticationManager) {
            super(url, "application/vnd.mendeley-document.1+json", authenticationManager);
        }

        @Override
        protected Document processJsonString(String jsonString) throws JSONException {
            return JsonParser.parseDocument(jsonString);
        }
    }
}

package com.mendeley.api.network.procedure;

import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.network.procedure.NetworkProcedure;

import org.json.JSONException;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * A NetworkProcedure specialised for making HTTP GET requests.
 */
public abstract class GetNetworkProcedure<ResultType> extends NetworkProcedure<ResultType> {
    private final String url;
    private final String contentType;

    protected GetNetworkProcedure(String url, String contentType,
                                  AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.url = url;
        this.contentType = contentType;
    }

    @Override
    protected int getExpectedResponse() {
        return 200;
    }

    public ResultType run() throws MendeleyException {
        try {
            con = getConnection(url, "GET", authenticationManager);
            con.addRequestProperty("Content-type", contentType);
            con.connect();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                throw new HttpResponseException(responseCode, getErrorMessage(con));
            }

            is = con.getInputStream();
            String jsonString = getJsonString(is);
            return processJsonString(jsonString);
        } catch (IOException | JSONException e) {
            throw new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    protected abstract ResultType processJsonString(String jsonString) throws JSONException;
}

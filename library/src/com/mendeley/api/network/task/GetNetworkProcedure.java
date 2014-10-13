package com.mendeley.api.network.task;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.UserCancelledException;

import org.json.JSONException;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * A NetworkProcedure specialised for making HTTP GET requests.
 */
public abstract class GetNetworkProcedure extends NetworkProcedure {
    @Override
    protected int getExpectedResponse() {
        return 200;
    }

    public void run(String url) throws MendeleyException {
        try {
            con = getConnection(url, "GET", getAccessTokenProvider());
            con.addRequestProperty("Content-type", getContentType());
            con.connect();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                throw new HttpResponseException(responseCode, getErrorMessage(con));
            }

            is = con.getInputStream();
            String jsonString = getJsonString(is);
            processJsonString(jsonString);
        } catch (IOException | JSONException e) {
            throw new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    protected abstract void processJsonString(String jsonString) throws JSONException;

    protected abstract String getContentType();
}

package com.mendeley.api.network;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.UserCancelledException;

import org.json.JSONException;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.*;

/**
 * A NetworkTask specialised for making HTTP GET requests.
 */
public abstract class GetNetworkTask extends NetworkTask {
    @Override
    protected int getExpectedResponse() {
        return 200;
    }

    @Override
    protected MendeleyException doInBackground(String... params) {
        String url = params[0];

        try {
            con = getConnection(url, "GET", getAccessTokenProvider());
            con.addRequestProperty("Content-type", getContentType());
            con.connect();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                return new HttpResponseException(responseCode, getErrorMessage(con));
            }

            if (isCancelled()) {
                return new UserCancelledException();
            }

            is = con.getInputStream();
            String jsonString = getJsonString(is);
            processJsonString(jsonString);
            return null;

        } catch (IOException | JSONException e) {
            return new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    protected abstract void processJsonString(String jsonString) throws JSONException;

    protected abstract String getContentType();
}

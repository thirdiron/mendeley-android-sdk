package com.mendeley.api.network;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;

/**
 * A NetworkTask specialised for making HTTP DELETE requests.
 */
public abstract class DeleteNetworkTask extends NetworkTask {
    @Override
    protected int getExpectedResponse() {
        return 204;
    }

    @Override
    protected MendeleyException doInBackground(String... params) {
        String url = params[0];

        try {
            con = getConnection(url, "DELETE");
            con.connect();

            getResponseHeaders();

            if (con.getResponseCode() != getExpectedResponse()) {
                return new HttpResponseException(getErrorMessage(con));
            } else {
                return null;
            }
        }	catch (IOException e) {
            return new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
    }
}

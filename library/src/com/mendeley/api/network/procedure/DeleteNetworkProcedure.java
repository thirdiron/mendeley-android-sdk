package com.mendeley.api.network.procedure;

import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.network.NetworkUtils;
import com.mendeley.api.network.task.NetworkTask;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getConnection;

/**
 * A NetworkProcedure specialised for making HTTP DELETE requests.
 */
public class DeleteNetworkProcedure extends NetworkProcedure<Void> {
    private final String url;

    public DeleteNetworkProcedure(String url, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.url = url;
    }

    @Override
    protected int getExpectedResponse() {
        return 204;
    }

    @Override
    protected Void run() throws MendeleyException {
        try {
            con = getConnection(url, "DELETE", authenticationManager);
            con.connect();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                throw new HttpResponseException(responseCode, NetworkUtils.getErrorMessage(con));
            }
        }	catch (IOException e) {
            throw new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
        return null;
    }
}

package com.mendeley.api.network.task;

import android.os.AsyncTask;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.params.Page;
import com.mendeley.api.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public abstract class NetworkTask extends AsyncTask<String, Integer, MendeleyException>
            implements RequestHandle {
    protected Page next;
    protected String location;
    protected Date serverDate;

    protected InputStream is = null;
    protected OutputStream os = null;
    protected HttpsURLConnection con = null;

    protected abstract int getExpectedResponse();

    protected abstract AccessTokenProvider getAccessTokenProvider();

    /**
     * Extracts the headers from the given HttpsURLConnection object.
     */
    protected void getResponseHeaders() throws IOException {
        Map<String, List<String>> headersMap = con.getHeaderFields();
        if (headersMap == null) {
            // No headers implies an error, which should be handled based on the HTTP status code;
            // no need to throw another error here.
            return;
        }
        for (String key : headersMap.keySet()) {
            if (key != null) {
                if (key.equals("Date")) {
                    SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss 'GMT'");
                    try {
                        serverDate = simpledateformat.parse(headersMap.get(key).get(0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else if (key.equals("Vary") || key.equals("Content-Type") || key.equals("X-Mendeley-Trace-Id") || key.equals("Connection") || key.equals("Content-Length") || key.equals("Content-Encoding") || key.equals("Mendeley-Count")) {// Unused

                } else if (key.equals("Location")) {
                    location = headersMap.get(key).get(0);

                    List<String> links = headersMap.get(key);
                    String linkString = null;
                    for (String link : links) {
                        try {
                            linkString = link.substring(link.indexOf("<") + 1, link.indexOf(">"));
                        } catch (IndexOutOfBoundsException e) {
                        }
                        if (link.indexOf("next") != -1) {
                            next = new Page(linkString);
                        }
                        // "last" and "prev" links are not used
                    }

                } else if (key.equals("Link")) {
                    List<String> links = headersMap.get(key);
                    String linkString = null;
                    for (String link : links) {
                        try {
                            linkString = link.substring(link.indexOf("<") + 1, link.indexOf(">"));
                        } catch (IndexOutOfBoundsException e) {
                        }
                        if (link.indexOf("next") != -1) {
                            next = new Page(linkString);
                        }
                        // "last" and "prev" links are not used
                    }

                }
            }
        }
    }

    protected void closeConnection() {
        if (con != null) {
            con.disconnect();
        }
        Utils.closeQuietly(is);
        Utils.closeQuietly(os);
    }

    @Override
    protected final void onPostExecute(MendeleyException exception) {
        if (exception == null) {
            onSuccess();
        } else {
            onFailure(exception);
        }
    }

    @Override
    protected void onCancelled(MendeleyException e) {
        if (e == null || !(e instanceof UserCancelledException)) {
            // This is not very neat, but we need to ensure that this reports a UserCancelledException,
            // otherwise the onFailure will be interpreted as an error.
            e = new UserCancelledException();
        }
        onFailure(e);
    }

    @Override
    protected final void onCancelled() {
        onFailure(new UserCancelledException());
    }

    protected void onProgressUpdate(Integer[] progress) {
        super.onProgressUpdate();
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(MendeleyException exception);

    public void cancel() {
        // If the request is cancelled, we simply cancel the AsyncTask.
        cancel(true);
    }
}

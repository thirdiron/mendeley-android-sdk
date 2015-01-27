package com.mendeley.api.network.procedure;

import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.network.procedure.Procedure;
import com.mendeley.api.params.Page;
import com.mendeley.api.util.DateUtils;
import com.mendeley.api.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Base class for all synchronous network calls.
 */
public abstract class NetworkProcedure<ResultType> extends Procedure<ResultType> {
    protected Page next;
    protected String location;
    protected Date serverDate;

    protected InputStream is = null;
    protected OutputStream os = null;
    protected HttpsURLConnection con = null;

    public NetworkProcedure(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected abstract int getExpectedResponse();

    /**
     * Extracts the headers from the given HttpsURLConnection object.
     */
    protected void getResponseHeaders() throws IOException, ParseException {
        Map<String, List<String>> headersMap = con.getHeaderFields();
        if (headersMap == null) {
            // No headers implies an error, which should be handled based on the HTTP status code;
            // no need to throw another error here.
            return;
        }
        for (String key : headersMap.keySet()) {
            if (key != null) {
                switch (key) {
                    case "Date":
                        final String dateHeader = headersMap.get(key).get(0);
                        serverDate = DateUtils.parseDateInHeader(dateHeader);
                        break;
                    case "Vary":
                    case "Content-Type":
                    case "X-Mendeley-Trace-Id":
                    case "Connection":
                    case "Content-Length":
                    case "Content-Encoding":
                    case "Mendeley-Count":
                        // Unused
                        break;
                    case "Location":
                        location = headersMap.get(key).get(0);
                    case "Link":
                        List<String> links = headersMap.get(key);
                        String linkString = null;
                        for (String link : links) {
                            try {
                                linkString = link.substring(link.indexOf("<")+1, link.indexOf(">"));
                            } catch (IndexOutOfBoundsException e) {}
                            if (link.indexOf("next") != -1) {
                                next = new Page(linkString);
                            }
                            // "last" and "prev" links are not used
                        }
                        break;
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
}

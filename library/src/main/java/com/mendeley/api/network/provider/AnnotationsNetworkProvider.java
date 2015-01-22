package com.mendeley.api.network.provider;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.annotations.AnnotationList;
import com.mendeley.api.model.Annotation;
import com.mendeley.api.network.Environment;
import com.mendeley.api.network.JsonParser;
import com.mendeley.api.network.procedure.GetNetworkProcedure;
import com.mendeley.api.network.procedure.PatchNetworkProcedure;
import com.mendeley.api.network.procedure.PostNetworkProcedure;
import com.mendeley.api.params.AnnotationRequestParameters;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.mendeley.api.network.NetworkUtils.API_URL;

/**
 * NetworkProvider for Annotations API calls.
 */
public class AnnotationsNetworkProvider {
    public static String ANNOTATIONS_BASE_URL = API_URL + "annotations";
    private static String CONTENT_TYPE = "application/vnd.mendeley-annotation.1+json";

    private final Environment environment;
    private final AccessTokenProvider accessTokenProvider;

    public AnnotationsNetworkProvider(Environment environment, AccessTokenProvider accessTokenProvider) {
        this.environment = environment;
        this.accessTokenProvider = accessTokenProvider;
    }

    /* URLS */

    public static String deleteAnnotationUrl(String documentId) {
        return ANNOTATIONS_BASE_URL + "/" + documentId;
    }

    public static String getAnnotationUrl(String documentId) {
        return ANNOTATIONS_BASE_URL + "/" + documentId;
    }

	public static String getAnnotationsUrl(AnnotationRequestParameters params) throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		url.append(ANNOTATIONS_BASE_URL);

		if (params != null) {
            StringBuilder paramsString = new StringBuilder();
			boolean firstParam = true;
            if (params.documentId != null) {
                paramsString.append(firstParam ? "?" : "&").append("document_id=" + params.documentId);
                firstParam = false;
            }
			if (params.groupId != null) {
				paramsString.append(firstParam ? "?" : "&").append("group_id=" + params.groupId);
				firstParam = false;
			}
            if (params.includeTrashed != null) {
                paramsString.append(firstParam ? "?" : "&").append("include_trashed=" + params.includeTrashed);
                firstParam = false;
            }
			if (params.modifiedSince != null) {
				paramsString.append(firstParam ? "?" : "&").append("modified_since="
                        + URLEncoder.encode(params.modifiedSince, "ISO-8859-1"));
				firstParam = false;
			}
            if (params.deletedSince != null) {
                paramsString.append(firstParam ? "?" : "&").append("deleted_since="
                        + URLEncoder.encode(params.deletedSince, "ISO-8859-1"));
                firstParam = false;
            }
			if (params.limit != null) {
				paramsString.append(firstParam ? "?" : "&").append("limit=" + params.limit);
				firstParam = false;
			}
            url.append(paramsString.toString());
		}
		
		return url.toString();
	}

    /* PROCEDURES */

    public static class GetAnnotationProcedure extends GetNetworkProcedure<Annotation> {
        public GetAnnotationProcedure(String url, AuthenticationManager authenticationManager) {
            super(url, CONTENT_TYPE, authenticationManager);
        }

        @Override
        protected Annotation processJsonString(String jsonString) throws JSONException {
            return JsonParser.parseAnnotation(jsonString);
        }
    }

    public static class GetAnnotationsProcedure extends GetNetworkProcedure<AnnotationList> {
        public GetAnnotationsProcedure(String url, AuthenticationManager authenticationManager) {
            super(url, CONTENT_TYPE, authenticationManager);
        }

        @Override
        protected AnnotationList processJsonString(String jsonString) throws JSONException {
            return new AnnotationList(JsonParser.parseAnnotationList(jsonString), next, serverDate);
        }
   }

    public static class PostAnnotationProcedure extends PostNetworkProcedure<Annotation> {
        public PostAnnotationProcedure(Annotation annotation, AuthenticationManager authenticationManager) throws JSONException {
            super(ANNOTATIONS_BASE_URL, CONTENT_TYPE,
                    JsonParser.jsonFromAnnotation(annotation), authenticationManager);
        }

        @Override
        protected Annotation processJsonString(String jsonString) throws JSONException {
            return JsonParser.parseAnnotation(jsonString);
        }
    }

    public static class PatchAnnotationProcedure extends PatchNetworkProcedure {
        public PatchAnnotationProcedure(String annotationId, String json, AuthenticationManager authenticationManager) {
            super(getUrl(annotationId), CONTENT_TYPE, json, null, authenticationManager);
        }

        private static String getUrl(String annotationId) {
            return ANNOTATIONS_BASE_URL + "/" + annotationId;
        }
    }
}

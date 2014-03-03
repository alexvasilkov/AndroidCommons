package com.azcltd.fluffycommons.network;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

public class RequestsBuilder {

    private final RequestsHelper requestsHelper;

    private HttpURLConnection connection;
    private Gson gson;
    private RequestsHelper.ResponseParser<Void> errorChecker;

    RequestsBuilder(RequestsHelper helper) {
        this.requestsHelper = helper;
    }

    /**
     * Creates builder using HttpURLConnection
     */
    public RequestsBuilder from(HttpURLConnection connection) {
        this.connection = connection;
        return this;
    }

    /**
     * Creates builder using plain url
     */
    public RequestsBuilder from(String url) throws IOException {
        return from(requestsHelper.openConnection(url));
    }

    /**
     * Sets custom Gson object to parse json from response
     */
    public RequestsBuilder useGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    private void ensureGsonProvided() {
        if (gson == null) gson = new Gson();
    }

    /**
     * Sets error checker parser. This parser should throw exception if repsponse error is detected.
     */
    public RequestsBuilder checkError(RequestsHelper.ResponseParser<Void> errorChecker) {
        this.errorChecker = errorChecker;
        return this;
    }

    /**
     * Sets json error checker parser. Json class should implement ErrorResponse interface and provide checkError()
     * method that should throw appropriate exception.
     */
    public RequestsBuilder checkJsonError(final Class<? extends ErrorResponse> errorType) {
        checkError(new RequestsHelper.ResponseParser<Void>() {
            @Override
            public Void parse(InputStream response, int responseCode) throws IOException {
                ensureGsonProvided();
                ErrorResponse errorResponse = null;
                try {
                    errorResponse = gson.fromJson(new InputStreamReader(response, "UTF-8"), errorType);
                } catch (Exception ignored) {
                    // no error if json cannot be parsed
                }
                if (errorResponse != null) errorResponse.checkError();
                return null;
            }
        });
        return this;
    }

    /**
     * Performs actual request and parses reponse as json object
     */
    public <T> T getAsJson(Class<T> responseClass) throws IOException {
        return getAsJson((Type) responseClass);
    }

    /**
     * Performs actual request and parses reponse as json object.
     */
    public <T> T getAsJson(final Type responseType) throws IOException {
        ensureGsonProvided();
        if (responseType == null) throw new NullPointerException("Response type was not specified");

        return get(new RequestsHelper.ResponseParser<T>() {
            @Override
            public T parse(InputStream response, int responseCode) throws IOException {
                Reader reader;
                if (errorChecker == null) {
                    reader = new InputStreamReader(response, "UTF-8");
                } else {
                    String responseStr = RequestsHelper.readToString(response);
                    InputStream in = new ByteArrayInputStream(responseStr.getBytes("UTF-8"));
                    errorChecker.parse(in, responseCode);
                    reader = new StringReader(responseStr);
                }

                RequestsHelper.checkHttpCode(responseCode);

                return gson.fromJson(reader, responseType);
            }
        });
    }

    /**
     * Performs actual request and parses reponse as plain string
     */
    public String getAsString() throws IOException {
        return get(new RequestsHelper.ResponseParser<String>() {
            @Override
            public String parse(InputStream response, int responseCode) throws IOException {
                RequestsHelper.checkHttpCode(responseCode);
                return RequestsHelper.readToString(response);
            }
        });
    }

    /**
     * Performs actual request using custom parser
     */
    public <T> T get(RequestsHelper.ResponseParser<T> parser) throws IOException {
        if (connection == null)
            throw new NullPointerException("HttpUrlConnection was not set using one of from(...) methods");
        if (parser == null)
            throw new NullPointerException("Reponse parser was not set");

        return requestsHelper.process(connection, parser);
    }

    public interface ErrorResponse {
        void checkError() throws IOException;
    }

}

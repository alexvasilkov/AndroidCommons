package com.azcltd.fluffycommons.network;

import android.util.Base64;
import android.util.Log;
import com.squareup.okhttp.OkHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to load and parse data from web services.<br/>
 * It is better to create and statically store instance of this class somewhere.<br/>
 * You can use {@link #builder()} method to perform actual requests.
 */
public class RequestsHelper {

    private String mTag = RequestsHelper.class.getSimpleName();
    private boolean mIsDebug;
    private final OkHttpClient mHttpClient;

    public RequestsHelper() {
        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
    }

    public void setDebug(boolean isDebug, String tagPreffix) {
        mTag = tagPreffix + ".HTTP";
        mIsDebug = isDebug;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public HttpURLConnection openConnection(String url) throws IOException {
        return mHttpClient.open(new URL(url));
    }

    public <T> T process(HttpURLConnection connection, ResponseParser<T> parser) throws IOException {
        if (mIsDebug) Log.d(mTag, "REQUEST: " + connection.getURL());

        InputStream origIn = null;

        try {
            int code = connection.getResponseCode();

            if (code / 100 == 4 || code / 100 == 5) {
                origIn = connection.getErrorStream();
            } else {
                origIn = connection.getInputStream();
            }

            InputStream in = origIn;

            if (mIsDebug) {
                String respStr = readToString(origIn);
                Log.d(mTag, "RESPONSE: " + connection.getURL() + "\n" + respStr);
                in = new ByteArrayInputStream(respStr.getBytes("UTF-8"));
            }

            return parser.parse(in, code);
        } finally {
            if (origIn != null)
                try {
                    origIn.close();
                } catch (Exception ignored) {
                }
        }
    }

    public RequestsBuilder builder() {
        return new RequestsBuilder(this);
    }

    public static void setBasicAuth(HttpURLConnection connection, String username, String password) throws IOException {
        String creds = URLEncoder.encode(username, "UTF-8") + ':' + URLEncoder.encode(password, "UTF-8");
        String encoded = new String(Base64.encode(creds.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8");
        connection.setRequestProperty("Authorization", "Basic " + encoded);
    }

    public static void writeBody(HttpURLConnection connection, String body) throws IOException {
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
            // Write the request.
            writer = new OutputStreamWriter(out = connection.getOutputStream(), "UTF-8");
            writer.write(body);
        } finally {
            if (writer != null) writer.close();
            if (out != null) out.close();
        }
    }

    public static void checkHttpCode(int responseCode) throws IOException {
        if (responseCode / 100 == 4 || responseCode / 100 == 5)
            throw new IOException("Error http response code: " + responseCode);
    }

    public static String readToString(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int c;
        byte[] data = new byte[4096];

        while ((c = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, c);
        }

        buffer.flush();

        return new String(buffer.toByteArray(), "UTF-8");
    }

    public interface ResponseParser<T> {
        T parse(InputStream response, int responseCode) throws IOException;
    }

}

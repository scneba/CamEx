package com.thestk.camex.web;

/**
 * Created by NEBA on 06-Aug-16.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class RequestHttp {

    private URL url;
    private HttpURLConnection urlConnection;
    private OutputStream outputStream;
    /**
     * Default constructor
     */
    public RequestHttp() {

    }

    /**
     * @param url         {@link String}
     * @param method      {@link Method}
     * @param conTimeout  int
     * @param readTimeout int
     * @return RequestHttp
     * @throws IOException
     */
    public RequestHttp setUP(String url, Method method, int conTimeout, int readTimeout) throws IOException {
        this.url = new URL(url);
        urlConnection = (HttpURLConnection) this.url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod(method.name());
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(conTimeout);
        urlConnection.setReadTimeout(readTimeout);
        if (method == Method.POST || method == Method.PUT) {
            urlConnection.setDoOutput(true);
            outputStream = urlConnection.getOutputStream();
        }
        return this;

    }

    /**
     * Writes query to open stream to server
     *
     * @param query String params in format of key1=v1&key2=v2 to open stream to server
     * @return HttpRequest this instance -> for chaining method @see line 22
     * @throws IOException - should be checked by caller
     */
    public RequestHttp withData(String query) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.write(query);
        writer.close();
        return this;
    }

    /**
     * Builds query on format of key1=v1&key2=v2 from given hashMap structure
     * for map: {name=Bubu, age=29} -> builds "name=Bubu&age=29"
     * for map: {Iam=Groot} -> builds "Iam=Groot"
     *
     * @param params HashMap consists of key-> value pairs to build query from
     * @return HttpRequest this instance -> for chaining method @see line 22
     * @throws IOException - should be checked by caller
     */
    public RequestHttp withData(HashMap<String, String> params) throws IOException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append((result.length() > 0 ? "&" : "") + entry.getKey() + "=" + entry.getValue());//appends: key=value (for first param) OR &key=value(second and more)
        }
        withData(result.toString());
        return this;
    }

    /**
     * Sending request to the server and pass to caller String as it received in response from server
     *
     * @return String printed from server's response
     * @throws IOException - should be checked by caller
     */
    public String sendAndReadString() throws IOException {
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());

        //handle url redirectsn
        if (!url.getHost().equals(urlConnection.getURL().getHost())) {
            throw new UnknownHostException("Url Redirect");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        for (String line; (line = br.readLine()) != null; ) response.append(line + "\n");
        Log.e("response", response.toString());
        return response.toString();
    }

    /**
     * @return JSONObject
     * @throws JSONException
     * @throws IOException
     */
    public JSONObject sendAndReadJSON() throws JSONException, IOException {
        return new JSONObject(sendAndReadString());
    }

    /**
     * @return JSONArray
     * @throws JSONException
     * @throws IOException
     */
    public JSONArray sendAndReadJSONArray() throws JSONException, IOException {
        return new JSONArray(sendAndReadString());
    }

    /**
     * Get response code from server
     *
     * @return
     * @throws IOException
     */
    public int getResponseCode() throws IOException {
        return urlConnection.getResponseCode(); //get response code in order to make decisions
    }

    /**
     * Get the response message from server
     *
     * @return String
     * @throws IOException
     */
    public String getResponseMessage() throws IOException {
        return urlConnection.getResponseMessage();
    }

    //supported request types
    public static enum Method {
        POST, GET, PUT, DELETE
    }

}

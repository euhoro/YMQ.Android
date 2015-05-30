package com.ymarq.eu.utilities;
/**
 * Created by eu on 11/25/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.ymarq.eu.ymarq.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by eu on 11/25/2014.
 */
public class UrlHelper {

    public static String LOGGER_TAG = "URLREQ:";
    public static boolean IsFailed = false;


    public static String GetFullUrl(Resources resources, String controller , Context activity)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String key = resources.getString(R.string.pref_baseurl_key);
        String def = resources.getString(R.string.pref_baseurl_default);

        String freeTxt =  prefs.getString(key, def);
        if( freeTxt == "")
        {
            key = resources.getString(R.string.pref_server_key);
            def = resources.getString(R.string.pref_server_azure);
            freeTxt = prefs.getString(key,def);
        }

        return freeTxt+controller;
    }


    public static String GetPhoneId4(Context activity)
    {
        String android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return  android_id;
        //BigInteger phoneid = new BigInteger(android_id, 16);
        //return phoneid;
        //return new BigInteger("8545332518168005054");
        //return new BigInteger("1111");
    }

    public static String uploadFileToServer(String targetUrl, String filename, String parameters) {

        targetUrl = targetUrl + parameters;
        return uploadFileToServer(targetUrl, filename);
    }

    public static String uploadFileToServer(String targetUrl, String filename) {
        String response = "error";
        Log.e("Image filename", filename);
        Log.e("url", targetUrl);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String pathToOurFile = filename;
        String urlServer = targetUrl;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String connstr = null;
            connstr = "Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                    + pathToOurFile + "\"" + lineEnd;
            Log.i("Connstr", connstr);

            outputStream.writeBytes(connstr);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.e("Image length", bytesAvailable + "");
            try {
                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        response = "outofmemoryerror";
                        return response;
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
                return response;
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i("Server Response Code ", "" + serverResponseCode);
            Log.i("Server Response Message", serverResponseMessage);

            if (serverResponseCode == 200) {
                response = "true";
            }

            String CDate = null;
            Date serverTime = new Date(connection.getDate());
            try {
                CDate = df.format(serverTime);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Date Exception", e.getMessage() + " Parse Exception");
            }
            Log.i("Server Response Time", CDate + "");

            filename = CDate
                    + filename.substring(filename.lastIndexOf("."),
                    filename.length());
            Log.i("File Name in Server : ", filename);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception ex) {
            // Exception handling
            response = "error";
            Log.e("Send file Exception", ex.getMessage() + "");
            ex.printStackTrace();
        }
        return response;
    }

    public static String requestPostUrlInline(String url, String postParameters) {
        String reqResult = null;
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        //disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);

            // handle POST parameters
            if (postParameters != null) {

                if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
                    //Log.i(LOGGER_TAG, "POST parameters: " + postParameters);
                }

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(
                        postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                //send the POST out
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
            }

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                //Log.i(LOGGER_TAG, "POST parameters: " + postParameters);
                // throw some exception
            }

            // read output (only for GET)
            if (postParameters != null) {
                return null;
            } else {
                //eugen
                //InputStream in =
                //new BufferedInputStream(urlConnection.getInputStream());
                //return getResponseText(in);


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                reqResult = buffer.toString();
                Log.i(LOGGER_TAG, "URLResult: " + reqResult);
                return reqResult;
            }


        } catch (MalformedURLException e) {
            Log.i(LOGGER_TAG, "EX2 MalformedURLException");
        } catch (SocketTimeoutException e) {
            Log.i(LOGGER_TAG, "EX2 SocketTimeoutException");
        } catch (IOException e) {
            Log.i(LOGGER_TAG, "EX2 IOException");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    public static String postJsonDataEx(String path, Map params) {
        String res = null;
        try {
            res = postJsonData(path, params);
        } catch (Exception ex) {
            return null;
        }
        return res;
    }

    public static String postJsonDataObj(String path, Object obj)  throws Exception
    {
        String a  = "srere";
       Map<String, Object> introspected=getMap(obj);
        return postJsonData(path,introspected);
        //0001-01-01T00:00:00
    }

    public static String postJsonDataObjString(String path, String objString)  throws Exception
    {
        JSONObject obj = new JSONObject(objString);
        Map<String, Object> introspected=jsonToMap(obj);
        return postJsonData(path,introspected);
    }


    public static Map jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }








    public static Map<String, Object> getMap(Object o) throws IllegalAccessException {
        Map<String, Object> result = new HashMap<String, Object>();
        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.get(o) != null)//eugen do not export nulls
                result.put(field.getName(), field.get(o));
        }
        return result;
    }

    public static String postJsonData(String path, Map params) throws Exception {
        boolean connectionError = false;
        InputStream inputStream = null;
        String result = "";
        try {
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();

            //url with the post data
            HttpPost httpost = new HttpPost(path);

            //convert parameters into JSON object
            //JSONObject holder = getJsonObjectFromMap(params);
            JSONObject holder = new JSONObject(params);


            //passes the results to a string builder/entity //hebrew
            StringEntity se = new StringEntity(holder.toString(),"UTF-8");

            //sets the post request as the resulting string
            httpost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it
            httpost.setHeader("Accept", "application/json; charset=UTF-8");
            httpost.setHeader("Content-type", "application/json");

            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();


            HttpParams httpParameters = new BasicHttpParams();

            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 7000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 7000; //todo check this again - it works faster on g1 and it should be much faster ( old val = 5000)
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            httpclient.setParams(httpParameters);


            //Object o = httpclient.execute(httpost, responseHandler);

            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        }
        catch (ConnectTimeoutException e)
        {
            //probably the url it is not correct
            connectionError = true;
            Log.e(LOGGER_TAG,e.toString());
            result = e.toString();
        }
        catch (Exception e)
        {
            connectionError = true;
           Log.e(LOGGER_TAG,e.toString());
            result = e.toString();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                connectionError = true;
                Log.e(LOGGER_TAG,squish.toString());
                result = squish.toString();
            }
        }
        return result;
    }

    private String getServerUrl2(String userCode) {
        //http://localhost/home/Login?userId=111111111
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("localhost")
                .appendPath("home")
                .appendPath("Login")
                .appendQueryParameter("userId", userCode);
        String myUrl = builder.build().toString();
        Log.i("LOG_TAG", "BUILD_URL: " + myUrl);
        return myUrl;
    }

}


package com.ymarq.eu;

import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.entities.DataExeption2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eu on 3/3/2015.
 */
//from http://stackoverflow.com/questions/5678478/how-to-get-date-time-stamp-in-android
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    private String uniqueId;

    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler(String localPath, String url,String userUnique) {
        this.localPath = localPath;
        this.url = url;
        this.uniqueId = userUnique;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String timestamp = s.format(new Date());
        //String timestamp = TimeStampFormatter.getInstance().getTimestamp();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
        if (url != null) {
            sendToServer(stacktrace, filename);
        }

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(final String stacktrace, final String filename) {
        //DefaultHttpClient httpClient = new DefaultHttpClient();
        //HttpPost httpPost = new HttpPost(url);
        //List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //nvps.add(new BasicNameValuePair("userId", uniqueId));
        //nvps.add(new BasicNameValuePair("fileName", filename));
        //nvps.add(new BasicNameValuePair("stackTrace", stacktrace));
        //try {
        //    httpPost.setEntity(
        //            new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        //    httpClient.execute(httpPost);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        DataExeption2 de = new DataExeption2() {{
            setStackTrace(stacktrace);
            setFileName(filename);
            setUserId(uniqueId);
        }};

        try {
            CloudEngine.getInstance().UploadError(
                  de.getAsJSON() , true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
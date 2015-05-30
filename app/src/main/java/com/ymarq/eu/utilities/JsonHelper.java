package com.ymarq.eu.utilities;

import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eu on 11/28/2014.
 */
public class JsonHelper {
    public static String[] getProductDataFromJson(String jSonResultString, int numDays)
            throws JSONException {

        //[{"Description":"Suzuki Swift","Hashtag":"Nice car","Id":"e7b6646b-4718-4abf-8260-73188d395c30","Image":"","PublisherId":""}]
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_DESCRIPTION = "Description";
        final String OWM_HASHTAG = "Hashtag";
        final String OWM_ID = "Id";
        final String OWM_IMAGE = "Image";

        final String OWM_LIST = "";

        //JSONObject listJson = new JSONObject(productList);
        //JSONArray productArray = listJson.getJSONArray(OWM_LIST);
        JSONArray productArray = new JSONArray(jSonResultString);

        String[] resultStrs = new String[productArray.length()];
        for (int i = 0; i < productArray.length(); i++) {

            String description;
            String hashtag;
            String productId;

            // Get the JSON object representing the day
            JSONObject product = productArray.getJSONObject(i);

            description = product.getString(OWM_DESCRIPTION);
            hashtag = product.getString(OWM_HASHTAG);
            productId = product.getString(OWM_ID);


            //resultStrs[i] = description + " - " + hashtag;
            resultStrs[i] = productId;
        }
        return resultStrs;
    }

    public static List<DataProduct> getProductDataFromJson(String jSonResultString)
            throws JSONException {

        //[{"Description":"Suzuki Swift","Hashtag":"Nice car","Id":"e7b6646b-4718-4abf-8260-73188d395c30","Image":"","PublisherId":""}]
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_DESCRIPTION = "Description";
        final String OWM_HASHTAG = "Hashtag";
        final String OWM_ID = "Id";
        final String OWM_IMAGE = "Image";
        final String OWM_UserId = "UserId";

        final String OWM_LIST = "";

        //JSONObject listJson = new JSONObject(productList);
        //JSONArray productArray = listJson.getJSONArray(OWM_LIST);
        JSONArray productArray = new JSONArray(jSonResultString);

        List<DataProduct> resultStrs = new ArrayList<DataProduct>();
        for (int i = 0; i < productArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject product = productArray.getJSONObject(i);

            DataProduct p = new DataProduct();
            p.UserId =product.getString(OWM_UserId);
            p.Description = product.getString(OWM_DESCRIPTION);
            p.Hashtag = product.getString(OWM_HASHTAG);
            p.Id = product.getString(OWM_ID);
            p.Image = product.getString(OWM_IMAGE);
            resultStrs.add(p);
        }
        return resultStrs;
    }

    public static List<DataMessage> getMessageDataFromJson4(String jSonResultString)
            throws JSONException {

        //[{"Description":"Suzuki Swift","Hashtag":"Nice car","Id":"e7b6646b-4718-4abf-8260-73188d395c30","Image":"","PublisherId":""}]
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_SenderId = "SenderId";
        final String OWM_Content = "Content";
        final String OWM_ID = "Id";
        final String OWM_ProductId = "ProductId";

        final String OWM_LIST = "";

        //JSONObject listJson = new JSONObject(productList);
        //JSONArray productArray = listJson.getJSONArray(OWM_LIST);
        JSONArray messageArray = new JSONArray(jSonResultString);

        List<DataMessage> resultStrs = new ArrayList<DataMessage>();
        for (int i = 0; i < messageArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject product = messageArray.getJSONObject(i);

            DataMessage dm = new DataMessage(product.getString(OWM_Content),product.getString(OWM_SenderId),product.getString(OWM_ID));

            resultStrs.add(dm);
        }
        return resultStrs;
    }


    public static List<DataSubscription>  getSubscriptionsDataFromJson2(String jSonResultString)
            throws JSONException {

        //[{"Description":"Suzuki Swift","Hashtag":"Nice car","Id":"e7b6646b-4718-4abf-8260-73188d395c30","Image":"","PublisherId":""}]
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_SenderId = "UserId";
        final String OWM_Content = "SearchText";
        final String OWM_ProductId = "Id";

        final String OWM_LIST = "";

        //JSONObject listJson = new JSONObject(productList);
        //JSONArray productArray = listJson.getJSONArray(OWM_LIST);
        JSONArray messageArray = new JSONArray(jSonResultString);

        List<DataSubscription> resultStrs = new ArrayList<DataSubscription>();
        for (int i = 0; i < messageArray.length(); i++) {

            String SenderId;
            String Content;
            String id;

            // Get the JSON object representing the day
            JSONObject product = messageArray.getJSONObject(i);

            SenderId = product.getString(OWM_SenderId);
            Content = product.getString(OWM_Content);
            id = product.getString(OWM_ProductId);


            //resultStrs[i] = description + " - " + hashtag;
            DataSubscription d = new DataSubscription(Content,SenderId);

            resultStrs.add(d);
        }
        return resultStrs;
    }

}

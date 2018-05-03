package pkg.android.skynet.yourcompanion.asynctask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pkg.android.skynet.yourcompanion.app.Constants;
import pkg.android.skynet.yourcompanion.app.YourCompanion;
import pkg.android.skynet.yourcompanion.callbacks.OnTaskComplete;
import pkg.android.skynet.yourcompanion.custom.CustomProgressDialog;

/**
 * Created by as on 5/6/2016.
 */
public class YourCompanionAsyncTask extends AsyncTask<String, Void, String> {

    private boolean isProgressDialogShow = false, isMultiPart = false;
    private String URL = "";
    private int postMethod;
    private final int CONNECTION_TIMEOUT = 15000;

    private HashMap<String, Object> requestParameter;

    private HttpResponse response;
    private OnTaskComplete onTaskComplete;
    private CustomProgressDialog mProgressDialog = null;
    private YourCompanion context;
    Context activityContext;

    public YourCompanionAsyncTask(Context context, OnTaskComplete onTaskComplete, String URL, HashMap<String, Object> requestParameter, int postMethod, boolean isProgressDialogShow) {
        this.context = (YourCompanion)context.getApplicationContext();
        this.activityContext = context;
        this.onTaskComplete = onTaskComplete;
        this.URL = URL;
        this.requestParameter = requestParameter;
        this.postMethod = postMethod;
        this.isProgressDialogShow = isProgressDialogShow;

        if (isProgressDialogShow) mProgressDialog = new CustomProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isProgressDialogShow) mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String responseString = null;
        InputStream is = null;

        try {
            if (postMethod == Constants.MEHTOD_GET) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(params[0]);
                response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                responseString = convertStreamToString(is);

            }else if (postMethod == Constants.MEHTOD_POST){
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                List<NameValuePair> nameValuePairs  = new ArrayList<NameValuePair>();

                System.out.println("URL ->"+params[0]);

                if (isMultiPart) {

                }else {
                    nameValuePairs = getPOSTEntity();

                    for (int i = 0; i < nameValuePairs.size(); i++) {
                        if (nameValuePairs.get(i).getName().contains("userfile")) {
                            if (!nameValuePairs.get(i).getValue().equals(""))
                                mpEntity.addPart(nameValuePairs.get(i).getName(), new FileBody(new File(nameValuePairs.get(i).getValue())));
                            else mpEntity.addPart(nameValuePairs.get(i).getName(), new StringBody(nameValuePairs.get(i).getValue()));
                        }else {
                            mpEntity.addPart(nameValuePairs.get(i).getName(), new StringBody(nameValuePairs.get(i).getValue()));
                        }
                    }

                    /*if (multipartNameValuePairs.size() > 0){
                        mpEntity.addPart(multipartNameValuePairs.get(0).getName(), new FileBody(new File(multipartNameValuePairs.get(0).getValue())));
                        System.out.println("Send As File ->" + mpEntity.getContentLength());
                        post.setEntity(mpEntity);
                    }*/
                    post.setEntity(mpEntity);
                }
                response = client.execute(post);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                responseString = convertStreamToString(is);

            }else if (postMethod == Constants.MEHTOD_JSON) {
                JSONObject requestObject = new JSONObject();
                String requestString = "";
                String inputjson = "";

                for (Map.Entry<String, Object> element : requestParameter.entrySet()) {
                    requestObject.accumulate(element.getKey(), element.getValue().toString());
                    requestString += URLEncoder.encode(element.getKey(), "UTF-8")
                            + "=" + URLEncoder.encode(element.getValue().toString(), "UTF-8")
                            + "&";

                /*Log.d(element.getKey().toString() + ": ", element.getValue().toString());*/
                }
                inputjson = requestObject.toString();
                Log.d("url: ", params[0]);
                Log.d("json input: ", inputjson);

                java.net.URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(CONNECTION_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(inputjson);
                writer.close();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    responseString = stringBuilder.toString();
                    Log.d("Json output: ", responseString);

                } finally {
                    conn.disconnect();
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        System.out.println("PROGRESS ->" + values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mProgressDialog != null) mProgressDialog.dismiss();

        if (context.isNetworkAvailable()) {
            if(response!=null && response.getStatusLine().getStatusCode() == 200){
                onTaskComplete.onTaskComplete(response.getStatusLine().getStatusCode(), Constants.SUCCESS, s, URL);
            }else{
                onTaskComplete.onTaskComplete(501, Constants.FAILURE, s, URL);
            }
        }else {
            if (!Constants.isAlertDialogShown)
                context.showAlertDialog((Activity)activityContext,"No Network", context.getString(android.R.string.ok));
        }


    }

    private List<NameValuePair> getPOSTEntity(){
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object o : requestParameter.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println("Request Parameter " + pair.getKey() + " -> " + pair.getValue());

            if (pair.getValue() != null) {
                pairs.add(new BasicNameValuePair(pair.getKey().toString(), pair.getValue().toString()));
            } else {
                pairs.add(new BasicNameValuePair(pair.getKey().toString(), ""));
            }
        }
        return  pairs;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

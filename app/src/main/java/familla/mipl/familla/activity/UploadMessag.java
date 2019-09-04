package familla.mipl.familla.activity;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by software6 on 05/04/2016.
 */
public class UploadMessag extends AsyncTask {

    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    HttpClient httpClient;
    HttpPost httpPost;
    String content, senderid, groupid, receipient;

    public UploadMessag(String content, String senderid, String groupid, String receipient) {
        this.content = content;
        this.senderid = senderid;
        this.groupid = groupid;
        this.receipient = receipient;
    }

    @Override
    protected Void doInBackground(Object[] params) {

        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost("http://thefamilla.com/android/upMessage.php");

        nameValuePair.add(new BasicNameValuePair("content", content));
        nameValuePair.add(new BasicNameValuePair("senderid", senderid));
        nameValuePair.add(new BasicNameValuePair("groupid", groupid));
        nameValuePair.add(new BasicNameValuePair("receipient", receipient));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            Log.e("result_medha", result);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}

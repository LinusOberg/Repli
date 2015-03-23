package servercalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import huka.com.repli.LoginActivity;
import huka.com.repli.R;

/**
 * Created by hugo on 3/19/15.
 */
public class UploadProfilePicAsyncTask extends  AsyncTask<File, Void, String> {
    private static UserInfoApi regService = null;
    private Context context;

    public UploadProfilePicAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(File... params) {
        File imageFile = params[0];
        String url = "";
        if (regService == null) {
            buildService();
            try {
                UserInfo userInfo = regService.getUploadUrl().execute();
                HttpResponse response = uploadImage(userInfo.getProfilePictureUrl(), imageFile);
                url = saveToDB(response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private HttpResponse uploadImage(String profilePictureUrl, File imageFile) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        System.out.printf("url ::: " + profilePictureUrl);
        HttpPost httppost = new HttpPost(profilePictureUrl);
        FileBody fileBody  = new FileBody(imageFile.getAbsoluteFile());
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        reqEntity.addPart("file", fileBody);
        httppost.setEntity(reqEntity);

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        httpclient.getConnectionManager().shutdown();
        return response;
    }

    private String saveToDB(HttpResponse response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        String profilePictureUrl = jsonObject.getString("servingUrl");
        String accountName = getAccountName();
        UserInfo userInfo = new UserInfo();
        userInfo.setAccountName(accountName);
        userInfo.setProfilePictureUrl(profilePictureUrl);
        UserInfo userInfoRes = regService.addProfilePicture(userInfo).execute();
        String url = userInfoRes.getProfilePictureUrl();
        System.out.println("URL: " + url);
        return url;
    }

    private String getAccountName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "none");
        return username;
    }

    private void buildService() {
        UserInfoApi.Builder builder = new UserInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null) //.setRootUrl("https://repliapp.appspot.com/_ah/api/");
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setRootUrl(LoginActivity.LOCALHOST_IP)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        regService = builder.build();
    }

    @Override
    protected void onPostExecute(String url) {
        if(!url.equals("")) {
            Toast.makeText(context, "Profile picture changed", Toast.LENGTH_LONG).show();
        }
    }
}

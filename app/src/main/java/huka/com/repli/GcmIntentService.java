package huka.com.repli;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import adapters.MyRecyclerCameraAdapter;
import servercalls.UploadPicToRandomAsyncTask;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                Intent cameraIntent = new Intent("unique_name");

                //put whatever data you want to send, if any
                cameraIntent.putExtra("message", extras.getString("message"));
                System.out.println("send brodcast");
                System.out.println("Blobkey from gcm: " + extras.getString("message"));
                //send broadcast
                this.getApplication().sendBroadcast(cameraIntent);

                //Toast.makeText(getApplicationContext(), extras.getString("message"), Toast.LENGTH_LONG).show();


//                String accountName = intent.getStringExtra("accountName");
//                Logger.getLogger("accountname received").log(Level.INFO, accountName);

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {

        // WeakReference<MyAsyncTask> asyncTaskWeakRef = new WeakReference<>(asyncTask);
     //   asyncTask.execute(message);
//
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
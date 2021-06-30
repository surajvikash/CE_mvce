package in.cricketexchange.app.cricketexchange.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.text.HtmlCompat;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.cricketexchange.app.cricketexchange.MyApplication;
import in.cricketexchange.app.cricketexchange.activities.HomeActivity;
import in.cricketexchange.app.cricketexchange.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessageService";

    MyApplication mApplication;
    private MyApplication getApp() {
        if (mApplication == null)
            mApplication = (MyApplication) getApplication();
        return mApplication;
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("notification ", "received");
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
//        Log.e("notification", "From "+remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //imageUri will contain URL of the image to be displayed with Notification
            JSONObject json = new JSONObject(remoteMessage.getData());

        }
        else {
            try {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                if(notification == null)
                    return;
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Default";
                    String description = "consoleNotification";
                    NotificationChannel channel = new NotificationChannel("Default", name, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(description);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Default")
                        .setContentText(notification.getBody())
                        .setContentTitle(notification.getTitle())
                        .setColor(Color.BLACK)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setWhen(new Date().getTime())
                        .setContentIntent(pendingIntent);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    notificationBuilder.setSmallIcon(R.drawable.logo_monochrome);
                }
                else
                    notificationBuilder.setSmallIcon(R.drawable.ic_notification_monochrome);

                if (notificationManager != null) {
                    Log.e("showing notification", "dsa" );
                    notificationManager.notify((""+notification.getTitle()).hashCode(), notificationBuilder.build());
                }
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean handleIntentOnMainThread(Intent intent) {
        return super.handleIntentOnMainThread(intent);
    }


    @Override
    public void onNewToken(@NonNull String s) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("PUSH_RC");
        }
        catch (Exception e){
            Log.e("remoteConfig", "TopicSubscriptionFailed "+e.getMessage());
        }
        super.onNewToken(s);
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}

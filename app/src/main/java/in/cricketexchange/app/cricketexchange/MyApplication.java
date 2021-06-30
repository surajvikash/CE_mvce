package in.cricketexchange.app.cricketexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class MyApplication extends MultiDexApplication {

    public native String a(int a);


    static
    {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP)
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    static {
        System.loadLibrary("native-lib");
    }


    private String dbUrl = a(0);

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(MyApplication.this);
        initializeAppCheck();
        initializeAds();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);


        dbUrl = getString(R.string.firebase_database_url);

        int dbInstance = 2;
        Random random = new Random();
        dbInstance = random.nextInt(19);
        if (!dbUrl.contains("test"))
            dbUrl = a(dbInstance);

        AppOpenManager appOpenManager = new AppOpenManager(this);

//        new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
//            @Override
//            public void onAppNotResponding(ANRError error) {
//                FirebaseCrashlytics.getInstance().log("ANR");
//                Exception e = new Exception("ANRWatchDog",new Throwable(error));
//                e.setStackTrace(error.getStackTrace());
//                FirebaseCrashlytics.getInstance().recordException(e);
//            }
//        }).start();
    }

    public String getDbUrl() {
        if (dbUrl == null || dbUrl.length() < 1) {
            Random random = new Random();
            int dbInstance = random.nextInt(19);
            dbUrl = a(dbInstance);
        }
//        dbUrl="https://cricket-exchange-testing.firebaseio.com";
//        dbUrl="https://cricket-exchange.firebaseio.com";
        return dbUrl;
    }

    private void initializeAppCheck(){
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
    }

    private void initializeAds(){
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                String process = getProcessName();
//                if (!process.equals(getPackageName()))
//                    WebView.setDataDirectorySuffix(process);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("MyApp Mobile ads", "initialized ");
               /* Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                    Log.e("MyApp status", ""+status.getInitializationState());
                }*/
            }
        });
        MobileAds.setAppMuted(true);
//        IronSource.setConsent(true);



    }
    private void print(SharedPreferences pref){
        Map<String,?> keys = pref.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.e("printPref",entry.getKey() + " : " +
                    entry.getValue().toString());
        }
    }

}

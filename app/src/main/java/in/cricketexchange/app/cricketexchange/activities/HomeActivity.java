package in.cricketexchange.app.cricketexchange.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;

import in.cricketexchange.app.cricketexchange.DataCallback;
import in.cricketexchange.app.cricketexchange.GetLiveMatches2Firebase;
import in.cricketexchange.app.cricketexchange.MyApplication;
import in.cricketexchange.app.cricketexchange.R;

public class HomeActivity extends AppCompatActivity implements DataCallback {

    GetLiveMatches2Firebase liveMatches2Listener;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        mTextView = findViewById(R.id.textview);


        liveMatches2Listener = new GetLiveMatches2Firebase(this, (MyApplication) getApplication());
        liveMatches2Listener.attachFirebaseListener();

        AdView adView = findViewById(R.id.home_banner);
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
    }


    @Override
    protected void onPause() {
        super.onPause();

        liveMatches2Listener.removeFirebaseListener();
    }




    DataSnapshot snapshot;
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        snapshot = dataSnapshot;
        Log.e("appCheck data loaded", ""+snapshot.getValue());

        mTextView.setText("Data loaded "+snapshot.getValue());
    }
}

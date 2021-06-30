package in.cricketexchange.app.cricketexchange;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetLiveMatches2Firebase {
    private ValueEventListener mListener;
    private DatabaseReference mRef;
    private DataCallback dataChange;
    private boolean isListenerAttached;

    public native String a();
    static {
        System.loadLibrary("native-lib");
    }

    MyApplication myApplication;
    public GetLiveMatches2Firebase(DataCallback dataChange, MyApplication myApplication){
        this.dataChange = dataChange;
        this.myApplication = myApplication;
    }

    public void attachFirebaseListener() {
        if(mRef== null || mListener==null)
            initialiseVariables();
        if(!isListenerAttached) {
            Log.e("appCheck", "listener added ");
            mRef.addValueEventListener(mListener);
            isListenerAttached = true;
        }
    }

    public void removeFirebaseListener() {
        if (mRef != null && mListener != null) {
            Log.e("appCheck", "listener removed ");
            mRef.removeEventListener(mListener);
        }
        isListenerAttached = false;
    }



    // After I add AppCheck dependancy, ValueEventListener callbacks does not get called

    private void initialiseVariables() {
       try {

           //childNode = "testingAppCheck"

           mRef = FirebaseDatabase.getInstance(myApplication.getDbUrl()).getReference(a());
           mListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snap) {
                   Log.e("appCheck", "snap loaded "+snap.getChildrenCount());
                   dataChange.onDataChange(snap);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Log.e("appCheck", "snap error "+databaseError.getMessage());
               }
           };
       } catch (Exception e) {
           Log.e("appCheck", "snap error2 "+e.getMessage());
           e.printStackTrace();
       }
    }
}

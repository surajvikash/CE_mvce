package in.cricketexchange.app.cricketexchange;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

import in.cricketexchange.app.cricketexchange.MyApplication;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {


	private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
	private AppOpenAd appOpenAd = null;

	private static boolean isShowingAd = false;
	private static boolean isLoadingAd = false;
	private long loadTime = 0;
	private long activityStopTime=0;

	private Activity currentActivity;

	private AppOpenAd.AppOpenAdLoadCallback loadCallback;

	private final MyApplication myApplication;

	public AppOpenManager(MyApplication myApplication) {
		this.myApplication = myApplication;
		this.myApplication.registerActivityLifecycleCallbacks(this);
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
	}


	@OnLifecycleEvent(ON_START)
	public void onStart() {
		Log.e("appOpen", "onStart");
		try {
			if (currentActivity.getLocalClassName().equalsIgnoreCase("player.PlayerProfileActivity")) {
				if(currentActivity.getIntent()!=null && !currentActivity.getIntent().hasExtra("packageName"))
					return;
				if (currentActivity.getIntent() != null && currentActivity.getIntent().hasExtra("packageName") && !currentActivity.getIntent().getStringExtra("packageName").equals(currentActivity.getPackageName())) {
					return;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		if(currentActivity!=null &&
				!currentActivity.getLocalClassName().equalsIgnoreCase("activities.PayLogin") &&
				!currentActivity.getLocalClassName().equalsIgnoreCase("activities.RemoveAdsActivity") &&
				!currentActivity.getLocalClassName().equalsIgnoreCase("activities.PaytmPayActivity") &&
				!currentActivity.getLocalClassName().equalsIgnoreCase("com.paytm.pgsdk.PaytmPGActivity")
		)
			showAdIfAvailable();
	}

	public void fetchAd() {
		// Have unused ad, no need to fetch another.
		if (isAdAvailable() || isLoadingAd) {
			return;
		}

		loadCallback =
				new AppOpenAd.AppOpenAdLoadCallback() {
					@Override
					public void onAdLoaded(AppOpenAd ad) {
						isLoadingAd = false;
						Log.e("appOpen", "loaded");
						AppOpenManager.this.appOpenAd = ad;
						AppOpenManager.this.loadTime = (new Date()).getTime();
					}

					@Override
					public void onAdFailedToLoad(LoadAdError loadAdError) {
						// Handle the error.
						isLoadingAd = false;
						Log.e("appOpen", "failed "+loadAdError.getMessage());
					}

				};
		isLoadingAd = true;
		AdRequest request = getAdRequest();
		AppOpenAd.load(
				myApplication, AD_UNIT_ID, request,
				AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
	}


	private AdRequest getAdRequest() {
		return new AdRequest.Builder().build();
	}


	public boolean isAdAvailable() {
		return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
	}


	private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
		long dateDifference = (new Date()).getTime() - this.loadTime;
		long numMilliSecondsPerHour = 3600000;
		return (dateDifference < (numMilliSecondsPerHour * numHours));
	}

	private boolean wasActivityStoppedSecondsAgo(int seconds){
		long timeDifference = (new Date()).getTime() - this.activityStopTime;
		long numMilliSecondsPerSecond = 1000;
		Log.e("appOpen time", ""+timeDifference/1000);
		return (timeDifference < (numMilliSecondsPerSecond * seconds));
	}

	public void showAdIfAvailable() {


		if (!isShowingAd && isAdAvailable() && !wasActivityStoppedSecondsAgo(5) ) {
			Log.e("appOpen", "showing");

			FullScreenContentCallback fullScreenContentCallback =
					new FullScreenContentCallback() {
						@Override
						public void onAdDismissedFullScreenContent() {
							// Set the reference to null so isAdAvailable() returns false.
							AppOpenManager.this.appOpenAd = null;
							isShowingAd = false;
							fetchAd();
						}

						@Override
						public void onAdFailedToShowFullScreenContent(AdError adError) {}

						@Override
						public void onAdShowedFullScreenContent() {
							isShowingAd = true;
						}
					};

			appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
			appOpenAd.show(currentActivity);

		} else {
			Log.e("appOpen", "not showing (Ad not available)");
			fetchAd();
		}
	}


	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

	@Override
	public void onActivityStarted(Activity activity) {
		currentActivity = activity;
	}

	@Override
	public void onActivityResumed(Activity activity) {
		currentActivity = activity;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		AppOpenManager.this.activityStopTime = (new Date()).getTime();
	}

	@Override
	public void onActivityPaused(Activity activity) {}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

	@Override
	public void onActivityDestroyed(Activity activity) {
		currentActivity = null;
	}

}

package com.test.adplayer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.ads.*;

import static com.facebook.ads.BuildConfig.DEBUG;


public class FacebookAdapter extends TestAdapter {
    private static final String TAG = InterstitialAd.class.getSimpleName();
    private static final String placement_id = "placement_id";
    private TestPlayer _testPlayer;
    private static InterstitialAd interstitialAd;

    public FacebookAdapter(final Activity activity, final TestPlayer testplayer) {
        _testPlayer = testplayer;
        if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
    }}

    public static void initialize(Activity activity) {
        initFBIntAd(activity);
        FacebookHelper.initialize(activity);
    }

    private static void initFBIntAd(Context context) {
        interstitialAd = new InterstitialAd(context, placement_id);
        // Set listeners for the Interstitial Ad
        final InterstitialAdListener adListener = new InterstitialAdListener() {
            private static final int MAX_NUMBER_OF_RETRIES = 3;
            private boolean shouldLoadAd = true;
            private int retryCount = 0;

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                if(shouldLoadAd) {
                /* Change shouldLoadAd value to false,
                    or a new interstitial ad will show immediately
                    when previous interstitial ad gets dismissed. */
                    shouldLoadAd = false;
                    interstitialAd.loadAd();
                }
                Log.e(TAG, "Interstitial ad dismissed.");
                interstitialAd.destroy();
                Log.d(TAG, "Interstitial ad destroy!");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                // Stop retrying when it reaches to MAX_NUMBER_OF_RETRIES
                if(retryCount < MAX_NUMBER_OF_RETRIES) {
                    retryCount += 1;
                    interstitialAd.loadAd();
                }
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig()
                .withAdListener(adListener)
                .build();

        interstitialAd.loadAd(loadAdConfig);
    }

    protected void Destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            Log.d(TAG, "Interstitial ad destroy!");
        }
    }

    @Override
    public void onPause(Activity activity) {
    }

    @Override
    public void onResume(Activity activity) {

    }
}

class FacebookHelper
        implements AudienceNetworkAds.InitListener {
    static void initialize(Context context) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            if (DEBUG) {
                AdSettings.turnOnSDKDebugger(context);
            }
            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new FacebookHelper())
                    .initialize();
        }
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult result) {
        Log.d(AudienceNetworkAds.TAG, result.getMessage());
    }
}

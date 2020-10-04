package com.bustracking.myschool.bustracker.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bustracking.myschool.bustracker.R;
import com.bustracking.myschool.bustracker.Utils.GDPR;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @BindView(R.id.adViewMainActivity)
    AdView adView;
    InterstitialAd interstitialAd;

    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        MobileAds.initialize(this,this.getResources().getString(R.string.ADS_APP_ID));
        user = auth.getCurrentUser();

        if (user == null) {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            checkPermissions();
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(this.getResources().getString(R.string.ADS_INTERSTITIAL_UNIT_ID));

            interstitialAd.loadAd(new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build());

            adView.loadAd(new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build());


        } else {
            Intent myIntent = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(myIntent);
            finish();

        }


    }


    public void registerAsUser(View v) {

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Intent myIntent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                    startActivity(myIntent);
                    super.onAdClosed();
                }
            });

        } else {
            interstitialAd.loadAd(new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build());
            Intent myIntent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
            startActivity(myIntent);
        }


    }

    public void registerAsDriver(View v) {

        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed() {
                    Intent myIntent = new Intent(getApplicationContext(), DriverRegistrationActivity.class);
                    startActivity(myIntent);

                    super.onAdClosed();
                }
            });

        }

        else
        {
            Intent myIntent = new Intent(getApplicationContext(), DriverRegistrationActivity.class);
            startActivity(myIntent);

        }

    }

    public void login(View v) {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                //   String permission = permissions[i];
                int grantResult = grantResults[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission
                                .READ_EXTERNAL_STORAGE}, 100);
                    }
                }

            }

        }

    }
}

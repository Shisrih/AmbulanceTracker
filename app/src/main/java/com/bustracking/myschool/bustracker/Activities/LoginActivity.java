package com.bustracking.myschool.bustracker.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.bustracking.myschool.bustracker.R;
import com.bustracking.myschool.bustracker.Utils.GDPR;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.editTextUserEmail) EditText editTextUserEmail;
    @BindView(R.id.editTextUserPassword) EditText editTextUserPassword;
    @BindView(R.id.userToolbar) Toolbar toolbar;
    @BindView(R.id.adViewLoginActivity) AdView adView;

    FirebaseAuth auth;
    ProgressDialog dialog;

    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(this.getResources().getString(R.string.ADS_INTERSTITIAL_UNIT_ID));

        interstitialAd.loadAd(new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build());

        adView.loadAd(new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build());
        dialog = new ProgressDialog(this);

    }


    public void login(View v)
    {

        dialog.setMessage("Logging in. Please wait.");
        dialog.show();

            if(editTextUserEmail.getText().toString().equals("") || editTextUserPassword.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Blank fields not allowed.",Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
            else
            {
                String newEmail = editTextUserEmail.getText().toString();

                auth.signInWithEmailAndPassword(newEmail,editTextUserPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {

                                                dialog.dismiss();

                                                if(interstitialAd.isLoaded())
                                                {
                                                    interstitialAd.show();
                                                    interstitialAd.setAdListener(new AdListener()
                                                    {
                                                        @Override
                                                        public void onAdClosed() {
                                                            Intent loginIntent = new Intent(LoginActivity.this, NavigationActivity.class);
                                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(loginIntent);
                                                            finish();
                                                            super.onAdClosed();
                                                        }
                                                    });
                                                }

                                                else
                                                {
                                                    Intent loginIntent = new Intent(LoginActivity.this, NavigationActivity.class);
                                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(loginIntent);
                                                    finish();
                                                }



                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Wrong email/password combination. Try again.",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                            }
                        });
            }
    }

}

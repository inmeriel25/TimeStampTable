package com.eklee.timestamptable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eklee.timestamptable.db.DatabaseHelper;
import com.eklee.timestamptable.models.Settings;
import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Judy on 2018-04-04.
 * Updated by Judy on 2018-07-17.
 */

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Context mContext;

    private static final String LOGTAG = "BannerTypeXML1";
    private BannerAdView adView = null;

    private String saveOriginalPhoto, saveUsingFavorite;
    private Switch originBtn, favoriteBtn;
    private DatabaseHelper mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "onCreate: started.");
        mContext = SettingsActivity.this;

        // Db open
        mDb = DatabaseHelper.getInstance(mContext);

        ImageView backScreen = findViewById(R.id.backScreen);
        backScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        originBtn = findViewById(R.id.originBtn);
        originBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    originBtn.setText(getString(R.string.string_no));
                    saveOriginalPhoto = getString(R.string.string_no);
                }else{
                    originBtn.setText(getString(R.string.string_yes));
                    saveOriginalPhoto = getString(R.string.string_yes);
                }
            }
        });

        favoriteBtn = findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    favoriteBtn.setText(getString(R.string.string_no));
                    saveUsingFavorite = getString(R.string.string_no);
                }else{
                    favoriteBtn.setText(getString(R.string.string_yes));
                    saveUsingFavorite = getString(R.string.string_yes);
                }
            }
        });

        ImageButton shareAppBtn = findViewById(R.id.shareApp);
        shareAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendShareApp();
            }
        });

        ImageButton sendMsgBtn = findViewById(R.id.sendMsg);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        TextView saveSettings = findViewById(R.id.saveSettings);
        saveSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: saveOriginalPhoto: " + saveOriginalPhoto);
                Settings settings = new Settings();
                settings.setSaveOriginal(saveOriginalPhoto);
                settings.setSaveFavorite(saveUsingFavorite);

                boolean isSaves = mDb.mDao.addSettings(settings);
                if(isSaves) {
                    Toast.makeText(mContext, getString(R.string.string_save), Toast.LENGTH_SHORT).show();
                    finish();
                } else{
                    Log.d(TAG, "failed to insert in DB");
                    Toast.makeText(mContext, "failed to save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initData();
        initAdFit();
    }


    private void initData(){
        Log.d(TAG, "initData: called. checking for initial data");
        ArrayList<Settings> items = mDb.mDao.selectSettings();
        if(items.size() > 0 && items.get(0).getSaveOriginal() != null ){
            if(items.get(0).getSaveOriginal().equals(getString(R.string.string_no))){
                Log.d(TAG, "initData: originBtn.setChecked(true);");
                originBtn.setChecked(true);
                saveOriginalPhoto = getString(R.string.string_no);
            } else{
                Log.d(TAG, "initData: originBtn.setChecked(false);");
                originBtn.setChecked(false);
                saveOriginalPhoto = getString(R.string.string_yes);
            }
        } else {
            originBtn.setChecked(false);
            saveOriginalPhoto = getString(R.string.string_yes);
        }

        if(items.size() > 0 && items.get(0).getSaveFavorite() != null ){
            if(items.get(0).getSaveFavorite().equals(getString(R.string.string_no))){
                Log.d(TAG, "initData: favoriteBtn.setChecked(true);");
                favoriteBtn.setChecked(true);
                saveUsingFavorite = getString(R.string.string_no);
            } else{
                Log.d(TAG, "initData: favoriteBtn.setChecked(false);");
                favoriteBtn.setChecked(false);
                saveUsingFavorite = getString(R.string.string_yes);
            }
        } else {
            favoriteBtn.setChecked(false);
            saveUsingFavorite = getString(R.string.string_yes);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //******************************* shareLink *******************************************************
    /**
     * sharing the app's google play address
     * update : 2018.07.17
     */
    private void sendShareApp() {
        Log.d(TAG, "sendShareApp: called.");

        String subject = "Time Stamp! Get your photo!";
        String appAddress = "https://play.google.com/store/apps/details?id=com.eklee.timestamptable";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, appAddress);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, subject));

    }

    //******************************* sendMsg *******************************************************
    private void sendMsg() {
        String subject = "Send your message to a developer";
        Intent emailIntent = new Intent();

        try {
            emailIntent.setAction(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dodrod4652@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject_mail));

            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");

            if(emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }

            startActivity(emailIntent);

        } catch (Exception e) {
            e.printStackTrace();
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dodrod4652@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject_mail));
            startActivity(Intent.createChooser(emailIntent, subject));
        }
    }

    //******************************* Adfit *******************************************************
    @Override
    public void onResume() {
        super.onResume();

        // lifecycle 사용이 불가능한 경우
        if (adView == null) return;
        adView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // lifecycle 사용이 불가능한 경우
        if (adView == null) return;
        adView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // lifecycle 사용이 불가능한 경우
        if (adView == null) return;
        adView.destroy();
    }

    private void initAdFit() {
        // initializing for AdFit sdk
        adView = (BannerAdView) findViewById(R.id.adview);
        adView.setClientId(getString(R.string.adfit_clientId));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailed(int code) {
                Log.d(TAG, "onAdFailed : " + code);
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }
        });

        adView.loadAd();
    }
}

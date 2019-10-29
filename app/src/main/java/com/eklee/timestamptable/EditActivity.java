package com.eklee.timestamptable;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eklee.timestamptable.Utils.MyCustomDialog;
import com.google.android.material.tabs.TabLayout;
import com.kakao.adfit.ads.ba.BannerAdView;

import com.bumptech.glide.Glide;
import com.eklee.timestamptable.Utils.CustomDateFormat;
import com.eklee.timestamptable.Utils.TextSetSpan;
import com.eklee.timestamptable.db.DatabaseHelper;
import com.eklee.timestamptable.models.Settings;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.kakao.adfit.ads.AdListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

/**
 * Created by Judy on 2018-01-28.
 * Updated by Judy on 2018-07-17.
 */

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    private static final int SHARE_PHOTO = 100;
    private Context mContext;

    private static final String LOGTAG = "BannerTypeXML1";
    private BannerAdView adView = null;

    private String mAppend = "file://"; //"file:// 무슨차이...."
    private String imgURL, savedEditImgURL;
    private TextView timeStamp, sticker, breakfast, lunch, dinner, snack1, snack2, snack3, study, preventText, runDay, userEditText;
    public TextView custom_edit_text;
    private RelativeLayout container;
    private boolean small;
    private ImageView editImage, textToWhite, textToGray, textToBlack;
    private ImageView chart, chart_breakfast, chart_lunch, chart_snack, chart_dinner, mission, confirm, running, bicycle, muscle;
    private TabLayout tabLayout;
    private ArrayList<String> categories;
    private long currentTime;
    private TextView changedDateTime, textSmall, textReset, stickerTag;
    private int mCurrentTextSize;

    private String favoriteStyle, favoriteFont, favoriteSize;

    private DatabaseHelper mDb;


    private static final boolean[] SIZE_SMALL = {
            false,
            true
    };

    private static final int[] TEXT_SIZE_TITLE = {
            R.string.string_small,
            R.string.string_big
    };

    private static final int[] TEXT_SIZE = {
            50,
            30
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mContext = EditActivity.this;
        Log.d(TAG, "onCreate: started. got the incoming image" + getIntent().getStringExtra(getString(R.string.selected_image)));
        Log.d(TAG, "onCreate: started. got the incoming image" + getIntent().getStringExtra(getString(R.string.captured_url)));

        // Db open
        mDb = DatabaseHelper.getInstance(mContext);

        //camera captured time
        currentTime = getIntent().getLongExtra(getString(R.string.currentTime), 0);
        small = false;
        editImage = findViewById(R.id.editImage);
        textToWhite = findViewById(R.id.edit_white);
        textToGray = findViewById(R.id.edit_gray);
        textToBlack = findViewById(R.id.edit_black);
        textSmall = findViewById(R.id.edit_small);
        stickerTag = findViewById(R.id.edit_tag);
        textReset = findViewById(R.id.edit_reset);
        breakfast = findViewById(R.id.breakfast);
        lunch = findViewById(R.id.lunch);
        dinner = findViewById(R.id.dinner);
        snack1 = findViewById(R.id.snack1);
        snack2 = findViewById(R.id.snack2);
        snack3 = findViewById(R.id.snack3);
        chart_breakfast = findViewById(R.id.chart_breakfast);
        chart_lunch = findViewById(R.id.chart_lunch);
        chart_snack = findViewById(R.id.chart_snack);
        chart_dinner = findViewById(R.id.chart_dinner);
        mission = findViewById(R.id.mission);
        confirm = findViewById(R.id.confirm);
        study = findViewById(R.id.study);
        preventText = findViewById(R.id.preventText);
        runDay = findViewById(R.id.runDay);
        running = findViewById(R.id.running);
        bicycle = findViewById(R.id.bicycle);
        muscle = findViewById(R.id.muscle);

        // user custom edit text widget
        custom_edit_text = findViewById(R.id.custom_edit_text);
        userEditText = findViewById(R.id.userEditText);
        userEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyCustomDialog dialog = new MyCustomDialog();
                dialog.show(getFragmentManager(), "MyCustomDialog");
            }
        });

        timeStamp = findViewById(R.id.selected_timeStamp);
        sticker = findViewById(R.id.selected_sticker);
        chart = findViewById(R.id.chart);
        container = findViewById(R.id.image_container);
        tabLayout = findViewById(R.id.edit_tab);
        changedDateTime = findViewById(R.id.changedDateTime);
        changedDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_datetimepicker);

                new SingleDateAndTimePickerDialog.Builder(mContext)
                        .curved()
                        .title(getString(R.string.string_datepicker))
                        .setDayFormatter(new SimpleDateFormat("yyyy.MM.dd", new Locale("en", "US")))
                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                //retrieve the SingleDateAndTimePicker
                                Log.d(TAG, "onDisplayed: called.");
                            }
                        })
                        .minutesStep(1)
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                Log.d(TAG, "onDateSelected: date: " + date);
                                currentTime = date.getTime();
                                Log.d(TAG, "onDateSelected: currentTime: " + currentTime);
                                setTime();
                            }
                        }).display();

            }
        });


        ImageView backScreen = findViewById(R.id.backScreen);
        backScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                onBackPressed();
            }
        });


        ImageView addFavorite = findViewById(R.id.addFavorite);
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAlert();
            }
        });

        ImageView sharePicture = findViewById(R.id.sharePicture);
        sharePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, getString(R.string.string_share), Toast.LENGTH_SHORT).show();
                sendSharePicture();
            }
        });

        TextView savePicture = findViewById(R.id.savePicture);
        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, getString(R.string.string_save), Toast.LENGTH_SHORT).show();
                saveImage();
                Intent intent = new Intent(mContext, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // controlling a touch event
        timeStamp.setOnTouchListener(new View.OnTouchListener() {
            float oldXvalue;
            float oldYvalue;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: drag starting");
                int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        oldXvalue = event.getX();
                        oldYvalue = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - oldXvalue);
                        v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getX() > width && v.getY() > height) {
                            v.setX(width);
                            v.setY(height);
                        } else if (v.getX() < 0 && v.getY() > height) {
                            v.setX(0);
                            v.setY(height);
                        } else if (v.getX() > width && v.getY() < 0) {
                            v.setX(width);
                            v.setY(0);
                        } else if (v.getX() < 0 && v.getY() < 0) {
                            v.setX(0);
                            v.setY(0);
                        } else if (v.getX() < 0 || v.getX() > width) {
                            if (v.getX() < 0) {
                                v.setX(0);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            } else {
                                v.setX(width);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            }
                        } else if (v.getY() < 0 || v.getY() > height) {
                            if (v.getY() < 0) {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(0);
                            } else {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(height);
                            }
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        sticker.setOnTouchListener(new View.OnTouchListener() {
            float oldXvalue;
            float oldYvalue;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: drag starting");
                int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        oldXvalue = event.getX();
                        oldYvalue = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - oldXvalue);
                        v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getX() > width && v.getY() > height) {
                            v.setX(width);
                            v.setY(height);
                        } else if (v.getX() < 0 && v.getY() > height) {
                            v.setX(0);
                            v.setY(height);
                        } else if (v.getX() > width && v.getY() < 0) {
                            v.setX(width);
                            v.setY(0);
                        } else if (v.getX() < 0 && v.getY() < 0) {
                            v.setX(0);
                            v.setY(0);
                        } else if (v.getX() < 0 || v.getX() > width) {
                            if (v.getX() < 0) {
                                v.setX(0);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            } else {
                                v.setX(width);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            }
                        } else if (v.getY() < 0 || v.getY() > height) {
                            if (v.getY() < 0) {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(0);
                            } else {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(height);
                            }
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        chart.setOnTouchListener(new View.OnTouchListener() {
            float oldXvalue;
            float oldYvalue;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: drag starting");
                int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        oldXvalue = event.getX();
                        oldYvalue = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - oldXvalue);
                        v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getX() > width && v.getY() > height) {
                            v.setX(width);
                            v.setY(height);
                        } else if (v.getX() < 0 && v.getY() > height) {
                            v.setX(0);
                            v.setY(height);
                        } else if (v.getX() > width && v.getY() < 0) {
                            v.setX(width);
                            v.setY(0);
                        } else if (v.getX() < 0 && v.getY() < 0) {
                            v.setX(0);
                            v.setY(0);
                        } else if (v.getX() < 0 || v.getX() > width) {
                            if (v.getX() < 0) {
                                v.setX(0);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            } else {
                                v.setX(width);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            }
                        } else if (v.getY() < 0 || v.getY() > height) {
                            if (v.getY() < 0) {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(0);
                            } else {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(height);
                            }
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        custom_edit_text.setOnTouchListener(new View.OnTouchListener() {
            float oldXvalue;
            float oldYvalue;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: drag starting");
                int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        oldXvalue = event.getX();
                        oldYvalue = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - oldXvalue);
                        v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getX() > width && v.getY() > height) {
                            v.setX(width);
                            v.setY(height);
                        } else if (v.getX() < 0 && v.getY() > height) {
                            v.setX(0);
                            v.setY(height);
                        } else if (v.getX() > width && v.getY() < 0) {
                            v.setX(width);
                            v.setY(0);
                        } else if (v.getX() < 0 && v.getY() < 0) {
                            v.setX(0);
                            v.setY(0);
                        } else if (v.getX() < 0 || v.getX() > width) {
                            if (v.getX() < 0) {
                                v.setX(0);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            } else {
                                v.setX(width);
                                v.setY(event.getRawY() - oldYvalue - v.getHeight());
                            }
                        } else if (v.getY() < 0 || v.getY() > height) {
                            if (v.getY() < 0) {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(0);
                            } else {
                                v.setX(event.getRawX() - oldXvalue);
                                v.setY(height);
                            }
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        setStyle();
        setTime();
        setFonts();
        editTabInit();
        setImage();
        setColor();
        setSticker();
        initFavoriteStyle();
        initAdFit();
    }

    private void editTabInit(){

        categories = new ArrayList<>();
        categories.add(getString(R.string.string_style));
        categories.add(getString(R.string.string_font));
        categories.add(getString(R.string.string_sticker));
        categories.add(getString(R.string.string_edit));

        for(int i = 0; i <categories.size(); i++){
            tabLayout.addTab(tabLayout.newTab().setText(categories.get(i)));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onClick: selected tabLayout no: " + tab.getPosition());
                setupEditTabView(categories.get(tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    // if I don't want to create fragment... but I'm not sure which one is better
    private void setupEditTabView(String selectedTab) {
        Log.d(TAG, "setupEditTabView: setting edit style tab");
        RelativeLayout styleContainer = findViewById(R.id.style_container);
        RelativeLayout styleContainer1 = findViewById(R.id.style_container1);
        RelativeLayout styleContainer2 = findViewById(R.id.style_container2);
        RelativeLayout styleContainer3 = findViewById(R.id.style_container3);

        if (selectedTab.equals(getString(R.string.string_style))) {
            styleContainer.setVisibility(View.VISIBLE);
            styleContainer1.setVisibility(View.GONE);
            styleContainer2.setVisibility(View.GONE);
            styleContainer3.setVisibility(View.GONE);

        } else if (selectedTab.equals(getString(R.string.string_font))) {
            styleContainer.setVisibility(View.GONE);
            styleContainer1.setVisibility(View.VISIBLE);
            styleContainer2.setVisibility(View.GONE);
            styleContainer3.setVisibility(View.GONE);

        } else if (selectedTab.equals(getString(R.string.string_sticker))) {
            styleContainer.setVisibility(View.GONE);
            styleContainer1.setVisibility(View.GONE);
            styleContainer2.setVisibility(View.VISIBLE);
            styleContainer3.setVisibility(View.GONE);

        } else if (selectedTab.equals(getString(R.string.string_edit))) {
            /*Snackbar.make(getWindow().getDecorView().getRootView(), "You can touch and move the text location", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(getResources().getColor(R.color.main_yellow))
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();*/

            styleContainer.setVisibility(View.GONE);
            styleContainer1.setVisibility(View.GONE);
            styleContainer2.setVisibility(View.GONE);
            styleContainer3.setVisibility(View.VISIBLE);
        }
    }

    private void setStyle(){
        Log.d(TAG, "setStyle: setting onclick for style view.");
        final int[] ids = {
                R.id.edit_style1,
                R.id.edit_style2,
                R.id.edit_style3,
                R.id.edit_style4,
                R.id.edit_style5,
                R.id.edit_style6,
                R.id.edit_style7
        };
        final int[] styles = {
                R.string.style1,
                R.string.style2,
                R.string.style3,
                R.string.style4,
                R.string.style5,
                R.string.style6,
                R.string.style7
        };

        for(int i = 0; i < ids.length; i++ ){
            final int p = i;
            findViewById(ids[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // saving the favorite style name for setting next time
                    favoriteStyle = String.valueOf(styles[p]);

                    String date = CustomDateFormat.getCustomDate(String.valueOf(styles[p]), currentTime);
//                    if(!small) {//find am, pm
//                        timeStamp.setText(TextSetSpan.getSizChangedText(date));
//                    }else{
                        timeStamp.setText(date);
//                    }
                }
            });
        }
    }

    private void setTime(){
        String date = CustomDateFormat.getCustomDate(String.valueOf(R.string.style1), currentTime);
        if(small){
            timeStamp.setText(date);
        }else {
            timeStamp.setText(date);
//            timeStamp.setText(TextSetSpan.getSizChangedText(date));
        }
        changedDateTime.setText(date);
    }

    private void setFonts(){
        Button font1 = findViewById(R.id.edit_font1);
        Button font2 = findViewById(R.id.edit_font2);
        Button font3 = findViewById(R.id.edit_font3);
        Button font4 = findViewById(R.id.edit_font4);
        Button font5 = findViewById(R.id.edit_font5);
        Button font6 = findViewById(R.id.edit_font6);
        Button font7 = findViewById(R.id.edit_font7);
        Button font8 = findViewById(R.id.edit_font8);
        Button font9 = findViewById(R.id.edit_font9);

        Button[] fontButtons = {
            font1, font2, font3, font4, font5, font6, font7, font8, font9
        };

        final String[] fonts = {
            getString(R.string.font_Mockup),
            getString(R.string.font_bgrovealtb),
            getString(R.string.font_Digitalt),
            getString(R.string.font_AlegreSans),
            getString(R.string.font_RawengulkSans),
            getString(R.string.font_BMDOHYEON),
            getString(R.string.font_misaeng),
            getString(R.string.font_tvn),
            getString(R.string.font_swagger)
        };

        //setting main font to textView
        Typeface defaultFont = Typeface.createFromAsset(this.getAssets(), getString(R.string.font_Mockup));
        timeStamp.setTypeface(defaultFont);
        sticker.setTypeface(defaultFont);

        for(int i = 0; i < fonts.length; i++){
            final String s = fonts[i];
            final Typeface typeface = Typeface.createFromAsset(this.getAssets(), fonts[i]);
            fontButtons[i].setTypeface(typeface);
            fontButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    favoriteFont = s;

                    timeStamp.setTypeface(typeface);
                    sticker.setTypeface(typeface);
                }
            });
        }
    }

    private void setColor(){
        textToWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamp.setTextColor(getResources().getColor(R.color.white));
                sticker.setTextColor(getResources().getColor(R.color.white));
            }
        });

        textToGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamp.setTextColor(getResources().getColor(R.color.main_yellow));
                sticker.setTextColor(getResources().getColor(R.color.main_yellow));
            }
        });

        textToBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStamp.setTextColor(getResources().getColor(R.color.black));
                sticker.setTextColor(getResources().getColor(R.color.black));
            }
        });

        stickerTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setTextColor(Color.BLACK);
                sticker.setBackground(getResources().getDrawable(R.drawable.ribbon_white));
            }
        });

        textSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTextSize = (mCurrentTextSize + 1) % TEXT_SIZE.length;
                small = SIZE_SMALL[mCurrentTextSize];
//                setTime();
                textSmall.setText(getString(TEXT_SIZE_TITLE[mCurrentTextSize]));
                timeStamp.setTextSize(TEXT_SIZE[mCurrentTextSize]);
                favoriteSize = String.valueOf(TEXT_SIZE[mCurrentTextSize]);
            }

        });

        textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                small = false;
                setTime();

                timeStamp.setTextColor(getResources().getColor(R.color.white));
                sticker.setTextColor(getResources().getColor(R.color.white));
                sticker.setBackground(null);
                textSmall.setText(getString(R.string.string_small));
                timeStamp.setTextSize(50);
                sticker.setText("");
                chart.setImageDrawable(null);
                changedDateTime.setClickable(true);
                changedDateTime.setBackgroundColor(getResources().getColor(R.color.transparent));

                timeStamp.setText("");
            }
        });


    }

    private void setSticker(){
        // adding confirmed sticker and preventing a correction
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.mission));

                // setting to current time
                currentTime = System.currentTimeMillis();
                setTime();

                changedDateTime.setClickable(false);
                changedDateTime.setBackgroundColor(getResources().getColor(R.color.box_darkgray));
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sticker.setText(R.string.confirm);
                chart.setImageDrawable(getResources().getDrawable(R.drawable.confirm_small));

                // setting to current time
                currentTime = System.currentTimeMillis();
                setTime();

                changedDateTime.setClickable(false);
                changedDateTime.setBackgroundColor(getResources().getColor(R.color.box_darkgray));
            }
        });
        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.study);

                // setting to current time
                currentTime = System.currentTimeMillis();
                setTime();

                changedDateTime.setClickable(false);
                changedDateTime.setBackgroundColor(getResources().getColor(R.color.box_darkgray));
            }
        });

        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sticker.setText(R.string.breakfast);
            }
        });
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.lunch);
            }
        });
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.dinner);
            }
        });
        snack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.snack1);
            }
        });
        snack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.snack2);
            }
        });
        snack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.snack3);
            }
        });
        chart_breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.breaky));
            }
        });
        chart_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.lunch));
            }
        });
        chart_snack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.snack));
            }
        });
        chart_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.dinner));
            }
        });
        runDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker.setText(R.string.runDay);
            }
        });

        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.running));
            }
        });
        bicycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.bicycle));
            }
        });
        muscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setImageDrawable(getResources().getDrawable(R.drawable.muscle));
            }
        });


    }

    /**
     * checking whether user wants to save original photos or not
     * added: 2018.04.08
     * try-catch checking for exception
     * update: 2019.01.15
     * */
    private void saveOriginPhoto(String imgURL){
        Log.d(TAG, "saveOriginPhoto url: " + imgURL);
        ArrayList<Settings> items = mDb.mDao.selectSettings();

        String saveOriginal = "";
        try{
            saveOriginal = items.get(0).getSaveOriginal();
        }
        catch(IndexOutOfBoundsException e) {
            saveOriginal = getString(R.string.string_yes);
        }
        catch(NullPointerException e) {
            saveOriginal = getString(R.string.string_yes);
        }
        finally {
            if(saveOriginal.equals("")){
                saveOriginal = getString(R.string.string_yes);
            }
        }

        if(saveOriginal.equals(getString(R.string.string_no))){
            File file = new File(imgURL);
            if(file.exists()){
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    Log.e(TAG, "saveOriginPhoto: IOException" + e.getMessage());
                }
                if(file.exists()){
                    getApplicationContext().deleteFile(file.getName());
                }
            }
        }
        // Media scanning for refreshing the deleted image in Gallery
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imgURL))));
    }

    /**
     * get the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.selected_image))){
            imgURL = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new selected image url: " + imgURL);
        }
        else if(intent.hasExtra(getString(R.string.captured_url))){
            imgURL = intent.getStringExtra(getString(R.string.captured_url));
            Log.d(TAG, "setImage: got new captured image url: " + imgURL);
        }

        try {
            Glide.with(mContext)
                    .asBitmap()
                    .load(imgURL)
                    .into(editImage);

        } catch (Exception e) {
            Toast.makeText(mContext, "failed to load the image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called.");
        super.onBackPressed();

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.selected_image))){
//            saveOriginPhoto(imgURL); 기존 갤러리 사진 원본까지 지워서 에러 발생 (2018.05.30 주석처리로 수정)
            Intent galleryIntent = new Intent(mContext, GalleryActivity.class);
            startActivity(galleryIntent);
            finish();
        }
        // checking whether user wants to save original photos or not
        // and delete
        if(intent.hasExtra(getString(R.string.captured_url))){
            saveOriginPhoto(imgURL);

            Intent cameraIntent = new Intent(mContext, CameraActivity.class);
            startActivity(cameraIntent);
            finish();
        }
    }

    /**
     * checking for file extension which can be '.jpg', '.png' ...
     * so changing the file name
     * update: 2018.04.10
     * */
    private void saveImage(){
        // checking whether user wants to save original photos or not
        // and delete
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.captured_url))) {
            saveOriginPhoto(imgURL);
        }

        /**
         * after changing the path of SD card file and save it
         * android 6.0 부터는 SAF(Storage Access Framework)를 쓰지 않으면 permissions을 부여 했더라도 외부 저장소에 쓸 수 없음
         * update: 2019.01.14
         * */
        File[] Dirs = ContextCompat.getExternalFilesDirs(this.getApplicationContext(), null);

        try{
            String pathSD = Dirs[1].toString(); // Dirs[0] = 내부저장소, Dirs[1] = 외부 sdcard
            int firstOpen = pathSD.indexOf("/");
            int secondOpen = pathSD.indexOf("/", firstOpen + 1);
            int thirdOpen = pathSD.indexOf("/", secondOpen + 1);
            String filename = pathSD.substring(firstOpen, thirdOpen + 1);
            File sdFile = new File(filename);

            if(imgURL.contains(sdFile.getPath())){
                imgURL = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + mContext.getString(R.string.app_lable_name) + "/";
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "saveImage: IndexOutOfBoundsException" + e.getMessage() );
        }catch(NullPointerException e){
            Log.e(TAG, "saveImage: NullPointerException" + e.getMessage() );
        }

        String capturedImgURL = imgURL.replace(imgURL.substring(imgURL.lastIndexOf("/") + 1), "") + "timestamp_";
        Log.d(TAG, "saveImage: capturedImgURL: " + capturedImgURL);

        container.buildDrawingCache();
        Bitmap captureView = container.getDrawingCache();

        savedEditImgURL = capturedImgURL + System.currentTimeMillis() + ".jpg";

        FileOutputStream fos = null;
        Log.d(TAG, "saveImage: savedEditImgURL: " + savedEditImgURL);

        try {
            fos = new FileOutputStream(savedEditImgURL);
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } 
        catch (FileNotFoundException e) {
            Log.e(TAG, "onBitmapReady: FileNotFoundException: " + e.getMessage());
        } 
        finally 
        {
            try {
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mAppend + savedEditImgURL)));
    }


    private void confirmAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("FAVORITE");
        builder.setMessage("즐겨찾는 스타일로 저장 하시겠습니까? \n (스티커는 적용되지 않습니다.)");
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                addFavoriteStyle();
                dialog.dismiss();
            }
        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //******************************* favorite style *******************************************************
    /**
     * before initializing the favorite style to timestamp textView
     * checking settings first
     * update : 2018.07.10
     *
     * try-catch checking for exception
     * update: 2019.01.15
     */
    private void initFavoriteStyle() {
        Log.d(TAG, "initFavoriteStyle: checking the favorite setting yes or no");
        ArrayList<Settings> favSettings = mDb.mDao.selectSettings();

        String fav = "";

        try{
            fav = favSettings.get(0).getSaveFavorite();
        }
        catch(IndexOutOfBoundsException e) {
            fav = getString(R.string.string_yes);
        }
        catch(NullPointerException e) {
            fav = getString(R.string.string_yes);
        }
        finally {
            if(fav.equals("")){
                fav = getString(R.string.string_yes);
            }
        }

        if( fav.equals(getString(R.string.string_yes)) ) {

            ArrayList<Settings> items = mDb.mDao.selectFavorite();

            String selStyle, selFont, selSize, selColor = "";

            try{
                selStyle = items.get(0).getStyle();
                selFont = items.get(0).getFont();
                selSize = items.get(0).getSize();
                selColor = items.get(0).getColor();
            }
            catch(IndexOutOfBoundsException e) {
                selStyle = "";
                selFont = "";
                selSize = "";
                selColor = "";
                Log.e(TAG, "initFavoriteStyle: IndexOutOfBoundsException" + e.getMessage() );
            }
            catch(NullPointerException e) {
                selStyle = "";
                selFont = "";
                selSize = "";
                selColor = "";
                Log.e(TAG, "initFavoriteStyle: NullPointerException" + e.getMessage() );
            }

            // 1.style
            if(selStyle != null && !selStyle.equals("")) {
                // saving the favorite style name for setting next time
                favoriteStyle = selStyle;
                String date = CustomDateFormat.getCustomDate(selStyle, currentTime);

//                if (!small) {//find am, pm
//                    timeStamp.setText(TextSetSpan.getSizChangedText(date));
//                } else {
                    timeStamp.setText(date);
//                }
            }

            // 2.font
            if(selFont != null && !selFont.equals("")) {
                favoriteFont = selFont;
                Typeface typeface = Typeface.createFromAsset(this.getAssets(), selFont);
                timeStamp.setTypeface(typeface);
            }

            // 3.size
            if(selSize != null && !selSize.equals("")) {
                favoriteSize = selSize;
                timeStamp.setTextSize(Float.parseFloat(selSize));
            }

            // 4.color
            if(selColor != null && !selColor.equals("")) {
                timeStamp.setTextColor(Integer.parseInt(selColor));
            }
        }
    }

    /**
     * adding the style to custom favorite table
     * update : 2018.07.10
     * only 1 style can be saved in favorite
     */
    private void addFavoriteStyle() {
        Log.d(TAG, "addFavoriteStyle: called.");
        String favoriteColor = String.valueOf(timeStamp.getCurrentTextColor());

        Settings settings = new Settings();
        settings.setStyle(favoriteStyle);
        settings.setFont(favoriteFont);
        settings.setSize(favoriteSize);
        settings.setColor(favoriteColor);

        boolean isSaves = mDb.mDao.addFavorite(settings);
        if(isSaves) {
            Toast.makeText(mContext, getString(R.string.string_save), Toast.LENGTH_SHORT).show();
        } else{
            Log.d(TAG, "failed to insert in DB");
            Toast.makeText(mContext, "failed to save", Toast.LENGTH_SHORT).show();
        }
    }


    //******************************* shareLink *******************************************************

    /**
     * sharing the picture with user app list
     * update : 2018.04.07
     * error: android.os.FileUriExposedException
     * targetSdkVersion >= 24, then we have to use FileProvider class to give access to the particular file or folder to make them accessible for other apps.
     */
    private void sendSharePicture() {
        Log.d(TAG, "sendSharePicture: called.");
        saveImage();

        Uri path = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() +
                    ".fileprovider", new File(savedEditImgURL));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );
        shareIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(shareIntent, "Share..."), SHARE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SHARE_PHOTO){
            Log.d(TAG, "onActivityResult: SHARE_PHOTO resultCode: " + resultCode);
            Intent intent = new Intent(mContext, CameraActivity.class);
            startActivity(intent);
            finish();
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

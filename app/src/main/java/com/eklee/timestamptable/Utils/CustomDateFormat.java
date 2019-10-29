package com.eklee.timestamptable.Utils;


import com.eklee.timestamptable.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Judy on 2018-01-30.
 */

public class CustomDateFormat {
    private static final String TAG = "CustomDateFormat";

    public static String getCustomDate(String selectedStyle, long modified) {
        SimpleDateFormat dateFormat = null;

        if(selectedStyle.equals(String.valueOf(R.string.style1))){
            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style2))){
            dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style3))){
            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style4))){
            dateFormat = new SimpleDateFormat("yyyy.MM.dd", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style5))){
            dateFormat = new SimpleDateFormat("MMM dd, yyyy", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style6))){
            dateFormat = new SimpleDateFormat("yyyy/MM/dd", new Locale("en", "US"));
        }else if (selectedStyle.equals(String.valueOf(R.string.style7))){
            dateFormat = new SimpleDateFormat("HH:mm", new Locale("en", "US"));
        }

        // null 체크
        if(dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("en", "US"));
        }

//        if(dateFormat.equals(null)) {
//            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm aa", new Locale("en", "US"));
//        }

        return dateFormat.format(new Date(modified));
    }

}

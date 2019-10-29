package com.eklee.timestamptable.Utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

/**
 * Created by Judy on 2018-02-02.
 */

public class TextSetSpan {
    private static final String TAG = "TextSetSpan";

    /**
     * changing am/pm smaller
     * @param date
     * @return
     */
    public static SpannableString getSizChangedText(String date) {
        int size = 50;
        String str = "";
        //2018
        date = date.substring(0, date.length()-8) + "\n" + date.substring(date.length()-8, date.length());

        Log.d(TAG, "getSizChangedText: incoming text: " + date);
        SpannableString spannableString = new SpannableString(date);
        int targetStartIndex = date.length()-3;
        int targetEndIndex = date.length();
        spannableString.setSpan(new AbsoluteSizeSpan(size), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

}

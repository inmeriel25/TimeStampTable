package com.eklee.timestamptable.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Judy on 2018-01-29.
 */

public class CalculateColumn {

    public static int calculateNoOfColumns(Context context) {
        //calculating screen size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 80); //100(3columns),150(2columns)
        return noOfColumns;

    }
}

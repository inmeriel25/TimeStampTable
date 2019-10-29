package com.eklee.timestamptable.db;

import com.eklee.timestamptable.models.Settings;

import java.util.ArrayList;

/**
 * Created by Judy on 2018-01-19.
 */

public interface IDatabaseDAO {

    ArrayList<Settings> selectSettings();
    ArrayList<Settings> selectFavorite();

}

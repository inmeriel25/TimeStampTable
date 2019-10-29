package com.eklee.timestamptable.Utils;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Judy on 2018-01-28.
 */

public class FileSearch {
    private static final String TAG = "FileSearch";

    /**
     * Search a directory and return a list of all directories contained inside
     * @param directory
     * @return
     *
     * update : 2018.04.07
     * error: java.lang.NullPointerException
     * listFiles could be null, make try/catch
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        String realFileName;
        File chkFile;
        File[] listchkFiles;

        try {
            for(int i = 0; i < listFiles.length; i++){
                if(listFiles[i].isDirectory()) {
                    realFileName = listFiles[i].getPath();
                    chkFile = new File(realFileName);
                    listchkFiles = chkFile.listFiles();
                    //Log.d(TAG, "getDirectoryPaths: chkFile: " + chkFile.getName() + "******** listFiless.length:  " + listchkFiles.length);
                    if(listchkFiles.length > 0){
                        pathArray.add(listFiles[i].getAbsolutePath());
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "getFilePaths: NullPointerException" + e.getMessage() );
        }


        return pathArray;
    }

    /**
     * Search a directory and return a list of all files contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        try {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isFile()) {
                    pathArray.add(listFiles[i].getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "getFilePaths: NullPointerException" + e.getMessage() );
        }
        return pathArray;
    }
}

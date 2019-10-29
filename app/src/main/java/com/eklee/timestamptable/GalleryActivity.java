package com.eklee.timestamptable;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eklee.timestamptable.Utils.CalculateColumn;
import com.eklee.timestamptable.Utils.FileSearch;
import com.eklee.timestamptable.Utils.RecyclerViewAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Judy on 2018-01-28.
 */

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryFragment";
    private Context mContext;

    private RecyclerView recyclerView;
    private ImageView galleryImage;
    private ArrayList<String> directories;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;

    private String ROOT_DIR =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    private String CAMERA = Environment.getExternalStorageDirectory().getPath() + "/DCIM/camera";
    private String mAppend = "file:/";
    private String selectedImageURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String APP_DIR =  ROOT_DIR + "/" + getString(R.string.app_name);

        Log.d(TAG, "onCreate: started." + APP_DIR);
        mContext = GalleryActivity.this;

        recyclerView = findViewById(R.id.recyclerView);
        galleryImage = findViewById(R.id.galleryImageView);
        directories = new ArrayList<>();
        tabLayout = findViewById(R.id.gallery_tab);
        appBarLayout = findViewById(R.id.appbar);

        ImageView backScreen = findViewById(R.id.backScreen);
        backScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: back to the camera activity");
                onBackPressed();
            }
        });

        TextView nextScreen = findViewById(R.id.nextScreen);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: go to the edit activity");
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(getString(R.string.selected_image), selectedImageURL);
                startActivity(intent);
                finish();
            }
        });

        init();

    }

    /**
     * error: java.lang.IndexOutOfBoundsException
     * .get(0) could be null, checking array list size
     * update : 2018.04.07
     *
     * add a SD card directories
     * check for the external SD card pics excluding ".~~" folders
     * update : 2018.12.27
     */
    private void init(){
        //check for other folders inside "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(ROOT_DIR) != null){
            directories = FileSearch.getDirectoryPaths(ROOT_DIR);
        }
        directories.add(CAMERA);

        try {
            //check for the external SD card pics excluding ".~~" folders
            ArrayList<String> pathArray = FileSearch.getDirectoryPaths(getExternalStorageDirectories()[0]);

            for (int i=0; i < pathArray.size(); i++) {
                if(pathArray.get(i) != null) {
                    if (pathArray.get(i).toUpperCase().contains("DCIM")) {
                        directories.add(pathArray.get(i)+"/Camera");
                    }
                    else if (!pathArray.get(i).toUpperCase().contains(".")) {
                        directories.add(pathArray.get(i));
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "init: IndexOutOfBoundsException " + e.getMessage());
            Toast.makeText(mContext, "no photos in this directory", Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException e) {
            Log.e(TAG, "init: NullPointerException " + e.getMessage());
            Toast.makeText(mContext, "no photos in this directory", Toast.LENGTH_SHORT).show();
        }

        // setting folders list in TabLayout
        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i = 0; i < directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index).replace("/","");
            directoryNames.add(string);
            tabLayout.addTab(tabLayout.newTab().setText(string));
        }

        try {
            setupRecyclerView(directories.get(0));
        }
        catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "init: IndexOutOfBoundsException " + e.getMessage());
            Toast.makeText(mContext, "no photos in this directory", Toast.LENGTH_SHORT).show();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               Log.d(TAG, "onClick: tabLayout no: " + tab.getPosition());
               setupRecyclerView(directories.get(tab.getPosition()));
           }
           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }
           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });

    }


    /**
     * error: java.lang.IndexOutOfBoundsException
     * .get(0) could be null, checking array list size
     * update : 2018.04.07
     *
     * 기존에 역순이던 갤러리 사진을 최신순으로 정렬
     * update : 2018.07.17
     *
     */
    private void setupRecyclerView(String selectedDirectory){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        // 최신순으로 정렬
        Collections.reverse(imgURLs);

        //set the column width
        int mNoOfColumns = CalculateColumn.calculateNoOfColumns(getApplicationContext());
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);

        recyclerView.setLayoutManager(mGridLayoutManager);

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mAppend, imgURLs, selectedImageURL);
        recyclerView.setAdapter(adapter);

        try {
            setImage(imgURLs.get(0), galleryImage, mAppend);
            selectedImageURL = imgURLs.get(0);

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "init: IndexOutOfBoundsException " + e.getMessage());
            Toast.makeText(mContext, "no photos in this directory", Toast.LENGTH_SHORT).show();
        }  catch (NullPointerException e) {
            Log.e(TAG, "init: IndexOutOfBoundsException " + e.getMessage());
            Toast.makeText(mContext, "no photos in this directory", Toast.LENGTH_SHORT).show();
        }

        adapter.setItemClick(new RecyclerViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
                setImage(imgURLs.get(position), galleryImage, mAppend);
                selectedImageURL = imgURLs.get(position);

                //set the scroll to top
                appBarLayout.setExpanded(true);
            }
        });
    }

    
    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image. imgURL: " + imgURL);

        try {
            Glide.with(mContext)
                    .asBitmap()
                    .load(imgURL)
                    .into(image);

        } catch (Exception e) {
            Toast.makeText(mContext, "failed to load the image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent cameraIntent = new Intent(mContext, CameraActivity.class);
        startActivity(cameraIntent);
        finish();
    }


    /* returns external storage paths (directory of external memory card) as array of Strings */
    public String[] getExternalStorageDirectories() {

        List<String> results = new ArrayList<>();

        //Method 1 for KitKat & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //File[] externalDirs = Context.getExternalFilesDirs(null);
            File[] Dirs = ContextCompat.getExternalFilesDirs(this.getApplicationContext(), null);

            if (Dirs.length > 1) {
                String pathSD = Dirs[1].toString(); // Dirs[0] = 내부저장소, Dirs[1] = 외부 sdcard
                int firstOpen = pathSD.indexOf("/");
                int secondOpen = pathSD.indexOf("/", firstOpen + 1);
                int thirdOpen = pathSD.indexOf("/", secondOpen + 1);
                String filename = pathSD.substring(firstOpen, thirdOpen + 1);
                File sdFile = new File(filename);

                String path = "";
                try{
                    path = sdFile.getPath();
                }catch(NullPointerException e) {
                    Log.d(TAG, "getExternalStorageDirectories: NullPointerException " + e.getMessage());
                    e.printStackTrace();
                }

                boolean addPath = false;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try{
                        addPath = Environment.isExternalStorageRemovable(sdFile);

                    }catch (IllegalArgumentException iae) {
                        Log.d(TAG, "getExternalStorageDirectories: IllegalArgumentException " + iae.getMessage());
                        iae.printStackTrace();
                        addPath = true;

                    }catch (NullPointerException npe) {
                        Log.d(TAG, "getExternalStorageDirectories: NullPointerException " + npe.getMessage());
                        npe.printStackTrace();
                        addPath = true;
                    }
                }
                else{
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(sdFile));
                }

                if(addPath){
                    results.add(path);
                }

            }

/*            for (File file : Dirs) {

                if(file == null) //solved NPE on some Lollipop devices
                    continue;

                String path = "";

                try {
                    path = sdPath.getPath();

                } catch(NullPointerException e) {
                    Log.d(TAG, "getExternalStorageDirectories: NullPointerException " + e.getMessage());
                    e.printStackTrace();
                }

                if(path.toLowerCase().startsWith(root))
                    continue;

                boolean addPath = false;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try{
                        addPath = Environment.isExternalStorageRemovable(file);

                    }catch (IllegalArgumentException iae) {
                        Log.d(TAG, "getExternalStorageDirectories: IllegalArgumentException " + iae.getMessage());
                        iae.printStackTrace();
                        addPath = true;

                    }catch (NullPointerException npe) {
                        Log.d(TAG, "getExternalStorageDirectories: NullPointerException " + npe.getMessage());
                        npe.printStackTrace();
                        addPath = true;
                    }
                }
                else{
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if(addPath){
                    results.add(path);
                }
            }*/
        }

        //Method 2 for all versions
        if (results.isEmpty()) {
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for (String voldPoint: devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }

        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        }else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(TAG, results.get(i)+" might not be extSDcard");
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            storageDirectories[i] = results.get(i);
        }

        return storageDirectories;
    }

}

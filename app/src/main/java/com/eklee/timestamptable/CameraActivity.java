package com.eklee.timestamptable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.eklee.timestamptable.Utils.Permissions;
import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Size;
import com.otaliastudios.cameraview.SizeSelector;
import com.otaliastudios.cameraview.SizeSelectors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static java.security.AccessController.getContext;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "CameraActivity";

    private static final int RC_SETTINGS_SCREEN = 125;
    private static final int REQUEST_PERMISSION = 10;

    private Context mContext;
    private CameraView camera;

    private boolean mCapturingPicture;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.TORCH
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_torch
    };
    private int mCurrentFlash;
    private String fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_camera);

        /**
         * creating notice popup window with using SharedPreference
         * update: 2018.11.24
         * */
        // 1.saving an application name
        String version;
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); // saving a UI status
        SharedPreferences.Editor editor = pref.edit(); // call an Editor
        editor.putString("check_version", version); // put any value
        editor.apply();

        // 2.getting a saved value
        String check_version = pref.getString("check_version", "");
        String check_status = pref.getString("check_status", "");

        try {
            if (!check_version.equals(check_status)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(R.string.app_notice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .setNegativeButton(R.string.app_notice_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String version;
                                try {
                                    PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    version = i.versionName;
                                } catch (PackageManager.NameNotFoundException e) {
                                    version = "";
                                }

                                SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("check_status", version);
                                editor.commit();
                                dialog.cancel();
                            }
                        });
                final AlertDialog dialog = builder.create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.layout_notice, null);
                dialog.setView(dialogLayout);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();

            }
        } catch (Exception e) {
            Toast.makeText(mContext, "어플리케이션 실행 에러. 개발자에게 문의하세요.   (error code: 20001)", Toast.LENGTH_SHORT).show();
        }

        mContext = CameraActivity.this;
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        camera = findViewById(R.id.camera);

        // selecting proper size
       // selectorPhotoSize();

        camera.addCameraListener(new CameraListener() {
            public void onPictureTaken(byte[] jpeg) {
                Log.d(TAG, "onPictureTaken: clicked camera button.");

                if(camera.getFacing() == Facing.FRONT) {
                    createOriginalFile(rotateImage(jpeg));
                } else {
                    createOriginalFile(jpeg);
                }
            }

            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                Log.e(TAG, "onCameraError: CameraException" + exception.getMessage() );
                Toast.makeText(mContext, "카메라 에러. 개발자에게 문의하세요.  (error code: 10011)", Toast.LENGTH_SHORT).show();

            }
        });



        findViewById(R.id.flash).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        findViewById(R.id.capturePhoto).setOnClickListener(this);
        findViewById(R.id.toggleCamera).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);

        methodRequiresPermission();
    }

    /**
     * if it doesn't match with square(1:1) ratio than selecting proper ratio for specific device like LG G4
     * LG G4 = smallest(), galaxyS4 = biggest();
     * update: 2019.01.10
     * 적용 보류하고 layout에서 width=match_parent로 변경
     * update: 2019.01.31
     * */
    private void selectorPhotoSize() {
        Log.d(TAG, "selectorPhotoSize: called.");
        int heightPixels, widthPixels = 0;
        try{
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            heightPixels = metrics.heightPixels;
            widthPixels = metrics.widthPixels;
        }catch (Exception e){
            heightPixels = 1080;
            widthPixels = 1920;
        }

        SizeSelector minWidth = SizeSelectors.minWidth(widthPixels);//1080
        SizeSelector minHeight = SizeSelectors.minHeight(heightPixels);//1920
        SizeSelector dimensions = SizeSelectors.and(minHeight, minWidth);
        SizeSelector ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 0); // Matches 1:1 sizes.

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            SizeSelector result = SizeSelectors.or(
                    SizeSelectors.and(ratio, dimensions), // Try to match both constraints
                    ratio, // If none is found, at least try to match the aspect ratio
                    SizeSelectors.biggest() // If none is found, take the biggest
            );
            camera.setPictureSize(result);

        }
        else{
            SizeSelector result = SizeSelectors.or(
                    SizeSelectors.and(ratio, dimensions), // Try to match both constraints
                    ratio, // If none is found, at least try to match the aspect ratio
                    SizeSelectors.smallest() // If none is found, take the smallest
            );
            camera.setPictureSize(result);


        }
    }

    private void createOriginalFile(byte[] jpeg) {


        CameraUtils.decodeBitmap(jpeg, 2000, 2000, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                Log.d(TAG, "onBitmapReady: creating original file in directory.");

                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        mContext.getString(R.string.app_lable_name)
                );

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en", "US")).format(new Date());

                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdir();
                }

                File mediaFile = null;
                try {
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                    if (!mediaFile.exists()) {
                        mediaFile.createNewFile();
                    }
                }
                catch(Exception e){
                    Log.w("creating file error", e.toString());
                    Toast.makeText(mContext, "파일 생성 실패. 개발자에게 문의하세요. (error code: 30001)", Toast.LENGTH_SHORT).show();
                }

                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(mediaFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                byte[] bitmapData = bos.toByteArray();
//
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(mediaFile);
//                    fos.write(bitmapData);
//                    fos.flush();
//                    fos.close();
                }
                catch (FileNotFoundException e) {
                    Log.e(TAG, "onBitmapReady: FileNotFoundException: " + e.getMessage());
                    Toast.makeText(mContext, "파일 생성 실패. 개발자에게 문의하세요. (error code: 30002)", Toast.LENGTH_SHORT).show();
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

                // Media scanning for showing the saved image in Gallery
                Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri fileContentUri = Uri.fromFile(mediaFile);
                fileUri = mediaFile.getPath();

                Log.d(TAG, "onBitmapReady: uri: " +  fileUri + " fileContentUri: " + fileContentUri);
                mediaScannerIntent.setData(fileContentUri);
                mContext.sendBroadcast(mediaScannerIntent);

                goToEditActivity(fileUri);
            }
        });
    }

    private void goToEditActivity(String fileUri) {
        Log.d(TAG, "onPicture: called.");
        mCapturingPicture = false;

        long currentTimeMillis = System.currentTimeMillis();
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();

        String imgURL = fileUri.replace("file://","");

        Intent intent = new Intent(mContext, EditActivity.class);
        intent.putExtra(getString(R.string.captured_url), imgURL);
        intent.putExtra(getString(R.string.currentTime), currentTimeMillis);
        startActivity(intent);
        finish();

        mCaptureTime = 0;
        mCaptureNativeSize = null;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.flash:
                if (mCapturingPicture) return;

                ImageView flash = findViewById(R.id.flash);
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                flash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                camera.setFlash(FLASH_OPTIONS[mCurrentFlash]);

                break;
            case R.id.settings:
                Intent sIntent = new Intent(mContext, SettingsActivity.class);
                startActivity(sIntent);

                break;
            case R.id.capturePhoto:
                capturePhoto();

                break;
            case R.id.gallery:
                Intent intent = new Intent(mContext, GalleryActivity.class);
                startActivity(intent);
                finish();

                break;
            case R.id.toggleCamera:
                toggleCamera();

                break;
        }
    }



    private void capturePhoto() {
        Log.d(TAG, "capturePhoto: called.");
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getPictureSize();
//        camera.capturePicture(); // origin size not ratio 1:1 high quality than snapshot
        camera.captureSnapshot(); // ratio 1:1
    }


    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                break;

            case FRONT:
              //  selectorPhotoSize();

                break;
        }
    }

    /**
     * rotating front camera
     * update: 2018.05.30
     * */
    public static byte[] rotateImage(byte[] source) {
        Bitmap bmp = BitmapFactory.decodeByteArray(source, 0, source.length);
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        Bitmap m = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        m.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera.start();
        }catch (Exception e) {
            Log.e(TAG, "onResume: " + e.getMessage() );
            Toast.makeText(mContext, "카메라 실행에 오류가 발생했습니다. 개발자에게 문의하세요. (error code: 10001)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            camera.stop();
        }catch (Exception e) {
            Log.e(TAG, "onResume: " + e.getMessage() );
            Toast.makeText(mContext, "카메라 실행에 오류가 발생했습니다. 개발자에게 문의하세요. (error code: 10002)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            camera.destroy();
        }catch (Exception e) {
            Log.e(TAG, "onResume: " + e.getMessage() );
            Toast.makeText(mContext, "카메라 실행에 오류가 발생했습니다. 개발자에게 문의하세요. (error code: 10003)", Toast.LENGTH_SHORT).show();
        }
    }

    //************************* Permission ************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been granted
        try {
            camera.start();
        }catch (Exception e) {
            Log.e(TAG, "onResume: " + e.getMessage() );
            Toast.makeText(mContext, "카메라 실행에 오류가 발생했습니다. 개발자에게 문의하세요. (error code: 10001)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been denied
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION)
    private void methodRequiresPermission() {
        Log.d(TAG, "methodRequiresTwoPermission: require sdcard permission");
        String [] permissions = Permissions.PERMISSIONS;

        if (EasyPermissions.hasPermissions(this, permissions)) {
            // Already have permission, do nothing
        } else {
            // Do not have permissions, request them now
            Log.d(TAG, "methodRequiresTwoPermission: Do not have permissions");
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask_again),
                    REQUEST_PERMISSION, permissions);
        }
    }
}
package com.android.example.myprojectmk2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Camera extends AppCompatActivity {

    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        // 請求相機權限
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, Configuration.REQUIRED_PERMISSIONS,
                    Configuration.REQUEST_CODE_PERMISSIONS);
        }

        Button camera_capture_button = findViewById(R.id.camera_capture_button);
        camera_capture_button.setOnClickListener(v -> takePhoto());

        // 設置照片保存的位置
        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();


    }

    private void takePhoto() {
        if (imageCapture != null) {
            // 带時間路徑確保唯一名
            File photoFile = new File(outputDirectory,
                    new SimpleDateFormat(Configuration.FILENAME_FORMAT,
                            Locale.SIMPLIFIED_CHINESE).format(System.currentTimeMillis())
                            + ".jpg");

            //指定照片的輸出方式
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                    .Builder(photoFile)
                    .build();


            imageCapture.takePicture(outputFileOptions,
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {//保存照片時的回調
                        Intent intent = new Intent(Camera.this, UploadImagetoMySQL.class);
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri savedUri = Uri.fromFile(photoFile);
                            String msg = "照片捕捉成功! " + savedUri;
                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d(Configuration.TAG, msg);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(Configuration.TAG, "Photo capture failed: " + exception.getMessage());
                        }
                    });
        }
    }


    private void startCamera() {
        // 將Camera的生命週期和Activity綁定在一起（設定生命週期所有者），這樣就不用手動控制相機的啟動和關閉
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                //將相機和當前生命週期的所有者綁定所需的對象
                ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();

                //創建一個Preview 實例，並設置該實例的 surface 提供者（provider）
                PreviewView viewFinder = (PreviewView)findViewById(R.id.viewFinder);
                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                //選擇後置攝像頭作為默認攝像頭
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                //創建拍照所需的實例
                imageCapture = new ImageCapture.Builder().build();

                //設置預覽幀分析
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, new MyAnalyzer());

                //重新綁定用例前先解綁
                processCameraProvider.unbindAll();

                //綁定用例至相機
                processCameraProvider.bindToLifecycle(Camera.this, cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis);

            } catch (Exception e) {
                Log.e(Configuration.TAG, "相機绑定失败！" + e);
            }
        }, ContextCompat.getMainExecutor(this));

    }

    //檢查所有所需的權限
    private boolean allPermissionsGranted() {
        for (String permission : Configuration.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static class MyAnalyzer implements ImageAnalysis.Analyzer{
        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public void analyze(@NonNull ImageProxy image) {
            Log.d(Configuration.TAG, "Image's stamp is " + Objects.requireNonNull(image.getImage()).getTimestamp());
            image.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Configuration.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "無法獲得權限！", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //獲取輸出目錄
    private File getOutputDirectory() {
        //獲取外部媒體目錄
        File mediaDir = new File(getExternalMediaDirs()[0], getString(R.string.app_name));
        //檢查目錄是否已存在,或以成功創建
        boolean isExist = mediaDir.exists() || mediaDir.mkdir();

        return isExist ? mediaDir : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    static class Configuration {
        public static final String TAG = "CameraxBasic";
        public static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        public static final int REQUEST_CODE_PERMISSIONS = 10;
        public static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    }
}
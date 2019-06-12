package com.raven.demo;

import android.Manifest;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // 控件
    Button btCamera,btSinglePic,btMultiPic;

    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 1;

    //单张请求码
    private static final int SINGLE_REQUEST_CODE = 2;

    // 多张
    private static final int MULTI_REQUEST_CODE = 3;

    // 权限集合
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化view
        btCamera = findViewById(R.id.bt_camera);
        btSinglePic = findViewById(R.id.bt_single_pic);
        btMultiPic = findViewById(R.id.bt_multi_pic);

        // 增添点击事件
        btCamera.setOnClickListener(this);
        btSinglePic.setOnClickListener(this);
        btMultiPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.bt_camera == id){
            // 调用系统相机
            callSystemCamera();
        }else if(R.id.bt_single_pic == id){
            // 调用单张相册
            callSinglePic();
        }else if(R.id.bt_multi_pic == id){
            // 调用多张相册选择
            callMultiPic();
        }
    }

    /**
     * 调用系统相机，包含权限检测
     */
    private void callSystemCamera() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            useCamera();
        } else {
            //没有打开相关权限、申请权限
            getPermission();
        }
    }

    /**
     * 调用相机
     */
    private void useCamera() {
        Intent intent = new Intent();
        //此处之所以诸多try catch，为了兼容
        intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * 调用系统相册，选择单张图片，包含权限检测
     */
    private void callSinglePic() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            chooseSinglePhoto();
        } else {
            //没有打开相关权限、申请权限
            getPermission();
        }
    }

    /**
     * 选择单张图片
     */
    private void chooseSinglePhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SINGLE_REQUEST_CODE);
    }

    /**
     * 调用第三方相册，选择多张图片
     */
    private void callMultiPic() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            chooseSMultiPhotos();
        } else {
            //没有打开相关权限、申请权限
            getPermission();
        }
    }

    private void chooseSMultiPhotos() {
        // 初始化相册引擎
        EasyPhotos.createAlbum(this, false, GlideEngine.getInstance())
                .setCount(20)        // 设置最大选取张数
                .setCleanMenu(false)
                .setPuzzleMenu(false)
                .start(MULTI_REQUEST_CODE);
    }


    /**
     * 处理相机或者相册的回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        传出data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 相机回调处理
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this,"拍照完成",Toast.LENGTH_SHORT).show();
        }

        // 多张回调处理
        if (requestCode == SINGLE_REQUEST_CODE && resultCode == RESULT_OK) {
            String photoPath = AlbumUtil.getRealPathFromUri(this,
                    data != null ? data.getData() : null);
            Toast.makeText(this,"选择的照片路径为"+photoPath,Toast.LENGTH_SHORT).show();
        }

            // 多张回调处理
        if (requestCode == MULTI_REQUEST_CODE && resultCode == RESULT_OK) {
            // 获取选择图片集合路径
            ArrayList<String> photosPath = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
            Toast.makeText(this, String.format("共选择了%d张图片", photosPath.size()),Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getPermission() {
        EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}

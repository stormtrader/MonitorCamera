package com.example.administrator.monitorcamera;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class CameraFragment extends Fragment{

    //startActivityForResult的请求常量
    private final static int REQUEST_DELETE_PHOTO = 1;

    //自定义时间类
    //private MyTime mTime=new MyTime();

    //相机类
    private Camera mCamera;

    //预览视图的接口
    private SurfaceHolder mSurfaceHolder;

    //进度条控件
    private View mProgressContainer;

    //当前打开的是哪一个摄像头
    private int switchCamera=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub


        //生成fragment视图
        View v = inflater.inflate(R.layout.fragment_camera, container,false);

        //隐藏进度条控件
        mProgressContainer = v.findViewById(R.id.camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        //显示最新照片的缩略图的按钮实例化
        Button viewButton = (Button) v.findViewById(R.id.camera_view_button);
        //最新照片的缩略图的按钮监听器
        viewButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                //  跳转ViewPagerActivity，请求ViewPagerActivity执行删除图片操作
                /*
                Intent i = new Intent();
                i.setClass(getActivity(), ViewPagerActivity.class);
                startActivityForResult(i, REQUEST_DELETE_PHOTO);
                */
            }
        });


        //切换镜头按钮实例化
        Button rotationViewButton = (Button) v.findViewById(R.id.camera_rotationview_button);
        //切换镜头按钮监听器
        rotationViewButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                //如果摄像头数目小于等于1，该按钮无效，返回
                if (Camera.getNumberOfCameras() <= 1) {
                    return ;
                }

                if(switchCamera == 1) {
                    //停掉原来摄像头的预览，并释放原来摄像头
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;

                    //打开当前选中的摄像头
                    switchCamera = 0;
                    mCamera = Camera.open(switchCamera);
                    try {
                        //通过surfaceview显示取景画面
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //开始预览
                    mCamera.startPreview();
                }else {
                    //停掉原来摄像头的预览，并释放原来摄像头
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;

                    //打开当前选中的摄像头
                    switchCamera = 1;
                    mCamera = Camera.open(switchCamera);

                    try {
                        //通过surfaceview显示取景画面
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //开始预览
                    mCamera.startPreview();
                }
            }

        });


        //照相按钮实例化
        Button takePictureButton = (Button) v.findViewById(R.id.camera_takepicture_button);
        //照相按钮监听器
        takePictureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (mCamera!=null) {
                    //相机的拍照方法
                    mCamera.takePicture(

                            //第一个回调方法，快门回调方法
                            new ShutterCallback() {
                                @Override
                                public void onShutter() {
                                    // TODO Auto-generated method stub
                                    //该方法回触发快门声音告知用户，并设置进度条显示
                                    mProgressContainer.setVisibility(View.VISIBLE);
                                }
                            }
                            //第二个，第三个回调方法为空
                            , null,null,
                            //最后一个回调方法，jpg图像回调方法
                            new PictureCallback() {

                                @Override
                                public void onPictureTaken(byte[] date, Camera camera) {
                                    // TODO Auto-generated method stub

                                    //根据当前时间自定义格式生成文件名
                                    //String filename = mTime.getYMDHMS()+".jpg";
                                    String filename = "a.jpg";
                                    //文件输出流
                                    FileOutputStream os = null;
                                    //默认文件保存成功
                                    boolean success = true;

                                    try {
                                        //私有打开应用沙盒文件夹下文件
                                        os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                                        //写文件
                                        os.write(date);

                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        success = false;
                                    }finally{

                                        try {
                                            if (os != null) {
                                                os.close();
                                            }
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            success = false;
                                        }
                                    }

                                    if (success) {
                                        //如果文件保存成功，进度条隐藏
                                        mProgressContainer.setVisibility(View.INVISIBLE);
                                        //再次预览
                                        try {
                                            mCamera.startPreview();
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            mCamera.release();
                                            mCamera=null;
                                        }
                                    }
                                }
                            });
                }

            }
        });

        //预览视图实例化
        SurfaceView mSurfaceView = (SurfaceView) v.findViewById(R.id.camera_surfaceView);
        //得到预览视图接口
        mSurfaceHolder = mSurfaceView.getHolder();
        //设置预览视图接口类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //添加预览视图接口的回调程序，监听视图的生命周期
        mSurfaceHolder.addCallback(new Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                // TODO Auto-generated method stub

                //当SurfaceView的视图层级结构被放到屏幕上时候，连接Camera和Surface
                try {

                    if (mCamera!=null) {
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }



            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                // TODO Auto-generated method stub

                //当Surface首次显示在屏幕上时候，设置好相机参数，开始预览
                if (mCamera==null) {
                    return;
                }

                setParameter(w, h);
                /*
                Camera.Parameters parameters = mCamera.getParameters();
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);

                parameters.setPreviewSize(s.width, s.height);

                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);

                Log.i("with, height", "width=" + s.width + ", height=" + s.height);
                parameters.setPictureSize(s.width, s.height);

                mCamera.setParameters(parameters);
                */
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    // TODO: handle exception
                    mCamera.release();
                    mCamera=null;
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

                //当视图从屏幕上移除的时候，停止预览
                if (mCamera!=null) {
                    mCamera.stopPreview();
                }
            }


        });

        return v;
    }

    private void setParameter(int wDisplay, int hDisplay) {

        Camera.Parameters parameters = mCamera.getParameters(); // 获取各项参数
        parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        parameters.setJpegQuality(100); // 设置照片质量
        //获得相机支持的照片尺寸,选择合适的尺寸
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        int maxSize = Math.max(wDisplay, hDisplay);
        int length = sizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < length; i++) {
                if (maxSize <= Math.max(sizes.get(i).width, sizes.get(i).height)) {
                    parameters.setPictureSize(sizes.get(i).width, sizes.get(i).height);
                    break;
                }
            }
        }
        List<Camera.Size> ShowSizes = parameters.getSupportedPreviewSizes();
        int showLength = ShowSizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < showLength; i++) {
                if (maxSize <= Math.max(ShowSizes.get(i).width, ShowSizes.get(i).height)) {
                    parameters.setPreviewSize(ShowSizes.get(i).width, ShowSizes.get(i).height);
                    Log.i("setparame", "wDisplay="+wDisplay + ", hdisplay="+hDisplay);
                    Log.i("setparame", "width="+ShowSizes.get(i).width + ", ShowSizes.get(i).height="+ShowSizes.get(i).height);

                    break;
                }
            }
        }


        mCamera.setParameters(parameters);
    }


    /******************************************]
     *
     * 穷举法找出具有最大数目像素的尺寸
     *
     * @param sizes
     * @param width
     * @param height
     * @return
     */
    public Size getBestSupportedSize(List<Size> sizes,int width,int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width*bestSize.height;
        for (Size s :sizes) {
            int area =s.width*s.height;
            if (area>largestArea) {
                bestSize=s;
                largestArea = area;
            }
        }
        return bestSize;
    }


    //接收活动结果，响应startActivityForResult()
    @Override
    public void onActivityResult(int request, int result, Intent mIntent) {
        // TODO Auto-generated method stub

        if (request == REQUEST_DELETE_PHOTO) {

            if (result == Activity.RESULT_OK) {
                //  跳转ViewPagerActivity，请求ViewPagerActivity执行删除图片操作

                /*int requestCode = 1;
                Intent i = new Intent();
                i.setClass(getActivity(), ViewPagerActivity.class);
                startActivityForResult(i, requestCode);
                 */

            }


        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("onPause");
        //程序中止暂停时，释放Camera
        if (mCamera!=null) {
            mCamera.release();
            mCamera=null;
        }

    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("onStop");
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //程序运行时，打开Camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(switchCamera);

        }else {
            mCamera = Camera.open();
        }

    }



}


package com.example.administrator.monitorcamera;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.OrientationEventListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

public class TestCameraActivity extends Activity {

    Camera camera;
    Button snap;
    Button switchCamera;
    
    SurfaceView surfaceView;
    int camera_id = 0;
    IOrientationEventListener iOriListener;
    
    final int SUCCESS = 233;
    final int DIFFN = 234;
    SnapHandler handler = new SnapHandler();

    PicProcess picProc = new PicProcess();
    
    int camera_direction = CameraInfo.CAMERA_FACING_BACK;

    private static final String TAG="TestCameraActivity";
    private static final String picDir = "/storage/sdcard0/testcamera/";

    boolean if_begin_task =  false;
    private class TakePic extends TimerTask {// public abstract class TimerTask implements Runnable{}
        @Override
        public void run() {
            if (if_begin_task) {

                // TODO Auto-generated method stub
                camera.takePicture(null, null, new PictureCallback(){

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        // TODO Auto-generated method stub
                        final byte[] tempdata = data;

                        Log.i("test", "take pic: i");

                        Thread thread = new Thread(new Runnable(){

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                File dir = new File(picDir);
                                if(!dir.exists())
                                {
                                    dir.mkdirs();
                                }
                                String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance()) + ".jpg";
                                String picPath = picDir + name;
                                File f = new File(picPath);
                                if(!f.exists())
                                {
                                    try {
                                        f.createNewFile();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                FileOutputStream outputStream;
                                try {
                                    outputStream = new FileOutputStream(f);
                                    outputStream.write(tempdata);
                                    outputStream.close();
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                Log.v("TestCameraActivityTag", "store success");
                                handler.sendEmptyMessage(SUCCESS);

                                int diffN = picProc.GetDiffN(picPath);
                                if (diffN > 1000) {
                                    String tmpStr = "Diff N = " + diffN;
                                    Message msg = new Message();
                                    msg.what = DIFFN;
                                    msg.obj = tmpStr;
                                    handler.sendMessage(msg);

                                    //SMSSender.sendSMS("13802258141", "Diff N ="+diffN);
                                }
                            }

                        });

                        thread.start();

                    }

                });
                /*
                Log.i("test", "before sleep 10000");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("test", "after sleep 10000");
                */

            }
        }
    }


    public void switchCamera()
    {
        if(camera_direction == CameraInfo.CAMERA_FACING_BACK)
        {
            camera_direction = CameraInfo.CAMERA_FACING_FRONT;
        }
        else
        {
            camera_direction = CameraInfo.CAMERA_FACING_BACK;
        }
        int mNumberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == camera_direction)
            {
                camera_id = i;
            }
        }
        if(null != camera)
        {
            camera.stopPreview();
            camera.release();
        }
        camera = Camera.open(camera_id);
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.startPreview();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        setCameraAndDisplay(surfaceView.getWidth(), surfaceView.getHeight());
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");
    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type
        String where = " address = '13802258141' AND date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            //String number = cur.getString(cur.getColumnIndex("address"));//手机号
            //String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));
            Log.v(TAG, "sms body="+body);
            SMSSender.sendSMS("13802258141", "Good State");
        }
    }


    private SmsObserver smsObserver;


    public Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO

    };

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            Log.i(TAG, "get new message");
            getSmsFromPhone();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_incell_camera);

        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true,
                smsObserver);

        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        switchCamera = (Button) this.findViewById(R.id.switch_btn);
        switchCamera.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switchCamera();
            }
            
        });
        snap = (Button) this.findViewById(R.id.snap);
        snap.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                    if_begin_task = true;
            }
            
        });
  
        
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new Callback(){

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                int mNumberOfCameras = Camera.getNumberOfCameras();

                // Find the ID of the default camera
                CameraInfo cameraInfo = new CameraInfo();
                for (int i = 0; i < mNumberOfCameras; i++)
                {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
                    {
                        camera_id = i;
                    }
                }
                camera = Camera.open(camera_id);
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview(); // ��ʼԤ��  

                    iOriListener.enable();
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
                
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height) {
                // TODO Auto-generated method stub
                setCameraAndDisplay(width, height);
                
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                if(null != camera)
                {
                    camera.release();
                    camera = null;
                }
                
            }
            
        });
        
        iOriListener = new IOrientationEventListener(this);

        Intent logs=new Intent(this,LogService.class);
        startService(logs);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TakePic(), 1, 10000);
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.iOriListener.disable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if_begin_task = false;
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getApplicationContext(), mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");
    }

    public class IOrientationEventListener extends OrientationEventListener{

        public IOrientationEventListener(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }


        @Override
        public void onOrientationChanged(int orientation) {
            // TODO Auto-generated method stub
            if(ORIENTATION_UNKNOWN == orientation)
            {
                return;
            }
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(camera_id, info);
            orientation = (orientation + 45) / 90 * 90;
            int rotation = 0;
            if(info.facing == CameraInfo.CAMERA_FACING_FRONT)
            {
                rotation = (info.orientation - orientation + 360) % 360;
            }
            else
            {
                rotation = (info.orientation + orientation) % 360;
            }
            if(null != camera)
            {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setRotation(rotation);
                camera.setParameters(parameters);
            }
            
        }
        
    }
    
    public void setCameraAndDisplay(int width, int height)
    {
        Camera.Parameters parameters = camera.getParameters();
        Log.i(TAG, "displaywidth="+width+", displayheight="+height);

        Log.i(TAG, "GetPicture szie");
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        Size picSize = CameraUtils.getProperSize(pictureSizeList, ((float)width)/height);
        if(null != picSize)
        {
            Log.i(TAG, "picSize.width="+picSize.width+", picSize.height="+picSize.height);
            parameters.setPictureSize(picSize.width, picSize.height);
        }
        else
        {
            picSize = parameters.getPictureSize();
        }

        Log.i(TAG, "get preview szie");
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Size preSize = CameraUtils.getProperSize(previewSizeList, ((float)width)/height);
        if(null != preSize)
        {Log.v("TestCameraActivityTag", preSize.width + "," + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
        

        float w = picSize.width;
        float h = picSize.height;
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams( (int)(height*(w/h)), height)); 
        
        parameters.setJpegQuality(100);
        

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        camera.cancelAutoFocus();
        camera.setDisplayOrientation(0);
        camera.setParameters(parameters);
    }
    
    class SnapHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == SUCCESS)
            {
                Toast.makeText(TestCameraActivity.this, "store success", Toast.LENGTH_SHORT).show();
            } else if (msg.what == DIFFN) {
                Toast.makeText(TestCameraActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
            }

            try {
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.startPreview();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
}

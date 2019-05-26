package com.paul.simpletools;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.github.chrisbanes.photoview.PhotoView;
import com.paul.simpletools.LessonAlbum.AlbumViewPhotoActivity;
import com.paul.simpletools.Tools.UsersEditActivity;
import com.paul.simpletools.dataBase.EveryDayBean;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;
import com.paul.simpletools.dataBase.TermData;

import org.litepal.LitePal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class PhotoTestActivity extends AppCompatActivity {
    private PhotoView photoView;
    private ProgressDialog pDialog = null;
    private ImageButton btn_ocr,btn_delete,btn_back,btn_note;
    private Integer iCount = 0;
    private  File file;
    private static final String TAG = "PhotoTestActivity";

    private String mImagePath;                   //用于存储最终目录，即根目录 / 要操作（存储文件）的文件夹

    private Uri mImageUri;                                  //指定的uri

    private String mImageName;                              //保存的图片的名字

    private File mImageFile;                                //图片文件

    private String courseName;                              //传过来的课程名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view_photo);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        courseName=getIntent().getStringExtra("courseName");
        Log.d(TAG, "开始...");
        initOCR();
        initData();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //检查是否获得写入权限，未获得则向用户请求
        if (ActivityCompat.checkSelfPermission(PhotoTestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //未获得，向用户请求
            Log.d(TAG, "无读写权限，开始请求权限。");
            ActivityCompat.requestPermissions(PhotoTestActivity.this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        } else {
            Log.d(TAG, "有读写权限，准备启动相机。");
            //启动照相机

            startCamera();
        }

    }
    void initData()
    {
        photoView=findViewById(R.id.pv_1);
        btn_ocr=findViewById(R.id.ib_ocr);
        btn_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected(PhotoTestActivity.this)) {
                    showprocess();
                    getOCR(mImagePath);
                }
                else
                {
                    Toasty.info(PhotoTestActivity.this,"没有网哦~",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        btn_delete=findViewById(R.id.ib_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto();
            }
        });
        btn_back=findViewById(R.id.album_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_note=findViewById(R.id.ib_note);
        btn_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(PhotoTestActivity.this,"功能还在开发中哦！",Toasty.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 返回用户是否允许权限的结果，并处理
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == 200) {
            //用户允许权限
            if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "用户已允许权限，准备启动相机。");
                //启动照相机
                startCamera();
            } else {  //用户拒绝
                Log.d(TAG, "用户已拒绝权限，程序终止。");
                Toast.makeText(this, "程序需要写入权限才能运行", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 启动相机，创建文件，并要求返回uri
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startCamera() {
        Intent intent = new Intent();
        //指定动作，启动相机
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Log.d(TAG, "指定启动相机动作，完成。");
        //创建文件
        createImageFile();
        Log.d(TAG, "创建图片文件结束。");
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d(TAG, "添加权限。");
        //获取uri
        mImageUri = FileProvider.getUriForFile(this, "com.paul.simpletools.provider", mImageFile);
        Log.d(TAG, "根据图片文件路径获取uri。");
        //将uri加入到额外数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        Log.d(TAG, "将uri加入启动相机的额外数据。");
        Log.d(TAG, "启动相机...");
        //启动相机并要求返回结果
        startActivityForResult(intent, MySupport.PHOTO_RESULT_CODE);
        Log.d(TAG, "拍摄中...");
    }

    /**
     * 创建图片文件
     */
    private void createImageFile(){
        Log.d(TAG, "开始创建图片文件...");
        String timeStamp = new SimpleDateFormat("MMdd_HHmm").format(new Date());
        //设置图片文件名（含后缀），以当前时间的毫秒值为名称
        mImageName = "课表拍拍_" + courseName+"_"+timeStamp+".jpg";
        Log.d(TAG, "设置图片文件的名称为："+mImageName);
        //创建图片文件
        mImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + MySupport.SD_APP_DIR_NAME + "/" +MySupport.PHOTO_DIR_NAME + "/", mImageName);
        //将图片的绝对路径设置给mImagePath，后面会用到
        mImagePath = mImageFile.getAbsolutePath();
        Log.d("你好",mImagePath);
        //按设置好的目录层级创建
        mImageFile.getParentFile().mkdirs();
        Log.d(TAG, "按设置的目录层级创建图片文件，路径："+mImagePath);
        //不加这句会报Read-only警告。且无法写入SD
        mImageFile.setWritable(true);
        Log.d(TAG, "将图片文件设置可写。");
    }

    /**
     * 处理返回结果。
     * 1、图片
     * 2、音频
     * 3、视频
     *
     * @param requestCode 请求码
     * @param resultCode  结果码 成功 -1 失败 0
     * @param data        返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "拍摄结束。");
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "返回成功。");
            Log.d(TAG, "请求码：" + requestCode + "  结果码：" + resultCode + "  data：" + data);
            switch (requestCode) {
                case MySupport.PHOTO_RESULT_CODE: {
                    Bitmap bitmap = null;
                    try {
                        //根据uri设置bitmap
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                        photoView.setImageBitmap(rotaingImageView(readPictureDegree(mImagePath),bitmap));
                        //imageView.setImageBitmap(rotaingImageView(readPictureDegree(mImagePath),bitmap));
                        //Toast.makeText(PhotoTestActivity.this,mImageUri.getPath(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "根据uri设置bitmap。");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //将图片保存到SD的指定位置
                    savePhotoToSD(bitmap);
                    //更新系统图库
                    updateSystemGallery();
                    Log.d(TAG, "结束。");
                    break;

                }
            }
        }
        else
        {
            //Toast.makeText(PhotoTestActivity.this,"错误！",Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    /**
     * 保存照片到SD卡的指定位置
     */
    private void savePhotoToSD(Bitmap bitmap) {
        Log.d(TAG, "将图片保存到指定位置。");
        //创建输出流缓冲区
        BufferedOutputStream os = null;
        try {
            //设置输出流
            os = new BufferedOutputStream(new FileOutputStream(mImageFile));
            Log.d(TAG, "设置输出流。");
            //压缩图片，100表示不压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            Log.d(TAG, "保存照片完成。");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    //不管是否出现异常，都要关闭流
                    os.flush();
                    os.close();
                    Log.d(TAG, "刷新、关闭流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新系统图库
     */
    private void updateSystemGallery() {
        //把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    mImageFile.getAbsolutePath(), mImageName, null);
            Log.d(TAG, "将图片文件插入系统图库。");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImagePath)));
        Log.d(TAG, "通知系统图库更新。");
    }

    void initOCR()
    {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.d("百度初始化","成功！");
            }

            @Override
            public void onError(OCRError error)
            {
                Log.d("百度初始化",error.getCause().toString());
                error.printStackTrace();
            }
        }, this.getApplicationContext(), "GeB7fIW57MSqx3zk3ZGZxujG", "9PA9AmpiEvd7UOxZo8giEouIbaDfdcGa");

    }
    public void getOCR(String filePath) {
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        //这里调用的是本地文件，使用时替换成你的本地文件
        File file=new File(filePath);
        Log.d("百度",filePath);
        param.setImageFile(file);
        // 调用通用文字识别服务
        OCR.getInstance(this).recognizeAccurateBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                // 调用成功，返回GeneralResult对象
                for (WordSimple wordSimple : result.getWordList()) {
                    // wordSimple不包含位置信息
                    WordSimple word = wordSimple;
                    sb.append(word.getWords());
                    //sb.append("\n");
                }
                //file.delete();
                //String返回
                pDialog.cancel();
                showAlterDialog(sb.toString());

                // json格式返回字符串result.getJsonRes())
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                Log.d("百度",error.getCause().toString());
                Toast.makeText(PhotoTestActivity.this,"识别失败！！！",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void showAlterDialog(String message){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(PhotoTestActivity.this);
        alterDiaglog.setIcon(R.mipmap.ic_launcher);//图标
        alterDiaglog.setTitle("文字识别结果");//文字
        alterDiaglog.setMessage(message);//提示消息
        final String tag=message;
        //积极的选择
        alterDiaglog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toasty.success(PhotoTestActivity.this,"哈哈(#^.^#)",Toast.LENGTH_SHORT).show();
            }
        });
        alterDiaglog.setNegativeButton("复制内容",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(PhotoTestActivity.CLIPBOARD_SERVICE);
                //创建ClipData对象
                //第一个参数只是一个标记，随便传入。
                //第二个参数是要复制到剪贴版的内容
                ClipData clip = ClipData.newPlainText("课表拍拍",tag );
                //传入clipdata对象.
                clipboard.setPrimaryClip(clip);
                Toasty.success(PhotoTestActivity.this,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
            }
        });

        //显示
        alterDiaglog.show();
    }


    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    void showprocess() {

        pDialog = new ProgressDialog(PhotoTestActivity.this);
        pDialog.setTitle("文字识别");
        pDialog.setIcon(R.mipmap.ic_launcher);
        pDialog.setMessage("正在识别中，速度取决于您的网速和照片大小，请耐心等待！");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();

        // 创建线程实例
        new Thread() {
            public void run() {

                try {
                    while (iCount <= 50) {
                        // 由线程来控制进度。
                        pDialog.setProgress(iCount++);
                        Thread.sleep(200);
                    }
                    pDialog.cancel();
                    //Toasty.error(PhotoTestActivity.this,"识别超时！",Toasty.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    pDialog.cancel();
                }
            }

        }.start();


    }
    void deletePhoto()
    {
        file=new File(mImagePath);
        final android.app.AlertDialog.Builder normalDialog =
            new android.app.AlertDialog.Builder(this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("警告");
        normalDialog.setMessage("确认删除该照片吗？删除后不可恢复！");
        normalDialog.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(file.exists())
                        {
                            file.delete();
                            Toasty.success(PhotoTestActivity.this,"删除成功",Toasty.LENGTH_SHORT).show();
                           finish();
                        }
                        else
                        {
                            Toasty.error(PhotoTestActivity.this,"删除失败，文件不存在！",Toasty.LENGTH_SHORT).show();
                        }
                    }


                });
        normalDialog.setNegativeButton("手滑了~",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getContext(),"吓死我了···",Toast.LENGTH_SHORT).show();
                        //Toasty.error(PhotoTestActivity.this, "吓死我了···", Toast.LENGTH_SHORT, true).show();
                    }
                });
        normalDialog.show();
    }
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}

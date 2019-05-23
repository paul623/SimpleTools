package com.paul.simpletools.LessonAlbum;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.paul.simpletools.PhotoTestActivity;
import com.paul.simpletools.R;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class AlbumViewPhotoActivity extends AppCompatActivity {
    private PhotoView photoView;
    private String photoPath;
    private ProgressDialog pDialog = null;
    private ImageButton btn_ocr,btn_delete,btn_back,btn_note;
    private Integer iCount = 0;
    private  File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view_photo);
        Intent intent=getIntent();
        photoPath=intent.getStringExtra("照片地址");
        photoView=findViewById(R.id.pv_1);
        file=new File(photoPath);
        Glide.with(this).load(file).into(photoView);
        initData();
    }
    private void initData()
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
        btn_ocr=findViewById(R.id.ib_ocr);
        btn_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showprocess();
                getOCR(photoPath);
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
                Toasty.info(AlbumViewPhotoActivity.this,"功能还在开发中哦！",Toasty.LENGTH_SHORT).show();
            }
        });
    }
    private void deletePhoto()
    {
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
                            Toasty.success(AlbumViewPhotoActivity.this,"删除成功",Toasty.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            //把返回数据存入Intent
                            intent.putExtra("result", "delete");
                            //设置返回数据
                            AlbumViewPhotoActivity.this.setResult(RESULT_OK, intent);
                            finish();
                        }
                    }


                });
        normalDialog.setNegativeButton("手滑了~",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getContext(),"吓死我了···",Toast.LENGTH_SHORT).show();
                        Toasty.error(AlbumViewPhotoActivity.this, "吓死我了···", Toast.LENGTH_SHORT, true).show();
                    }
                });
        normalDialog.show();
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
                Toasty.error(AlbumViewPhotoActivity.this,"识别失败！！！",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void showAlterDialog(String message){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(AlbumViewPhotoActivity.this);
        alterDiaglog.setIcon(R.mipmap.ic_launcher);//图标
        alterDiaglog.setTitle("文字识别结果");//文字
        alterDiaglog.setMessage(message);//提示消息
        final String tag=message;
        //积极的选择
        alterDiaglog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toasty.success(AlbumViewPhotoActivity.this,"哈哈(#^.^#)",Toast.LENGTH_SHORT).show();
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
                Toasty.success(AlbumViewPhotoActivity.this,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
            }
        });

        //显示
        alterDiaglog.show();
    }
    private void showprocess() {

        pDialog = new ProgressDialog(AlbumViewPhotoActivity.this);
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
                        Thread.sleep(80);
                    }
                    pDialog.cancel();
                    //Toasty.error(PhotoTestActivity.this,"识别超时！",Toasty.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    pDialog.cancel();
                }
            }

        }.start();


    }

}

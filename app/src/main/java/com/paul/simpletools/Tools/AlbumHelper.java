package com.paul.simpletools.Tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.paul.simpletools.dataBase.MyPhotoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/23 13：17
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class AlbumHelper {
    public static List<MyPhotoBean> getMyPhotoBean()
    {
        String Path="/SimpleTools/photo";
        List<MyPhotoBean> photoBeans=new ArrayList<>();
        List<String> names=getFileName(Environment.getExternalStorageDirectory()+Path);
        for(String photo:names)
        {
            MyPhotoBean myPhotoBean=splitName(photo);
            if(myPhotoBean!=null)
            {
                photoBeans.add(myPhotoBean);
                //myPhotoBean.save();
            }
        }
        return photoBeans;
    }
    public static List<String> getPhotoName(List<MyPhotoBean> myPhotoBeans)
    {
        List<String> names=new ArrayList<>();
        Object object;
        for(MyPhotoBean myPhotoBean:myPhotoBeans)
        {
            names.add(myPhotoBean.getLessonName());
        }
        object=names.stream().distinct().collect(Collectors.toList());
        names=(List<String>) object;
        return names;
    }
    public static List<MyPhotoBean> splitWithName(List<MyPhotoBean> myPhotoBeans,String name)
    {
        List<MyPhotoBean> result=new ArrayList<>();
        for(MyPhotoBean myPhotoBean:myPhotoBeans)
        {
            if(myPhotoBean.getLessonName().equals(name))
            {
                result.add(myPhotoBean);
            }
        }
        return result;
    }
    private static List<String> getFileName(String fileAbsolutePath) {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                Log.e("eee", "文件名 ： " + filename);
                vecFile.add(filename);
            }
        }
        return vecFile;
    }
    private static MyPhotoBean splitName(String name)
    {
        String Path="/SimpleTools/photo/";
        MyPhotoBean myPhotoBean=new MyPhotoBean();
        myPhotoBean.setPhotoName(name);
        myPhotoBean.setPhotoPath(Environment.getExternalStorageDirectory()+Path+name);
        String aa[]=name.split("_");
        if(aa.length==4)
        {
            myPhotoBean.setLessonName(aa[1]);
            if(myPhotoBean.getLessonName().equals(""))
            {
                myPhotoBean.setLessonName("其他");
            }
            myPhotoBean.setDate(aa[2]);
            String bb[]=aa[3].split("\\.");
            myPhotoBean.setTime(bb[0]);
            return myPhotoBean;
        }
        else
        {
            return null;
        }
    }

}

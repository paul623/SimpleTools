package com.paul.simpletools.classbox.api;

import org.apache.commons.codec.digest.DigestUtils;

import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 超级课程表加密方法
 * Created by Liu ZhuangFei on 2018/8/8.
 */

public class SuperEncrypt {
    //加密
    public static String encrypt(String paramString) {
        String paramString2 = getEncryptKey();
        try {
            paramString = URLEncoder.encode(paramString, "utf-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(DigestUtils.md5(paramString2), "AES");
            Cipher localCipher = Cipher.getInstance("AES");
            localCipher.init(1, secretKeySpec);
            paramString = byte2HexStr(localCipher.doFinal(paramString.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramString;
    }

    //获取加密Key
    private static String getEncryptKey() {
        String paramString = "friday_syllabus";
        int j = 0;
        String encrypt = new String();
        char[] array1 = "0123456789abcdef".toCharArray();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(paramString.getBytes());
            byte[] md5bytes = md.digest();//md5值
            char[] keychars = new char[32];
            for (int i = 0; i < 16; i++) {
                int k = md5bytes[i];
                int m = j + 1;
                keychars[j] = array1[(k >>> 4 & 0xF)];
                j = m + 1;
                keychars[m] = array1[(k & 0xF)];
            }
            encrypt = new String(keychars).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encrypt;
    }

    //字节码转16进制字符串
    private static String byte2HexStr(byte[] b) {
        String temp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            temp = Integer.toHexString(b[n] & 0xFF);
            sb.append((temp.length() == 1) ? "0" + temp : temp);
        }
        return sb.toString().toUpperCase().trim();
    }
}

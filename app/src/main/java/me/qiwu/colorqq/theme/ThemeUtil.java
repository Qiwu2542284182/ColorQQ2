package me.qiwu.colorqq.theme;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.qiwu.colorqq.util.SettingUtil;

public class ThemeUtil {
    private static final String CipherMode = "AES/CFB/NoPadding";//使用CFB加密，需要设置IV


    public static int[] sColor = {
            0x03,0x00,0x08,0x00,0x54,0x01,0x00,0x00,0x01,0x00,0x1C,0x00,0x84,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x01,0x00,0x00,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x08,0x00,0x00,0x00,
            0x12,0x00,0x00,0x00,0x3F,0x00,0x00,0x00,0x4A,0x00,0x00,0x00,0x05,0x05,0x63,0x6F,0x6C,0x6F,0x72,0x00,0x07,0x07,
            0x61,0x6E,0x64,0x72,0x6F,0x69,0x64,0x00,0x2A,0x2A,0x68,0x74,0x74,0x70,0x3A,0x2F,0x2F,0x73,0x63,0x68,0x65,0x6D,
            0x61,0x73,0x2E,0x61,0x6E,0x64,0x72,0x6F,0x69,0x64,0x2E,0x63,0x6F,0x6D,0x2F,0x61,0x70,0x6B,0x2F,0x72,0x65,0x73,
            0x2F,0x61,0x6E,0x64,0x72,0x6F,0x69,0x64,0x00,0x08,0x08,0x73,0x65,0x6C,0x65,0x63,0x74,0x6F,0x72,0x00,0x04,0x04,
            0x69,0x74,0x65,0x6D,0x00,0x00,0x00,0x00,0x80,0x01,0x08,0x00,0x0C,0x00,0x00,0x00,0xA5,0x01,0x01,0x01,0x00,0x01,
            0x10,0x00,0x18,0x00,0x00,0x00,0x02,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0x01,0x00,0x00,0x00,0x02,0x00,0x00,0x00,
            0x02,0x01,0x10,0x00,0x24,0x00,0x00,0x00,0x02,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x03,0x00,
            0x00,0x00,0x14,0x00,0x14,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x01,0x10,0x00,0x38,0x00,0x00,0x00,
            0x03,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x04,0x00,0x00,0x00,0x14,0x00,0x14,0x00,0x01,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0x08,0x00,0x00,0x1C,
            0x66,0x66,0x66,0x66,0x03,0x01,0x10,0x00,0x18,0x00,0x00,0x00,0x03,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,
            0xFF,0xFF,0x04,0x00,0x00,0x00,0x03,0x01,0x10,0x00,0x18,0x00,0x00,0x00,0x02,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,
            0xFF,0xFF,0xFF,0xFF,0x03,0x00,0x00,0x00,0x01,0x01,0x10,0x00,0x18,0x00,0x00,0x00,0x02,0x00,0x00,0x00,0xFF,0xFF,
            0xFF,0xFF,0x01,0x00,0x00,0x00,0x02,0x00,0x00,0x00
    };


    //0x80
    //0xa5
    //0xff
    //0x84

    public static boolean isDefautTheme(){
        return "0".equals(SettingUtil.getInstance().getString("current_theme_id","0"));
    }

    public static String getCurrentThemeId(){
        return SettingUtil.getInstance().getString("current_theme_id","0");
    }

    public static void setCurrentTheme(String id){
        SettingUtil.getInstance().putString("current_theme_id",id);
    }
    // /** 创建密钥 **/
    private static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append(password);
        while (sb.length() < 32) {
            sb.append("0");
        }
        if (sb.length() > 32) {
            sb.setLength(32);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    // /** 加密字节数据 **/
    private static byte[] encrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            System.out.println(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 加密(结果为16进制字符串) **/
    public static String encrypt(String password,String content ) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password);
        String result = byte2hex(data);
        return result;
    }


    // /** 解密字节数组 **/
    private static byte[] decrypt(byte[] content, String password) {

        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));

            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 解密16进制的字符串为字符串 **/
    public static String decrypt(String password,String content) {
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    // /** 字节数组转成16进制字符串 **/
    private static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuilder sb = new StringBuilder(b.length * 2);
        String tmp = "";
        for (byte aB : b) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(aB & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }

    // /** 将hex字符串转换成字节数组 **/
    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }


    public static byte[] hexStringToByte(String hex) {
        if (hex == null) {
            return null;
        }
        if (hex.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[hex.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = hex.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public static  String bytesToHexString(byte[] bArray) {
        if (bArray==null)return null;
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] readInputStream(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }


}

package me.qiwu.colorqq.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.activity.BaseApplication;

public class FileUtil {

    public static String readTextFromFile(File file){
        String content = "";
        try {
            InputStream instream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //分行读取
            while (( line = buffreader.readLine()) != null) {
                content += line + "\n";
            }
            instream.close();
        }
        catch (Exception e) {
            e.fillInStackTrace();
        }
        return content;
    }

    public static String readTextFromInputStream(InputStream inputStream){
        String content = "";
        try {
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //分行读取
            while (( line = buffreader.readLine()) != null) {
                content += line + "\n";
            }
            inputStream.close();
        }
        catch (Exception e) {
            e.fillInStackTrace();
        }
        return content;
    }

    public static boolean writeToFile(File file, String content) {
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter;
        if (file.exists())file.delete();
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /*删除文件夹所有内容（包括文件夹）
    path：文件夹路径
     */

    public static boolean delAllFile(String path) {
        if (!delAllFileInner(path)){
            return false;
        }
        return new File(path).delete();
    }

    private static boolean delAllFileInner(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFileInner(path + "/" + tempList[i]);//先删除文件夹里面的文件
                temp.delete();//再删除空文件夹
            }
        }
        return true;
    }

    public static boolean unAssetTheme(Context context,String fileNmae){
        File tempFile = null;
        File file1 = new File(getThemePath()+"默认主题");
        file1.mkdirs();
        boolean tg = false;
        try {
            tempFile = new File(BaseApplication.getContext().getDataDir(),"temp");
            FileUtil.copyFile(context.getAssets().open(fileNmae),tempFile.getAbsolutePath());
            ZipFile zipFile = new ZipFile(tempFile);
            Enumeration<? extends ZipEntry> zes = zipFile.entries();
            while (zes.hasMoreElements()) {
                ZipEntry zipEntry = zes.nextElement();
                if (zipEntry.isDirectory()){
                    File file = new File(file1,zipEntry.getName());
                    file.mkdirs();
                }else {
                    FileUtil.copyFile(zipFile.getInputStream(zipEntry),new File(file1,zipEntry.getName()).getAbsolutePath());
                }
            }
            tg = true;
        }catch (Exception e){
            tg = false;
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("错误提示").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }finally {
            if (tempFile!=null&&tempFile.exists()){
                tempFile.delete();
            }

        }
        return tg;

    }

    public static boolean unDiyTheme(Context context,File diyTheme,String id){
        boolean tg = false;
        File tempFile = null;
        try {
            tempFile = new File(BaseApplication.getContext().getDataDir(),id);
            tempFile.mkdirs();
            ZipFile zipFile = new ZipFile(diyTheme);
            Enumeration<? extends ZipEntry> zes = zipFile.entries();
            while (zes.hasMoreElements()) {
                ZipEntry zipEntry = zes.nextElement();
                if (zipEntry.isDirectory()){
                    File file = new File(tempFile,zipEntry.getName());
                    file.mkdirs();
                }else {
                    FileUtil.copyFile(zipFile.getInputStream(zipEntry),new File(tempFile,zipEntry.getName()).getAbsolutePath());
                }
            }
            tg = true;
        }catch (Exception e){
            tg = false;
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("错误提示").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }
        return tg;

    }

    public static boolean unAsset(Context context,String fileName,String path){
        boolean tg = false;
        ZipInputStream zipInputStream = null;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
            ZipEntry zipEntry ;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                if (zipEntry.isDirectory()){
                    File file = new File(path+zipEntry.getName());
                    file.mkdirs();
                }else {
                    File file = new File(path+zipEntry.getName());
                    file.getParentFile().mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        fileOutputStream.flush();
                    }
                    fileOutputStream.close();
                }
            }
            tg = true;
        }catch (Exception e){
            tg = false;
            new AlertDialog.Builder(context).setTitle("错误提示").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }finally {
            if (zipInputStream!=null){
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return tg;

    }


    public static boolean checkMusicFiles(Context context){
        File file1 = new File(getMusicPath()+"icons");
        if (file1.exists()&&file1.list()!=null&&file1.list().length!=0)return true;
        boolean tg = false;
        ZipInputStream zipInputStream = null;
        try {
            InputStream inputStream = context.getAssets().open("QQColor2.zip");
            zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
            ZipEntry zipEntry ;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                if (zipEntry.isDirectory()){
                    File file = new File(getModulePath()+zipEntry.getName());
                    file.mkdirs();
                }else {
                    File file = new File(getModulePath()+zipEntry.getName());
                    if (file.getPath().contains("music")){
                        file.getParentFile().mkdirs();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                            fileOutputStream.flush();
                        }
                        fileOutputStream.close();
                    }
                }
            }
            tg = true;
        }catch (Exception e){
            tg = false;
            new AlertDialog.Builder(context).setTitle("错误提示").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }finally {
            if (zipInputStream!=null){
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return tg;
    }

    public static boolean checkFiles(Context context){
        checkMusicFiles(context);
        boolean tg = false;
        ZipInputStream zipInputStream = null;
        try {
            InputStream inputStream = context.getAssets().open("QQColor2.zip");
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry ;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                if (zipEntry.isDirectory()){
                    File file = new File(getModulePath()+zipEntry.getName());
                    file.mkdirs();
                }else {
                    File file = new File(getModulePath()+zipEntry.getName());
                    if (file.getName().equals("drawer_bg.png")||file.getName().equals("ic_default.png")){
                        file.getParentFile().mkdirs();
                        if (!file.exists()){
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            int len;
                            byte[] buffer = new byte[1024];
                            while ((len = zipInputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, len);
                                fileOutputStream.flush();
                            }
                            fileOutputStream.close();
                        }
                    }



                }
            }
            tg = true;
        }catch (Exception e){
            tg = false;
            new AlertDialog.Builder(context).setTitle("错误提示").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }finally {
            if (zipInputStream!=null){
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return tg;
    }

    /*
    复制文件
    oldFile：从哪里复制
    newFile：复制到哪里
     */
    public static boolean copyFile(String oldFile, String newFile){
        File old = new File(oldFile);
        File newf = new File(newFile);
        if (!old.exists())return false;
        if (newf.exists())deleteFile(newFile);
        //获得原文件流
        try {
            FileInputStream inputStream = new FileInputStream(new File(oldFile));
            byte[] data = new byte[1024];
            //输出流
            int n;
            FileOutputStream outputStream =new FileOutputStream(new File(newFile));
            //开始处理流
            while ((n = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, n);
            }
            inputStream.close();
            outputStream.close();
            return true;
        }catch (Exception e){

            return false;
        }

    }

    public static boolean saveObject(Object o,File file){
        if (file.exists())file.delete();
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(o);
            return true;
        } catch (Throwable throwable){
            throwable.printStackTrace();
            return false;
        } finally {
            if (fileOutputStream!=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream!=null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        input.close();
        return output.toByteArray();

    }

    public static Object getObjectFromFile(File file){
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            return objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream!=null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean copyFile(InputStream inputStream, String newFile){
        File newf = new File(newFile);
        if (newf.exists())deleteFile(newFile);
        if (!newf.getParentFile().exists()){
            newf.getParentFile().mkdirs();
        }
        try {
            newf.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获得原文件流
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] data = new byte[1024];
            //输出流
            FileOutputStream outputStream =new FileOutputStream(new File(newFile));
            //开始处理流
            while (bufferedInputStream.read(data) != -1) {
                outputStream.write(data);
            }
            bufferedInputStream.close();
            return true;
        }catch (Exception e){

            return false;
        }

    }

    /*
    删除文件
    filePath：文件路径
     */
    public static void deleteFile(String filePath){
        File file = new File(filePath);
        if (file.exists()&&file.isFile()){
            file.delete();

        }
    }


    /*
    解压zip文件
    path：压缩包路径
    outpath：文件输出路径
     */
    public static boolean unZip(Context context, String path, String outPath){
        File theme = new File(path);
        ZipInputStream zipInputStream = null;
        boolean z = false;
        if (theme.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(theme);
                zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry ;
                String fileNmae = outPath;
                File themeFile = new File(fileNmae);
                if (!themeFile.exists()){
                    themeFile.mkdirs();
                } else {
                    delAllFile(fileNmae);
                }
                while ((zipEntry = zipInputStream.getNextEntry()) != null){
                    if (zipEntry.isDirectory()){
                        File file = new File(fileNmae+"/"+zipEntry.getName());
                        file.mkdirs();
                    }else {
                        File file = new File(fileNmae+"/"+zipEntry.getName());
                        file.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                            fileOutputStream.flush();
                        }
                        fileOutputStream.close();
                    }
                }
                z = true;
            } catch (Exception e) {
                DialogUtil.showTip(context,e.toString());
                z = false;
            }finally {
                if (zipInputStream!=null){
                    try {
                        zipInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        if (z){
            theme.delete();
        }
        return z;
    }


    public static void unZip2(String zipPath,String to) throws Throwable{
        bin.zip.ZipFile zipFile = new bin.zip.ZipFile(zipPath);
        if (!to.endsWith("/")){
            to = to + "/";
        }
        Enumeration<bin.zip.ZipEntry> zipEntries =  zipFile.getEntries();
        while (zipEntries.hasMoreElements()){
            bin.zip.ZipEntry zipEntry = zipEntries.nextElement();
            String path = to + zipEntry.getName();
            if (zipEntry.isDirectory()){
                new File(path).mkdirs();
            } else {
                FileUtil.copyFile(zipFile.getInputStream(zipEntry),path);
            }

        }
        zipFile.close();
    }

    //将设置文件权限设置为777
    @SuppressWarnings ({"deprecation", "ResultOfMethodCallIgnored"})
    @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
    public static void setWorldReadable(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                File dataDir = new File(context.getApplicationInfo().dataDir);
                File prefsDir = new File(dataDir, "shared_prefs");
                File prefsFile = new File(prefsDir, BuildConfig.APPLICATION_ID + ".xml");
                if (prefsFile.exists()) {
                    for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                        file.setReadable(true,false);
                        file.setExecutable(true,false);
                    }
                    copySettingXml();
                }

            }
        },200);


    }

    //获取设置文件
    private static File getSettingXml(){
        return new File(Environment.getDataDirectory(), "data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + BuildConfig.APPLICATION_ID + ".xml");
    }

    //复制设置文件到本地
    private static void copySettingXml(){
        File file = getSettingXml();
        File setting = new File(getModulePath()+"setting.xml");

        try {
            if (!setting.exists()){
                setting.createNewFile();
            }
            int length=(int)file.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(file);
            fin.read(buff);
            fin.close();
            String result=new String(buff,"UTF-8");
            FileWriter writer = new FileWriter(setting, false);
            writer.write(result);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    /*
    将文件夹的内容全部压缩
     */
    public static boolean zip(String src, String dest)  {
        //提供了一个数据项压缩成一个ZIP归档输出流
        boolean z=false;
        ZipOutputStream out = null;
        File outFile = new File(dest);//源文件或者目录
        File fileOrDirectory = new File(src);//压缩文件路径
        try {

            out = new ZipOutputStream(new FileOutputStream(outFile));
            //如果此文件是一个文件，否则为false。
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
            z = true;
        } catch (IOException ex) {
            z = false;
            ex.printStackTrace();
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return z;
    }

    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath)  {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //实例代表一个条目内的ZIP归档
                ZipEntry entry = new ZipEntry(curPath
                        + fileOrDirectory.getName());
                //条目的信息写入底层流
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], curPath
                            + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static byte[] getFileByte(String filePath){
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);
            byte[] buffer = null;
            byte[] bytes = new byte[1000];
            int n;
            while ((n=fileInputStream.read(bytes))!=-1){
                byteArrayOutputStream.write(bytes,0,n);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
            buffer = byteArrayOutputStream.toByteArray();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getDataThemeFile(String id){
        return new File(new File(BaseApplication.getContext().getDataDir(),"theme"),id);
    }

    public static String getThemeFilePath(){
        File file = new File(getThemePath()+"themes/");
        if (!file.exists()){
            file.mkdirs();
        }
        return getThemePath()+"themes/";
    }

    //返回模块文件路径
    public static String getModulePath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/QQColor2/";
    }

    //返回模块fab资源路径
    public static String getFabPath(){
        return getModulePath()+"fab/";
    }

    public static String getDrawerPath(){
        return getModulePath()+"drawer/";
    }


    public static String getThemePath(){
        return getModulePath()+"theme/";
    }

    public static String getDefThemePath(){
        return getModulePath()+"theme/默认主题/";
    }

    //返回模块顶栏资源路径
    public static String getTopPath(){
        return getModulePath()+"top/";
    }

    //返回模块tab资源路径
    public static String getTabPath(){
        return getModulePath()+"tab/";
    }

    public static String getTabIconPath(String fileName){
        return getTabPath()+fileName;
    }

    public static Bitmap getTabIcon(String name){
        Bitmap bitmap = null;
        File file = new File(getTabIconPath(name));
        if (!file.exists()){
            Context moduleContext = XposedCompact.getModuleContext(QQHelper.getContext());
            if (moduleContext != null){
                try {
                    bitmap = BitmapFactory.decodeStream(moduleContext.getAssets().open("icons/" + name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        if (bitmap == null)return null;
        return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1f), (int) (bitmap.getHeight() * 1f), true);
    }
    public static Bitmap getFabIcon(String name){
        File file = new File(getFabIconPath(name));
        if (!file.exists()){
            Context moduleContext = BaseApplication.getContext();
            if (moduleContext == null){
                moduleContext = XposedCompact.getModuleContext(QQHelper.getContext());
            }
            if (moduleContext != null){
                try {
                    return BitmapFactory.decodeStream(moduleContext.getAssets().open("icons/" + name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return BitmapFactory.decodeFile(getFabIconPath(name));
    }

    public static String getFabIconPath(String fileName){
        if (fileName.endsWith(".png")){
            return getFabPath()+fileName;
        }
        return getFabPath() + fileName+".png";
    }

    public static Bitmap getTopIcon(String fileName){
        File file = new File(getTopPath() + fileName);
        if (!file.exists()){
            Context moduleContext = XposedCompact.getModuleContext(QQHelper.getContext());
            if (moduleContext != null){
                try {
                    return BitmapFactory.decodeStream(moduleContext.getAssets().open("icons/" + fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static String getVipPath(){
        return getModulePath()+"vip/";
    }

    public static String getFullBgPath(){
        return getVipPath()+"fullBackground/";
    }

    public static String getSoundDefPath(){
        return getVipPath()+"sound/default.mp3";
    }

    public static String getSoundDefPath(String uin){
        return getVipPath()+"sound/"+uin+".mp3";
    }

    public static String getMusicPath(){
        return getVipPath()+"music/";
    }

    public static String getMusicAlbum(long id){
        return getVipPath()+"music/album/" + id;
    }

    public static String getMusicLrcPath(String lrcName){
        return getMusicPath()+"lrc/"+lrcName+".lrc";
    }

    public static List<String> getMusicList(){
        File file = new File(getMusicListPath());
        if (!file.exists()){
            file.mkdirs();
        }
        ArrayList<String> arrayList = new ArrayList(Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return TextUtils.isDigitsOnly(name);
            }
        })));
        if (!arrayList.contains("0")){
            arrayList.add(0,"0");
        }
        return arrayList;
    }



    public static String getMusicListPath(){
        return getMusicPath()+"list/";
    }

    public static Bitmap getMusicListBitmap(String id){
        if ("0".equals(id)){
            return BitmapFactory.decodeFile(getMusicDefaultCover());
        }
        return BitmapFactory.decodeFile(getMusicListPath()+id+".icon");
    }

    public static String getMusicListName(String id){
        if ("0".equals(id)){
            return "本地音乐";
        }
        return FileUtil.readTextFromFile(new File(getMusicListPath()+id));
    }


    public static String getMusicTLrcPath(String lrcName){
        return getMusicPath()+"lrc/"+lrcName+".tlrc";
    }

    public static String getMusicDefaultCover(){
        return getMusicPath()+"icons/ic_default.png";
    }

    public static String getMusicIcon(String ic){
        return getMusicPath()+"icons/"+ic;
    }

    public static String getMusicIcon(){
        return getMusicPath()+"icons/";
    }

    public static String getInputIcon(String ic){
        return getVipPath()+"input/"+ic + ".png";
    }

    public static String getTitleBarIcon(String ic){
        return getVipPath()+"titleBar/"+ic + ".png";
    }
}

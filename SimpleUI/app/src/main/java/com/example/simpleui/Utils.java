package com.example.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.speech.tts.TextToSpeech;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by user on 2015/11/30.
 */
public class Utils {
    public static void writeFile(Context context, String fileName, String content) {//寫檔
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);//有錯誤訊息,要用try catch
            fos.write(content.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String fileName) {//讀檔
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];//用byte把東西存進去
            fis.read(buffer, 0, buffer.length);//讀檔
            fis.close();
            return new String(buffer);//把buffer轉回字串
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";//,如果是新的app,用null會容易有錯誤,所以用空字串
    }

    public static Uri getPhotoUri() {//uri標準的識別符號
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(dir.exists() == false){
            dir.mkdirs();//若沒有此資料夾,則新增一個料夾
        }
        File file = new File(dir, "simpleui_photo.png");//只存這個檔案名稱
        return Uri.fromFile(file);
    }

    public static byte[] uriToBytes(Context context, Uri uri) {//把圖檔轉成byte,在傳上Parse,之後在從那拉下來
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);//把圖檔餵進來
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] urlToBytes(String urlString){
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer)) != -1){
               baos.write(buffer, 0, len);
            }

            return baos.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

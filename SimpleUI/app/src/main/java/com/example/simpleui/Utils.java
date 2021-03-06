package com.example.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
        //getExternalStoragePublicDirectory取得外部儲存體存放公開檔案的目錄
        //DIRECTORY_PICTURES: 一般的圖片檔
        if(dir.exists() == false){
            dir.mkdirs();//若沒有此資料夾,則新增一個料夾
        }
        File file = new File(dir, "simpleui_photo.png");//只存這個檔案名稱
        return Uri.fromFile(file);
    }

    public static byte[] uriToBytes(Context context, Uri uri) {//把圖檔轉成byte,在傳上Parse,之後在從那拉下來
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);//把圖檔餵進來
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//暫存BYTE陣列
            byte[] buffer = new byte[1024];//緩衝
            int len = 0;//紀錄讀進來長度
            while ((len = is.read(buffer)) != -1){
                baos.write(buffer, 0, len);//從緩衝區讀取buffer裡面0~length-1的位置
            }
            return baos.toByteArray();//ByteArrayOutputStream轉成位元陣列,並回傳
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

    public static String getGeoCodingUrl(String address){
        try {
            address = URLEncoder.encode(address, "utf-8");
            //使用java.net.URLEncoder類別的靜態encode()方法作編碼的動作
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url =
                "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + address;
        Log.d("debug", url);
        return url;
    }


    public  static String getStaticMapUrl(double[] latLng, int zoom){
        String center = latLng[0] + "," + latLng[1];
        String url =
                "https://maps.googleapis.com/maps/api/staticmap?center="+
                        center + "&zoom=" + zoom + "&size=640x400" ;

        return url;
    }

    public static double[] getLatLngFromJsonString(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);

            JSONObject locationObject = object.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            double lat = locationObject.getDouble("lat");
            double lng = locationObject.getDouble("lng");

            return new double[]{lat, lng};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double[] addressToLatLng(String address) {
        String url = Utils.getGeoCodingUrl(address);
        byte[] bytes = Utils.urlToBytes(url);
        String result = new String(bytes);
       return Utils.getLatLngFromJsonString(result);
    }
}

package com.example.simpleui;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 2015/11/30.
 */
public class Utils {
    public static void writeFile(Context context, String fileName, String content){//寫檔
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
    public static String readFile(Context context, String fileName ){//讀檔
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[]buffer = new byte[1024];//用byte把東西存進去
            fis.read(buffer, 0, buffer.length);//讀檔
            fis.close();
            return new String(buffer);//把buffer轉回字串
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

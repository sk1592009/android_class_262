package com.example.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;
    private EditText inputText;
    private CheckBox hideCheckBox;
    private ListView historyListView;
    private Spinner storeInfoSpinner;//宣告下拉是選單

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String menuResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
//        //新增parse的object
//        ParseObject testObject = new ParseObject("People");//定義class名稱
//        testObject.put("name", "Tom");//定義蘭為名稱和值
//        testObject.put("age", "23");
//        testObject.saveInBackground();

        setContentView(R.layout.activity_main);
        storeInfoSpinner = (Spinner) findViewById(R.id.storeInfoSpinner);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);// sharedPreferences會儲存你上次所輸入的資料
        editor = sharedPreferences.edit();

        inputText = (EditText)findViewById(R.id.inputText);//去R裡面找Id是inputText
//        inputText.setText("1234");//設定inputText的值
        inputText.setText(sharedPreferences.getString("inputText", ""));
        inputText.setOnKeyListener(new View.OnKeyListener() {//判定鍵盤的動作

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){//ACTION_DOWN表示當按下鍵盤時
                    if(keyCode == KeyEvent.KEYCODE_ENTER){//KEYCODE_ENTER為當按下Enter鍵
                        submit(v);//執行sumbit
                        return true;
                    }
                }
                return false;
            }
        });
        hideCheckBox = (CheckBox) findViewById(R.id.hideCheckBox);
//        hideCheckBox.setChecked(true);
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", isChecked);
                editor.commit();
            }
        });

        hideCheckBox.setChecked(sharedPreferences.getBoolean("hideCheckBox", false));

        historyListView = (ListView) findViewById(R.id.historyListView);//ListView是一個動態狀態
        setHistory();
        setStoreInfo();//設定選單裡的資料
    }

    private void setStoreInfo() {
        String[] stores = getResources().getStringArray(R.array.storeInfo);//字串陣列從哪來
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stores);
        storeInfoSpinner.setAdapter(storeAdapter);
    }

    private void setHistory() {
//        String[] data = new String[]{"1","2","3","4","5","6","7","8","9","10"};//做一個陣列
        String[] rawdata = Utils.readFile(this, "history.txt").split("\n");//從Utils讀檔找到歷史檔,split是以什麼方式做切割
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);//這是一對一丟進去

        List<Map<String, String>> data = new ArrayList<>();//data的定義 list裡面的每個元件是map, map裡面讓他string對到string
        /*Map
                "name" -> "Tom"
                "birthday" -> "19901010"
                "sex" -> "M"
                */
        for (int i =0 ; i < rawdata.length; i++){
            try {
                JSONObject object = new JSONObject(rawdata[i]);
                String note = object.getString("note");
                JSONArray array = object.getJSONArray("menu");

                Map<String, String> item = new HashMap<>();
                item.put("note", note);
                item.put("drinkNum", "15");

                item.put("storeInfo", "NTU Store");

                data.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String[] from = {"note", "drinkNum", "storeInfo"};//填的是Map的key name
        int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};//這裡填的是id
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item, from, to);

        historyListView.setAdapter(adapter);
    }

/*
{
    "note": "this is note",
    "menu": [...]
}
 */

    public void submit(View view){
        String text = inputText.getText().toString();//先拿到資料,再將資料轉為字串
        // String text = inputText.getText().toString() + "," +menuResult;
        editor.putString("inputText", text);//在sharedPreferences裡面存inputText的內容
        editor.commit();
        //重組資訊  將menu和所輸入的值包在一起
        try {
            JSONObject orderData = new JSONObject();
            if (menuResult == null)
                menuResult = "[ ]";
            JSONArray array = new JSONArray(menuResult);
            orderData.put("note", text);
            orderData.put("menu", array);
            Utils.writeFile(this, "history.txt", orderData.toString() +"\n");
            //新增parse的object
            ParseObject testObject = new ParseObject("Order");//定義class名稱
            testObject.put("note", text);//定義欄位名稱和值
            testObject.put("menu", array);
            testObject.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Utils.writeFile(this, "history.txt", text+"\n");//寫檔
        if(hideCheckBox.isChecked()){//判斷checkbox是否打勾
            text = "***";//若有打勾則顯示*,若打勾則顯示原本的字串
            inputText.setText("***");
        }
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show();//為顯示出入的資料,使用Long代表顯示時間較長,使用short顯示時間較短
//        inputText.setText("");

        //'讓Toast出來的東西是從讀檔的資料出來的
//        String fileContent = Utils.readFile(this, "history.txt");//讀檔
//        Toast.makeText(this, fileContent, Toast.LENGTH_LONG).show();
        setHistory();
    }



    public void goToMenu(View view){
        Intent intent = new Intent();
        intent.setClass(this,DrinkMenuActivity.class);//要前往的activity
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);//原本是用starActivity是不會回傳值
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //requestCode是startActivityForResult的result, resultCode結束後索回傳的, data結束後回傳的data
        if (requestCode == REQUEST_CODE_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK){
//                String result = data.getStringExtra("result");
//                Log.d("debug", result);
                menuResult = data.getStringExtra("result");
            }
        }
    }
}

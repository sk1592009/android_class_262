package com.example.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private EditText inputText;
    private CheckBox hideCheckBox;
    private ListView historyListView;
    private Spinner storeInfoSpinner;//宣告下拉是選單
    private ImageView photoImageView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private String menuResult;
    private boolean hasPhoto = false;
    private List<ParseObject> queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        //新增parse的object
//        ParseObject testObject = new ParseObject("People");//定義class名稱
//        testObject.put("name", "Tom");//定義欄位名稱和值
//        testObject.put("age", "23");
//        testObject.saveInBackground();

        setContentView(R.layout.activity_main);
        storeInfoSpinner = (Spinner) findViewById(R.id.storeInfoSpinner);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        // sharedPreferences會儲存你上次所輸入的資料
        /*MODE_PRIVATE: 建立的 SharedPreferences 檔案只能讓目前的 App 讀寫 or
        該檔案是私有的，其它應用程式都無法存取（預設值）。
        */
        editor = sharedPreferences.edit();
        photoImageView = (ImageView) findViewById(R.id.photo);
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
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToOrderDetail(position);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setHistory();
        setStoreInfo();//設定選單裡的資料
    }

    private void goToOrderDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);
        ParseObject object = queryResult.get(position);
        intent.putExtra("storeInfo", object.getString("storeInfo"));//讀取
        intent.putExtra("note", object.getString("note"));
        startActivity(intent);
    }

    private void setStoreInfo() {
        //以下是從array.xml那去取
//        String[] stores = getResources().getStringArray(R.array.storeInfo);//字串陣列從哪來
//        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stores);
//        storeInfoSpinner.setAdapter(storeAdapter);

        //到parse那手動去新增資料,再從parse那把資料傳回來
        ParseQuery<ParseObject> query = new ParseQuery<>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {//把objects裡面的東西串在一起
                String[] stores = new String[objects.size()];
                for (int i = 0; i < stores.length; i++){
                    ParseObject object = objects.get(i);
                    stores[i] = object.getString("name") + "," + object.getString("address");
                }
                ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stores);
                storeInfoSpinner.setAdapter(storeAdapter);
            }
        });
    }

    private void setHistory() {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");//從Order拿出來
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                queryResult = objects;
                List<Map<String, String>> data = new ArrayList<>();//data的定義 list裡面的每個元件是map, map裡面讓他string對到string
                /*Map
                "name" -> "Tom"
                "birthday" -> "19901010"
                "sex" -> "M"
                */
                for (int i =0 ; i < objects.size(); i++){
                    ParseObject object = objects.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    JSONArray array = object.getJSONArray("menu");

                    Map<String, String> item = new HashMap<>();
                    item.put("note", note);
                    item.put("drinkNum", "15");
                    item.put("storeInfo", storeInfo);

                    data.add(item);
                }
                String[] from = {"note", "drinkNum", "storeInfo"};//填的是Map的key name
                int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};//這裡填的是id
                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listview_item, from, to);

                historyListView.setAdapter(adapter);
                historyListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
//        String[] data = new String[]{"1","2","3","4","5","6","7","8","9","10"};//做一個陣列

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);//這是一對一丟進去
//        String[] rawdata = Utils.readFile(this, "history.txt").split("\n");//從Utils讀檔找到歷史檔,split是以什麼方式做切割
//        List<Map<String, String>> data = new ArrayList<>();//data的定義 list裡面的每個元件是map, map裡面讓他string對到string
//        /*Map
//                "name" -> "Tom"
//                "birthday" -> "19901010"
//                "sex" -> "M"
//                */
//        for (int i =0 ; i < rawdata.length; i++){
//            try {
//                JSONObject object = new JSONObject(rawdata[i]);
//                String note = object.getString("note");
//                JSONArray array = object.getJSONArray("menu");
//
//                Map<String, String> item = new HashMap<>();
//                item.put("note", note);
//                item.put("drinkNum", "15");
//
//                item.put("storeInfo", "NTU Store");
//
//                data.add(item);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        String[] from = {"note", "drinkNum", "storeInfo"};//填的是Map的key name
//        int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};//這裡填的是id
//        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item, from, to);
//
//        historyListView.setAdapter(adapter);
    }

/*
{
    "note": "this is note",
    "menu": [...]
}
 */

    public void submit(View view){

        progressDialog.setTitle("Loading...");//秀出還在Loading,並不能做其他事情
        progressDialog.show();

        String text = inputText.getText().toString();//先拿到資料,再將資料轉為字串
        // String text = inputText.getText().toString() + "," +menuResult;

        /*
        寫入資料：
        1.呼叫edit()方法來取得SharedPreferences.Editor物件。
        2.將要寫入的資料透過呼叫方法來存入，如：putString()、putInt()。
        3.呼叫commit()來交付資料。
         */
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
            ParseObject orderObject = new ParseObject("Order");//定義class名稱
            // 每個JSONObject都能利用JSONObject.put(key, value)來增加屬性
            orderObject.put("note", text);//定義欄位名稱和值
            orderObject.put("storeInfo", storeInfoSpinner.getSelectedItem());//寫入的地方
            orderObject.put("menu", array);
            if(hasPhoto == true){
                Uri uri = Utils.getPhotoUri();
                ParseFile parseFile = new ParseFile("photo.png", Utils.uriToBytes(this, uri));
                orderObject.put("photo", parseFile);
            }
            orderObject.saveInBackground(new SaveCallback() {//會很順的送出,不需等回傳
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    Log.d("debug", "line:167");
                    if (e == null){
                        Toast.makeText(MainActivity.this, "[SaveCallback] ok.", Toast.LENGTH_SHORT).show();
                    } else{
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "[SaveCallback] fail.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            Log.d("debug", "line:176");
//
//              try { //這段save要等資料送出去,再等東西回來
//                  orderObject.save();
//              } catch (ParseException e) {
//                  e.printStackTrace();
//              }

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
        } else if (requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK){
                //Bitmap bm = data.getParcelableExtra("data");//會把圖片存在這data中
                //photoImageView.setImageBitmap(bm);
                Uri uri = Utils.getPhotoUri();
                photoImageView.setImageURI(uri);//從uri拿照片
                hasPhoto = true;//判斷照相是否成功
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_take_photo){
            Toast.makeText(this, "take photo", Toast.LENGTH_SHORT).show();
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);//確認好相片,會跳回來


    }
}

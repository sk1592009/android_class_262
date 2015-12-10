package com.example.simpleui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DrinkMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//         fab.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                 }
//            });
    }
    public void add(View view){
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

    public void done(View view){
        JSONArray jsonData = getData();
        Intent data = new Intent();
        data.putExtra("result", jsonData.toString());//name是所引值
        setResult(RESULT_OK, data);// 告訴結果的動作
        finish();//把activity結束,會到MainActivity的onActivityResult
    }
      /*JASON Array
    *[{"name": "black tea", "l": "2", "m": "0"},
    * {"name": "milk tea", "l": "10", "m": "3"},
    *{"name": "green tea", "l": "5", "m": "3"} ]
     */
    public JSONArray getData(){
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        for (int i = 0; i < count - 1; i++){
            LinearLayout ll = (LinearLayout) rootLinearLayout.getChildAt(i);

            TextView drinkNameTextView = (TextView) ll.getChildAt(0);//取得第0個
            Button lButton = (Button) ll.getChildAt(1);
            Button mButton = (Button) ll.getChildAt(2);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());

            try{
                JSONObject object = new JSONObject();
                object.put("name", drinkName);//json值傳入
                object.put("l", lNumber);
                object.put("m", mNumber);
                array.put(object);//把json存進陣列
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return array;

    }
}
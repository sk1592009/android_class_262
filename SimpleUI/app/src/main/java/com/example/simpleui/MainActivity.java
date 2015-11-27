package com.example.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private CheckBox hideCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = (EditText)findViewById(R.id.inputText);//去R裡面找Id是inputText
        inputText.setText("1234");//設定inputText的值

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

    }

    public void submit(View view){
        String text = inputText.getText().toString();//先拿到資料,再將資料轉為字串
        if(hideCheckBox.isChecked()){//判斷checkbox是否打勾
            text = "***";//若有打勾則顯示*,若打勾則顯示原本的字串
            inputText.setText("***");
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();//為顯示出入的資料,使用Long代表顯示時間較長,使用short顯示時間較短
//        inputText.setText("");
    }
}

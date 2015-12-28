package com.example.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.jar.Manifest;

public class OrderDetailActivity extends AppCompatActivity {

    //private String address;
    private TextView addressTextView;
    private ImageView staticMapImage;
    private Switch mapSwitch;
    private WebView staticMapWeb;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        addressTextView = (TextView) findViewById(R.id.address);
        staticMapImage = (ImageView) findViewById(R.id.staticMapImage);
        staticMapWeb = (WebView) findViewById(R.id.webView);
        staticMapWeb.setVisibility(View.GONE);//一開始會兩個都顯示,故要先隱藏起來

        mapSwitch = (Switch) findViewById(R.id.mapSwitch);
        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    staticMapImage.setVisibility(View.GONE);
                    staticMapWeb.setVisibility(View.VISIBLE);
                } else {
                    staticMapImage.setVisibility(View.VISIBLE);//VISIBLE存在並且看得到
                    staticMapWeb.setVisibility(View.GONE);
                }
            }
        });

        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.googleMap);
//        googleMap = mapFragment.getMap();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });
        String note = getIntent().getStringExtra("note");
        String storeInfo = getIntent().getStringExtra("storeInfo");

//        Log.d("debug", note);
//        Log.d("debug", storeInfo);

        String address = storeInfo.split(",")[1];
//        1.
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //String url = "https://maps.googleapis.com/maps/api/geocode/json?address=taipei101";
////                String url = Utils.getGeoCodingUrl(address);
////                byte[] bytes = Utils.urlToBytes(url);
////                String result = new String(bytes);
////                double[] latLng = Utils.getLatLngFromJsonString(result);
////
////                Log.d("debug", result);
//                double[] latLng = Utils.addressToLatLng(address);
//                Log.d("debug", latLng[0] + "," + latLng[1]);
//
//                Button button = (Button) findViewById(R.id.button);
//                button.setText(latLng[0] + "," + latLng[1]);
//            }
//        });
//        thread.start();

//        2.
        /*
        AsyncTask 的運作有 4 個階段：

        onPreExecute -- AsyncTask 執行前的準備工作，例如畫面上顯示進度表，
        doInBackground -- 實際要執行的程式碼就是寫在這裡，
        onProgressUpdate -- 用來顯示目前的進度，
        onPostExecute -- 執行完的結果 - Result 會傳入這裡。
         */
//        AsyncTask task = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {//一定實做
//                Utils.addressToLatLng(address);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {//mainThread上去執行    不一定要實做
//
//            }
//        };
//        task.execute();
        addressTextView.setText(address);
        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);
    }

//    class GeoCodingTask extends AsyncTask<Void, Void, Void> {
    /*這三個類型被用於一個異步任務，如下：
    1. Params，啟動任務執行的輸入參數
    2. Progress，後台任務執行的百分比
    3. Result，後台計算的結果類型
    */
    class GeoCodingTask extends AsyncTask<String, Void, byte[]> {
//        @Override
         /*
         onProgressUpdate(Progress...),一次呼叫 publishProgress(Progress...)後調用 UI線程。
         執行時間是不確定的。這個方法用於當後台計算還在進行時在用戶界面顯示進度。
         例如：這個方法可以被用於一個進度條動畫或在文本域顯示記錄。
         */
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }

        private String url;
        private double[] latLng;
        @Override
        /*doInBackground(Params...)，後台線程執行onPreExecute()完後立即調用，
        這步被用於執行較長時間的後台計算。異步任務的參數也被傳到這步。
        計算的結果必須在這步返回，將傳回到上一步。
        */
        protected byte[] doInBackground(String... params) {//是在另一個thread
            String address = params[0];
//            return Utils.addressToLatLng(address);//原為 double[]
            //改成byte[]後,
//            double[] latLng = Utils.addressToLatLng(address);
//            String url = Utils.getStaticMapUrl(latLng, 17);//WebView在這沒辦法回傳,故改成以下並在上方宣告
            latLng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latLng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        //onPostExecute(Result), 當後台計算結束時，調用 UI線程。後台計算結果作為一個參數傳遞到這步。
        //protected void onPostExecute(double[] latLng)
        protected void onPostExecute(byte[] bytes) {//在mainThread
//            addressTextView.setText(latLng[0] + "," + latLng[1]);//要做這行,要在mainThread裡面做

//            String url = Utils.getStaticMapUrl(latLng, 10);
//            byte[] bytes = Utils.urlToBytes(url);/*因為urlToBytes要在另一個mainThread去做,
//            若要在background做,就要把 double[] 改為byte[],並把這行和上行移到doInBackground裡*/
            staticMapWeb.loadUrl(url);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
            staticMapImage.setImageBitmap(bm);

            LatLng storeAddress = new LatLng(latLng[0], latLng[1]);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeAddress, 17));

            //顯示商店位置(旗子)
            String[] storeInfo = getIntent().getStringExtra("storeInfo").split(",");
            googleMap.addMarker(new MarkerOptions()
                    .title(storeInfo[0])
                    .snippet(storeInfo[1])
                    .position(storeAddress));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(OrderDetailActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            //顯示自己的位置
            //googleMap.setMyLocationEnabled(true);


        }
    }

}

package com.example.simpleui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrderDetailActivity extends AppCompatActivity {

    //private String address;
    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        addressTextView = (TextView) findViewById(R.id.address);

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
    class GeoCodingTask extends AsyncTask<String, Void, double[]> {
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }

    @Override
        protected double[] doInBackground(String... params) {//是在另一個thread
            String address = params[0];
            return Utils.addressToLatLng(address);
        }

        @Override
        protected void onPostExecute(double[] latLng) {//在mainThread
            addressTextView.setText(latLng[0] + "," + latLng[1]);//要做這行,要在mainThread裡面做
        }
}

}

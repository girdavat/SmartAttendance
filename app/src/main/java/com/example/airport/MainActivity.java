package com.example.airport;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends AppCompatActivity {
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    Bundle select;
    SoapPrimitive response11 = null;
    private static final String SOAP_ACTION_check = "http://tempuri.org/check";
    private static final String SOAP_ACTION_update = "http://tempuri.org/updateAttendance";
    private static final String SOAP_ACTION_IMEI = "http://tempuri.org/setImei";

    private static final String OPERATION_NAME_check = "check";
    private static final String OPERATION_NAME_update = "updateAttendance";
    private static final String OPERATION_NAME_setImei = "setImei";

    private static final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";

    private static final String SOAP_ADDRESS = "http://ibeaconkhas.somee.com/myservice.asmx";


    private static final Map<String, List<String>> PLACES_BY_BEACONS;
    long sID;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("7980:3795", new ArrayList<String>() {{
            add("Heavenly Sandwiches");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827
            add("Green & Green Salads");
            // "Green & Green Salads" is the next closest
            add("Mini Panini");
            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("648:12", new ArrayList<String>() {{
            add("Mini Panini");
            add("Green & Green Salads");
            add("Heavenly Sandwiches");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private Region region;
    long id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        Log.d("oncrete", "içi");
        select = getIntent().getExtras();
        id = select.getLong("studentID");
        Toast.makeText(getBaseContext(), "ID'niz :  " + id, Toast.LENGTH_SHORT).show();
        
        if (savedInstanceState == null) {
            Log.i("fragment", "içi");
        }

        Log.i("okan", "mert");
        AsyncCallCheckSection task1 = new AsyncCallCheckSection();
        Log.i("mert", "mert");
        task1.execute();
        Log.i("okan", "okan");

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    // TODO: update the UI here
                    Log.d("School", "Nearest places: " + places);
                }
            }
        });
        region = new Region("ranged region", UUID.fromString("8492e75f-4fd6-469d-b132-043fe94921d8"), null, null);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onresume", "başı: ");
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        Log.d("onstop", "başı: ");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("ıtemsselected", "başı: ");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callA() {
        AsyncCall a = new AsyncCall();
        a.execute();
    }

    public void callB() {
        AsyncCallB b = new AsyncCallB();
        b.execute();
    }

    private void updateHelp(long studentID, int sectionID) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_update);
            
        request.addProperty("studentID", studentID);
        request.addProperty("sectionID", sectionID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);

        try {

            androidHttpTransport.call(SOAP_ACTION_update, envelope);

            final SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            Log.d("update response", " " + response.toString());


            runOnUiThread(new Runnable() {
				// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Success!!! :)", Toast.LENGTH_LONG).show();
				}
            });

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(getBaseContext(), e.getMessage() + "Failed to attend", Toast.LENGTH_LONG).show();

        }
    }

    private boolean setImei(long imei, long studentID) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_setImei);
	               
        request.addProperty("studentID", studentID);

        request.addProperty("imei", imei);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);

        try {

            androidHttpTransport.call(SOAP_ACTION_IMEI, envelope);

            response11 = (SoapPrimitive) envelope.getResponse();

            

        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(1);

        }
        return Boolean.parseBoolean(response11.toString());
    }

    private boolean checkSection(int sectionID) {
        Log.d("checksection bası", "");
        Object response = null;
        try {
            SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            env.dotNet = true;
            env.xsd = SoapSerializationEnvelope.XSD;
            env.enc = SoapSerializationEnvelope.ENC;

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_check);
            request.addProperty("sectionID", sectionID);
            Log.d("checksection", "" + sectionID);
            env.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);
            androidHttpTransport.call(SOAP_ACTION_check, env);

            response = env.getResponse();
            String resultValue = response.toString();
            Log.d("checksection", "1");
            SoapObject a = (SoapObject) env.bodyIn;
            Log.d("checksection", "2");
            Object property = a.getProperty(0);
            Log.d("checksection", "3");
            SoapObject info = (SoapObject) property;
            Log.d("checksection", "" + info.getProperty(0));
            Log.d("checksection", "" + a.toString());
            return Boolean.parseBoolean(info.getProperty(0).toString());//Boolean.parseBoolean(property.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("checksection", "hata");
            return false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.airport/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.airport/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class AsyncCallCheckSection extends AsyncTask<Void, Void, Void> {
        ProgressDialog progDailog;

        protected Void doInBackground(Void... params) {
            Log.d("", "doInBackground call stu");

            int k = 1;
            final boolean b = checkSection(k);

            runOnUiThread(new Runnable() {
				// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    callB();
                    Button bt1 = (Button) findViewById(R.id.btnNumber1);
                    if (b == true) {
                        Toast.makeText(getBaseContext(), "When you see the notification, click the button!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getBaseContext(), "You can just attend the lecture between 20th and 30th minutes. Please make sure that you are in right time period!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    bt1.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (MyApplication.getX() == 1) {
                                callA();
                                ((TextView) findViewById(R.id.textView2)).setText(" Wait a while you see the information");
                            } else
                                Toast.makeText(getBaseContext(), "You are not in the region so you can't attend!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.i("", "onPostExecute");
            progDailog.dismiss();
        }

        protected void onPreExecute() {
            Log.i("", "onPreExecute");
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected void onProgressUpdate(Void... values) {
            Log.i("", "onProgressUpdate");
        }


    }

    private class AsyncCall extends AsyncTask<Void, Void, Void> {
        ProgressDialog progDailog;

        protected Void doInBackground(Void... params) {
            Log.d("", "doInBackground call stu");

            updateHelp(id, 4);

            runOnUiThread(new Runnable() {
				// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
                @SuppressWarnings("deprecation")
                @Override
                public void run() {


                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.i("", "onPostExecute");
            progDailog.dismiss();
        }

        protected void onPreExecute() {
            Log.i("", "onPreExecute");
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected void onProgressUpdate(Void... values) {
            Log.i("", "onProgressUpdate");
        }


    }

    private class AsyncCallB extends AsyncTask<Void, Void, Void> {
        ProgressDialog progDailog;

        protected Void doInBackground(Void... params) {
            Log.d("", "doInBackground call stu");

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            long mImei = Long.parseLong(telephonyManager.getDeviceId());

            final boolean chec1 = setImei(mImei, id);

            runOnUiThread(new Runnable() {// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    if (chec1 == false) {
                        Toast.makeText(getBaseContext(), "Imeı check failed!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Imei check done!", Toast.LENGTH_SHORT).show();
                       
                    }
                    //Toast
                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.i("", "onPostExecute");
            progDailog.dismiss();
        }

        protected void onPreExecute() {
            Log.i("", "onPreExecute");
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected void onProgressUpdate(Void... values) {
            Log.i("", "onProgressUpdate");
        }


    }
}
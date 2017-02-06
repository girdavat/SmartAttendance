package com.example.airport;

/**
 * Created by mert on 8.05.2016.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class login extends AppCompatActivity {
    private static final String SOAP_ACTION_registerHelperData = "http://tempuri.org/Login";
    
    studentData a;
    private static final String OPERATION_NAME_registerHelperData = "Login";// your webservice web method name
    

    private static final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";

    private static final String SOAP_ADDRESS = "http://ibeaconkhas.somee.com/myservice.asmx";
    static EditText sID,sPassword;
    long id;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sID=(EditText) findViewById(R.id.stunumber);
        sPassword=(EditText) findViewById(R.id.passwordT);
        
        Button loginBt=(Button) findViewById(R.id.btn1);
        loginBt.setOnClickListener(new onClickListener() {
            @Override
            public void onClick(View view) {
                callAsyn();
                Bundle bundle = new Bundle();
                bundle.putLong("studentID", id);
                Intent myintent = new Intent(view.getContext(), MainActivity.class);
                myintent.putExtras(bundle);
                startActivityForResult(myintent, 0);
                

            }
        });
           
    }
    
    private studentData getHelperData(long id,String pass){
        //menuItem Objelerimiz array halinde

        studentData item = null;
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_registerHelperData);
    	/* aşağıdakiler webservice method parametreleri
    	 * 
    	 */
        Log.i("item........ ", "1");
        request.addProperty("id", id);
        request.addProperty("password", pass);
        Log.i("item........ ", "2");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Log.i("item........ ", "3");
        try {
            Log.i("item........ ", "41");
            androidHttpTransport.call(SOAP_ACTION_registerHelperData, envelope);
            Log.i("item........ ", "4");
            SoapObject a = (SoapObject) envelope.bodyIn;
            Log.i("item........ ", "5");
            Object property = a.getProperty(0);
            Log.i("item........ ", "içinde2");
            // burdanda içine ulaşabiliyon uzunluk yada variableları görebilirsin
            SoapObject info = (SoapObject) property;
            Log.i("item........ ", "içinde3");
            if (property instanceof SoapObject) {
                Log.i("ozge", "1 " + info.getProperty(0).toString());
                Log.i("ozge", "1 " + info.getProperty(1).toString());
                Log.i("ozge", "1 " + info.getProperty(2).toString());

                Object sIDD =  info.getProperty(2);
                //Log.d("ozge","1 "+helperName.toString());
                Object name =  info.getProperty(0);
                Object surname =  info.getProperty(1);
                //SoapObject infoSurname = (SoapObject) helperSurname;
                
                Log.d("ozge sidd",sIDD.toString());


                //if (property instanceof SoapObject) {
                long hID = Long.parseLong(sIDD.toString());
                String sName = name.toString();
                String sSurname = surname.toString();
                Log.i("ozge","1");
                item = new studentData(hID,sName,sSurname);
                Log.d("item mert   ", "id:" + item.getStudentID() + " name:" + item.getStudentName() + " " + item.getStudentSurame());
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getBaseContext(), e.getMessage()+" ddd", Toast.LENGTH_LONG).show();
            Log.i("", e.getMessage() + " ddd");
        }

        return item;
    }
    public void callAsyn(){
        id=Long.parseLong(sID.getText().toString());
        pass=sPassword.getText().toString();
       // Toast.makeText(getBaseContext(),"callAsyn içindeyiz id: "+id,Toast.LENGTH_SHORT).show();
        AsyncCallgetHelper task1 = new AsyncCallgetHelper();
        task1.execute();
        
    }
    private class AsyncCallgetHelper extends AsyncTask<Void, Void, Void> {
        ProgressDialog progDailog;
        protected Void doInBackground(Void... params) {
            Log.i("", "doInBackground call stu");
            
            a = getHelperData(id,pass);

            runOnUiThread(new Runnable() {// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    //tabların iç layoutları
                    Log.i("ozge", "id: " + a.getStudentID() + " name:" + a.getStudentName() + " " + a.getStudentSurame());
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
            progDailog = new ProgressDialog(login.this);
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

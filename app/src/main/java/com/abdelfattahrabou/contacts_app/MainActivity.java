package com.abdelfattahrabou.contacts_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int READ_CONTACTS_PERMISSION_CODE = 100;
    Button call, sms, contact,search;
    ImageButton smsBtn,uploadBtn,callBtn;
    TextView resultat;
    Spinner countrySpinner ;
    EditText countryTxt;
    RadioButton nameRBtn;
    RadioButton numberRBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callBtn = findViewById(R.id.callBtn);
        callBtn.setOnClickListener(this);
        smsBtn = findViewById(R.id.smsBtn);
        smsBtn.setOnClickListener(this);
        search=findViewById(R.id.searchBtn);
        search.setOnClickListener(this);
        uploadBtn = findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(this);
        resultat=findViewById(R.id.resultatTxt);
        countryTxt=findViewById(R.id.edtTxt);
        nameRBtn=findViewById(R.id.nameRdBtn);
        numberRBtn=findViewById(R.id.numRdBtn);
        countrySpinner=(Spinner) findViewById(R.id.spinner);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCountry= (String) adapterView.getItemAtPosition(i);
                switch (selectedCountry){
                    case "Morocco": countryTxt.setText("+212");
                        resultat.setText("");
                    break;
                    case "France": countryTxt.setText("+33");
                        resultat.setText("");
                        break;
                    case "Spain": countryTxt.setText("+34");
                    break;
                    case "Italy": countryTxt.setText("+39");
                    break;
                    case "Portugal": countryTxt.setText("+351");
                    break;
                    case "India": countryTxt.setText("+91");
                    break;
                    case "United states": countryTxt.setText("+1");
                    break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        checkPermission(Manifest.permission.READ_CONTACTS, READ_CONTACTS_PERMISSION_CODE);

    }

    @Override
    public void onClick(View v) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        if(v == callBtn){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+resultat.getText().toString()));
            startActivity(intent);
        }
        if(v == smsBtn){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+resultat.getText().toString()));
            startActivity(intent);
        }
        if(v == uploadBtn){
            JSONArray jsonArray = getContacts(this.getContentResolver());
            String url="http://192.168.5.169:8080/contact/manyContacts";
            JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Toast.makeText(MainActivity.this,"Numbers uploaded successfully", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this,"Numbers uploaded successfully", Toast.LENGTH_LONG).show();

                }
            });

            queue.add(request_json);
        }
        if(v==search) {
            String searchBox = countryTxt.getText().toString();
            if (nameRBtn.isChecked()) {
                String url = "http://192.168.5.169:8080/contact/name="+searchBox+"";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String telephone = response.getString("telephone").toString();
                                    Toast.makeText(MainActivity.this, telephone, Toast.LENGTH_LONG).show();

                                    resultat.setText(telephone);
                                } catch (Exception e) {
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                queue.add(jsonObjectRequest);
            }
            if(numberRBtn.isChecked()){
                String url = "http://192.168.5.169:8080/contact/telephone="+searchBox+"";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String name = response.getString("name").toString();
                                    resultat.setText(name);
                                } catch (Exception e) {
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                queue.add(jsonObjectRequest);
            }
        }

    }
    public JSONArray getContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        JSONArray jsonArray=new JSONArray();
        while (phones.moveToNext()) {

            JSONObject jsonObject=new JSONObject();
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            try {
               jsonObject.put("imei", "");
               jsonObject.put("name",name);
               jsonObject.put("telephone",phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            jsonArray.put(jsonObject);
        }
        phones.close();// close cursor
        return jsonArray;
    }
    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == READ_CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Contacts Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Contacts Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }

    public void onRadioButtonClick(View view) {

    }
}
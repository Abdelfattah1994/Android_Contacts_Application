package com.abdelfattahrabou.contacts_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_CONTACTS_PERMISSION_CODE = 100;
    Button call, sms, contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call = findViewById(R.id.call);
        call.setOnClickListener(this);
        sms = findViewById(R.id.sms);
        sms.setOnClickListener(this);
        contact = findViewById(R.id.contact);
        contact.setOnClickListener(this);
        checkPermission(Manifest.permission.READ_CONTACTS, READ_CONTACTS_PERMISSION_CODE);

    }

    @Override
    public void onClick(View v) {

        if(v == call){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0652145875"));
            startActivity(intent);
        }
        if(v == sms){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:0102030405"));
            startActivity(intent);
        }
        if(v == contact){



            List<String> liste = getNumber(this.getContentResolver());
            Toast.makeText(this, liste+"", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        List <String> contacts = new ArrayList<>();
        while (phones.moveToNext()) {
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add (name + " : " + phoneNumber );
        }
        phones.close();// close cursor
        return contacts;
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

}
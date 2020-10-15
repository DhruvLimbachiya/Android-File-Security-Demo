package com.example.androidsecuritydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText nameEditText,emailEditText;
    EncryptedFile encryptedFile;
    MasterKey mainKey;
    Context context;
    String email;
    String name;

    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textcview);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);

        context = getApplicationContext();
        try {
            // Create a master key for encryption & decryption using Builder pattern
            mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


        String sharedPrefsFile = getString(R.string.app_name);

        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    sharedPrefsFile,
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


    }

    public void readDataFromFile(View view) throws GeneralSecurityException, IOException {

        // EncryptedFile object to encrypt file content
        encryptedFile = new EncryptedFile.Builder(
                context,
                new File(MainActivity.this.getFilesDir(),"test.txt"),  // File want to read
                mainKey, // Master key for encryption
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB) // Algorithm or FileEncryptionScheme for encryption
                .build();


        InputStream inputStream = encryptedFile.openFileInput();  // Open the encrypted file to read
        int next = inputStream.read(); // Read the encrypted file

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // ByteArrayOutputStream to print the content of file to read

        while (next != -1){
            byteArrayOutputStream.write(next); // Write the file content in byteArrayOutputStream object
            next = inputStream.read(); // Read the next character until it encounter the end (-1)
        }

        byte[] plainText = byteArrayOutputStream.toByteArray(); // Convert into byte array

        String textString  = new String(plainText); // Create a new string with converted plain text byte array

        textView.setText(textString); // display the file content in textView
    }


    public void writeDataToFile(View view) throws GeneralSecurityException, IOException {

        File file = new File(MainActivity.this.getFilesDir(),"test.txt");

        if(file.exists()){
            Toast.makeText(context, "File already exist. Unable to replace.", Toast.LENGTH_SHORT).show();
        }else {
            encryptedFile = new EncryptedFile.Builder(
                    context,
                    file,
                    mainKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                    .build();

            byte[] fileContent = "MY SUPER-SECRET INFORMATION"
                    .getBytes(StandardCharsets.UTF_8);

            OutputStream outputStream = encryptedFile.openFileOutput(); // Open the encrypted file to write
            outputStream.write(fileContent); // write the file content
            outputStream.flush(); // flush the stream
            outputStream.close(); // close the stream

            Toast.makeText(context, "Data written to encrypted file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the user entered data into SharedPreferences on Click on "Save Data To Shared Preference" Button
     * @param view - Button
     */
    public void saveDataToSharedPreference(View view) {
        email = emailEditText.getText().toString();
        name = nameEditText.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name",name);
        editor.putString("email",email);
        editor.apply();

        Toast.makeText(context, "Data saved successfully into SharedPreferences!", Toast.LENGTH_SHORT).show();
        clearText();
    }

    /**
     * Clear the editTextView.
     */
    private void clearText() {
        nameEditText.setText("");
        emailEditText.setText("");
    }

    /**
     * Retrieve data from the Shared Preference on click on "Retrieve Data From Shared Preference" button
     * @param view - button
     */
    public void retrieveDataFromSharedPreference(View view) {

        name = sharedPreferences.getString("name","no data");
        email = sharedPreferences.getString("email","no data");

        nameEditText.setText(name);
        emailEditText.setText(email);

    }
}
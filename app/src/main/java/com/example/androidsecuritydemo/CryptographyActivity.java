package com.example.androidsecuritydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptographyActivity extends AppCompatActivity {

    private TextView encryptedDataTextView,decryptedDataTextView;
    private Cipher cipher;
    private SecretKey secretKey;
    private  IvParameterSpec ivParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptography);

        EditText dataEditText = findViewById(R.id.dataToEncryptedEditText);
        encryptedDataTextView = findViewById(R.id.encryptedDataTextView);
        decryptedDataTextView = findViewById(R.id.decryptedDataTextView);

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); // Create an instance of a cipher of AES algorithm with CBC mode.
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            ivParams = new IvParameterSpec(iv);  // Initial vector
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        encryptionConfiguration();

        dataEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                byte[] plainText  = charSequence.toString().getBytes();  // get the user entered data and convert into bytes
                encryptData(plainText);
                decryptData();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    byte[] cipherText; // variable for storing cipher text.


    /**
     * Encrypt the given plain text using AES256 algorithm.
     * @param plainText - text to encrypt
     */
    private void encryptData(byte[] plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivParams); // initialize cipher by specifying encrypt mode, key to use & initial vector
            cipherText = cipher.doFinal(plainText); // encrypt plain text.
            String cipherTextString = new String(cipherText); // convert byte array into user understandable string.
            encryptedDataTextView.setText(cipherTextString); // display encrypted text.

        } catch (BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void decryptData() {
        try {
            cipher.init(Cipher.DECRYPT_MODE,secretKey, ivParams);// initialize cipher by specifying decrypt mode, key to use & initial vector
            byte[] plainText = cipher.doFinal(cipherText); // decrypt plain text.
            String plainTextString = new String(plainText); // convert byte array into user understandable string.
            decryptedDataTextView.setText(plainTextString); // display decrypted text.
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configuration require for message/data encryption.
     */
    private void encryptionConfiguration() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // Create a system for generating keys.
            keyGenerator.init(256); // Generate key of 256 bits size.

            secretKey = keyGenerator.generateKey(); // Generate a secret(private) key using generator(keyGenerator) instance.

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
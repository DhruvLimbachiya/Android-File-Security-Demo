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

    private EditText dataEditText;
    private TextView encryptedDataTextView,decryptedDataTextView;
    private Cipher cipher;
    private SecretKey secretKey;
    private byte[] initialVector;
    private  IvParameterSpec ivParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptography);

        dataEditText =findViewById(R.id.dataToEncryptedEditText);
        encryptedDataTextView = findViewById(R.id.encryptedDataTextView);
        decryptedDataTextView = findViewById(R.id.decryptedDataTextView);

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); // Create an instance of a cipher algorithm.
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            ivParams = new IvParameterSpec(iv);
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
                byte[] plainText  = charSequence.toString().getBytes();

                encryptData(plainText);
                decryptData();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    byte[] cipherText;


    /**
     * Encrypt the given plain text using AES256 algorithm.
     * @param plainText - text to encrypt
     */
    private void encryptData(byte[] plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivParams);
            cipherText = cipher.doFinal(plainText);
            String cipherTextString = new String(cipherText);
            encryptedDataTextView.setText(cipherTextString);

        } catch (BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void decryptData() {
        try {
            cipher.init(Cipher.DECRYPT_MODE,secretKey, ivParams);// Create an instance of a cipher algorithm.
            byte[] plainText = cipher.doFinal(cipherText);
            String plainTextString = new String(plainText);
            decryptedDataTextView.setText(plainTextString);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configuration require for message/data encryption.
     */
    private void encryptionConfiguration() {
        try {

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // Create a system for generating keys
            keyGenerator.init(256); // Generate key of 256 bits size.

            secretKey = keyGenerator.generateKey(); // Generate a secret(private) key using generator(keyGenerator) instance.


            initialVector = cipher.getIV();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
package com.codificador.firebasepushnotification;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextToToken, editTextMessage;
    TextView textViewMyToken;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
    }

    private void initComponents(){
        findViewById(R.id.buttonRefreshToken).setOnClickListener(this);
        findViewById(R.id.buttonSend).setOnClickListener(this);
        findViewById(R.id.buttonCopyToken).setOnClickListener(this);
        textViewMyToken = findViewById(R.id.textViewMyAccessToken);
        editTextToToken = findViewById(R.id.editTextToToken);
        editTextMessage = findViewById(R.id.editTextMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonRefreshToken:
                refreshToken();
                break;
            case R.id.buttonSend:
                sendMessage();
                break;
            case R.id.buttonCopyToken:
                copyToken();
                break;
        }
    }

    private void refreshToken(){
        String token = sharedPreferences.getString("mytoken","NOT AVAILABLE");
        textViewMyToken.setText(token);
    }

    private void sendMessage(){
        String message = editTextMessage.getText().toString();
        String toToken = editTextToToken.getText().toString();

        //SERVER KEY will be available in Firebase Console -> Settings -> CLOUD MESSAGING - >Server Key
        String serverKey = "SERVER_KEY";

        String data = "{\"data\": { \n" +
                "\"title\": \"Sathish\",\n" +
                "\"detail\": \""+message+"\",\n" +
                "}, \n" +
                "\"to\" : \""+toToken+"\"\n" +
                "}";
        String url = "https://fcm.googleapis.com/fcm/send";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization","key="+serverKey);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(getApplicationContext(), url, entity, "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this, "Message Failed to send", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void copyToken(){
        String token = sharedPreferences.getString("mytoken","NOT AVAILABLE");
        //copy access token to clip board
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("accessToken", token);
        clipboard.setPrimaryClip(clip);
    }
}
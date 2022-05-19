package com.mosaza.falldetectionapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mosaza.falldetectionapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

public class LiveStreamActivity extends AppCompatActivity {

    private int SERVER_PORT = 1234;
    private String SERVER_IP;
    private ClientThread clientThread;
    private Thread thread;
    private Handler handler;

    private Socket socket;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        SERVER_IP = getIntent().getStringExtra("SERVER_IP");

        handler = new Handler();
        image = findViewById(R.id.live_stream_image);

        connectToServer();
    }

    private void showImage(final Bitmap bmp){
        handler.post(new Runnable() {
            @Override
            public void run() {
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getWidth(), false));
            }
        });
    }

    private void showMessage(final String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveStreamActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void connectToServer(){
        Toast.makeText(this, "Connecting to server", Toast.LENGTH_LONG).show();
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(clientThread != null){
            try {
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.currentThread().interrupt();
            thread.interrupt();
            clientThread = null;
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                try {
                    InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
                    socket = new Socket(serverAddress, SERVER_PORT);
                    showMessage("Connected to server");

                    while (!Thread.currentThread().isInterrupted()) {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(socket.getInputStream(), "utf-8"))) {
                            String responseLine, image;

                            while ((responseLine = br.readLine()) != null) {
                                responseLine = responseLine.trim();
                                if(responseLine.contains("image")){
                                    image = responseLine.substring("\"image\": \"".length());
                                    image = image.substring(0, image.length() - 1);
                                    byte[] decoded = Base64.getDecoder().decode(image);
                                    for (int i = 0; i < decoded.length; i++) {
                                        decoded[i] = (byte) Byte.toUnsignedInt(decoded[i]);
                                    }
                                    Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                                    showImage(bmp);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("TAG", "EX: " + e.getMessage());
                }
            }
            else{
                showMessage("To run live stream you need android version Oreo or more");
            }

        }
    }
}
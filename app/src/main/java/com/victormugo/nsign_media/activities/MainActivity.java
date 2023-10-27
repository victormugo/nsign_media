package com.victormugo.nsign_media.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.victormugo.nsign_media.api.models.VoMedia;
import com.victormugo.nsign_media.databinding.ActivityMainBinding;
import com.victormugo.nsign_media.services.LoadMediaData;
import com.victormugo.nsign_media.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(Core.TAG, "onCreate");

        // Verficar existencia del fichero

        //  Si no existe, petición api para la descarga
        //  Si existe, verificar si está descomprimido
        //      Si no está, descomprimir
        //      leer events.json

        // Iniciar servicio
        Core.activateService(getBaseContext(), LoadMediaData.class);

        // Bucle para reproducir los datos
        //this.doLoopMedia();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(Core.TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(Core.TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(Core.TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(Core.TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(Core.TAG, "onStop");

        // Finalizar servicio
        Core.finishService(getBaseContext(), LoadMediaData.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(Core.TAG, "onDestroy");
    }

    /**
     * Método para reproducir en loop los datos del fichero events.json
     */
    public void doLoopMedia() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // background job

            handler.post(() -> {
                InputStream inputstream = null;
                /*try {
                    inputstream = getBaseContext().getAssets().open("media/" + name);
                    Drawable drawable = Drawable.createFromStream(inputstream, null);
                    Log.d(Core.TAG, "---------> drawable: " + drawable);
                    binding.imageMedia.setImageDrawable(drawable);
                    inputstream .close();

                } catch (IOException e) {
                    Log.d(Core.TAG, "---------> e: " + e.getMessage());
                    throw new RuntimeException(e);
                }*/
            });
        });
    }
}
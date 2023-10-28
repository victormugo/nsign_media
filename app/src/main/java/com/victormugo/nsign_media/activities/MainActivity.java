package com.victormugo.nsign_media.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.victormugo.nsign_media.bus.IntentServiceResult;
import com.victormugo.nsign_media.databinding.ActivityMainBinding;
import com.victormugo.nsign_media.services.LoadMediaData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;

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
        EventBus.getDefault().register(this);
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

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Core.TAG, "onDestroy");
    }

    @Subscribe
    public void onEvent(IntentServiceResult intentServiceResult) {

        runOnUiThread(() -> {

            // Saber si es png o mp4
            String[] separated = intentServiceResult.getResource().getName().split("\\.");
            if (separated[1].equals(Core.image)) {
                // ------ ES IMAGEN
                // Esconder el binding del video
                binding.videoMedia.setVisibility(View.GONE);
                binding.imageMedia.setVisibility(View.VISIBLE);

                InputStream inputstream;
                try {
                    inputstream = getBaseContext().getAssets().open("media/" + intentServiceResult.getResource().getName());
                    Drawable drawable = Drawable.createFromStream(inputstream, null);

                    binding.imageMedia.getLayoutParams().width = intentServiceResult.getWidth();
                    binding.imageMedia.getLayoutParams().height = intentServiceResult.getHeigh();

                    binding.imageMedia.setX(intentServiceResult.getX());
                    binding.imageMedia.setY(intentServiceResult.getY());

                    binding.imageMedia.setImageDrawable(drawable);

                    inputstream.close();

                } catch (IOException e) {
                    Log.d(Core.TAG, "---------> e: " + e.getMessage());
                    throw new RuntimeException(e);
                }

            } else if (separated[1].equals(Core.video)) {
                // ------ ES VIDEO
                // Esconder el binding de la imagen
                binding.videoMedia.setVisibility(View.VISIBLE);
                binding.imageMedia.setVisibility(View.GONE);

                try {
                    Log.d(Core.TAG, "------------------> intentServiceResult.getResource().getName()).toString(): " + intentServiceResult.getResource().getName());

                    String name = intentServiceResult.getResource().getName();
                    name = name.toLowerCase();

                    // name = name.replace(" ", "");

                    String[] firstName = name.split(" ");
                    name = firstName[0];

                    Log.d(Core.TAG, "------------------> name: " + name);

                    MediaController mediaController = new MediaController(this);
                    binding.videoMedia.setMediaController(mediaController);
                    String fileName = "android.resource://"+  getPackageName() + "/raw/" + name;
                    Log.d(Core.TAG, "------------------> fileName: " + fileName);
                    binding.videoMedia.setVideoURI(Uri.parse(fileName));

                    binding.imageMedia.getLayoutParams().width = intentServiceResult.getWidth();
                    binding.imageMedia.getLayoutParams().height = intentServiceResult.getHeigh();

                    binding.imageMedia.setX(intentServiceResult.getX());
                    binding.imageMedia.setY(intentServiceResult.getY());

                    binding.videoMedia.start();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                // Formato no reconocido
            }

        });
    }
}
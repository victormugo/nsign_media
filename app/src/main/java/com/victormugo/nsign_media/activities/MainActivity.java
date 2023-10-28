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

        Log.d(Core.TAG, "-------------> entra en el onCreate");

        binding.loading.setVisibility(View.VISIBLE);


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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registrar tunel entre background y foreground
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Finalizar servicio
        Core.finishService(getBaseContext(), LoadMediaData.class);

        // Finalizar tunel entre background y foreground
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(IntentServiceResult intentServiceResult) {

        runOnUiThread(() -> {

            Log.d(Core.TAG, "-----------------> Entra en onEvent");
            binding.loading.setVisibility(View.GONE);

            // Saber si es png o mp4
            String[] separated = intentServiceResult.getResource().getName().split("\\.");

            if (separated[1].equals(Core.image)) {
                // ------ IMAGEN ------

                // Esconder vista del video
                // Mostrar vista de imagen
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
                // ------ VIDEO ------

                // Esconder vista de la imagen
                // Mostrar vista del video
                binding.videoMedia.setVisibility(View.VISIBLE);
                binding.imageMedia.setVisibility(View.GONE);

                try {
                    Log.d(Core.TAG, "------------------> intentServiceResult.getResource().getName()).toString(): " + intentServiceResult.getResource().getName());

                    String name = separated[0];

                    name = name.toLowerCase(); // Cambiar todas las letras a minusculas
                    name = name.replace(" ", "_"); // Cambiar todos los espacios en blanco a _

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
                    Log.d(Core.TAG, "-------------> e: " + e.getMessage());
                    throw new RuntimeException(e);
                }

            } else {
                // Formato no reconocido
            }

        });
    }
}
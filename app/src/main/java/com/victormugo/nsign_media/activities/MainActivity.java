package com.victormugo.nsign_media.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.victormugo.nsign_media.api.models.VoResource;
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

        EventBus.getDefault().register(this);
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

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Core.TAG, "onDestroy");
    }

    @Subscribe
    public void onEvent(VoResource voResource) {

        runOnUiThread(() -> {

            InputStream inputstream = null;
            try {
                inputstream = getBaseContext().getAssets().open("media/" + voResource.getName());
                Drawable drawable = Drawable.createFromStream(inputstream, null);
                Log.d(Core.TAG, "---------> drawable: " + drawable);
                binding.imageMedia.setImageDrawable(drawable);
                inputstream .close();

            } catch (IOException e) {
                Log.d(Core.TAG, "---------> e: " + e.getMessage());
                throw new RuntimeException(e);
            }

        });
    }
}
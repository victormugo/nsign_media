package com.victormugo.nsign_media.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import com.victormugo.nsign_media.R;
import com.victormugo.nsign_media.bus.IntentServiceResult;
import com.victormugo.nsign_media.databinding.ActivityMainBinding;
import com.victormugo.nsign_media.services.LoadMediaData;
import com.victormugo.nsign_media.utils.Dialogs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 7;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(Core.TAG, "-------------> entra en el onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Core.TAG, "-------------> entra en el onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Core.TAG, "-------------> entra en el onStart");

        // Mostrar loading mientras se cargan los recursos
        binding.loading.setVisibility(View.VISIBLE);

        binding.imageMedia.setVisibility(View.GONE);
        binding.videoMedia.setVisibility(View.GONE);

        // Verificar permisos
        requestForStoragePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Core.TAG, "-------------> entra en el onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Core.TAG, "-------------> entra en el onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Core.TAG, "-------------> entra en el onStop");

        // Botón OFF
        desactivateMechanism();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Core.TAG, "-------------> entra en el onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(Core.TAG, "-----> Entra aqui solo cuando es android below 11");
        Log.d(Core.TAG, "-----> requestCode: " + requestCode);

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                activateMechanism();

            } else {
                Dialogs.showMaterialDialog(getString(R.string.permission_storage_denied), getString(R.string.app_name), (dialog, which) -> dialog.dismiss(), false, MainActivity.this);
                binding.loading.setVisibility(View.GONE);
            }
        }
    }

    private void requestForStoragePermissions() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Android is 11 (R) o superior
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);

            } catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }

        } else {
            // Por debajo de Android 11
            Log.d(Core.TAG, "---------------> Entra en below11");

            ActivityCompat.requestPermissions(this,  new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Android is 11 (R) o superior
            if (Environment.isExternalStorageManager()) {
                //Manage External Storage Permissions YES
                Log.d(Core.TAG, "Above 11 ----- onActivityResult: Manage External Storage Permissions Granted");

                // Botón ON
                activateMechanism();

            } else {
                // Mostrar mensaje al usuario de que si acepta el permiso, la aplicación no puede continuar
                Dialogs.showMaterialDialog(getString(R.string.permission_storage_denied), getString(R.string.app_name), (dialog, which) -> dialog.dismiss(), false, MainActivity.this);
                binding.loading.setVisibility(View.GONE);
            }
        }
    });

    /**
     * Método para activar el proceso de ejecuciones de la aplicación
     */
    public void activateMechanism() {

        // Iniciar servicio
        Core.activateService(getBaseContext(), LoadMediaData.class);

        // Registrar tunel entre background y foreground
        EventBus.getDefault().register(this);
    }

    /**
     * Método para desactivar las ejecuciones de la aplicación
     */
    public void desactivateMechanism() {

        // Finalizar servicio
        Core.finishService(getBaseContext(), LoadMediaData.class);

        // Finalizar tunel entre background y foreground
        EventBus.getDefault().unregister(this);
    }

    /**
     * Método que recibe los parámetros del servicio en background
     * @param intentServiceResult Clase que se recibe del servicio
     */
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
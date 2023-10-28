package com.victormugo.nsign_media.utils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.victormugo.nsign_media.activities.Core;
import com.victormugo.nsign_media.api.models.VoMedia;
import com.victormugo.nsign_media.api.models.VoPlaylists;
import com.victormugo.nsign_media.api.models.VoResource;
import com.victormugo.nsign_media.bus.IntentServiceResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.ResponseBody;

public class Utils {

    /**
     * Método para realizar la lectura del fichero events.json
     * @return VoMedia a partir del JSON events.json
     */
    public static VoMedia loadJSONFromAsset() {
        VoMedia voMedia;
        String json;

        try {
            // InputStream is = context.getAssets().open(Core.JSON_FILE);
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Core.JSON_FILE;  // fist part gets the directory of download folder and last part is your file name
            InputStream is = new BufferedInputStream(new FileInputStream(path));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

            // Grabar en la clase Media la información del fichero
            Gson gson = new Gson();
            voMedia = gson.fromJson(json,VoMedia.class);

        } catch (IOException ex) {
            // No existe el fichero
            // Petición para descargarlo
            Log.d(Core.TAG, "error: " + ex.getMessage());

            ex.printStackTrace();
            return null;
        }
        return voMedia;
    }

    /**
     * Método para cargar el siguiente recurso para mostrarlo en pantalla
     * @param voMedia Clase media
     * @return VoResource
     */
    public static IntentServiceResult loadNextMediaFile(VoMedia voMedia) {

        // Verificar que si todos los recursos se han mostrado, volver a inciarlizar
        verifyAllResourcesLoaded(voMedia);

        for (int i = 0; i<voMedia.getPlaylists().size(); i++) {
            VoPlaylists voPlaylists = voMedia.getPlaylists().get(i);

            Log.d(Core.TAG, "-----> id:" + voPlaylists.getId());
            Log.d(Core.TAG, "-----> width:" + voPlaylists.getWidth());

            for (int j = 0; j<voPlaylists.getResources().size(); j++) {
                VoResource voResource = voPlaylists.getResources().get(j);

               if (!voResource.isDone()) {
                    Log.d(Core.TAG, "---------> name: " + voResource.getName());

                   voResource.setDone(true);

                   return new IntentServiceResult(voPlaylists.getX(), voPlaylists.getY(), voPlaylists.getWidth(), voPlaylists.getHeigh(), voResource);
                }
            }
        }
        return null;
    }

    /**
     * Método que calcula el número total de recursos a mostrar en pantalla
     * @param voMedia Clase media
     * @return total = Número total de recursos
     */
    public static int totalResources(VoMedia voMedia) {
        int total = 0;

        for (int i = 0 ; i<voMedia.getPlaylists().size(); i++) {
            VoPlaylists voPlaylists = voMedia.getPlaylists().get(i);

            for (int j = 0; j<voPlaylists.getResources().size(); j++) {
                total ++;
            }
        }
        return total;
    }

    /**
     * Método que devuelve si todos los recursos se han visualizado para volver a iniciar
     * @param voMedia Clase media
     * @return boolean
     */
    public static boolean allResourcesDone(VoMedia voMedia) {

        for (int i = 0 ; i<voMedia.getPlaylists().size(); i++) {
            VoPlaylists voPlaylists = voMedia.getPlaylists().get(i);

            for (int j = 0; j<voPlaylists.getResources().size(); j++) {
                VoResource voResource = voPlaylists.getResources().get(j);

                if (!voResource.isDone()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Método que verifica si todos los recursos ya han sido mostrados en pantalla para volver a empezar
     * @param voMedia Clase media
     */
    public static void verifyAllResourcesLoaded(VoMedia voMedia) {
        // Buscar el número de recursos realizados
        boolean allDone = allResourcesDone(voMedia);
        Log.d(Core.TAG, "-----------> allDone: " + allDone);

        if (allDone) {
            // Están todos realizados.
            // Inicializarlos de nuevo a false
            inicializeMediaDone(voMedia);
        }
    }

    /**
     * Método para inicializar de nuevo todos los recursos a NO realizados
     * @param voMedia Clase media
     */
    public static void inicializeMediaDone(VoMedia voMedia) {

        for (int i = 0 ; i<voMedia.getPlaylists().size(); i++) {
            VoPlaylists voPlaylists = voMedia.getPlaylists().get(i);

            for (int j = 0; j < voMedia.getPlaylists().get(i).getResources().size(); j++) {
                VoResource voResource = voPlaylists.getResources().get(j);
                voResource.setDone(false);
            }
        }
    }


    public static boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Core.FILE_NAME);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    // Log.d(Core.TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                // Descomprimir fichero
                final File rar = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Core.FILE_NAME);
                Log.d(Core.TAG, "------------------> rar: " + rar);

                final File destinationFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator);
                Log.d(Core.TAG, "-------------------> destinationFolder: " + destinationFolder);

                // TODO
                // Junrar.extract(rar, destinationFolder);

                return true;

            } catch (IOException e) {
                return false;

            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }

        } catch (IOException e) {
            return false;
        }
    }

}

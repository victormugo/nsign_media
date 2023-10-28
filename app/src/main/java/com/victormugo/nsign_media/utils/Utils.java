package com.victormugo.nsign_media.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.victormugo.nsign_media.activities.Core;
import com.victormugo.nsign_media.api.models.VoMedia;
import com.victormugo.nsign_media.api.models.VoPlaylists;
import com.victormugo.nsign_media.api.models.VoResource;
import com.victormugo.nsign_media.bus.IntentServiceResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    /**
     * Método para realizar la lectura del fichero events.json
     * @return VoMedia a partir del JSON events.json
     */
    public static VoMedia loadJSONFromAsset(Context context) {
        VoMedia voMedia;
        String json;

        try {
            InputStream is = context.getAssets().open("events.json");
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

}

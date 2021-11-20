package com.example.dev4puzzle_v3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ServicioMusica extends Service {

    //Objeto reproductor.
    MediaPlayer reproductor;

    @Override
    //Se crea el método para crear el reproductor y se facilita la ruta a la carpeta donde está el archivo a reproducir.
    public void onCreate() {
        reproductor = MediaPlayer.create(this, R.raw.audio);
    }

    @Override
    //Se inicia el reproductor de música.
    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        reproductor.start();
        return START_STICKY;
    }

    //Se para el reproductor de música
    @Override
    public void onDestroy() {
        reproductor.stop();
    }

    //Método obligatorio para las clases Service
    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

}
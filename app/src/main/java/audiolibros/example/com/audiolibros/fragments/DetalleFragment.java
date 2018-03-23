package audiolibros.example.com.audiolibros.fragments;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.io.IOException;

import audiolibros.example.com.audiolibros.Aplicacion;
import audiolibros.example.com.audiolibros.Libro;
import audiolibros.example.com.audiolibros.MainActivity;
import audiolibros.example.com.audiolibros.R;
import audiolibros.example.com.audiolibros.ZoomSeekBar;

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

public class DetalleFragment extends Fragment implements View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final int ID_NOTIFICACION = 1;
    static final String ID_CANAL = "channel_id";
    private NotificationManager notificManager;
    private NotificationCompat.Builder notificacion;
    private RemoteViews remoteViews;

    public static String ARG_ID_LIBRO = "id_libro";
    MediaPlayer mediaPlayer;
    MediaController mediaController;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle, contenedor, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        return vista;
    }

    @Override
    public void onResume() {
        DetalleFragment detalleFragment = (DetalleFragment) getFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment == null) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }

    private void ponInfoLibro(int id, View vista) {
        Libro libro = ((Aplicacion) getActivity().getApplication())
                .getListaLibros().get(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        Aplicacion aplicacion = (Aplicacion) getActivity().getApplication();
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(libro.urlImagen, aplicacion.getLectorImagenes());
        vista.setOnTouchListener(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        Uri audio = Uri.parse(libro.urlAudio);
        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir " + audio, e);
        }

        remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.reproducir, android.R.drawable.ic_media_play);
        remoteViews.setImageViewResource(R.id.imagen, getImageResource(libro));
        remoteViews.setTextViewText(R.id.titulo, libro.titulo);
        remoteViews.setTextColor(R.id.titulo, Color.BLACK);
        remoteViews.setTextViewText(R.id.texto, libro.autor);
        remoteViews.setTextColor(R.id.texto, Color.BLACK);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion = new NotificationCompat.Builder(getActivity(), ID_CANAL).setContent(remoteViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Notificación personalizada")
                .setContentIntent(pendingIntent);
        notificManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(ID_CANAL, "Nombre del canal", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Descripción del canal");
            notificManager.createNotificationChannel(channel);
        }
        notificManager.notify(ID_NOTIFICACION, notificacion.build());
    }

    private int getImageResource(Libro libro) {
        switch (libro.titulo) {
            case "Avecilla":
                return R.drawable.avecilla;
            case "Canción de Rolando":
                return R.drawable.cancion_rolando;
            case "Divina Comedia":
                return R.drawable.divinacomedia;
            case "La iliada":
                return R.drawable.iliada;
            case "Kappa":
                return R.drawable.kappa;
            case "Matrimonio de sabuesos":
                return R.drawable.matrimonio_sabuesos;
            case "Viejo Pancho, El":
                return R.drawable.viejo_pancho;
            default:
                return R.drawable.books;
        }
    }

    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            mediaPlayer.start();
        }
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(R.id.fragment_detalle));
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override
    public boolean onTouch(View vista, MotionEvent evento) {
        mediaController.show();
        return false;
    }

    @Override
    public void onStop() {
        mediaController.hide();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onStop();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}

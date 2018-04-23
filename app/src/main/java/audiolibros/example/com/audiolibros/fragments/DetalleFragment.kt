package audiolibros.example.com.audiolibros.fragments

import android.app.Fragment
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RemoteViews
import android.widget.TextView

import com.android.volley.toolbox.NetworkImageView

import java.io.IOException

import audiolibros.example.com.audiolibros.Aplicacion
import audiolibros.example.com.audiolibros.Libro
import audiolibros.example.com.audiolibros.MainActivity
import audiolibros.example.com.audiolibros.R
import audiolibros.example.com.audiolibros.ZoomSeekBar

/**
 * Created by Miguel Á. Núñez on 29/01/2018.
 */

class DetalleFragment : Fragment(), View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
    private var notificManager: NotificationManager? = null
    private var notificacion: NotificationCompat.Builder? = null
    private var remoteViews: RemoteViews? = null
    var mediaPlayer: MediaPlayer? = null
    private var mediaController: MediaController? = null

    override fun onCreateView(inflador: LayoutInflater, contenedor: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vista = inflador.inflate(R.layout.fragment_detalle, contenedor, false)
        val args = arguments
        if (args != null) {
            val position = args.getInt(ARG_ID_LIBRO)
            ponInfoLibro(position, vista)
        } else {
            ponInfoLibro(0, vista)
        }
        return vista
    }

    override fun onResume() {
        val detalleFragment = fragmentManager.findFragmentById(R.id.detalle_fragment) as? DetalleFragment
        if (detalleFragment == null) {
            (activity as MainActivity).mostrarElementos(false)
        }
        super.onResume()
    }

    private fun ponInfoLibro(id: Int, vista: View) {
        val libro = (activity.application as Aplicacion).listaLibros?.get(id)
        (vista.findViewById<View>(R.id.titulo) as TextView).text = libro?.titulo
        (vista.findViewById<View>(R.id.autor) as TextView).text = libro?.autor
        val aplicacion = activity.application as Aplicacion
        (vista.findViewById<View>(R.id.portada) as NetworkImageView).setImageUrl(libro?.urlImagen, aplicacion.getLectorImagenes())
        vista.setOnTouchListener(this)
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnPreparedListener(this)
        mediaController = MediaController(activity)
        val audio = Uri.parse(libro?.urlAudio)
        try {
            mediaPlayer!!.setDataSource(activity, audio)
            mediaPlayer!!.prepareAsync()
        } catch (e: IOException) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir $audio", e)
        }

        remoteViews = RemoteViews(activity.packageName, R.layout.custom_notification)
        remoteViews!!.setImageViewResource(R.id.reproducir, android.R.drawable.ic_media_play)
        remoteViews!!.setImageViewResource(R.id.imagen, getImageResource(libro!!))
        remoteViews!!.setTextViewText(R.id.titulo, libro.titulo)
        remoteViews!!.setTextColor(R.id.titulo, Color.BLACK)
        remoteViews!!.setTextViewText(R.id.texto, libro.autor)
        remoteViews!!.setTextColor(R.id.texto, Color.BLACK)

        val intent = Intent(activity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificacion = NotificationCompat.Builder(activity, ID_CANAL).setContent(remoteViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Notificación personalizada")
                .setContentIntent(pendingIntent)
        notificManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(ID_CANAL, "Nombre del canal", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Descripción del canal"
            notificManager!!.createNotificationChannel(channel)
        }
        notificManager!!.notify(ID_NOTIFICACION, notificacion!!.build())
    }

    private fun getImageResource(libro: Libro): Int {
        when (libro.titulo) {
            "Avecilla" -> return R.drawable.avecilla
            "Canción de Rolando" -> return R.drawable.cancion_rolando
            "Divina Comedia" -> return R.drawable.divinacomedia
            "La iliada" -> return R.drawable.iliada
            "Kappa" -> return R.drawable.kappa
            "Matrimonio de sabuesos" -> return R.drawable.matrimonio_sabuesos
            "Viejo Pancho, El" -> return R.drawable.viejo_pancho
            else -> return R.drawable.books
        }
    }

    fun ponInfoLibro(id: Int) {
        ponInfoLibro(id, view!!)
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer")
        val preferencias = PreferenceManager
                .getDefaultSharedPreferences(activity)
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            mediaPlayer.start()
        }
        mediaController!!.setMediaPlayer(this)
        mediaController!!.setAnchorView(view!!.findViewById(R.id.fragment_detalle))
        mediaController!!.isEnabled = true
        mediaController!!.show()
    }

    override fun onTouch(vista: View, evento: MotionEvent): Boolean {
        mediaController!!.show()
        return false
    }

    override fun onStop() {
        mediaController!!.hide()
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        } catch (e: Exception) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()")
        }

        super.onStop()
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun getCurrentPosition(): Int {
        try {
            return mediaPlayer!!.currentPosition
        } catch (e: Exception) {
            return 0
        }

    }

    override fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    override fun pause() {
        mediaPlayer!!.pause()
    }

    override fun seekTo(pos: Int) {
        mediaPlayer!!.seekTo(pos)
    }

    override fun start() {
        mediaPlayer!!.start()
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    companion object {

        private val ID_NOTIFICACION = 1
        internal val ID_CANAL = "channel_id"

        var ARG_ID_LIBRO = "id_libro"
    }
}

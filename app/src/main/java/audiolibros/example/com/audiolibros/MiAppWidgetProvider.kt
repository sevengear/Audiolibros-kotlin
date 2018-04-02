package audiolibros.example.com.audiolibros

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews

import android.content.Context.MODE_PRIVATE

/**
 * Created by Miguel Á. Núñez on 17/02/2018.
 */

class MiAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            actualizaWidget(context, widgetId)
        }
    }

    companion object {

        fun actualizaWidget(context: Context, widgetId: Int) {
            val pref = context.getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE)
            val id = pref.getInt("ultimo", -1)
            if (id >= 0) {
                val libro = (context.applicationContext as Aplicacion)
                        .listaLibros!![id]
                val remoteViews = RemoteViews(context.packageName, R.layout.widget)
                remoteViews.setTextViewText(R.id.txtAutor, libro.autor)
                remoteViews.setTextViewText(R.id.txtTitle, libro.titulo)
                remoteViews.setImageViewUri(R.id.imgList, Uri.parse(libro.urlImagen))
                AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews)
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                remoteViews.setOnClickPendingIntent(R.id.imgList, pendingIntent)
            }
        }
    }
}

package audiolibros.example.com.audiolibros;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Miguel Á. Núñez on 17/02/2018.
 */

public class MiAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            actualizaWidget(context, widgetId);
        }
    }

    public static void actualizaWidget(Context context, int widgetId) {
        SharedPreferences pref = context.getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE);
        int id = pref.getInt("ultimo", -1);
        if (id >= 0) {
            Libro libro = ((Aplicacion) context.getApplicationContext())
                    .getListaLibros().get(id);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.txtAutor, libro.getAutor());
            remoteViews.setTextViewText(R.id.txtTitle, libro.getTitulo());
            remoteViews.setImageViewUri(R.id.imgList, Uri.parse(libro.getUrlImagen()));
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.imgList, pendingIntent);
        }
    }
}

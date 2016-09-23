package com.habitrpg.android.habitica.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.habitrpg.android.habitica.R;

public abstract class BaseWidgetProvider extends AppWidgetProvider {

    protected Context context;

    /**
     * Returns number of cells needed for given size of the widget.<br/>
     * see http://stackoverflow.com/questions/14270138/dynamically-adjusting-widgets-content-and-layout-to-the-size-the-user-defined-t
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        this.context = context;
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        appWidgetManager.partiallyUpdateAppWidget(appWidgetId,
                sizeRemoteViews(context, options, appWidgetId));

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
                newOptions);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public RemoteViews sizeRemoteViews(Context context, Bundle options, int widgetId) {
        this.context = context;
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = options
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        // First find out rows and columns based on width provided.
        int rows = getCellsForSize(minHeight);
        int columns = getCellsForSize(minWidth);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                layoutResourceId());

        return configureRemoteViews(remoteViews, widgetId, columns, rows);
    }

    abstract public int layoutResourceId();

    abstract public RemoteViews configureRemoteViews(RemoteViews remoteViews, int widgetId, int columns, int rows);
}
package br.com.devfocus.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetUpdater {
    suspend fun update(context: Context) {
        DevFocusWidget().updateAll(context)
    }
}

package com.defter.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.NumberFormat
import java.util.Locale

class DefterWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {

        private fun formatTl(amount: Float): String {
            val nf = NumberFormat.getNumberInstance(Locale("tr", "TR"))
            nf.minimumFractionDigits = 2
            nf.maximumFractionDigits = 2
            return nf.format(amount) + " \u20BA" // ₺
        }

        fun updateAllWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            val prefs = context.getSharedPreferences(
                DefterWidgetPlugin.PREFS_NAME, Context.MODE_PRIVATE
            )
            val todayIncome = prefs.getFloat(DefterWidgetPlugin.KEY_TODAY_INCOME, 0f)
            val todayExpense = prefs.getFloat(DefterWidgetPlugin.KEY_TODAY_EXPENSE, 0f)
            val monthIncome = prefs.getFloat(DefterWidgetPlugin.KEY_MONTH_INCOME, 0f)
            val monthExpense = prefs.getFloat(DefterWidgetPlugin.KEY_MONTH_EXPENSE, 0f)
            val monthLabel = prefs.getString(DefterWidgetPlugin.KEY_MONTH_LABEL, "") ?: ""

            val todayNet = todayIncome - todayExpense
            val monthNet = monthIncome - monthExpense

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_defter)

                views.setTextViewText(R.id.widget_month_label, monthLabel)
                views.setTextViewText(R.id.widget_today_net, formatTl(todayNet))
                views.setTextViewText(R.id.widget_month_income, "+" + formatTl(monthIncome))
                views.setTextViewText(R.id.widget_month_expense, "-" + formatTl(monthExpense))
                views.setTextViewText(R.id.widget_month_net, formatTl(monthNet))

                // Widget'a dokununca uygulamayı aç
                val launchIntent = context.packageManager
                    .getLaunchIntentForPackage(context.packageName)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, launchIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

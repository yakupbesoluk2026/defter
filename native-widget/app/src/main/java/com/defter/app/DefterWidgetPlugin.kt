package com.defter.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

/**
 * Defter uygulamasındaki web katmanı ile ana ekran widget'ı arasındaki köprü.
 * www/index.html içindeki pushWidgetData() fonksiyonu, her kayıt
 * eklendiğinde/silindiğinde bu eklentinin updateSummary metodunu çağırır.
 */
@CapacitorPlugin(name = "DefterWidget")
class DefterWidgetPlugin : Plugin() {

    companion object {
        const val PREFS_NAME = "defter_widget_prefs"
        const val KEY_TODAY_INCOME = "today_income"
        const val KEY_TODAY_EXPENSE = "today_expense"
        const val KEY_MONTH_INCOME = "month_income"
        const val KEY_MONTH_EXPENSE = "month_expense"
        const val KEY_MONTH_LABEL = "month_label"
        const val KEY_UPDATED_AT = "updated_at"
    }

    @PluginMethod
    fun updateSummary(call: PluginCall) {
        val ctx: Context = context
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit()
            .putFloat(KEY_TODAY_INCOME, call.getDouble("todayIncome", 0.0)!!.toFloat())
            .putFloat(KEY_TODAY_EXPENSE, call.getDouble("todayExpense", 0.0)!!.toFloat())
            .putFloat(KEY_MONTH_INCOME, call.getDouble("monthIncome", 0.0)!!.toFloat())
            .putFloat(KEY_MONTH_EXPENSE, call.getDouble("monthExpense", 0.0)!!.toFloat())
            .putString(KEY_MONTH_LABEL, call.getString("monthLabel", ""))
            .putLong(KEY_UPDATED_AT, System.currentTimeMillis())
            .apply()

        // Ekrandaki tüm Defter widget örneklerini yeniden çizmesi için bildir
        val appWidgetManager = AppWidgetManager.getInstance(ctx)
        val componentName = ComponentName(ctx, DefterWidgetProvider::class.java)
        val ids = appWidgetManager.getAppWidgetIds(componentName)
        if (ids.isNotEmpty()) {
            DefterWidgetProvider.updateAllWidgets(ctx, appWidgetManager, ids)
        }

        val ret = JSObject()
        ret.put("ok", true)
        call.resolve(ret)
    }
}

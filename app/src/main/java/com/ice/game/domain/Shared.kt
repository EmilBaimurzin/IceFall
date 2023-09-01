package com.ice.game.domain

import android.content.Context

class Shared(private val context: Context) {
    private val sp = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

    fun getBest(): Int = sp.getInt("BEST", 0)
    fun setBest(best: Int) = sp.edit().putInt("BEST", best).apply()

    fun setVolume(volume: Int) {
        sp.edit().putInt("VOLUME", volume).apply()
    }

    fun getVolume(): Int = sp.getInt("VOLUME", 50)

    fun setVibro(value: Boolean) {
        sp.edit().putBoolean("VIBRO", value).apply()
    }

    fun getVibro(): Boolean = sp.getBoolean("VIBRO", true)
}
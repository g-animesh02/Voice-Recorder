package com.recvoice.vc_recorder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

lateinit var switchName: RadioButton
lateinit var switchDark: RadioButton
lateinit var mp3: RadioButton
lateinit var wav: RadioButton
lateinit var high: RadioButton
lateinit var low: RadioButton
lateinit var standard: RadioButton
lateinit var sharedPreference: SharedPreferences
lateinit var toolbarSettings: Toolbar
lateinit var drawerLayout: DrawerLayout

class MainSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference =
            getSharedPreferences(getString(R.string.table_settings), Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(getString(R.string.dark_mode), false))
            setTheme(R.style.mainPageDark)
        else
            setTheme(R.style.mainPageLight)
        setContentView(R.layout.activity_main_settings)

        switchDark = findViewById(R.id.switchDarkMode)
        switchName = findViewById(R.id.switchName)
        mp3 = findViewById(R.id.mp3)
        wav = findViewById(R.id.wav)
        high = findViewById(R.id.high)
        standard = findViewById(R.id.standard)
        low = findViewById(R.id.low)
        toolbarSettings = findViewById(R.id.toolbarSettings)
        drawerLayout = findViewById(R.id.drawerLayout)

        setSupportActionBar(toolbarSettings)
        supportActionBar?.title = "Settings"
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (sharedPreference.getBoolean(getString(R.string.dark_mode), false)) {
            switchDark.isChecked = true
        }
        if (sharedPreference.getBoolean(getString(R.string.name), false))
            switchName.isChecked = true
        if (sharedPreference.getBoolean(getString(R.string.mp3), false))
            mp3.isChecked = true
        if (sharedPreference.getBoolean(getString(R.string.wav), false))
            wav.isChecked = true
        if (sharedPreference.getBoolean(getString(R.string.high), false))
            high.isChecked = true
        if (sharedPreference.getBoolean(getString(R.string.low), false))
            low.isChecked = true
        if (sharedPreference.getBoolean(getString(R.string.standard), false))
            standard.isChecked = true

        if (!(sharedPreference.getBoolean(
                getString(R.string.low),
                false
            )) && (!(sharedPreference.getBoolean(
                getString(R.string.standard),
                false
            ))) && (!(sharedPreference.getBoolean(getString(R.string.high), false)))
        ) {
            standard.isChecked = true
            sharedPreference.edit().putBoolean(getString(R.string.standard), true).apply()
        }
        if (!(sharedPreference.getBoolean(
                getString(R.string.mp3),
                false
            )) && !(sharedPreference.getBoolean(getString(R.string.wav), false))
        ) {
            mp3.isChecked = true
            sharedPreference.edit().putBoolean(getString(R.string.mp3), true).apply()
        }
        switchDark.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.dark_mode), false)) {

                sharedPreference.edit().putBoolean(getString(R.string.dark_mode), false).apply()
                finish()
                startActivity(Intent(this@MainSettings, this@MainSettings::class.java))
                overridePendingTransition(R.anim.anim_fadein_dark,R.anim.anim_fadeout_dark)
                finish()
                switchDark.isChecked = false
            } else {
                sharedPreference.edit().putBoolean(getString(R.string.dark_mode), true).apply()
                finish()
                startActivity(Intent(this@MainSettings, this@MainSettings::class.java))
                overridePendingTransition(R.anim.anim_fadein_dark,R.anim.anim_fadeout_dark)
                finish()
                switchDark.isChecked = true

            }

        }

        switchName.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.name), false)) {
                switchName.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.name), false).apply()
            } else {
                switchName.isChecked = true
                sharedPreference.edit().putBoolean(getString(R.string.name), true).apply()
            }
        }

        mp3.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.mp3), false)) {
                mp3.isChecked = false
                wav.isChecked = true
                sharedPreference.edit().putBoolean(getString(R.string.mp3), false).apply()
                sharedPreference.edit().putBoolean(getString(R.string.wav), true).apply()
            } else {
                mp3.isChecked = true
                wav.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.mp3), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.wav), false).apply()
            }
        }

        wav.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.wav), false)) {
                wav.isChecked = false
                mp3.isChecked = true
                sharedPreference.edit().putBoolean(getString(R.string.mp3), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.wav), false).apply()
            } else {
                wav.isChecked = true
                mp3.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.wav), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.mp3), false).apply()
            }
        }

        high.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.high), false)) {
                high.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.high), false).apply()
                if (!(sharedPreference.getBoolean(
                        getString(R.string.low),
                        false
                    )) && !(sharedPreference.getBoolean(getString(R.string.standard), false))
                ) {
                    standard.isChecked = true
                    sharedPreference.edit().putBoolean(getString(R.string.standard), true).apply()
                }
            } else {
                high.isChecked = true
                low.isChecked = false
                standard.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.high), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.standard), false).apply()
                sharedPreference.edit().putBoolean(getString(R.string.low), false).apply()
            }
        }

        standard.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.standard), false)) {
                standard.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.standard), false).apply()
                if (!(sharedPreference.getBoolean(
                        getString(R.string.low),
                        false
                    )) && (!(sharedPreference.getBoolean(
                        getString(R.string.standard),
                        false
                    ))) && (!(sharedPreference.getBoolean(getString(R.string.high), false)))
                ) {
                    standard.isChecked = true
                    sharedPreference.edit().putBoolean(getString(R.string.standard), true).apply()
                }
            } else {
                standard.isChecked = true
                high.isChecked = false
                low.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.standard), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.high), false).apply()
                sharedPreference.edit().putBoolean(getString(R.string.low), false).apply()
            }
        }

        low.setOnClickListener {
            if (sharedPreference.getBoolean(getString(R.string.low), false)) {
                low.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.low), false).apply()
                if (!(sharedPreference.getBoolean(
                        getString(R.string.high),
                        false
                    )) && !(sharedPreference.getBoolean(getString(R.string.standard), false))
                ) {
                    standard.isChecked = true
                    sharedPreference.edit().putBoolean(getString(R.string.standard), true).apply()
                }
            } else {
                low.isChecked = true
                high.isChecked = false
                standard.isChecked = false
                sharedPreference.edit().putBoolean(getString(R.string.low), true).apply()
                sharedPreference.edit().putBoolean(getString(R.string.high), false).apply()
                sharedPreference.edit().putBoolean(getString(R.string.standard), false).apply()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.anim_fadein_activity,R.anim.anim_fadeout_activity)
            finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.anim_fadein_activity,R.anim.anim_fadeout_activity)
        finishAffinity()

        super.onBackPressed()
    }
}
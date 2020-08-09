package com.recvoice.vc_recorder

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scwang.wave.MultiWaveHeader
import java.io.File
import java.io.IOException

lateinit var multiWaveHeader: MultiWaveHeader
lateinit var multiWaveFooter: MultiWaveHeader
const val RECORD_AUDIO_REQUEST_CODE = 123
lateinit var floatingPlay: FloatingActionButton
lateinit var floatingFiles: FloatingActionButton
lateinit var toolbar: Toolbar
lateinit var chronometer: Chronometer
lateinit var aniFadeIn: Animation
lateinit var aniFadeOut: Animation
lateinit var sharedPreferences: SharedPreferences
var mRecorder: MediaRecorder? = null
var starter: Int = 0
var footer: Int = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.table_settings), Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean(getString(R.string.dark_mode),false)){
            setTheme(R.style.mainPageDark)
            starter = resources.getColor(R.color.closing_wave_dark)
            footer = resources.getColor(R.color.wave_start_dark)
        }
        else{
            setTheme(R.style.mainPageLight)
            starter = resources.getColor(R.color.closing_wave_light)
            footer = resources.getColor(R.color.wave_start_light)
        }
        setContentView(R.layout.activity_main)

        sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()

        floatingPlay = findViewById(R.id.floatingPlay)
        floatingFiles = findViewById(R.id.floatingMenu)
        toolbar = findViewById(R.id.toolBarMain)
        chronometer = findViewById(R.id.chronometer)
        multiWaveHeader = findViewById(R.id.multiWave_header)
        multiWaveFooter = findViewById(R.id.multiWave_footer)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        aniFadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_fadein)
        aniFadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_fadeout)
        getPermissionToRecordAudio()
        mRecorder = MediaRecorder()

        floatingPlay.setOnClickListener {

            if (!(sharedPreferences.getBoolean(getString(R.string.isRecord),false))) {
                mRecorder = MediaRecorder()
                mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                sharedPreferences.edit().putBoolean(getString(R.string.isRecord),true).apply()
                val root: File = Environment.getExternalStorageDirectory()
                val file =
                    File(root.absolutePath.toString() + "/Voice Recorder/Audios")
                if (!file.exists()) {
                    file.mkdirs()
                }

                var mText: String
                if (sharedPreferences.getBoolean(getString(R.string.name), false)) {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Save File")
                    val viewInflated: View = LayoutInflater.from(this)
                        .inflate(R.layout.save_file, null, false)
                    val input = viewInflated.findViewById(R.id.input) as EditText
                    builder.setView(viewInflated)
                    builder.setPositiveButton(
                        android.R.string.ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                        mText = input.text.toString()
                        while (mText.isEmpty()) {
                            continue
                        }
                        val fileName: String =
                            if(sharedPreferences.getBoolean(getString(R.string.mp3),false)) {
                            root.absolutePath + "/Voice Recorder/Audios/" + ("$mText.mp3")
                        } else{
                            root.absolutePath + "/Voice Recorder/Audios/" + ("$mText.wav")
                        }

                        if(sharedPreferences.getBoolean(getString(R.string.high),false)) {
                            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                            mRecorder?.setAudioEncodingBitRate(128000)
                            mRecorder?.setAudioSamplingRate(44100)

                        } else if(sharedPreferences.getBoolean(getString(R.string.standard),false)){
                                mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                mRecorder?.setAudioEncodingBitRate(64)
                            mRecorder?.setAudioSamplingRate(44100)
                        }
                        else{
                            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            mRecorder?.setAudioEncodingBitRate(16)
                        }
                        mRecorder?.setOutputFile(fileName)

                        try {
                            floatingPlay.setImageResource(R.drawable.pause)
                            mRecorder?.prepare()
                            mRecorder?.start()
                            wave()
                            chronometer.base = SystemClock.elapsedRealtime()
                            chronometer.start()
                            sharedPreferences.edit().putBoolean(getString(R.string.isRecord),true).apply()



                            Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()
                            floatingPlay.setImageResource(R.drawable.play)

                            Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                    builder.setNegativeButton(
                        android.R.string.cancel
                    ) { dialog, _ ->
                        dialog.cancel()
                        multiWaveHeader.isVisible = false
                        multiWaveFooter.isVisible = false
                        sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()
                    }

                    builder.show()

                } else {
                    val fileName: String =
                        if(sharedPreferences.getBoolean(getString(R.string.mp3),false)) {
                            root.absolutePath + "/Voice Recorder/Audios/" + (System.currentTimeMillis()
                                .toString()+".mp3")
                        } else{
                            root.absolutePath + "/Voice Recorder/Audios/" + (System.currentTimeMillis().toString()+".wav")
                        }
                    if(sharedPreferences.getBoolean(getString(R.string.high),false)) {
                        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        mRecorder?.setAudioEncodingBitRate(128000)
                        mRecorder?.setAudioSamplingRate(44100)

                    } else if(sharedPreferences.getBoolean(getString(R.string.standard),false)){
                        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        mRecorder?.setAudioEncodingBitRate(64)
                        mRecorder?.setAudioSamplingRate(44100)
                    }
                    else{
                        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        mRecorder?.setAudioEncodingBitRate(16)
                    }
                    mRecorder?.setOutputFile(fileName)
                    try {
                        floatingPlay.setImageResource(R.drawable.pause)
                        mRecorder?.prepare()
                        mRecorder?.start()
                        wave()
                        chronometer.base = SystemClock.elapsedRealtime()
                        chronometer.start()



                        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()
                        floatingPlay.setImageResource(R.drawable.play)

                        Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            } //play
            else {
                sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()
                try {
                    try {
                        mRecorder?.stop()
                    } catch (e: java.lang.Exception) {
                        print(e)
                    }

                    mRecorder?.reset()
                    mRecorder?.release()
                    mRecorder = null
                    multiWaveHeader.startAnimation(aniFadeOut)
                    multiWaveFooter.startAnimation(aniFadeOut)
                    multiWaveHeader.isVisible = false
                    multiWaveFooter.isVisible = false
                    floatingPlay.setImageResource(R.drawable.play)

                    chronometer.stop()
                    chronometer.base = SystemClock.elapsedRealtime()
                    Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) { //pause
                    sharedPreferences.edit().putBoolean(getString(R.string.isRecord),true).apply()
                    Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }
        }

        floatingFiles.setOnClickListener {
          val  i: Intent = Intent(this@MainActivity,Recordings::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.anim_fadein_activity,R.anim.anim_fadeout_activity)
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.settings) {
            if(sharedPreferences.getBoolean(getString(R.string.isRecord),false)) {
                sharedPreferences.edit().putBoolean(getString(R.string.isRecord),false).apply()
                try {
                    try {
                        mRecorder?.stop()
                    } catch (e: java.lang.Exception) {
                        print(e)
                    }

                    mRecorder?.reset()
                    mRecorder?.release()
                    mRecorder = null
                    multiWaveHeader.startAnimation(aniFadeOut)
                    multiWaveFooter.startAnimation(aniFadeOut)
                    multiWaveHeader.isVisible = false
                    multiWaveFooter.isVisible = false
                    floatingPlay.setImageResource(R.drawable.play)

                    chronometer.stop()
                    chronometer.base = SystemClock.elapsedRealtime()
                    Toast.makeText(this, "Recording Saved", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) { //pause
                    e.printStackTrace()
                }
            }
            val i = Intent(this@MainActivity, MainSettings::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.anim_fadein_activity,R.anim.anim_fadeout_activity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "You must give permissions to use this app. App is exiting.",
                    Toast.LENGTH_SHORT
                ).show()
                finishAffinity()
            }
        }
    }

    private fun wave() {
        multiWaveHeader.isVisible = true
        multiWaveFooter.isVisible = true
        multiWaveHeader.startAnimation(aniFadeIn)
        multiWaveFooter.startAnimation(aniFadeIn)

        multiWaveHeader.velocity = 3F
        multiWaveFooter.velocity = 3F

        multiWaveHeader.progress = 1F
        multiWaveFooter.progress = 1F

        multiWaveHeader.isRunning
        multiWaveHeader.isRunning

        multiWaveHeader.gradientAngle = 45
        multiWaveFooter.gradientAngle = 45

        multiWaveHeader.waveHeight = 50
        multiWaveFooter.waveHeight = 50

        multiWaveHeader.startColor = starter
        multiWaveFooter.startColor = starter

        multiWaveHeader.closeColor = footer
        multiWaveFooter.closeColor = footer

    }
}







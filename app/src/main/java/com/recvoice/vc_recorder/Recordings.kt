package com.recvoice.vc_recorder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

lateinit var sharedPreferen: SharedPreferences
lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var recyclerAdapter: RecorderRecyclerAdapter
lateinit var toolbars: Toolbar
lateinit var relativeLayout: RelativeLayout
class Recordings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferen =
            getSharedPreferences(getString(R.string.table_settings), Context.MODE_PRIVATE)
        if(sharedPreferen.getBoolean(getString(R.string.dark_mode),false)){
            setTheme(R.style.mainPageDark)

        }
        else{
            setTheme(R.style.mainPageLight)

        }
        setContentView(R.layout.activity_recordings)
        relativeLayout = findViewById(R.id.recyclerError)
        recyclerView = findViewById(R.id.recyclerViewRecord)
        val root: File = Environment.getExternalStorageDirectory()
        val file =
            File(root.absolutePath.toString() + "/Voice Recorder/Audios/")
        if (!file.exists()) {
            file.mkdirs()
        }
        toolbars = findViewById(R.id.toolBarRecording)
        setSupportActionBar(toolbars)
        supportActionBar?.title = "Recordings"
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val path: String =
            Environment.getExternalStorageDirectory().toString() + "/Voice Recorder/Audios/"
        val directory = File(path)

        val files: Array<File>? = directory.listFiles()
        if (files != null) {
            if (files.isEmpty()){
                relativeLayout.isVisible = true
                recyclerView.isVisible = false
            } else {


                layoutManager = LinearLayoutManager(this)
                recyclerAdapter = RecorderRecyclerAdapter(this as Context, files, sharedPreferen)
                recyclerView.adapter = recyclerAdapter

                recyclerAdapter.notifyDataSetChanged()
                recyclerView.layoutManager = layoutManager
                recyclerView.invalidate()
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        (layoutManager as LinearLayoutManager).orientation
                    )
                )
            }
        }else{
            relativeLayout.isVisible = true
            recyclerView.isVisible = false
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
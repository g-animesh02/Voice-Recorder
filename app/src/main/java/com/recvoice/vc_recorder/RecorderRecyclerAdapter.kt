package com.recvoice.vc_recorder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.scwang.wave.MultiWaveHeader
import java.io.File


class RecorderRecyclerAdapter(
    val context: Context,
    var itemList: Array<File>?,
    var sharedPref: SharedPreferences
) : RecyclerView.Adapter<RecorderRecyclerAdapter.RecorderViewHolder>() {

    class RecorderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recordingname)
        val imagePlay: ImageView = view.findViewById(R.id.playImage)
        var mPlayer: MediaPlayer? = MediaPlayer()
        val deleteFile: ImageView = view.findViewById(R.id.deleteFile)
        val rowWaveHeader: MultiWaveHeader = view.findViewById(R.id.row_wave)
        val chronometers: Chronometer = view.findViewById(R.id.chronometerRec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecorderViewHolder {
        val viewss = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false)
        return RecorderViewHolder(viewss)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecorderViewHolder, position: Int) {

        sharedPref.edit().putBoolean(context.getString(R.string.rcPlay)+position.toString(), false).apply()
        val text = itemList?.get(position)?.name
        holder.textView.text = text.toString()
        holder.deleteFile.setOnClickListener {
            val builder =
                AlertDialog.Builder(context)
            builder.setTitle("Delete File")
            builder.setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()

                val fdelete = File(itemList?.get(position).toString())
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        val result = itemList?.toMutableList()
                        result?.removeAt(position)
                        itemList = result?.toTypedArray()
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemList?.size ?: 0)

                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
                //doNothing

            }
            builder.show()

        }

        holder.mPlayer = MediaPlayer()

        holder.imagePlay.setOnClickListener {

            if (!(sharedPref.getBoolean(context.getString(R.string.rcPlay)+position.toString(), false))) {
                holder.chronometers.isVisible = true
                holder.chronometers.base = SystemClock.elapsedRealtime()

                holder.chronometers.start()
                holder.mPlayer = MediaPlayer()

                sharedPref.edit().putBoolean(context.getString(R.string.rcPlay)+position.toString(), true).apply()


                holder.mPlayer?.setDataSource(itemList?.get(position).toString())
                waves(holder)
                holder.mPlayer?.prepare()
                holder.mPlayer?.start()
                holder.imagePlay.setImageResource(R.drawable.ic_pause)
                holder.mPlayer?.setOnCompletionListener(OnCompletionListener {
                    sharedPref.edit().putBoolean(context.getString(R.string.rcPlay)+position.toString(), false).apply()
                    holder.imagePlay.setImageResource(R.drawable.ic_play)
                    holder.mPlayer?.release()
                    holder.chronometers.stop()
                    holder.chronometers.isVisible = false
                    holder.rowWaveHeader.isVisible = false
                    holder.rowWaveHeader.startAnimation(aniFadeOut)
                    holder.mPlayer = null
                })
            } else {
                holder.chronometers.stop()
                sharedPref.edit().putBoolean(context.getString(R.string.rcPlay)+position.toString(), false).apply()
                holder.imagePlay.setImageResource(R.drawable.ic_play)
                holder.mPlayer?.release()
                holder.rowWaveHeader.isVisible = false
                holder.rowWaveHeader.startAnimation(aniFadeOut)
                holder.mPlayer = null

            }
        }
    }

    private fun waves(holder: RecorderViewHolder) {
        holder.rowWaveHeader.isVisible = true

        holder.rowWaveHeader.startAnimation(aniFadeIn)

        holder.rowWaveHeader.velocity = 2F

        holder.rowWaveHeader.progress = 1F

        holder.rowWaveHeader.isRunning

        holder.rowWaveHeader.gradientAngle = 45

        multiWaveHeader.waveHeight = 50


    }

}

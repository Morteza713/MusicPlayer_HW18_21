package com.example.musicplayer_hw18_21

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class MusicAdapter(var musicList: List<Music>,var musicClickListener: OnMusicClickListener?):RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
//    private lateinit var musicList: List<Music>
//    private var musicClickListener: OnMusicClickListener? = null
    private var playingMusicPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_music, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bindMusic(musicList!![position])
    }

    override fun getItemCount(): Int {
        return musicList!!.size
    }

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_music)
        private val artistTv: TextView = itemView.findViewById(R.id.tv_music_artist)
        private val musicNameTv: TextView = itemView.findViewById(R.id.tv_music_name)
        private val animationView: LottieAnimationView = itemView.findViewById(R.id.animationView)
        fun bindMusic(music: Music) {
            imageView.setImageResource(music.coverResId)
            artistTv.text = music.artist
            musicNameTv.text = music.name
            if (adapterPosition == playingMusicPos) {
                animationView.visibility = View.VISIBLE
            } else {
                animationView.visibility = View.GONE
            }
            itemView.setOnClickListener { musicClickListener!!.onClick(music, adapterPosition) }
        }

    }

    fun notifyMusicChange(music: Music) {
        val index = musicList.indexOf(music)
        if (index != -1) {
            if (index != playingMusicPos) {
                notifyItemChanged(playingMusicPos)
                playingMusicPos = index
                notifyItemChanged(playingMusicPos)
            }
        }
    }

    interface OnMusicClickListener {
        fun onClick(music: Music?, position: Int)
    }
}
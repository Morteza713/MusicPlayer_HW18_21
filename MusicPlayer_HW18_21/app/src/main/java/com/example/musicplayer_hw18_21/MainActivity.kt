package com.example.musicplayer_hw18_21

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer_hw18_21.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.*

class MainActivity : AppCompatActivity(), MusicAdapter.OnMusicClickListener {
    private var binding: ActivityMainBinding? = null
    private val musicList: List<Music> = getList()
    private var mediaPlayer: MediaPlayer? = null
    private var musicState = MusicState.STOPPED
    private var timer: Timer? = null
    private var isDragging = false
    private var cursor = 0
    private lateinit var musicAdapter: MusicAdapter

    override fun onClick(music: Music?, position: Int) {
        timer!!.cancel()
        timer!!.purge()
        mediaPlayer!!.release()
        cursor = position
        onMusicChange(musicList[cursor])
    }
    internal enum class MusicState {
        PLAYING, PAUSED, STOPPED
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_main)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        musicAdapter = MusicAdapter(musicList, this)
        recyclerView.adapter = musicAdapter

        onMusicChange(musicList[cursor])
        binding!!.playBtn.setOnClickListener(View.OnClickListener {
            when (musicState) {
                MusicState.PLAYING -> {
                    mediaPlayer!!.pause()
                    musicState = MusicState.PAUSED
                    binding!!.playBtn.setImageResource(R.drawable.ic_play_32dp)
                }
                MusicState.PAUSED, MusicState.STOPPED -> {
                    mediaPlayer!!.start()
                    musicState = MusicState.PLAYING
                    binding!!.playBtn.setImageResource(R.drawable.ic_pause_24dp)
                }
            }
        })

        binding!!.musicSlider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            binding!!.positionTv.text = convertMillisToString(value.toLong())
        })

        binding!!.musicSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
                isDragging = true
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                isDragging = false
                mediaPlayer!!.seekTo(slider.value.toInt())
            }
        })

        binding!!.nextBtn.setOnClickListener { goNext() }

        binding!!.prevBtn.setOnClickListener { goPrev() }
    }

    private fun goPrev() {
        timer!!.cancel()
        timer!!.purge()
        mediaPlayer!!.release()
        if (cursor == 0) {
            cursor = musicList.size - 1
        } else cursor--
        onMusicChange(musicList[cursor])
    }

    private fun onMusicChange(music: Music) {
        musicAdapter.notifyMusicChange(music)
        binding!!.musicSlider.value = 0F
        mediaPlayer = MediaPlayer.create(this, music.musicFileResId)
        mediaPlayer!!.setOnPreparedListener {
            mediaPlayer!!.start()
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (!isDragging) binding!!.musicSlider.value = mediaPlayer!!.currentPosition
                            .toFloat()
                    }
                }
            }, 1000, 1000)
            binding!!.durationTv.text = (convertMillisToString(mediaPlayer!!.duration.toLong()))
            binding!!.musicSlider.valueTo = mediaPlayer!!.duration.toFloat()
            musicState = MusicState.PLAYING
            binding!!.playBtn.setImageResource(R.drawable.ic_pause_24dp)
            mediaPlayer!!.setOnCompletionListener { goNext() }
        }
        binding!!.coverIv.setImageResource(music.coverResId)
        binding!!.artistIv.setImageResource(music.artistResId)
        binding!!.artistTv.text = music.artist
        binding!!.musicNameTv.text = music.name
    }


    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        mediaPlayer!!.release()
        mediaPlayer = null
    }

    private fun goNext() {
        timer!!.cancel()
        timer!!.purge()
        mediaPlayer!!.release()
        if (cursor < musicList.size - 1) {
            cursor++
        } else cursor = 0
        onMusicChange(musicList[cursor])
    }
    private fun convertMillisToString(durationInMillis: Long): String? {
        val second = durationInMillis / 1000 % 60
        val minute = durationInMillis / (1000 * 60) % 60
        return String.format(Locale.US, "%02d:%02d", minute, second)
    }
    private fun getList(): List<Music> {
        val musicList: MutableList<Music> = ArrayList()
        val music1 = Music()
        music1.artist = ("Evan Band")
        music1.name = ("Chehel Gis")
        music1.coverResId = (R.drawable.music_1_cover)
        music1.artistResId = (R.drawable.music_1_artist)
        music1.musicFileResId = (R.raw.music_1)
        val music2 = Music()
        music2.artist=("Reza Sadeghi")
        music2.name=("Tanha tarin")
        music2.coverResId=(R.drawable.music_2_cover)
        music2.musicFileResId=(R.drawable.music_2_artist)
        music2.musicFileResId=(R.raw.music_2)
        val music3 = Music()
        music3.artist=("Reza Bahram")
        music3.name=("Hich")
        music3.coverResId=(R.drawable.music_3_cover)
        music3.musicFileResId=(R.drawable.music_3_artist)
        music3.musicFileResId=(R.raw.music_3)
        musicList.add(music2)
        musicList.add(music3)
        musicList.add(music1)
        return musicList
    }
}
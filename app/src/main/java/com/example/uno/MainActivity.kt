package com.example.uno

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.uno.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var navegacion : BottomNavigationView
    private lateinit var mediaPlayer: MediaPlayer
    private val mOnNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->  

        when (item.itemId){
            R.id.regla1 -> {
                supportFragmentManager.commit {
                    replace<ReglasFragment>(R.id.reglas)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.regla2 -> {
                supportFragmentManager.commit {
                    replace<Reglas2Fragment>(R.id.reglas)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.jugar -> {
                supportFragmentManager.commit {
                    replace<JugarFragment>(R.id.reglas)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.beat)
        mediaPlayer.isLooping = true//se repite una vez finalice
        mediaPlayer.setVolume(0.3f, 0.3f)

        navegacion = findViewById(R.id.navmenu)
        navegacion.setOnNavigationItemSelectedListener(mOnNavMenu)
        supportFragmentManager.commit {
            replace<ReglasFragment>(R.id.reglas)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }




        /*
        var intent = Intent(this, Ganador::class.java)
        startActivity(intent)
       */

    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }
}
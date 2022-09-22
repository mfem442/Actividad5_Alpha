package com.example.actividad5_labuena

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun mapas(view: View?){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
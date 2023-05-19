package com.example.uno


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.uno.databinding.ActivityIngresarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Ingresar : AppCompatActivity() {
    private lateinit var binding: ActivityIngresarBinding

    private var  carta = 3
    private var  turno = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarSiexisteCartaCentral()
        verificarSiexistenjugadores()
        cargar()

        binding.iniciar.setOnClickListener {
            val editext=  binding.nombre
            val nombre = editext.text.toString()
            if(nombre.isEmpty()){
                Toast.makeText(this,"Por favor Ingresa tu nombre",Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, Juego::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("carta", carta)
                intent.putExtra("turno", turno)
                startActivity(intent)
            }
        }
    }






    //carga la imagen de portada
    fun cargar() {
        val imageResource = R.drawable.uno //
        binding.imagen.setImageResource(imageResource)
    }
    //verficar si ahi carta central para cambiarla o no
    private fun verificarSiexisteCartaCentral(){
        val database = Firebase.database
        val cartaRef = database.getReference("juego/carta")
        cartaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener el valor de la carta
                val cartaFirebase = dataSnapshot.getValue(Int::class.java)
                // Verificar si la carta tiene el valor de 2626
                if (cartaFirebase == 2626) {
                    carta = 0 //esta vacio
                } else {
                    carta = 1//existe carta
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    //Verifica si ahi jugadores en Firebase
    private fun verificarSiexistenjugadores(){
        val database = Firebase.database
        val turnoRef = database.getReference("juego/turno")
        turnoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener el valor del turno
                val turnoFirebase = dataSnapshot.getValue(Int::class.java)
                // Verificar si el turno tiene el valor de 0 "Vacio"
                if (turnoFirebase == 0) {
                    turno = 1
                    turnoRef.setValue(turno)

                } else {
                    turno = 2
                    turnoRef.setValue(turno)//Cambia el turno en firebase
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
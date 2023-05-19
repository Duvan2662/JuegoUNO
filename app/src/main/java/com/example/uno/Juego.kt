package com.example.uno



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uno.databinding.ActivityJuegoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Juego : AppCompatActivity() {
    private lateinit var binding: ActivityJuegoBinding

    //Parametros que se reciben de la actividad Ingresar
    private var  carta = 3
    private lateinit var nombre:String
    private var turnoglobal = 0

    //Cantidad inicial de cartas
    private  var totalcartas = 7

    //dialogo e alerta
    private  lateinit var dialogo : AlertDialog


    //adaptador para la vista de las cartas
    private lateinit var adapter:ButtonsAdapter
    //gridview donde estan mis cartas
    private lateinit var gridView:GridView


    //Lista de cartas ya usadas
    private var usadas = mutableListOf(104, 105, 106, 107)
    //Mazo del jugador en botones
    private var mazoJugador = mutableListOf<Button>()
    //Mazo del jugador en objetos para poder compararlo
    private var objetomazoJugador = mutableListOf<Carta>()
    //Carta inicial en objeto
    private lateinit var cartaCentro: Carta




    //Variable de ganador global
    private var ganadorGlobal = "noahi"
    //Opcional
    private var aviso = "vacio"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ButtonsAdapter(this, mazoJugador)
        gridView = binding.grid


        recibirParametros()
        cargar()
        mensajeTurno()
        verificarturno()
        verificarJugada()
        cargabotones()//Carga los botones
        actualizarInformacion()
        inicio()//carta de inicio
        mazo()//carga el mazo
        jugar()//Jugar
        verificarUno()
    }

    //Carga los parametros para jugar de la actividad Ingresar
    private fun recibirParametros(){
        //Recibe el nombre
        val nombreRecibido = intent.getStringExtra("nombre")
        nombre = nombreRecibido.toString()

        //Recibe si ahi o no carta y dependiendo de eso genera la carta central
        val cartaRecibido = intent.getIntExtra("carta", 0)
        carta = cartaRecibido

        //Recibe si ahi o no carta y dependiendo de eso genera la carta central
        val turnoRecibido = intent.getIntExtra("turno", 0)
        turnoglobal = turnoRecibido
    }

    //Cargar mensaje de espera tu turno
    private fun mensajeTurno(){
        dialogo = AlertDialog.Builder(this).setView(R.layout.cargar_layout).setCancelable(false).create()
    }

    //Carga los avatars
    private fun cargar() {
        val imageResource = R.drawable.avatar1//Imagen del avatar 1
        binding.imagenAvatar1.setImageResource(imageResource)//Coloca la imagen en la imagen del layout
        val imageResource2 = R.drawable.avatar2 //Imagen del avatar 2
        binding.imagenAvatar2.setImageResource(imageResource2)//Coloca la imagen en la imagen del layout
    }
    //boton coger cartas
    private fun cargabotones(){
        binding.coger.setOnClickListener {
            val button = Button(this)
            val numero = numeroramdom()
            val imagen = "carta$numero" // Nombre de imagen en una variable
            button.id = numero// Asigna un ID único a cada botón
            val resourceId = resources.getIdentifier(imagen, "drawable", packageName) // Obtiene el identificador del recurso de imagen
            button.setBackgroundResource(resourceId) // Establece la imagen de fondo del botón
            mazoJugador.add(button) // Agrega el botón a la lista
            crearObjeto(numero)//Crea un objeto de tipo carta con las propiedades de color y numero
            totalcartas ++
            actualizarInformacion()
            eventoclic(button)
            adapter.notifyDataSetChanged()


            binding.pasar.visibility = View.VISIBLE
            binding.coger.isEnabled = false
        }
        //Pasar
        binding.pasar.setOnClickListener {
            actualizarTurno2()
            binding.pasar.visibility = View.INVISIBLE
            binding.coger.isEnabled = true
        }

        //boton azul
        binding.botonAzul.setOnClickListener {
            cartaCentro.color = "Azul"
            binding.color.text = "Color Azul"
            binding.botonAzul.visibility = View.INVISIBLE
            binding.botonAmarillo.visibility = View.INVISIBLE
            binding.botonRojo.visibility = View.INVISIBLE
            binding.botonverde.visibility = View.INVISIBLE
        }

        //boton Amarillo
        binding.botonAmarillo.setOnClickListener {
            cartaCentro.color = "Amarillo"
            binding.color.text = "Color Azul"
            binding.botonAzul.visibility = View.INVISIBLE
            binding.botonAmarillo.visibility = View.INVISIBLE
            binding.botonRojo.visibility = View.INVISIBLE
            binding.botonverde.visibility = View.INVISIBLE
        }

        //boton Rojo
        binding.botonRojo.setOnClickListener {
            cartaCentro.color = "Rojo"
            binding.color.text = "Color Rojo"
            binding.botonAzul.visibility = View.INVISIBLE
            binding.botonAmarillo.visibility = View.INVISIBLE
            binding.botonRojo.visibility = View.INVISIBLE
            binding.botonverde.visibility = View.INVISIBLE
        }

        //boton Verde
        binding.botonverde.setOnClickListener {
            cartaCentro.color = "Verde"
            binding.color.text = "Color Verde"
            binding.botonAzul.visibility = View.INVISIBLE
            binding.botonAmarillo.visibility = View.INVISIBLE
            binding.botonRojo.visibility = View.INVISIBLE
            binding.botonverde.visibility = View.INVISIBLE
        }
    }

    //Genera el numero aleatorio y carga la imagen con la que inicia el juego
    private fun inicio() {
        val poderes = mutableListOf(
            10,
            11,
            12,
            22,
            23,
            24,
            35,
            36,
            37,
            47,
            48,
            49,
            60,
            61,
            62,
            72,
            73,
            74,
            85,
            86,
            87,
            97,
            98,
            99,
            100,
            101,
            102,
            103,
            104,
            105,
            106,
            107
        )
        var randomNumber = (0..107).random()
        while (poderes.contains(randomNumber)) {
            randomNumber = (0..107).random()
        }
        crearCartaCentral(randomNumber)
        enviarNumeroFirebase(randomNumber)
        usadas.add(randomNumber)
    }

    //Funcion para generar el objeto carta Central
    private fun crearCartaCentral(numero: Int) {
        if(carta == 0){
            val color = colorObjeto(numero)
            val num = numeroObjeto(numero)
            cartaCentral(numero)//imagen
            cartaCentro = Carta(numero, num, color)
            binding.color.text = "Color $color"
        }
        if(carta == 1){
            val database = FirebaseDatabase.getInstance()
            val cartaRef = database.getReference("juego/carta")
            cartaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val carta = dataSnapshot.getValue(Int::class.java)
                    val color = colorObjeto(carta!!.toInt())
                    val num = numeroObjeto(carta)
                    cartaCentral(numero)//imagen
                    cartaCentro = Carta(numero, num, color)
                    binding.color.text = "Color $color"
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            carta = 0
        }

    }

    //Genera la imagen de la carta central
    private fun cartaCentral(numero: Int) {
        val resourceName = "carta_$numero" // Nombre de la imagen de la carta
        val imageResource = resources.getIdentifier(resourceName, "drawable", packageName) // Identificador del recurso de imagen
        binding.imagenjuego.setImageResource(imageResource)
    }

    //Generar numero Ramdom
    private fun numeroramdom(): Int {
        var randomNumber = (0..107).random()
        while (usadas.contains(randomNumber)) {
            randomNumber = (0..107).random()
        }
        usadas.add(randomNumber)
        return randomNumber
    }

    //Generar Mazo
    private fun mazo() {
        val buttonInicio = Button(this)
        val resourceId2 = resources.getIdentifier("cartaini", "drawable", packageName)
        buttonInicio.setBackgroundResource(resourceId2)
        mazoJugador.add(buttonInicio)
        for (i in 0..6) {
            val button = Button(this)
            val numero = numeroramdom()
            val imagen = "carta$numero" // Nombre de imagen en una variable
            button.id = numero// Asigna un ID único a cada botón
            val resourceId = resources.getIdentifier(imagen, "drawable", packageName) // Obtiene el identificador del recurso de imagen
            button.setBackgroundResource(resourceId) // Establece la imagen de fondo del botón
            mazoJugador.add(button) // Agrega el botón a la lista
            crearObjeto(numero)//Crea un objeto de tipo carta con las propiedades de color y numero
        }
        gridView.adapter = adapter

    }

    //Funciones para Generar Mazo en objetos carta
    private fun crearObjeto(numero: Int) {
        val color = colorObjeto(numero)
        val num = numeroObjeto(numero)
        val obj = Carta(numero, num, color)
        objetomazoJugador.add(obj)
    }
    private fun numeroObjeto(numero: Int): String {
        val num : String
        when (numero) {
            10, 22, 35, 47, 60, 72, 85, 97 -> {
                num = "Devuelve"
            }
            11, 23, 36, 48, 61, 73, 86, 98 -> {
                num = "Negado"
            }
            12, 24, 37, 49, 62, 74, 87, 99 -> {
                num = "Sumar2"
            }
            0, 25, 50, 75 -> {
                num = "Cero"
            }
            1, 13, 26, 38, 51, 63, 76, 88 -> {
                num = "Uno"
            }
            2, 14, 27, 39, 52, 64, 77, 89 -> {
                num = "Dos"
            }
            3, 15, 28, 40, 53, 65, 78, 90 -> {
                num = "Tres"
            }
            4, 16, 29, 41, 54, 66, 79, 91 -> {
                num = "Cuatro"
            }
            5, 17, 30, 42, 55, 67, 80, 92 -> {
                num = "Cinco"
            }
            6, 18, 31, 43, 56, 68, 81, 93 -> {
                num = "Seis"
            }
            7, 19, 32, 44, 57, 69, 82, 94 -> {
                num = "Siete"
            }
            8, 20, 33, 45, 58, 70, 83, 95 -> {
                num = "Ocho"
            }
            9, 21, 34, 46, 59, 71, 84, 96 -> {
                num = "Nueve"
            }
            100, 101, 102, 103 -> {
                num = "Sumar4"
            }
            104, 105, 106, 107 -> {
                num = "CambioColor"
            }
            else -> {
                num = "Error"
            }
        }

        return num
    }
    private fun colorObjeto(numero: Int): String {

        var color = ""

        if (numero in 0..24) {
            color = "Azul"
        }
        if (numero in 25..49) {
            color = "Verde"
        }
        if (numero in 50..74) {
            color = "Rojo"
        }
        if (numero in 75..99) {
            color = "Amarillo"
        }
        if (numero in 100..107) {
            color = "Negro"
        }

        return color
    }


    //Funcion jugar
    private  fun jugar(){
        for(boton in mazoJugador){
            eventoclic(boton)
        }
        gridView.adapter = adapter
    }

    //funcion evento
    private fun eventoclic(boton:Button){
        boton.setOnClickListener { view->
            val mas2 = listOf(12,24,37,49,62,74,87,99)
            val mas4 = listOf(100,101,102,103)
            val buscar = objetomazoJugador.find { it.id == boton.id }
            if(buscar!= null) {
                val numero = buscar.id
                Log.d("Mazo", "Botón $numero clickeado")
                val selectedButton = view as Button
                val buscandoCarta = objetomazoJugador.find { it.id == numero}
                if(buscandoCarta != null){
                    if((cartaCentro.color == buscandoCarta.color) ||(cartaCentro.nombre == buscandoCarta.nombre) || mas4.contains(buscandoCarta.id)){
                        crearCartaCentral(numero)
                        enviarNumeroFirebase(numero)
                        if(mas4.contains(numero)){
                            binding.botonAzul.visibility = View.VISIBLE
                            binding.botonAmarillo.visibility = View.VISIBLE
                            binding.botonverde.visibility = View.VISIBLE
                            binding.botonRojo.visibility = View.VISIBLE
                        }
                        if(mas2.contains(numero)){
                            aviso = "lamande"
                        }
                        binding.pasar.visibility = View.INVISIBLE
                        binding.coger.isEnabled = true
                        actualizarTurno()
                        mazoJugador.remove(selectedButton)
                        objetomazoJugador.removeIf { it.id == numero}
                        totalcartas--
                        actualizarInformacion()

                    }else{
                        Toast.makeText(this, "Esta carta no tiene ni el color ni el numero de la carta central", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    println("No se encontro el objeto con id $numero")
                }

            }
            gridView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    //Actualiza el nombre y el numero de cartas
    private fun actualizarInformacion(){
        binding.nombrejugador2.text = nombre
        binding.cartas2.text = totalcartas.toString()
        comprobarCartas()
    }

    //Enviar boton a la base de datos
    private fun enviarNumeroFirebase(numero:Int){
        val database = Firebase.database
        val cartaRef = database.getReference("juego/carta")
        cartaRef.setValue(numero)
        cartaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val number = snapshot.value.toString()
                crearCartaCentral(number.toInt())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    //Dependiendo si es el turno o no del jugador muestra la panatalla de carga
    private fun verificarturno(){
        val database = Firebase.database
        val turnoRef = database.getReference("juego/turno")
        turnoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener el valor de la carta
                val turno = dataSnapshot.getValue(Int::class.java)
                if (turno == turnoglobal) {
                    dialogo.dismiss()
                }else{
                    dialogo.show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    //Cambia el valor del turno para que le toque al otro jugador
    private fun actualizarTurno() {
        val database = Firebase.database
        val turnoRef = database.getReference("juego/turno")
        if (cartaCentro.nombre != "Devuelve" && cartaCentro.nombre != "Negado" && cartaCentro.nombre != "Sumar2" && cartaCentro.nombre != "Sumar4") {
            if(turnoglobal==1){
                turnoRef.setValue(2)
                aviso = "vacio"
            }
            if(turnoglobal==2){
                turnoRef.setValue(1)
                aviso = "vacio"
            }
        }
    }

    //Cambia el valor del turno para que le toque al otro jugador
    private fun actualizarTurno2(){
        val database = Firebase.database
        val turnoRef = database.getReference("juego/turno")
        if(turnoglobal==1){
            turnoRef.setValue(2)
            aviso = "vacio"
        }
        if(turnoglobal==2){
            turnoRef.setValue(1)
            aviso = "vacio"
        }
    }

    //Funcion que me sirve  para verficar si hubo una carta de +4 o +2
    private fun verificarJugada(){
        val database = Firebase.database
        val cartaRef = database.getReference("juego/carta")
        val turnoRef = database.getReference("juego/turno")

        cartaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val carta = snapshot.getValue(Int::class.java)
                turnoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val turno = dataSnapshot.getValue(Int::class.java)
                        if(turno != turnoglobal && aviso=="vacio"){
                            verificar(carta!!.toInt())
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
                actualizarInformacion()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "Failed to read value.", error.toException())
            }

        })
    }

    //Funcion que me sirve para aumentar el mazo del rival si un jugador tira un +4 o un +2
    private fun verificar(carta:Int){
        val mas2 = listOf(12,24,37,49,62,74,87,99)
        val mas4 = listOf(100,101,102,103)
        if(mas2.contains(carta)){
            for (i in 0..1) {
                val button = Button(this)
                val numero = numeroramdom()
                val imagen = "carta$numero" // Nombre de imagen en una variable
                button.id = numero// Asigna un ID único a cada botón
                val resourceId = resources.getIdentifier(imagen, "drawable", packageName) // Obtiene el identificador del recurso de imagen
                button.setBackgroundResource(resourceId) // Establece la imagen de fondo del botón
                mazoJugador.add(button) // Agrega el botón a la lista
                totalcartas ++
                actualizarInformacion()//Actualiza la cantidad de botones
                crearObjeto(numero)//Crea un objeto de tipo carta con las propiedades de color y numero
                eventoclic(button)
            }
            gridView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if(mas4.contains(carta)){
            for (i in 0..3) {
                val button = Button(this)
                val numero = numeroramdom()
                val imagen = "carta$numero" // Nombre de imagen en una variable
                button.id = numero// Asigna un ID único a cada botón
                val resourceId = resources.getIdentifier(imagen, "drawable", packageName) // Obtiene el identificador del recurso de imagen
                button.setBackgroundResource(resourceId) // Establece la imagen de fondo del botón
                mazoJugador.add(button) // Agrega el botón a la lista
                totalcartas ++
                actualizarInformacion()//Actualiza la cantidad de botones
                crearObjeto(numero)//Crea un objeto de tipo carta con las propiedades de color y numero
                eventoclic(button)
            }
            gridView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    //Si ya ahi ganador me muestra la vista de perdedor o ganador
    private fun verificarUno(){
        val database = Firebase.database
        val ganadorRef = database.getReference("juego/ganador")
        ganadorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ganador = dataSnapshot.getValue(String::class.java)
                if(ganador == ganadorGlobal){
                    ganador()
                    reiniciar()
                }
                if(ganadorGlobal =="noahi" && ganador !="Vacio"){
                    perdedor()
                    reiniciar()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    //Inicia la actividad de ganador
    private fun ganador(){
        val intent = Intent(this, Ganador::class.java)
        startActivity(intent)
    }

    //Inicia la actividad de perdedor
    private fun perdedor(){
        val intent = Intent(this, Perdedor::class.java)
        startActivity(intent)
    }

    //Verifica si ya ahi un ganador o no
    private fun comprobarCartas(){
        if(totalcartas == 0){
            cambiarGanador()
        }
    }

    //Si ya ahi un ganador. En la tabla ganador de Firebase coloca el nombre del ganador
    private fun cambiarGanador(){
        ganadorGlobal = nombre
        val database = Firebase.database
        val ganadorRef = database.getReference("juego/ganador")
        ganadorRef.setValue(nombre)
    }

    //Reinicia firebase
    private fun reiniciar(){
        val database = Firebase.database
        val ganadorRef = database.getReference("juego/ganador")
        ganadorRef.setValue("Vacio")
        val cartaRef = database.getReference("juego/carta")
        cartaRef.setValue(2626)
        val turnoRef = database.getReference("juego/turno")
        turnoRef.setValue(0)
    }
}






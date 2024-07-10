package com.redsystemstudio.appcomprayventa.Fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.redsystemstudio.appcomprayventa.Adaptadores.AdaptadorAnuncio
import com.redsystemstudio.appcomprayventa.Adaptadores.AdaptadorCategoria
import com.redsystemstudio.appcomprayventa.Carrito
import com.redsystemstudio.appcomprayventa.Constantes
import com.redsystemstudio.appcomprayventa.Modelo.ModeloAnuncio
import com.redsystemstudio.appcomprayventa.Modelo.ModeloCategoria
import com.redsystemstudio.appcomprayventa.R
import com.redsystemstudio.appcomprayventa.RvListenerCategoria
import com.redsystemstudio.appcomprayventa.SeleccionarUbicacion
import com.redsystemstudio.appcomprayventa.databinding.FragmentInicioBinding

class FragmentInicio : Fragment() {

    private lateinit var binding : FragmentInicioBinding

    private companion object{
        private const val MAX_DISTANCIA_MOSTRAR_ANUNCIO = 10
    }

    private lateinit var mContext : Context

    private lateinit var anuncioArrayList : ArrayList<ModeloAnuncio>
    private lateinit var adaptadorAnuncio : AdaptadorAnuncio
    private lateinit var locacionSP : SharedPreferences

    private var actualLatitud = 0.0
    private var actualLongitud = 0.0
    private var actualDireccion = ""

    private lateinit var currentUserId: String

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentInicioBinding.inflate(LayoutInflater.from(mContext),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locacionSP = mContext.getSharedPreferences("LOCACION_SP", Context.MODE_PRIVATE)

        actualLatitud = locacionSP.getFloat("ACTUAL_LATITUD", 0.0f).toDouble()
        actualLongitud = locacionSP.getFloat("ACTUAL_LONGITUD", 0.0f).toDouble()
        actualDireccion = locacionSP.getString("ACTUAL_DIRECCION", "")!!

        if (actualLatitud != 0.0 && actualLongitud !=0.0){
            binding.TvLocacion.text = actualDireccion
        }

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        filtrosSnniper()
        cargarCategorias()
        cargarAnuncios("Todos")

        binding.TvLocacion.setOnClickListener {
            val intent = Intent(mContext, SeleccionarUbicacion::class.java)
            seleccionarUbicacionARL.launch(intent)
        }

        binding.EtBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(filtro: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    val consulta = filtro.toString()
                    adaptadorAnuncio.filter.filter(consulta)
                } catch (e:Exception) {
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.IbLimpiar.setOnClickListener {
            val consulta = binding.EtBuscar.text.toString().trim()
            if (consulta.isNotEmpty()){
                binding.EtBuscar.setText("")
                Toast.makeText(context,"Se ha limpiado el campo de búsqueda",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"No se ha ingresado una consulta",Toast.LENGTH_SHORT).show()
            }
        }

        binding.IbCarrito.setOnClickListener{
            val intent = Intent(mContext, Carrito::class.java)
            startActivity(intent)
        }
    }

    private val seleccionarUbicacionARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if (resultado.resultCode == Activity.RESULT_OK){
            val data = resultado.data
            if (data!=null){
                actualLatitud = data.getDoubleExtra("latitud", 0.0)
                actualLongitud = data.getDoubleExtra("longitud",0.0)
                actualDireccion = data.getStringExtra("direccion").toString()

                locacionSP.edit()
                    .putFloat("ACTUAL_LATITUD", actualLatitud.toFloat())
                    .putFloat("ACTUAL_LONGITUD", actualLongitud.toFloat())
                    .putString("ACTUAL_DIRECCION", actualDireccion)
                    .apply()

                binding.TvLocacion.text = actualDireccion

                cargarAnuncios("Todos")
            } else {
                Toast.makeText(
                    context, "Cancelado",Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun cargarCategorias(){
        val categoriaArrayList = ArrayList<ModeloCategoria>()
        for (i in 0 until Constantes.categorias.size){
            val modeloCategoria = ModeloCategoria(Constantes.categorias[i], Constantes.categoriasIcono[i])
            categoriaArrayList.add(modeloCategoria)
        }

        val adaptadorCategoria = AdaptadorCategoria(
            mContext,
            categoriaArrayList,
            object : RvListenerCategoria{
                override fun onCategoriaClick(modeloCategoria: ModeloCategoria) {
                    val categoriaSeleccionada = modeloCategoria.categoria
                    cargarAnuncios(categoriaSeleccionada)
                }
            }
        )

        binding.categoriaRv.adapter = adaptadorCategoria
    }

    private fun cargarAnuncios(categoria : String, filtro: String? = null){
        anuncioArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                anuncioArrayList.clear()
                for (ds in snapshot.children){
                    try {
                        val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)
                        val distancia = calcularDistanciaKM(
                            modeloAnuncio?.latitud ?: 0.0,
                            modeloAnuncio?.longitud ?: 0.0
                        )

                        if (modeloAnuncio?.uid != currentUserId) {
                            if (categoria == "Todos"){
                                if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                    anuncioArrayList.add(modeloAnuncio!!)
                                }
                            } else {
                                if (modeloAnuncio!!.categoria == categoria){
                                    if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                        anuncioArrayList.add(modeloAnuncio)
                                    }
                                }
                            }
                        }
                    } catch (e:Exception) {
                    }
                }

                // Aplicar filtro si está definido
                filtro?.let {
                    when (it) {
                        "Más antiguos" -> anuncioArrayList.sortBy { it.tiempo }
                        "Más recientes" -> anuncioArrayList.sortByDescending { it.tiempo }
                        "Precio menor a mayor" -> anuncioArrayList.sortBy { it.precio.toDoubleOrNull() }
                        "Precio mayor a menor" -> anuncioArrayList.sortByDescending { it.precio.toDoubleOrNull() }
                    }
                }

                adaptadorAnuncio = AdaptadorAnuncio(mContext, anuncioArrayList)
                binding.anunciosRv.adapter = adaptadorAnuncio
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun calcularDistanciaKM(latitud: Double, longitud: Double) : Double{
        val puntoPartida = Location(LocationManager.NETWORK_PROVIDER)
        puntoPartida.latitude = actualLatitud
        puntoPartida.longitude = actualLongitud

        val puntoFinal = Location(LocationManager.NETWORK_PROVIDER)
        puntoFinal.latitude = latitud
        puntoFinal.longitude = longitud

        val distanciaMetros = puntoPartida.distanceTo(puntoFinal).toDouble()
        return distanciaMetros / 1000
    }

    private fun filtrosSnniper() {
        val opcionesFiltro = arrayOf(
            "Más antiguos",
            "Más recientes",
            "Precio menor a mayor",
            "Precio mayor a menor"
        )
        val adaptadorSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opcionesFiltro)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = adaptadorSpinner

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> cargarAnuncios("Todos", "Más antiguos")
                    1 -> cargarAnuncios("Todos", "Más recientes")
                    2 -> cargarAnuncios("Todos", "Precio menor a mayor")
                    3 -> cargarAnuncios("Todos", "Precio mayor a menor")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}

package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.id_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.niño
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NiñosParticipantes : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    var adaptadorNiñosParticipantes: AdaptadorNiñosParticipantes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)


        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        txt_buscar.visibility = View.GONE


        bundle()

        actualizar.setOnRefreshListener {
            if (!niño.isNullOrEmpty()) {
                niño.clear()
                actualizar.isRefreshing = true
                adaptadorNiñosParticipantes!!.limpiar()
                getParticipantes(id_sesion)
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getParticipantes(id_sesion)
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            val intent = Intent(this, SeleccionarNiños::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion", id_sesion)
            startActivity(intent)
        }

        btn_asistencia.setOnClickListener {
            val intent = Intent(this, AsistenciaParticipantes::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion", id_sesion)
            startActivity(intent)
        }


        btn_volver.setOnClickListener {
            adaptadorNiñosParticipantes = null
            finish()
        }


    }

    fun bundle() {
        data = intent.extras
        if (data != null) {

            if (data!!.containsKey("id_sesion")) {
                id_sesion = data!!.getString("id_sesion")!!
                if(id_sesion.isNotEmpty()){
                    getParticipantes(id_sesion)
                }

            }
        }
    }

    override fun onBackPressed() {
        adaptadorNiñosParticipantes = null
        finish()
    }

    override fun onResume() {
        super.onResume()
        getParticipantes(id_sesion)

    }

    fun getParticipantes(id_sesion:String) {
        val dialog  = cargando(this, "Cargando\nparticipantes")

        val call: Call<ArrayList<Participante>> = apiSSN!!.getParticipantesXIDSSN(id_sesion)
        call.enqueue(object : Callback<ArrayList<Participante>> {
            override fun onResponse(call: Call<ArrayList<Participante>>, response: Response<ArrayList<Participante>>) {
                niño = response.body()!!

                if(niño.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    btn_asistencia.visibility = View.GONE
                    imagen_lista_vacia.startAnimation(stb)

                }else{
                    vacia.visibility = View.GONE
                    btn_asistencia.visibility = View.VISIBLE
                    adaptadorNiñosParticipantes =
                        AdaptadorNiñosParticipantes(
                            niño,
                            this@NiñosParticipantes,
                            apiSSN!!

                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorNiñosParticipantes
                    adaptadorNiñosParticipantes!!.notifyDataSetChanged()

                }
                dialog!!.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Participante>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var niño: ArrayList<Participante> = arrayListOf()
        var id_sesion: String = ""
    }

}

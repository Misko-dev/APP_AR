package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Participante.Asistencia
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciasParticipante.companion.asistencias
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciasParticipante.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciasParticipante.companion.id_participante
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AsistenciasParticipante : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    var adaptadorAsistenciaParticipantes: AdaptadorAsistenciasParticipantes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)


        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        txt_buscar.visibility = View.GONE


        bundle()

        actualizar.setOnRefreshListener {
            if (!asistencias.isNullOrEmpty()) {
                asistencias.clear()
                actualizar.isRefreshing = true
                adaptadorAsistenciaParticipantes!!.limpiar()
                getAsistencias(id_participante)
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getAsistencias(id_participante)
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            /*val intent = Intent(this, SeleccionarNiños::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion", id_sesion)
            startActivity(intent)*/
        }

        btn_asistencia.setOnClickListener {
            /*val intent = Intent(this, AsistenciaParticipantes::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion", id_sesion)
            startActivity(intent)*/
        }


        btn_volver.setOnClickListener {
            adaptadorAsistenciaParticipantes = null
            finish()
        }


    }

    fun bundle() {
        data = intent.extras
        if (data != null) {

            if (data!!.containsKey("id_participante")) {
                id_participante = data!!.getString("id_participante")!!
                if(id_participante.isNotEmpty()){
                    getAsistencias(id_participante)
                }

            }
        }
    }

    override fun onBackPressed() {
        adaptadorAsistenciaParticipantes = null
        finish()
    }

    override fun onResume() {
        super.onResume()
        getAsistencias(id_participante)

    }

    fun getAsistencias(id_participante:String) {
        val dialog  = cargando(this, "Cargando\nregistros")

        val call: Call<ArrayList<Asistencia>> = apiSSN!!.getAsistenciasXID(id_participante)
        call.enqueue(object : Callback<ArrayList<Asistencia>> {
            override fun onResponse(call: Call<ArrayList<Asistencia>>, response: Response<ArrayList<Asistencia>>) {
                asistencias = response.body()!!

                if(asistencias.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    btn_asistencia.visibility = View.GONE
                    imagen_lista_vacia.startAnimation(stb)

                }else{
                    vacia.visibility = View.GONE
                    btn_asistencia.visibility = View.VISIBLE
                    adaptadorAsistenciaParticipantes =
                        AdaptadorAsistenciasParticipantes(
                            asistencias,
                            this@AsistenciasParticipante,
                            apiSSN!!

                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorAsistenciaParticipantes
                    adaptadorAsistenciaParticipantes!!.notifyDataSetChanged()

                }
                dialog!!.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Asistencia>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var asistencias: ArrayList<Asistencia> = arrayListOf()
        var id_participante: String = ""
    }

}

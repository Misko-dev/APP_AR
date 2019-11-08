package cl.app.autismo_rancagua.Vista.Mantenedores.Niño

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Modelo.Educacion.Educacion
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarEducacion.companion.educacion
import kotlinx.android.synthetic.main.seleccionar_cargo_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionarEducacion : AppCompatActivity() {


    var apiNNS: ApiNNS? = null
    var adaptadorSeleccionarEducacion: AdaptadorSeleccionarEducacion? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_cargo_activity)

        btn_registrar_evento.visibility = View.GONE
        txt_buscar.visibility = View.GONE

        apiNNS = ApiClient.retrofit!!.create(ApiNNS::class.java)

        btn_volver_tbv.setOnClickListener {
            finish()
        }

        getEducacion()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getEducacion() {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<java.util.ArrayList<Educacion>> = apiNNS!!.getEducacion()
        call.enqueue(object : Callback<java.util.ArrayList<Educacion>> {
            override fun onResponse(call: Call<java.util.ArrayList<Educacion>>, response: Response<java.util.ArrayList<Educacion>>) {
                educacion = response.body()!!

                adaptadorSeleccionarEducacion =
                    AdaptadorSeleccionarEducacion(educacion, this@SeleccionarEducacion,applicationContext)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_seleccionar.layoutManager = mLayoutManager
                lista_seleccionar.itemAnimator = DefaultItemAnimator()
                lista_seleccionar.adapter = adaptadorSeleccionarEducacion

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<java.util.ArrayList<Educacion>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }


    object companion {
        var data: Bundle? = null
        var educacion: ArrayList<Educacion> = arrayListOf()
    }
}

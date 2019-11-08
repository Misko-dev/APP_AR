package cl.app.autismo_rancagua.Vista.Mantenedores.Profesional

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Modelo.Profesional.CargoProfesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.SeleccionarCargo.companion.cargo
import kotlinx.android.synthetic.main.seleccionar_cargo_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionarCargo : AppCompatActivity() {


    var apiPRO: ApiPRO? = null
    var adaptadorSeleccionarCargo: AdaptadorSeleccionarCargo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_cargo_activity)

        btn_registrar_evento.visibility = View.GONE
        txt_buscar.visibility = View.GONE

        apiPRO = ApiClient.retrofit!!.create(ApiPRO::class.java)

        btn_volver_tbv.setOnClickListener {
            finish()
        }


        getCargos()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    object companion {
        var data: Bundle? = null
        var cargo: ArrayList<CargoProfesional> = arrayListOf()
    }


    fun getCargos() {
        val dialog = cargando(this, "Cargando\ncargos")
        val call: Call<java.util.ArrayList<CargoProfesional>> = apiPRO!!.getCargos()
        call.enqueue(object : Callback<java.util.ArrayList<CargoProfesional>> {
            override fun onResponse(call: Call<java.util.ArrayList<CargoProfesional>>, response: Response<java.util.ArrayList<CargoProfesional>>) {
                cargo = response.body()!!
                adaptadorSeleccionarCargo =
                    AdaptadorSeleccionarCargo(
                        cargo,
                        this@SeleccionarCargo,
                        applicationContext
                    )
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_seleccionar.layoutManager = mLayoutManager
                lista_seleccionar.itemAnimator = DefaultItemAnimator()
                lista_seleccionar.adapter = adaptadorSeleccionarCargo

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<java.util.ArrayList<CargoProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }
}

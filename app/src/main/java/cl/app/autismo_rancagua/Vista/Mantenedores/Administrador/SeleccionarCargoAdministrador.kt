package cl.app.autismo_rancagua.Vista.Mantenedores.Administrador

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Modelo.Administrador.CargoAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.SeleccionarCargoAdministrador.companion.cargo
import kotlinx.android.synthetic.main.seleccionar_cargo_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionarCargoAdministrador : AppCompatActivity() {


    var apiAdm: ApiADM? = null
    var adaptadorSeleccionarCargoAdministrador: AdaptadorSeleccionarCargoAdministrador? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_cargo_activity)

        btn_registrar_evento.visibility = View.GONE
        txt_buscar.visibility = View.GONE

        apiAdm = ApiClient.retrofit!!.create(ApiADM::class.java)


        btn_volver_tbv.setOnClickListener {
            finish()
        }

        getCargos()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getCargos() {
        val dialog = cargando(this, "Cargando\ncargos")
        val call: Call<java.util.ArrayList<CargoAdministrador>> = apiAdm!!.getCargos()
        call.enqueue(object : Callback<java.util.ArrayList<CargoAdministrador>> {
            override fun onResponse(call: Call<java.util.ArrayList<CargoAdministrador>>, response: Response<java.util.ArrayList<CargoAdministrador>>) {
                cargo = response.body()!!
                adaptadorSeleccionarCargoAdministrador =
                    AdaptadorSeleccionarCargoAdministrador(
                        cargo,
                        this@SeleccionarCargoAdministrador,
                        applicationContext
                    )
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_seleccionar.layoutManager = mLayoutManager
                lista_seleccionar.itemAnimator = DefaultItemAnimator()
                lista_seleccionar.adapter = adaptadorSeleccionarCargoAdministrador

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<java.util.ArrayList<CargoAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }



    object companion {
        var data: Bundle? = null
        var cargo: ArrayList<CargoAdministrador> = arrayListOf()
    }
}

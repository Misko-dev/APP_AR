package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaPro
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.MCuentasProfesional.companion.cuentas
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MCuentasProfesional : AppCompatActivity() {


    var apiCtaPro: ApiCtaPro? = null
    var adaptadorMantenedorCuentas: AdaptadorMantenedorCuentasProfesional? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiCtaPro = ApiClient.retrofit!!.create(ApiCtaPro::class.java)
        btn_asistencia.visibility= View.GONE


        getCuentas()

        actualizar.setOnRefreshListener {
            if (!cuentas.isNullOrEmpty()) {
                cuentas.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorCuentas!!.limpiar()
                getCuentas()
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getCuentas()
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MCuentasProfesional, RGCuentasProfesional::class.java))
        }

        btn_volver.setOnClickListener {
            adaptadorMantenedorCuentas = null
            finish()
        }
    }



    override fun onBackPressed() {
        adaptadorMantenedorCuentas = null
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        getCuentas()
        /* }*/
    }


    object companion {
        var data: Bundle? = null

        var cuentas: ArrayList<CuentaProfesional> = arrayListOf()
    }


    fun getCuentas() {
        val dialog = cargando(this, "Cargando\ncuentas")
        val call: Call<ArrayList<CuentaProfesional>> = apiCtaPro!!.getCuentas()
        call.enqueue(object : Callback<ArrayList<CuentaProfesional>> {
            override fun onResponse(call: Call<ArrayList<CuentaProfesional>>, response: Response<ArrayList<CuentaProfesional>>) {
                cuentas = response.body()!!

                if(cuentas.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE
                    adaptadorMantenedorCuentas =
                        AdaptadorMantenedorCuentasProfesional(
                            cuentas,
                            this@MCuentasProfesional,
                            apiCtaPro!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorCuentas
                    adaptadorMantenedorCuentas!!.notifyDataSetChanged()
                }
                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<CuentaProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

}

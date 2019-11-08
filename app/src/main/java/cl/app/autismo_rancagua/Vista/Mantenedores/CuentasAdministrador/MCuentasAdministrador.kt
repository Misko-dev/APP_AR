package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.MCuentasAdministrador.companion.cuentas
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.MCuentasAdministrador.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.MCuentasAdministrador.companion.id_administrador
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaAdm
import cl.app.autismo_rancagua.Utilidades.cargando
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MCuentasAdministrador : AppCompatActivity() {

    var apiCtaAdm: ApiCtaAdm? = null
    var adaptadorMantenedorCuentas: AdaptadorMantenedorCuentasAdministrador? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiCtaAdm = ApiClient.retrofit!!.create(ApiCtaAdm::class.java)
        btn_asistencia.visibility= View.GONE


        bundle()
        getCuentas(id_administrador)


        actualizar.setOnRefreshListener {
            if(!cuentas.isNullOrEmpty()){
                cuentas.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorCuentas!!.limpiar()
                getCuentas(id_administrador)
                actualizar.isRefreshing = false
            }else{
                actualizar.isRefreshing = true
                getCuentas(id_administrador)
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MCuentasAdministrador, RGCuentasAdministrador::class.java))
        }

        btn_volver.setOnClickListener {
            adaptadorMantenedorCuentas = null
            finish()
        }
    }



    fun getCuentas(id : String) {
        val dialog = cargando(this, "Cargando\ncuentas")
        val call: Call<ArrayList<CuentaAdministrador>> = apiCtaAdm!!.getCuentaXID(id)
        call.enqueue(object : Callback<ArrayList<CuentaAdministrador>> {
            override fun onResponse(call: Call<ArrayList<CuentaAdministrador>>, response: Response<ArrayList<CuentaAdministrador>>) {
                cuentas = response.body()!!

                if(cuentas.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE

                    adaptadorMantenedorCuentas =
                        AdaptadorMantenedorCuentasAdministrador(
                            cuentas,
                            this@MCuentasAdministrador,
                            apiCtaAdm!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorCuentas
                    adaptadorMantenedorCuentas!!.notifyDataSetChanged()

                }
                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<CuentaAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun bundle() {
        data = intent.extras
        if (data != null) {
            id_administrador = data!!.getString("id_administrador")!!
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
            getCuentas(id_administrador)
       /* }*/
    }


    object companion {
        var data: Bundle? = null
        var id_administrador: String = ""
        var cuentas: ArrayList<CuentaAdministrador> = arrayListOf()
    }


}

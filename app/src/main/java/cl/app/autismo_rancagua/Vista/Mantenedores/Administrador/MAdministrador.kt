package cl.app.autismo_rancagua.Vista.Mantenedores.Administrador

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.MAdministrador.companion.administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.MAdministrador.companion.texto_busqueda
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MAdministrador : AppCompatActivity() {


    var apiAdm: ApiADM? = null
    var adaptadorMantenedorAdministrador: AdaptadorMantenedorAdministrador? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiAdm = ApiClient.retrofit!!.create(ApiADM::class.java)
        btn_asistencia.visibility= View.GONE


        getAdministradores("administradores","")

        actualizar.setOnRefreshListener {
            if (!administrador.isNullOrEmpty()) {
                administrador.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorAdministrador!!.limpiar()
                getAdministradores("administradores","")
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getAdministradores("administradores","")
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MAdministrador, RGAdministrador::class.java))
        }


        btn_volver.setOnClickListener {
            adaptadorMantenedorAdministrador = null
            finish()
        }

        txt_buscar.queryHint = "Buscar por nombre, apellido o rut..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getAdministradores("administradores", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getAdministradores("administradores", newText)
                return false
            }
        })
    }

    override fun onBackPressed() {
        adaptadorMantenedorAdministrador = null
        texto_busqueda = ""
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        if(texto_busqueda.isNotEmpty()){
            getAdministradores("administradores", texto_busqueda)
        }else{
            getAdministradores("administradores", "")
        }
        /* }*/
    }

    fun getAdministradores(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\nadministradores")
        }
        val call: Call<ArrayList<Administrador>> = apiAdm!!.getAdministradores(type,key)
        call.enqueue(object : Callback<ArrayList<Administrador>> {
            override fun onResponse(call: Call<ArrayList<Administrador>>, response: Response<ArrayList<Administrador>>) {
                administrador = response.body()!!

                if(administrador.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE
                    adaptadorMantenedorAdministrador =
                        AdaptadorMantenedorAdministrador(
                            administrador,
                            this@MAdministrador,
                            apiAdm!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorAdministrador
                    adaptadorMantenedorAdministrador!!.notifyDataSetChanged()


                }
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<ArrayList<Administrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }

    object companion {
        var data: Bundle? = null

        var administrador: ArrayList<Administrador> = arrayListOf()
        var texto_busqueda = ""
    }

}

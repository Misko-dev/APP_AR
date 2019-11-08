package cl.app.autismo_rancagua.Vista.Mantenedores.Profesional

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
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.MProfesional.companion.profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.MProfesional.companion.texto_busqueda
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MProfesional : AppCompatActivity() {


    var apiPRO: ApiPRO? = null
    var adaptadorMantenedorProfesional: AdaptadorMantenedorProfesional? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiPRO = ApiClient.retrofit!!.create(ApiPRO::class.java)

        getProfesionales("profesionales" ,"")
        btn_asistencia.visibility= View.GONE

        actualizar.setOnRefreshListener {
            if (!profesional.isNullOrEmpty()) {
                profesional.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorProfesional!!.limpiar()
                getProfesionales("profesionales" ,"")
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getProfesionales("profesionales" ,"")
                actualizar.isRefreshing = false
            }
        }


        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MProfesional, RGProfesional::class.java))
        }

        btn_volver.setOnClickListener {
            adaptadorMantenedorProfesional = null
            texto_busqueda = ""
            finish()
        }

        txt_buscar.queryHint = "Buscar por nombre, apellido o rut..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getProfesionales("profesionales", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getProfesionales("profesionales", newText)
                return false
            }
        })
    }


    override fun onBackPressed() {
        adaptadorMantenedorProfesional = null
        texto_busqueda = ""
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        if(texto_busqueda.isNotEmpty()){
            getProfesionales("profesionales", texto_busqueda)
        }else{
            getProfesionales("profesionales","")
        }
        /* }*/
    }


    object companion {
        var data: Bundle? = null
        var profesional: ArrayList<Profesional> = arrayListOf()
        var texto_busqueda = ""
    }

    fun getProfesionales(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\nprofesionales")
        }
        val call: Call<ArrayList<Profesional>> = apiPRO!!.getProfesionales(type,key)
        call.enqueue(object : Callback<ArrayList<Profesional>> {
            override fun onResponse(call: Call<ArrayList<Profesional>>, response: Response<ArrayList<Profesional>>) {
                profesional = response.body()!!

                if(profesional.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE
                    adaptadorMantenedorProfesional =
                        AdaptadorMantenedorProfesional(
                            profesional,
                            this@MProfesional,
                            apiPRO!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorProfesional
                    adaptadorMantenedorProfesional!!.notifyDataSetChanged()
                }
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<ArrayList<Profesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }
}

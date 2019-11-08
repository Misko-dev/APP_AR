package cl.app.autismo_rancagua.Vista.Mantenedores.Evento

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
import cl.app.autismo_rancagua.Api.Eventos.ApiEVE
import cl.app.autismo_rancagua.Modelo.Evento.Evento
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.MEvento.companion.adaptadorMantenedorTaller
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.MEvento.companion.eventos
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.MEvento.companion.texto_busqueda
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MEvento : AppCompatActivity() {


    var apiEVE: ApiEVE? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiEVE = ApiClient.retrofit!!.create(ApiEVE::class.java)
        btn_asistencia.visibility= View.GONE

        getEventos("eventos","")

        actualizar.setOnRefreshListener {
            if (!eventos.isNullOrEmpty()) {
                eventos.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorTaller!!.limpiar()
                getEventos("eventos","")
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getEventos("eventos","")
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MEvento, RGEvento::class.java))
        }


        btn_volver.setOnClickListener {
            adaptadorMantenedorTaller = null
            texto_busqueda = ""
            finish()
        }

        txt_buscar.queryHint = "Buscar por titulo..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getEventos("eventos", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getEventos("eventos", newText)
                return false
            }
        })
    }

    override fun onBackPressed() {
        adaptadorMantenedorTaller = null
        texto_busqueda = ""
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        if(texto_busqueda.isNotEmpty()){
            getEventos("eventos", texto_busqueda)
        }else{
            getEventos("eventos","")
        }
        /* }*/
    }

    fun getEventos(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\neventos")
        }
        val call: Call<ArrayList<Evento>> = apiEVE!!.getEventos(type,key)
        call.enqueue(object : Callback<ArrayList<Evento>> {
            override fun onResponse(call: Call<ArrayList<Evento>>, response: Response<ArrayList<Evento>>) {
                eventos = response.body()!!

                if(eventos.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE

                    adaptadorMantenedorTaller =
                        AdaptadorMantenedorEvento(
                            eventos,
                            this@MEvento,
                            apiEVE!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorTaller
                    adaptadorMantenedorTaller!!.notifyDataSetChanged()

                }

                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<ArrayList<Evento>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var eventos: ArrayList<Evento> = arrayListOf()
        var adaptadorMantenedorTaller: AdaptadorMantenedorEvento? = null
        var texto_busqueda = ""
    }
}

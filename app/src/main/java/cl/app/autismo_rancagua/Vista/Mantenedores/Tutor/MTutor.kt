package cl.app.autismo_rancagua.Vista.Mantenedores.Tutor

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
import cl.app.autismo_rancagua.Api.Personas.ApiTTR
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.MTutor.companion.texto_busqueda
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.MTutor.companion.tutor
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MTutor : AppCompatActivity() {


    var apiTTR: ApiTTR? = null
    var adaptadorMantenedorTutor: AdaptadorMantenedorTutor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)

        apiTTR = ApiClient.retrofit!!.create(ApiTTR::class.java)
        getTutores("tutores", "")

        btn_asistencia.visibility= View.GONE

        actualizar.setOnRefreshListener {
            if (!tutor.isNullOrEmpty()) {
                tutor.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorTutor!!.limpiar()
                getTutores("tutores", "")
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getTutores("tutores", "")
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MTutor, RGTutor::class.java))
        }


        btn_volver.setOnClickListener {
            adaptadorMantenedorTutor = null
            texto_busqueda = ""
            finish()
        }


        txt_buscar.queryHint = "Buscar por nombre, apellido o rut..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getTutores("tutores", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getTutores("tutores", newText)
                return false
            }
        })
    }

    override fun onBackPressed() {
        adaptadorMantenedorTutor = null
        texto_busqueda = ""
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        if(texto_busqueda.isNotEmpty()){
            getTutores("tutores", texto_busqueda)
        }else{
            getTutores("tutores","")
        }
        /* }*/
    }



    fun getTutores(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\ntutores")
        }
        val call: Call<ArrayList<Tutor>> = apiTTR!!.getTutores(type,key)
        call.enqueue(object : Callback<ArrayList<Tutor>> {
            override fun onResponse(call: Call<ArrayList<Tutor>>, response: Response<ArrayList<Tutor>>) {
                tutor = response.body()!!

                if(tutor.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else {
                    vacia.visibility = View.GONE
                    adaptadorMantenedorTutor =
                        AdaptadorMantenedorTutor(
                            tutor,
                            this@MTutor,
                            apiTTR!!
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorTutor
                    adaptadorMantenedorTutor!!.notifyDataSetChanged()
                }
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<ArrayList<Tutor>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var tutor: ArrayList<Tutor> = arrayListOf()
        var texto_busqueda = ""
    }

}

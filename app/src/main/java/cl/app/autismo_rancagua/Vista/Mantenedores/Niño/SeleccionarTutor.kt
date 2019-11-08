package cl.app.autismo_rancagua.Vista.Mantenedores.Niño

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarTutor.companion.texto_busqueda
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarTutor.companion.tutores
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.seleccionar_cargo_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionarTutor : AppCompatActivity() {

    var apiNNS: ApiNNS? = null
    var adaptadorSeleccionarTutor: AdaptadorSeleccionarTutor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_cargo_activity)


        apiNNS = ApiClient.retrofit!!.create(ApiNNS::class.java)

        btn_registrar_evento.visibility = View.GONE

        btn_volver_tbv.setOnClickListener {
            texto_busqueda = ""
            finish()
        }

        getTutores("tutores","")


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
        super.onBackPressed()
        texto_busqueda = ""
        finish()
    }

    fun getTutores(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\ntutores")
        }
        val call: Call<java.util.ArrayList<Tutor>> = apiNNS!!.getTutores(type,key)
        call.enqueue(object : Callback<java.util.ArrayList<Tutor>> {
            override fun onResponse(call: Call<java.util.ArrayList<Tutor>>, response: Response<java.util.ArrayList<Tutor>>) {
                tutores = response.body()!!

                adaptadorSeleccionarTutor =
                    AdaptadorSeleccionarTutor(tutores, this@SeleccionarTutor,applicationContext)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_seleccionar.layoutManager = mLayoutManager
                lista_seleccionar.itemAnimator = DefaultItemAnimator()
                lista_seleccionar.adapter = adaptadorSeleccionarTutor
                adaptadorSeleccionarTutor!!.notifyDataSetChanged()
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<java.util.ArrayList<Tutor>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(texto_busqueda.isNotEmpty()){
            getTutores("tutores", texto_busqueda)
        }
    }


    object companion {
        var data: Bundle? = null
        var tutores: ArrayList<Tutor> = arrayListOf()
        var texto_busqueda = ""
    }
}

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
import cl.app.autismo_rancagua.Modelo.Colegio.Colegio
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarColegio.companion.colegios
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarColegio.companion.texto_busqueda
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.seleccionar_cargo_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeleccionarColegio : AppCompatActivity() {


    var apiNNS: ApiNNS? = null
    var adaptadorSeleccionarColegio: AdaptadorSeleccionarColegio? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_cargo_activity)

        btn_registrar_evento.visibility = View.GONE


        apiNNS = ApiClient.retrofit!!.create(ApiNNS::class.java)


        btn_registrar_evento.visibility = View.GONE

        btn_volver_tbv.setOnClickListener {
            texto_busqueda = ""
            finish()
        }

        getColegios("colegios","")


        txt_buscar.queryHint = "Buscar por nombre..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getColegios("colegios", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getColegios("colegios", newText)
                return false
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        texto_busqueda = ""
        finish()
    }

    object companion {
        var data: Bundle? = null
        var colegios: ArrayList<Colegio> = arrayListOf()
        var texto_busqueda = ""
    }


    fun getColegios(type: String, key: String) {
        var dialog:TipDialog? = null
        if(key.isEmpty()){
           dialog = cargando(this, "Cargando\ncolegios")
        }

        val call: Call<java.util.ArrayList<Colegio>> = apiNNS!!.getColegios(type,key)
        call.enqueue(object : Callback<java.util.ArrayList<Colegio>> {
            override fun onResponse(call: Call<java.util.ArrayList<Colegio>>, response: Response<java.util.ArrayList<Colegio>>) {
                colegios = response.body()!!

                adaptadorSeleccionarColegio =
                    AdaptadorSeleccionarColegio(colegios, this@SeleccionarColegio,applicationContext)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_seleccionar.layoutManager = mLayoutManager
                lista_seleccionar.itemAnimator = DefaultItemAnimator()
                lista_seleccionar.adapter = adaptadorSeleccionarColegio
                adaptadorSeleccionarColegio!!.notifyDataSetChanged()
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<java.util.ArrayList<Colegio>>, t: Throwable) {
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
            getColegios("colegios", texto_busqueda)
        }
    }


}



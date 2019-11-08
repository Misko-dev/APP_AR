package cl.app.autismo_rancagua.Vista.Mantenedores.Niño

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño.companion.niño
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño.companion.texto_busqueda
import com.kongzue.dialog.v3.TipDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MNiño : AppCompatActivity() {


    var apiNNS: ApiNNS? = null
    var adaptadorMantenedorNiño: AdaptadorMantenedorNiño? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mantenedor_activity)


        apiNNS = ApiClient.retrofit!!.create(ApiNNS::class.java)
        getNiños("niños","")

        btn_asistencia.visibility= View.GONE

        actualizar.setOnRefreshListener {
            if (!niño.isNullOrEmpty()) {
                niño.clear()
                actualizar.isRefreshing = true
                adaptadorMantenedorNiño!!.limpiar()
                getNiños("niños","")
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getNiños("niños","")
                actualizar.isRefreshing = false
            }
        }

        btn_registrar.setOnClickListener {
            startActivity(Intent(this@MNiño, RGNiño::class.java))
        }


        btn_volver.setOnClickListener {
            adaptadorMantenedorNiño = null
            texto_busqueda = ""
            finish()
        }

        txt_buscar.queryHint = "Buscar por nombre, apellido o rut..."
        txt_buscar.isIconifiedByDefault = false
        txt_buscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                texto_busqueda = query
                getNiños("niños", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                texto_busqueda = newText
                getNiños("niños", newText)
                return false
            }
        })
    }

    override fun onBackPressed() {
        adaptadorMantenedorNiño = null
        texto_busqueda = ""
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        if(texto_busqueda.isNotEmpty()){
            getNiños("niños", texto_busqueda)
        }else{
            getNiños("niños", "")
        }
        /* }*/
    }

    fun getNiños(type: String, key: String) {
        var dialog: TipDialog? = null
        if(key.isEmpty()){
            dialog = cargando(this, "Cargando\nniños")
        }
        val call: Call<ArrayList<Niño>> = apiNNS!!.getNiños(type,key)
        call.enqueue(object : Callback<ArrayList<Niño>> {
            override fun onResponse(call: Call<ArrayList<Niño>>, response: Response<ArrayList<Niño>>) {
                niño = response.body()!!

                if(niño.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                }else{
                    vacia.visibility = View.GONE
                    adaptadorMantenedorNiño =
                        AdaptadorMantenedorNiño(
                            niño,
                            this@MNiño,
                            apiNNS!!

                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorMantenedorNiño
                    adaptadorMantenedorNiño!!.notifyDataSetChanged()

                }
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }


            override fun onFailure(call: Call<ArrayList<Niño>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                if(dialog != null){
                    dialog!!.doDismiss()
                }
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var niño: ArrayList<Niño> = arrayListOf()
        var texto_busqueda = ""
    }

}

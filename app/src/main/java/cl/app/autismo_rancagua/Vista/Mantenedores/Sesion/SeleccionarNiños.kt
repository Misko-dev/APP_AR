package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.SeleccionarNiños.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.SeleccionarNiños.companion.id_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.SeleccionarNiños.companion.niño
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.mantenedor_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SeleccionarNiños : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    var adaptadorSeleccionarNiño: AdaptadorSeleccionarNiño? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_participantes_activity)


        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        bundle()

        btn_registrar_evento.visibility = View.GONE
        txt_buscar.visibility = View.GONE


        actualizar.setOnRefreshListener {
            if (!niño.isNullOrEmpty()) {
                niño.clear()
                actualizar.isRefreshing = true
                adaptadorSeleccionarNiño!!.limpiar()
                getNiños(id_sesion)
                actualizar.isRefreshing = false
            } else {
                actualizar.isRefreshing = true
                getNiños(id_sesion)
                actualizar.isRefreshing = false
            }
        }

        btn_volver_tbv.setOnClickListener {
            adaptadorSeleccionarNiño = null
            finish()
        }

    }

    override fun onBackPressed() {
        adaptadorSeleccionarNiño = null
        finish()
    }

    override fun onResume() {
        super.onResume()
        getNiños(id_sesion)

    }

    fun bundle() {
        data = intent.extras
        if (data != null) {

            if (data!!.containsKey("id_sesion")) {
                id_sesion = data!!.getString("id_sesion")!!
                if(id_sesion.isNotEmpty()){
                    getNiños(id_sesion)
                }

            }
        }
    }

    fun getNiños(id_sesion: String) {
        val dialog = cargando(this, "Cargando\nniños")

        val call: Call<ArrayList<Niño>> = apiSSN!!.getNiñosXID(id_sesion)
        call.enqueue(object : Callback<ArrayList<Niño>> {
            override fun onResponse(call: Call<ArrayList<Niño>>, response: Response<ArrayList<Niño>>) {
                niño = response.body()!!

                if(niño.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    sub_titulo_vacia.text = ("No hay niños disponibles.\nVerifica si es que ya estan todos en tu lista.")
                    imagen_lista_vacia.startAnimation(stb)

                }else{
                    vacia.visibility = View.GONE
                    adaptadorSeleccionarNiño =
                        AdaptadorSeleccionarNiño(
                            niño,
                            this@SeleccionarNiños,
                            apiSSN!!,
                            id_sesion

                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorSeleccionarNiño
                    adaptadorSeleccionarNiño!!.notifyDataSetChanged()

                }
                dialog!!.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Niño>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog!!.doDismiss()
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var niño: ArrayList<Niño> = arrayListOf()
        var id_sesion:String = ""
    }

}

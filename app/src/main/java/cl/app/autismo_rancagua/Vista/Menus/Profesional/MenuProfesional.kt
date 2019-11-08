package cl.app.autismo_rancagua.Vista.Menus.Profesional

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Modelo.Profesional.CargoProfesional
import cl.app.autismo_rancagua.Vista.Menus.Profesional.MenuProfesional.companion.cuenta
import cl.app.autismo_rancagua.Vista.Menus.Profesional.MenuProfesional.companion.data
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profesional_activity_menu.*
import kotlinx.android.synthetic.main.profesional_menu_lista.*
import kotlinx.android.synthetic.main.toolbar_principal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuProfesional : AppCompatActivity() {


    var apiPRO: ApiPRO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profesional_activity_menu)

        apiPRO = ApiClient.retrofit!!.create(ApiPRO::class.java)
        bundle()


        boton_menu.setOnClickListener {
            listener_menu_principal()
        }


        btn_desconectar.setOnClickListener {
            /*startActivity(Intent(this, Login::class.java))*/
            finish()
        }
    }

    private fun listener_menu_principal() {
        if (drawerLayout.isDrawerOpen(menu_principal)) {
            drawerLayout.closeDrawer(menu_principal)
        } else {
            drawerLayout.openDrawer(menu_principal)
        }
    }

    override fun onBackPressed() {}
    fun bundle() {
        data = intent.extras
        if (data != null) {

            cuenta = data!!.getParcelableArrayList("listaCuenta")!!

            if (cuenta.isNotEmpty()) {
                for (datos in cuenta) {
                    val correo = datos.CORREO
                    val contraseña = datos.CONTRASEÑA
                    val id_profesional = datos.ID_PROFESIONAL

                    getProfesionalXID(id_profesional)
                }
            }
        }
    }

    fun getProfesionalXID(id_profesional: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<Profesional>> = apiPRO!!.getProfesionalXID(id_profesional)
        call.enqueue(object : Callback<ArrayList<Profesional>> {
            override fun onResponse(call: Call<ArrayList<Profesional>>, response: Response<ArrayList<Profesional>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val id_cargo_administrador = d.ID_CARGO_PROFESIONAL
                    val nombres = d.NOMBRES
                    val apellidos = d.APELLIDOS
                    val rut = d.RUT
                    val url = d.URL_FOTO
                    txt_nombre_completo_menu.text = ("$nombres $apellidos")
                    txt_rut_menu.text = rut

                    getCargoXID(id_cargo_administrador)

                    if (url != "null") {
                        Glide.with(applicationContext)
                            .load(url)
                            .thumbnail(0.5f)
                            .apply(
                                RequestOptions().override(0, 225).diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                )
                            )
                            .into(foto_perfil_menu)
                    }
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Profesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getCargoXID(id_cargo_profesional: String) {
        val dialog = cargando(this, "Cargando\ncargo")
        val call: Call<java.util.ArrayList<CargoProfesional>> = apiPRO!!.getCargoXID(id_cargo_profesional)
        call.enqueue(object : Callback<java.util.ArrayList<CargoProfesional>> {
            override fun onResponse(call: Call<java.util.ArrayList<CargoProfesional>>, response: Response<java.util.ArrayList<CargoProfesional>>) {
                val cargo = response.body()!!
                for (item in cargo){
                    val nombre = item.NOMBRE
                    txt_titulo_toolbar_principal.text = nombre
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<java.util.ArrayList<CargoProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    interface interface_datos_profesional {
        fun onSuccess(datos: ArrayList<Profesional>)
        fun onFail(mensaje: String)
    }

    interface interface_cargo {
        fun onSuccess(cargo: String)
        fun onFail(mensaje: String)
    }

    object companion {
        var data: Bundle? = null
        var correo: String = ""
        var contraseña: String = ""

        var ip = ""

        var cuenta: ArrayList<CuentaProfesional> = arrayListOf()
        var datos_profesional: ArrayList<Profesional> = arrayListOf()
    }

}

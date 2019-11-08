package cl.app.autismo_rancagua.Vista.Menus.Administrador


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.CLImagenes.solicitarMultiplePermisos
import cl.app.autismo_rancagua.Api.Indicadores.ApiKPI
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CargoAdministrador
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.Modelo.Indicador.Indicador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.MAdministrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.MProfesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.MTutor
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador.companion.contraseña
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador.companion.correo
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador.companion.cuenta
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador.companion.data
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador.companion.id_administrador
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.administrador_activity_menu.*
import kotlinx.android.synthetic.main.administrador_ayuda.*
import kotlinx.android.synthetic.main.administrador_menu.*
import kotlinx.android.synthetic.main.administrador_menu_indicadores.*
import kotlinx.android.synthetic.main.administrador_menu_personas.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuAdministrador : AppCompatActivity() {


    var apiAdm: ApiADM? = null
    var apiKPI: ApiKPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.administrador_activity_menu)

        apiAdm = ApiClient.retrofit!!.create(ApiADM::class.java)
        apiKPI = ApiClient.retrofit!!.create(ApiKPI::class.java)
        bundle()
        solicitarMultiplePermisos(this@MenuAdministrador, this)

        val atg = AnimationUtils.loadAnimation(this, R.anim.atg)
        val atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo)
        val atgthree = AnimationUtils.loadAnimation(this, R.anim.atgthree)

        imagen_ayuda.startAnimation(atg)
        txt_titulo_ayuda.startAnimation(atgtwo)
        txt_subtitulo_ayuda.startAnimation(atgtwo)
        btn_contactar.startAnimation(atgthree)

        btn_mantenedores.setOnClickListener {
            if (menu_personas.visibility == View.VISIBLE) {
                menu_personas.visibility = View.GONE
            }else{
                menu_personas.visibility = View.VISIBLE
                menu_indicadores.visibility = View.GONE
                menu_personas.startAnimation(atg)
            }
        }

        admin.setOnClickListener {
            menu_personas.visibility = View.GONE
            startActivity(Intent(this@MenuAdministrador, MAdministrador::class.java))
        }
        profesional.setOnClickListener {
            menu_personas.visibility = View.GONE
            startActivity(Intent(this@MenuAdministrador, MProfesional::class.java))
        }
        niño.setOnClickListener {
            menu_personas.visibility = View.GONE
            startActivity(Intent(this@MenuAdministrador, MNiño::class.java))
        }
        tutor.setOnClickListener {
            menu_personas.visibility = View.GONE
            startActivity(Intent(this@MenuAdministrador, MTutor::class.java))
        }

        btn_eventos.setOnClickListener {
            startActivity(Intent(this@MenuAdministrador, EventosAdministrador::class.java))

        }

        btn_desconectar.setOnClickListener {
            Alerter.create(this)
                .setTitle("Alerta")
                .setText("¿Deseas cerrar sesión?")
                .setBackgroundDrawable(resources.getDrawable(R.drawable.bgitem))
                .setIcon(R.drawable.ic_desconectar)
                .setIconColorFilter(0)
                .enableSwipeToDismiss()
                .addButton("SI", R.style.AlertButton, View.OnClickListener {
                    /*Toast.makeText(this@KotlinDemoActivity, "Okay Clicked", Toast.LENGTH_LONG).show()*/
                    finish()
                })
                .addButton("NO", R.style.AlertButton, View.OnClickListener {
                    /*Toast.makeText(this@KotlinDemoActivity, "No Clicked", Toast.LENGTH_LONG).show()*/
                    Alerter.hide()
                })
                .show()
        }



        btn_indicador.setOnClickListener {
            if (menu_indicadores.visibility == View.VISIBLE) {
                menu_indicadores.visibility = View.GONE
            }else{
                menu_indicadores.visibility = View.VISIBLE
                menu_personas.visibility = View.GONE
                menu_indicadores.startAnimation(atg)
                getKPIADM()
                getKPIPRO()
                getKPINNS()
                getKPITTR()
            }
        }












    }




    override fun onBackPressed() {}
    fun bundle() {
        data = intent.extras
        if (data != null) {

            cuenta = data!!.getParcelableArrayList("listaCuenta")!!

            if (cuenta.isNotEmpty()) {
                for (datos in cuenta) {
                    correo = datos.CORREO
                    contraseña = datos.CONTRASEÑA
                    id_administrador = datos.ID_ADMINISTRADOR
                    getAdministradorXID(id_administrador)

                }
            }
        }
    }

    fun getAdministradorXID(id_administrador: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<Administrador>> = apiAdm!!.getAdministradorXID(id_administrador)
        call.enqueue(object : Callback<ArrayList<Administrador>> {
            override fun onResponse(call: Call<ArrayList<Administrador>>, response: Response<ArrayList<Administrador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val id_cargo_administrador = d.ID_CARGO_ADMINISTRADOR
                    val nombres = d.NOMBRES
                    val apellidos = d.APELLIDOS
                    val rut = d.RUT
                    val url = d.URL_FOTO
                    txt_nombre.text = ("Bienvenido \n$nombres")
                    /*txt_rut_menu.text = rut*/

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
                            .into(foto_perfil)
                    }
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Administrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getCargoXID(id_cargo_administrador: String) {
        val dialog = cargando(this, "Cargando\ncargo")
        val call: Call<java.util.ArrayList<CargoAdministrador>> = apiAdm!!.getCargoXID(id_cargo_administrador)
        call.enqueue(object : Callback<java.util.ArrayList<CargoAdministrador>> {
            override fun onResponse(call: Call<java.util.ArrayList<CargoAdministrador>>, response: Response<java.util.ArrayList<CargoAdministrador>>) {
                val cargo = response.body()!!
                for (item in cargo){
                    val nombre = item.NOMBRE
                    txt_cargo.text = nombre
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<java.util.ArrayList<CargoAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }


    fun getKPIADM() {
        val dialog = cargando(this, "Cargando\nKPIs")
        val call: Call<ArrayList<Indicador>> = apiKPI!!.getKPIADM()
        call.enqueue(object : Callback<ArrayList<Indicador>> {
            override fun onResponse(call: Call<ArrayList<Indicador>>, response: Response<ArrayList<Indicador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val TOTAL_HABILITADOS = d.TOTAL_HABILITADOS
                    val TOTAL_REGISTROS = d.TOTAL_REGISTROS
                    var KPI = d.KPI.toFloat() * 100

                    if(KPI.toString().isEmpty()){
                        KPI = 0F
                    }

                    /*SETEA EL PORCENTAJE EN EL GRAFICO Y EN EL TEXTVIEW*/
                    kpi_admin.percent = KPI

                    porcentaje_admin.text = ("$KPI%")
                    txt_total_adm.setText(TOTAL_REGISTROS)
                    txt_habilitados_adm.setText(TOTAL_HABILITADOS)
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Indicador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getKPIPRO() {
        val dialog = cargando(this, "Cargando\nKPIs")
        val call: Call<ArrayList<Indicador>> = apiKPI!!.getKPIPRO()
        call.enqueue(object : Callback<ArrayList<Indicador>> {
            override fun onResponse(call: Call<ArrayList<Indicador>>, response: Response<ArrayList<Indicador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val TOTAL_HABILITADOS = d.TOTAL_HABILITADOS
                    val TOTAL_REGISTROS = d.TOTAL_REGISTROS
                    var KPI = d.KPI.toFloat() * 100

                    if(KPI.toString().isEmpty()){
                        KPI = 0F
                    }

                    /*SETEA EL PORCENTAJE EN EL GRAFICO Y EN EL TEXTVIEW*/
                    kpi_prof.percent = KPI
                    porcentaje_prof.text = ("$KPI%")
                    txt_total_pro.setText(TOTAL_REGISTROS)
                    txt_habilitados_pro.setText(TOTAL_HABILITADOS)
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Indicador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getKPINNS() {
        val dialog = cargando(this, "Cargando\nKPIs")
        val call: Call<ArrayList<Indicador>> = apiKPI!!.getKPINNS()
        call.enqueue(object : Callback<ArrayList<Indicador>> {
            override fun onResponse(call: Call<ArrayList<Indicador>>, response: Response<ArrayList<Indicador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val TOTAL_HABILITADOS = d.TOTAL_HABILITADOS
                    val TOTAL_REGISTROS = d.TOTAL_REGISTROS
                    var KPI = d.KPI.toFloat() * 100

                    if(KPI.toString().isEmpty()){
                        KPI = 0F
                    }

                    /*SETEA EL PORCENTAJE EN EL GRAFICO Y EN EL TEXTVIEW*/
                    kpi_niño.percent = KPI
                    porcentaje_niño.text = ("$KPI%")
                    txt_total_niño.setText(TOTAL_REGISTROS)
                    txt_habilitados_niño.setText(TOTAL_HABILITADOS)
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Indicador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getKPITTR() {
        val dialog = cargando(this, "Cargando\nKPIs")
        val call: Call<ArrayList<Indicador>> = apiKPI!!.getKPITTR()
        call.enqueue(object : Callback<ArrayList<Indicador>> {
            override fun onResponse(call: Call<ArrayList<Indicador>>, response: Response<ArrayList<Indicador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val TOTAL_HABILITADOS = d.TOTAL_HABILITADOS
                    val TOTAL_REGISTROS = d.TOTAL_REGISTROS
                    var KPI = d.KPI.toFloat() * 100

                    if(KPI.toString().isEmpty()){
                        KPI = 0F
                    }

                    /*SETEA EL PORCENTAJE EN EL GRAFICO Y EN EL TEXTVIEW*/
                    kpi_tutor.percent = KPI

                    porcentaje_tutor.text = ("$KPI%")
                    txt_total_tutor.setText(TOTAL_REGISTROS)
                    txt_habilitados_tutor.setText(TOTAL_HABILITADOS)
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Indicador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        val atg = AnimationUtils.loadAnimation(this, R.anim.atg)
        val atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo)
        val atgthree = AnimationUtils.loadAnimation(this, R.anim.atgthree)


        imagen_ayuda.startAnimation(atg)
        txt_titulo_ayuda.startAnimation(atgtwo)
        txt_subtitulo_ayuda.startAnimation(atgtwo)
        btn_contactar.startAnimation(atgthree)
    }

    object companion {
        var data: Bundle? = null
        var correo: String = ""
        var contraseña: String = ""
        var id_administrador = ""

        var cuenta: ArrayList<CuentaAdministrador> = arrayListOf()
        var datos_administrador: ArrayList<Administrador> = arrayListOf()
    }
}

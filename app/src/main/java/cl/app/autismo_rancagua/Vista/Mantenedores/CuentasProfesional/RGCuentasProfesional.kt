package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaPro
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Utilidades.validarEmail
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional.companion.cuenta
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional.companion.id_cuenta_registrada
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional.companion.id_profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional.companion.profesional
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_datos_cuenta.*
import kotlinx.android.synthetic.main.registrar_cuentas_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RGCuentasProfesional : AppCompatActivity() {

    var apiCtaPro: ApiCtaPro? = null
    var apiPRO: ApiPRO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_cuentas_activity)

        apiCtaPro = ApiClient.retrofit!!.create(ApiCtaPro::class.java)
        apiPRO = ApiClient.retrofit!!.create(ApiPRO::class.java)
        bundle()


        btn_guardar.setOnClickListener {
            validar_datos()
        }

        btn_volver.setOnClickListener {
            finish()
        }
    }

    fun validar_datos() {
        val email = txt_correo.text.toString().trim()
        val contraseña = txt_contraseña.text.toString().trim()

        var boolean = false

        if (TextUtils.isEmpty(email)) {
            Toasty.error(applicationContext, "Campo email vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (!validarEmail(email)) {
            Toasty.error(applicationContext, "Correo invalido.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(contraseña)) {
            Toasty.error(applicationContext, "Campo contraseña vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        } else if (contraseña.length < 4) {
            Toasty.error(
                applicationContext,
                "La contraseña debe tener 4 numeros.",
                Toast.LENGTH_SHORT,
                true
            ).show()
            boolean = true
        } else if (id_profesional.isEmpty()) {
            Toasty.error(applicationContext, "Seleccione un usuario.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        }

        if (!boolean) {

            if (data!!.containsKey("id_profesional")) {
                addCuenta(id_profesional,email,contraseña,1)
            }else{
                updateCuenta(id_cuenta_registrada, id_profesional,email,contraseña)
            }

        }
    }

    fun bundle() {
        data = intent.extras
        if (data != null) {
            txt_titulo.text = ("Modificar cuenta")

            if (data!!.containsKey("cuenta")) {
                id_cuenta_registrada = data!!.getString("id_cuenta_registrada")!!
                cuenta = data!!.getParcelable("cuenta")!!
                txt_correo.setText(cuenta.CORREO)
                txt_contraseña.setText(cuenta.CONTRASEÑA)
                id_profesional = cuenta.ID_PROFESIONAL
                /*getProfesionalXID(id_profesional)*/
            }
            if(data!!.containsKey("id_profesional")){
                id_profesional = data!!.getString("id_profesional")!!
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



    object companion {
        var data: Bundle? = null
        var id_cuenta_registrada = ""
        var id_profesional= ""
        var profesional: Profesional = Profesional()
        var cuenta: CuentaProfesional = CuentaProfesional()

        var datos_profesional: ArrayList<Profesional> = arrayListOf()

    }


    fun addCuenta(id_administrador: String, correo:String, contraseña:String, estado:Int) {
        val dialog = cargando(this, "Registrando\ncuenta")
        val call: Call<CuentaProfesional> = apiCtaPro!!.addCuenta(id_administrador,correo,contraseña,estado)
        call.enqueue(object : Callback<CuentaProfesional> {
            override fun onResponse(call: Call<CuentaProfesional>, response: Response<CuentaProfesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGCuentasProfesional, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGCuentasProfesional,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaProfesional>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateCuenta(id_cuenta_administrador: String, id_administrador: String, correo:String, contraseña:String) {
        val dialog = cargando(this, "Modificando\ncuenta")
        val call: Call<CuentaProfesional> = apiCtaPro!!.updateCuenta(id_cuenta_administrador,id_administrador,correo,contraseña)
        call.enqueue(object : Callback<CuentaProfesional> {
            override fun onResponse(call: Call<CuentaProfesional>, response: Response<CuentaProfesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGCuentasProfesional, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGCuentasProfesional,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaProfesional>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun getProfesionalXID(id_profesional: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<Profesional>> = apiPRO!!.getProfesionalXID(id_profesional)
        call.enqueue(object : Callback<ArrayList<Profesional>> {
            override fun onResponse(call: Call<ArrayList<Profesional>>, response: Response<ArrayList<Profesional>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val NOMBRES = d.NOMBRES
                    val APELLIDOS = d.APELLIDOS

                    txt_usuario.setText("$NOMBRES $APELLIDOS")
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Profesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }
}

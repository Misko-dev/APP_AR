package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaAdm
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Utilidades.validarEmail
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador.companion.administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador.companion.cuenta
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador.companion.id_administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador.companion.id_cuenta_registrada
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

class RGCuentasAdministrador : AppCompatActivity() {


    var apiCtaAdm: ApiCtaAdm? = null
    var apiAdm: ApiADM? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_cuentas_activity)

        apiCtaAdm = ApiClient.retrofit!!.create(ApiCtaAdm::class.java)
        apiAdm = ApiClient.retrofit!!.create(ApiADM::class.java)
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
        } else if (id_administrador.isEmpty()) {
            Toasty.error(applicationContext, "Seleccione un usuario.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        }

        if (!boolean) {

            if (data!!.containsKey("id_administrador")) {
                addCuenta(id_administrador,email,contraseña,1)
            }else{
                updateCuenta(id_cuenta_registrada, id_administrador,email,contraseña)
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
                id_administrador = cuenta.ID_ADMINISTRADOR
               /* getAdministradorXID(id_administrador)*/
            }

            if(data!!.containsKey("id_administrador")){
                id_administrador = data!!.getString("id_administrador")!!
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
        var id_administrador = ""
        var administrador: Administrador = Administrador()
        var cuenta: CuentaAdministrador = CuentaAdministrador()

        var datos_administrador: ArrayList<Administrador> = arrayListOf()

    }

    fun addCuenta(id_administrador: String, correo:String, contraseña:String, estado:Int) {
        val dialog = cargando(this, "Registrando\ncuenta")
        val call: Call<CuentaAdministrador> = apiCtaAdm!!.addCuenta(id_administrador,correo,contraseña,estado)
        call.enqueue(object : Callback<CuentaAdministrador> {
            override fun onResponse(call: Call<CuentaAdministrador>, response: Response<CuentaAdministrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGCuentasAdministrador, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGCuentasAdministrador,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaAdministrador>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateCuenta(id_cuenta_administrador: String, id_administrador: String, correo:String, contraseña:String) {
        val dialog = cargando(this, "Modificando\ncuenta")
        val call: Call<CuentaAdministrador> = apiCtaAdm!!.updateCuenta(id_cuenta_administrador,id_administrador,correo,contraseña)
        call.enqueue(object : Callback<CuentaAdministrador> {
            override fun onResponse(call: Call<CuentaAdministrador>, response: Response<CuentaAdministrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGCuentasAdministrador, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGCuentasAdministrador,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaAdministrador>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun getAdministradorXID(id_administrador: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<Administrador>> = apiAdm!!.getAdministradorXID(id_administrador)
        call.enqueue(object : Callback<ArrayList<Administrador>> {
            override fun onResponse(call: Call<ArrayList<Administrador>>, response: Response<ArrayList<Administrador>>) {
                val admin = response.body()!!
                for (d in admin) {
                    val NOMBRES = d.NOMBRES
                    val APELLIDOS = d.APELLIDOS

                    txt_usuario.setText("$NOMBRES $APELLIDOS")
                }

                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Administrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }
}

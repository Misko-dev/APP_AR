package cl.app.autismo_rancagua.Vista.UI

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaAdm
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaPro
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.validarEmail
import cl.app.autismo_rancagua.Vista.Menus.Administrador.MenuAdministrador
import cl.app.autismo_rancagua.Vista.Menus.Profesional.MenuProfesional
import cl.app.autismo_rancagua.Vista.UI.Login.companion.cuenta_administrador
import cl.app.autismo_rancagua.Vista.UI.Login.companion.cuenta_profesional
import cl.app.autismo_rancagua.Vista.UI.Login.companion.data
import cl.app.autismo_rancagua.Vista.UI.Login.companion.tipo_usuario
import com.android.volley.toolbox.Volley
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.login_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : AppCompatActivity() {


    var apiCtaAdm: ApiCtaAdm? = null
    var apiCtaPro: ApiCtaPro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        apiCtaAdm = ApiClient.retrofit!!.create(ApiCtaAdm::class.java)
        apiCtaPro = ApiClient.retrofit!!.create(ApiCtaPro::class.java)

        bundle()

        cargar_animaciones()
        listener_botones()
        listener_password()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    fun listener_password() {
        txt_num1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?,start: Int, before: Int, count: Int) {
                if (count > 0){
                    txt_num2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun afterTextChanged(s: Editable?) {}})


        txt_num2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?,start: Int, before: Int, count: Int) {
                if (count > 0){
                    txt_num3.requestFocus()
                }else{
                    txt_num1.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun afterTextChanged(s: Editable?) {}})

        txt_num3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?,start: Int, before: Int, count: Int) {
                if (count > 0){
                    txt_num4.requestFocus()
                }else{
                    txt_num2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun afterTextChanged(s: Editable?) {}})

        txt_num4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?,start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){
                if (after == 0){
                    txt_num3.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}})
    }

    fun listener_botones() {
        btn_ingresar_login.setOnClickListener {
            val correo = txt_ema_login.text.toString()
            val num1 = txt_num1.text.toString()
            val num2 = txt_num2.text.toString()
            val num3 = txt_num3.text.toString()
            val num4 = txt_num4.text.toString()
            val password = num1 + num2 + num3 + num4
            login(correo, password)
        }


        btn_pass_login.setOnClickListener {
            val correo: String = txt_ema_login.text.toString()
            if (TextUtils.isEmpty(correo)) {
                Toasty.info(applicationContext, "Ingrese su correo.", Toast.LENGTH_SHORT, true)
                    .show()
            } else if (!validarEmail(correo)) {
                Toasty.error(applicationContext, "Correo invalido.", Toast.LENGTH_SHORT, true)
                    .show()
            } else {
                /*HACER METODO QUE ENVIE CORREO DE RECUPERACION DE CONTRASEÑA*/
            }
        }

        btn_limpiar.setOnClickListener {
            txt_ema_login.setText("")
        }
    }

    fun bundle() {
        data = intent.extras
        if (data != null) {
            tipo_usuario = data!!.getString("tipo_usuario")!!

            when (tipo_usuario) {
                "ADMINISTRADOR" -> {
                    txt_ema_login.setText("guillermo.lagos05@gmail.com")
                    txt_num1.setText("1")
                    txt_num2.setText("2")
                    txt_num3.setText("3")
                    txt_num4.setText("4")

                }
                "PROFESIONAL" -> {
                   /* authProfesional(email, password)*/
                }
            }
        }
    }

    fun login(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) -> {
                Toasty.error(this, "Campo email vacio.", Toast.LENGTH_SHORT, true).show()
                return
            }
            TextUtils.isEmpty(password) -> {
                Toasty.error(this, "Campo contraseña vacio.", Toast.LENGTH_SHORT, true)
                    .show()
                return
            }
            password.length < 4 -> {
                Toasty.info(this, "La contraseña no tiene 4 numeros.", Toast.LENGTH_SHORT, true)
                    .show()
                return
            }
            else -> {

                when (tipo_usuario) {
                    "ADMINISTRADOR" -> {
                        authAdministrador(email, password)
                    }
                    "PROFESIONAL" -> {
                        authProfesional(email, password)
                    }
                }
            }
        }
    }

    fun authAdministrador(correo: String, contraseña: String) {
        val dialog = cargando(this, "Validando\ndatos")
        val call: Call<ArrayList<CuentaAdministrador>> =
            apiCtaAdm!!.authAdministrador(correo, contraseña)
        call.enqueue(object : Callback<ArrayList<CuentaAdministrador>> {
            override fun onResponse(
                call: Call<ArrayList<CuentaAdministrador>>,
                response: Response<ArrayList<CuentaAdministrador>>
            ) {
                cuenta_administrador = response.body()!!

                if (cuenta_administrador.isNotEmpty()) {
                    dialog.doDismiss()
                    txt_num1.requestFocus()
                    txt_ema_login.setText("")
                    txt_num1.setText("")
                    txt_num2.setText("")
                    txt_num3.setText("")
                    txt_num4.setText("")

                    val intent =
                        Intent(applicationContext, MenuAdministrador::class.java)
                    intent.putParcelableArrayListExtra(
                        "listaCuenta",
                        ArrayList(cuenta_administrador)
                    )
                    startActivity(intent)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@Login, "Oops! Error al ingresar\n¡Inténtalo de nuevo!")
                }
            }

            override fun onFailure(call: Call<ArrayList<CuentaAdministrador>>, t: Throwable) {
                Log.e("ERROR", "Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun authProfesional(correo: String, contraseña: String) {
        val dialog = cargando(this, "Validando\ndatos")
        val call: Call<ArrayList<CuentaProfesional>> =
            apiCtaPro!!.authProfesional(correo, contraseña)
        call.enqueue(object : Callback<ArrayList<CuentaProfesional>> {
            override fun onResponse(
                call: Call<ArrayList<CuentaProfesional>>,
                response: Response<ArrayList<CuentaProfesional>>
            ) {
                cuenta_profesional = response.body()!!

                if (cuenta_profesional.isNotEmpty()) {
                    dialog.doDismiss()
                    txt_num1.requestFocus()
                    txt_ema_login.setText("")
                    txt_num1.setText("")
                    txt_num2.setText("")
                    txt_num3.setText("")
                    txt_num4.setText("")

                    val intent = Intent(applicationContext, MenuProfesional::class.java)
                    intent.putParcelableArrayListExtra("listaCuenta", ArrayList(cuenta_profesional))
                    startActivity(intent)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@Login, "Oops! Error al ingresar\n¡Inténtalo de nuevo!")
                }
            }

            override fun onFailure(call: Call<ArrayList<CuentaProfesional>>, t: Throwable) {
                Log.e("ERROR", "Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun cargar_animaciones(){
        val fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop)
        val frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom)

        txt_ar.startAnimation(fromtop)
        txt_ema_login.startAnimation(fromtop)
        btn_limpiar.startAnimation(fromtop)
        txt_num1.startAnimation(frombottom)
        txt_num2.startAnimation(frombottom)
        txt_num3.startAnimation(frombottom)
        txt_num4.startAnimation(frombottom)
        btn_ingresar_login.startAnimation(frombottom)
        btn_pass_login.startAnimation(frombottom)


    }

    object companion {
        var data: Bundle? = null
        var tipo_usuario: String = ""

        var cuenta_administrador: ArrayList<CuentaAdministrador> = arrayListOf()
        var cuenta_profesional: ArrayList<CuentaProfesional> = arrayListOf()
    }

    override fun onResume() {
        super.onResume()
        cargar_animaciones()
        txt_ema_login.requestFocus()
    }

}

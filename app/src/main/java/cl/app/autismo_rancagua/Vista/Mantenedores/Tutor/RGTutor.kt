package cl.app.autismo_rancagua.Vista.Mantenedores.Tutor


import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import es.dmoral.toasty.Toasty
import java.util.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiTTR
import cl.app.autismo_rancagua.Api.CLImagenes.galeria
import cl.app.autismo_rancagua.Api.CLImagenes.subirImagenTutor
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.*
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_fecha_nacimiento_adulto
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_telefono
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.obtener_edad
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.RGTutor.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.RGTutor.companion.tutor
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.RGTutor.companion.url_foto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_datos_contacto.*
import kotlinx.android.synthetic.main.form_foto_perfil.*
import kotlinx.android.synthetic.main.form_datos_personales.*
import kotlinx.android.synthetic.main.form_salud.*
import kotlinx.android.synthetic.main.registrar_persona_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RGTutor : AppCompatActivity() {

    var apiTTR: ApiTTR? = null

    var id_tutor_registrado = ""
    var rut = ""
    var nombres = ""
    var apellidos = ""
    var edad = ""
    var sexo = ""
    var fecha_seleccionada: String = ""
    var telefono = ""
    var direccion = ""
    var correo = ""
    var sistema_salud = ""
    val datos = HashMap<String, String>()
    /*SUBIR IMAGEN*/
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_persona_activity)

        apiTTR = ApiClient.retrofit!!.create(ApiTTR::class.java)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        bundle()

        form_contacto.visibility = View.VISIBLE
        form_salud.visibility = View.VISIBLE

        txt_rut.addTextChangedListener(Mascara("##.###.###-#"))

        if (cb_hombre.isChecked) {
            sexo = cb_hombre.text.toString()
        }

        if(cb_salud_fonasa.isChecked){
            sistema_salud = cb_salud_fonasa.text.toString()
        }



        listener_sexo()
        listener_salud()


        btn_seleccionar_imagen.setOnClickListener {
            url_foto = ""
            galeria(1, this@RGTutor)
        }


        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(btn_seleccionar_fecha)
        }


        btn_guardar.setOnClickListener {
            validar_datos()
        }

        btn_volver.setOnClickListener {
            finish()
        }


        txt_titulo.setOnClickListener {
            val rut_tutores = this.resources.getStringArray(R.array.rut_adultos)
            val nombre_tutores = this.resources.getStringArray(R.array.nombres_adultos)
            val apellidos_tutores = this.resources.getStringArray(R.array.apellidos_adultos)


            val random_rut = rut_tutores[Random().nextInt(rut_tutores.size)]
            val random_nombre = nombre_tutores[Random().nextInt(nombre_tutores.size)]
            val random_apellido = apellidos_tutores[Random().nextInt(apellidos_tutores.size)]



            val random_fecha = generar_fecha_nacimiento_adulto()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fecha_formateada = random_fecha.format(formatter)
            fecha_seleccionada = fecha_formateada
            val edad_random_fecha = obtener_edad(random_fecha)


            txt_rut.setText(random_rut)
            txt_nombres.setText(random_nombre)
            txt_apellidos.setText(random_apellido)
            txt_edad.setText(edad_random_fecha)
            txt_telefono.setText(generar_telefono())
            txt_direccion.setText("Villa $random_nombre #$edad_random_fecha")
            txt_correo.setText("$random_nombre$random_apellido$edad_random_fecha@gmail.com")


            btn_seleccionar_fecha.text = (fecha_formateada)
        }
    }

    fun validar_datos() {
        rut = txt_rut.text.toString().trim()
        nombres = txt_nombres.text.toString().trim()
        apellidos = txt_apellidos.text.toString().trim()
        edad = txt_edad.text.toString()


        correo = txt_correo.text.toString()
        direccion = txt_direccion.text.toString()
        telefono = txt_telefono.text.toString()

        var boolean = false

        if (TextUtils.isEmpty(rut)) {
            Toasty.error(applicationContext, "Campo rut vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (!validarRut(rut)) {
            Toasty.error(this, "Rut invalido.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(nombres)) {
            Toasty.error(applicationContext, "Campo nombres vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        } else if (TextUtils.isEmpty(apellidos)) {
            Toasty.error(applicationContext, "Campo apellidos vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        } else if (!validarNombreApellido(nombres)) {
            Toasty.error(this, "Nombre invalido.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (!validarNombreApellido(apellidos)) {
            Toasty.error(this, "Apellido invalido.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (edad.isEmpty()) {
            Toasty.error(this, "Campo edad vacio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (edad.toInt() < 18) {
            Toasty.error(this, "El tutor debe tener más de 18 años.", Toast.LENGTH_LONG, true)
                .show()
            boolean = true
        } else if (fecha_seleccionada.isEmpty()) {
            Toasty.error(this, "Seleccione la fecha de nacimiento.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (telefono.isEmpty()) {
            Toasty.error(this, "Campo telefono vacio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (direccion.isEmpty()) {
            Toasty.error(this, "Campo direccion vacio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (correo.isEmpty()) {
            Toasty.error(this, "Campo correo alternativo vacio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        }

        if (!boolean) {
            if (data_bundle == null) {
                when (filePath) {
                    null -> {
                        mensaje_rojo(this, "Seleccione una foto.")
                    }

                    else -> {
                        subirImagenTutor(
                            this@RGTutor,
                            filePath!!,
                            storageReference!!,
                            object : interface_foto_perfil_tutor {
                                override fun onSuccess(url: String) {
                                    addTutor(rut,nombres,apellidos,edad,fecha_seleccionada,sexo,correo,telefono,direccion,sistema_salud,url,1)
                                }

                                override fun onFail(mensaje: String) {
                                    mensaje_rojo(this@RGTutor, mensaje)
                                }
                            })
                    }
                }

            } else if (data_bundle != null) {
                if (filePath != null) {
                    subirImagenTutor(
                        this@RGTutor,
                        filePath!!,
                        storageReference!!,
                        object : interface_foto_perfil_tutor {
                            override fun onSuccess(url: String) {
                                updateTutor(id_tutor_registrado,rut,nombres,apellidos,edad,fecha_seleccionada,
                                    sexo,correo,telefono,direccion,sistema_salud,url)
                            }

                            override fun onFail(mensaje: String) {
                                mensaje_rojo(this@RGTutor, mensaje)
                            }
                        })
                } else {
                    updateTutor(id_tutor_registrado,rut,nombres,apellidos,edad,fecha_seleccionada,
                        sexo,correo,telefono,direccion,sistema_salud, url_foto)
                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



    private fun listener_salud() {
        cb_salud_fonasa.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sistema_salud = cb_salud_fonasa.text.toString()
            }
        }

        cb_salud_isapre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sistema_salud = cb_salud_isapre.text.toString()
            }
        }

        cb_salud_no_tiene.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sistema_salud = cb_salud_no_tiene.text.toString()
            }
        }
    }

    private fun listener_sexo() {
        cb_hombre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sexo = cb_hombre.text.toString()
            }
        }

        cb_mujer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sexo = cb_mujer.text.toString()
            }
        }
    }

    private fun bundle() {
        data_bundle = intent.extras
        if (data_bundle != null) {

            txt_titulo.text = ("Modificar ficha")

            if (data_bundle!!.containsKey("tutor")) {

                tutor = data_bundle!!.getParcelable("tutor")!!
                id_tutor_registrado = tutor.ID_TUTOR

                val RUT = tutor.RUT
                val NOMBRES = tutor.NOMBRES
                val APELLIDOS = tutor.APELLIDOS
                val EDAD = tutor.EDAD
                val FECHA_NACIMIENTO = tutor.FECHA_NACIMIENTO
                val SEXO = tutor.SEXO
                val CORREO = tutor.CORREO
                val TELEFONO = tutor.TELEFONO
                val DIRECCION = tutor.DIRECCION
                val SISTEMA_SALUD = tutor.SISTEMA_SALUD
                val URL_FOTO = tutor.URL_FOTO
                val ESTADO = tutor.ESTADO

                when (SEXO) {
                    "Hombre" -> {
                        cb_hombre.isChecked = true
                    }

                    "Mujer" -> {
                        cb_mujer.isChecked = true
                    }
                }

                telefono = TELEFONO
                direccion = DIRECCION
                correo = CORREO
                sistema_salud = SISTEMA_SALUD

                when (sistema_salud) {
                    "Fonasa" -> {
                        cb_salud_fonasa.isChecked = true
                    }

                    "Isapre" -> {
                        cb_salud_isapre.isChecked = true
                    }

                    "No tiene" -> {
                        cb_salud_no_tiene.isChecked = true
                    }
                }

                fecha_seleccionada = FECHA_NACIMIENTO
                edad = EDAD
                url_foto = URL_FOTO
                filePath = null
                txt_rut.setText(RUT)
                txt_nombres.setText(NOMBRES)
                txt_apellidos.setText(APELLIDOS)
                txt_edad.setText(EDAD)
                btn_seleccionar_fecha.text = FECHA_NACIMIENTO

                txt_telefono.setText(telefono)
                txt_direccion.setText(direccion)
                txt_correo.setText(correo)


                if (URL_FOTO != "null") {
                    Glide.with(this@RGTutor)
                        .load(URL_FOTO)
                        .thumbnail(0.5f).fitCenter()
                        .apply(
                            RequestOptions().override(0, 100).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            )
                        )
                        .into(foto_perfil)
                }
            }
        }

    }

    @SuppressLint("NewApi")
    fun cargar_date_picker(button: Button) {
        val calendar = Calendar.getInstance()
        val datePickerDialog =
            EightFoldsDatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    var dia = ""
                    var mes = ""
                    if (dayOfMonth < 10) {
                        dia = "0${dayOfMonth}"
                    } else {
                        dia = "$dayOfMonth"
                    }
                    if (month + 1 < 10) {
                        mes = "0${month + 1}"
                    } else {
                        mes = (month + 1).toString()
                    }

                    button.text = ("$year-$mes-$dia")
                    fecha_seleccionada = ("$year-$mes-$dia")
                    val edad = obtener_edad(LocalDate.parse(fecha_seleccionada))
                    txt_edad.setText(edad)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )

        datePickerDialog.setMinDate(1940,1,1)
        datePickerDialog.setMaxDate(2001,12,1)

        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

                    if (data == null || data.data == null) {
                        return
                    }

                    filePath = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                        foto_perfil.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    interface interface_foto_perfil_tutor {
        fun onSuccess(url: String)
        fun onFail(mensaje: String)
    }

    object companion {
        var data_bundle: Bundle? = null
        var tutor: Tutor = Tutor()
        var id = ""


        var url_foto = ""

    }
    fun addTutor(rut:String, nombres:String, apellidos:String,
                       edad:String, fecha_nacimiento:String, sexo: String, correo:String, telefono:String,
                       direccion: String, sistema_salud:String , url_foto:String,estado:Int) {
        val dialog = cargando(this, "Registrando\ntutor")
        val call: Call<Tutor> = apiTTR!!.addTutor(rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,correo,telefono,direccion,sistema_salud,url_foto,estado)
        call.enqueue(object : Callback<Tutor> {
            override fun onResponse(call: Call<Tutor>, response: Response<Tutor>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGTutor, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGTutor,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Tutor>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateTutor(id_tutor:String,rut:String, nombres:String, apellidos:String,
                    edad:String, fecha_nacimiento:String, sexo: String, correo:String, telefono:String,
                    direccion: String, sistema_salud:String, url_foto:String ) {
        val dialog = cargando(this, "Modificando\ntutor")
        val call: Call<Tutor> = apiTTR!!.updateTutor(id_tutor,rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,correo,telefono,direccion,sistema_salud,url_foto)
        call.enqueue(object : Callback<Tutor> {
            override fun onResponse(call: Call<Tutor>, response: Response<Tutor>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGTutor, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGTutor,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Tutor>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

}

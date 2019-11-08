package cl.app.autismo_rancagua.Vista.Mantenedores.Profesional


import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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
import android.view.View
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Api.CLImagenes.galeria
import cl.app.autismo_rancagua.Api.CLImagenes.subirImagenProfesional
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.Modelo.Profesional.CargoProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.*
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_fecha_nacimiento_adulto
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_telefono
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.obtener_edad
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional.companion.cargo_profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional.companion.id_cargo_profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional.companion.profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional.companion.url_foto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_cargo.*
import kotlinx.android.synthetic.main.form_datos_contacto.*
import kotlinx.android.synthetic.main.form_foto_perfil.*
import kotlinx.android.synthetic.main.form_datos_personales.*
import kotlinx.android.synthetic.main.registrar_persona_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RGProfesional : AppCompatActivity() {


    var apiPRO: ApiPRO? = null

    var id_profesional_registrado = ""
    var rut = ""
    var nombres = ""
    var apellidos = ""
    var edad = ""
    var sexo = ""
    var fecha_seleccionada: String = ""
    var telefono = ""
    var direccion = ""
    var correo = ""
    var cargo_seleccionado = ""
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


        apiPRO = ApiClient.retrofit!!.create(ApiPRO::class.java)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        bundle()

        form_cargo.visibility = View.VISIBLE
        form_contacto.visibility = View.VISIBLE

        txt_rut.addTextChangedListener(Mascara("##.###.###-#"))

        if (cb_hombre.isChecked) {
            sexo = cb_hombre.text.toString()
        }


        listener_sexo()


        btn_seleccionar_imagen.setOnClickListener {
            url_foto = ""
            galeria(1, this@RGProfesional)
        }


        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(btn_seleccionar_fecha)
        }

        btn_seleccionar_cargo.setOnClickListener {
            startActivityForResult(Intent(this, SeleccionarCargo::class.java), 2)
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
        } else if(cargo_seleccionado.isEmpty()){
            Toasty.error(this, "Seleccione un cargo.", Toast.LENGTH_LONG, true).show()
            boolean = true
        }

        if (!boolean) {
            if (data_bundle == null) {
                when (filePath) {
                    null -> {
                        mensaje_rojo(this, "Seleccione una foto.")
                    }

                    else -> {
                        subirImagenProfesional(
                            this@RGProfesional,
                            filePath!!,
                            storageReference!!,
                            object : interface_foto_perfil_profesional {
                                override fun onSuccess(url: String) {
                                    addProfesional(id_cargo_profesional,rut,nombres,apellidos,edad,
                                        fecha_seleccionada,sexo,correo,telefono,direccion,url,1)
                                }

                                override fun onFail(mensaje: String) {
                                    mensaje_rojo(this@RGProfesional, mensaje)
                                }
                            })
                    }
                }

            } else if (data_bundle != null) {
                if (filePath != null) {
                    subirImagenProfesional(
                        this@RGProfesional,
                        filePath!!,
                        storageReference!!,
                        object : interface_foto_perfil_profesional {
                            override fun onSuccess(url: String) {
                                updateProfesional(id_profesional_registrado,
                                    id_cargo_profesional,rut,nombres,apellidos,edad,
                                    fecha_seleccionada,sexo,correo,telefono,direccion,url)
                            }

                            override fun onFail(mensaje: String) {
                                mensaje_rojo(this@RGProfesional, mensaje)
                            }
                        })
                } else {
                    updateProfesional(id_profesional_registrado,
                        id_cargo_profesional,rut,nombres,apellidos,edad,
                        fecha_seleccionada,sexo,correo,telefono,direccion, url_foto)
                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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

            if (data_bundle!!.containsKey("profesional")) {

                profesional = data_bundle!!.getParcelable("profesional")!!
                id_profesional_registrado = profesional.ID_PROFESIONAL

                val ID_CARGO_PROFESIONAL = profesional.ID_CARGO_PROFESIONAL
                val RUT = profesional.RUT
                val NOMBRES = profesional.NOMBRES
                val APELLIDOS = profesional.APELLIDOS
                val EDAD = profesional.EDAD
                val FECHA_NACIMIENTO = profesional.FECHA_NACIMIENTO
                val SEXO = profesional.SEXO
                val CORREO = profesional.CORREO
                val TELEFONO = profesional.TELEFONO
                val DIRECCION = profesional.DIRECCION
                val URL_FOTO = profesional.URL_FOTO
                val ESTADO = profesional.ESTADO

                when (SEXO) {
                    "Hombre" -> {
                        cb_hombre.isChecked = true
                    }

                    "Mujer" -> {
                        cb_mujer.isChecked = true
                    }
                }

                sexo = SEXO
                telefono = TELEFONO
                direccion = DIRECCION
                correo = CORREO



                fecha_seleccionada = FECHA_NACIMIENTO
                edad = EDAD
                url_foto = URL_FOTO
                filePath = null
                id_cargo_profesional = ID_CARGO_PROFESIONAL
                txt_rut.setText(RUT)
                txt_nombres.setText(NOMBRES)
                txt_apellidos.setText(APELLIDOS)
                txt_edad.setText(EDAD)
                btn_seleccionar_fecha.text = FECHA_NACIMIENTO

                txt_telefono.setText(telefono)
                txt_direccion.setText(direccion)
                txt_correo.setText(correo)


                if (URL_FOTO != "null") {
                    Glide.with(this@RGProfesional)
                        .load(URL_FOTO)
                        .thumbnail(0.5f).fitCenter()
                        .apply(
                            RequestOptions().override(0, 100).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            )
                        )
                        .into(foto_perfil)
                }

                getCargoXID(id_cargo_profesional)
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

            2->{
                if (resultCode == RESULT_OK) {
                    cargo_profesional = data!!.getParcelableExtra("cargo")!!
                    id_cargo_profesional = cargo_profesional.ID_CARGO_PROFESIONAL

                    if (id_cargo_profesional.isNotEmpty()) {
                        val NOMBRE = cargo_profesional.NOMBRE

                        cargo_seleccionado = NOMBRE
                       txt_cargo.setText(NOMBRE)
                    }
                }
            }
        }
    }

    interface interface_foto_perfil_profesional {
        fun onSuccess(url: String)
        fun onFail(mensaje: String)
    }


    object companion {
        var data_bundle: Bundle? = null
        var profesional: Profesional = Profesional()
        var id = ""

        var url_foto = ""


        var id_cargo_profesional = ""
        var cargo_profesional: CargoProfesional = CargoProfesional()

    }

    fun addProfesional(id_cargo_profesional: String, rut:String, nombres:String, apellidos:String,
                       edad:String, fecha_nacimiento:String, sexo: String, correo:String, telefono:String,
                       direccion: String, url_foto:String, estado:Int) {
        val dialog = cargando(this, "Registrando\nprofesional")
        val call: Call<Profesional> = apiPRO!!.addProfesional(id_cargo_profesional,rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,correo,telefono,direccion,url_foto,estado)
        call.enqueue(object : Callback<Profesional> {
            override fun onResponse(call: Call<Profesional>, response: Response<Profesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGProfesional, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGProfesional,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Profesional>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateProfesional(id_profesional:String, id_cargo_profesional: String, rut:String, nombres:String, apellidos:String,
                            edad:String, fecha_nacimiento:String, sexo: String, correo:String, telefono:String,
                            direccion: String, url_foto:String) {
        val dialog = cargando(this, "Modificando\nprofesional")
        val call: Call<Profesional> = apiPRO!!.updateProfesional(id_profesional,id_cargo_profesional,rut,nombres,apellidos,
            edad,fecha_nacimiento, sexo,correo,telefono,direccion,url_foto)
        call.enqueue(object : Callback<Profesional> {
            override fun onResponse(call: Call<Profesional>, response: Response<Profesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGProfesional, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGProfesional,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Profesional>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun getCargoXID(id_cargo_profesional: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<CargoProfesional>> = apiPRO!!.getCargoXID(id_cargo_profesional)
        call.enqueue(object : Callback<ArrayList<CargoProfesional>> {
            override fun onResponse(call: Call<ArrayList<CargoProfesional>>, response: Response<ArrayList<CargoProfesional>>) {
                val cargo = response.body()!!
                for (item in cargo){
                    val nombre = item.NOMBRE
                    txt_cargo.setText(nombre)
                    cargo_seleccionado = nombre
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<ArrayList<CargoProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }
}

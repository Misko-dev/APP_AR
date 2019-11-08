package cl.app.autismo_rancagua.Vista.Mantenedores.Administrador


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
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Api.CLImagenes.galeria
import cl.app.autismo_rancagua.Api.CLImagenes.subirImagenAdministrador
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CargoAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.*
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_fecha_nacimiento_adulto
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_telefono
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.obtener_edad
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador.companion.administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador.companion.cargo_administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador.companion.id_cargo_administrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador.companion.url_foto
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


class RGAdministrador : AppCompatActivity() {

    var apiAdm: ApiADM? = null

    var id_administrador_registrado = ""
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

        apiAdm = ApiClient.retrofit!!.create(ApiADM::class.java)
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
            galeria(1, this@RGAdministrador)
        }


        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(btn_seleccionar_fecha)
        }

        btn_seleccionar_cargo.setOnClickListener {
            startActivityForResult(Intent(this, SeleccionarCargoAdministrador::class.java), 2)
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
            Toasty.error(this, "El administrador debe tener más de 18 años.", Toast.LENGTH_LONG, true)
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
        } else if (cargo_seleccionado.isEmpty()) {
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
                        subirImagenAdministrador(
                            this@RGAdministrador,
                            filePath!!,
                            storageReference!!,
                            object : interface_foto_perfil_administrador {
                                override fun onSuccess(url: String) {
                                    addAdministrador(id_cargo_administrador,rut,nombres,apellidos,edad,
                                        fecha_seleccionada,sexo,correo,telefono,direccion,url,1)
                                }

                                override fun onFail(mensaje: String) {
                                    mensaje_rojo(this@RGAdministrador, mensaje)
                                }
                            })
                    }
                }

            } else if (data_bundle != null) {
                if (filePath != null) {
                    subirImagenAdministrador(
                        this@RGAdministrador,
                        filePath!!,
                        storageReference!!,
                        object : interface_foto_perfil_administrador {
                            override fun onSuccess(url: String) {
                                updateAdministrador(id_administrador_registrado,
                                    id_cargo_administrador,rut,nombres,apellidos,edad,
                                    fecha_seleccionada,sexo,correo,telefono,direccion,url)
                            }

                            override fun onFail(mensaje: String) {
                                mensaje_rojo(this@RGAdministrador, mensaje)
                            }
                        })
                } else {
                    updateAdministrador(id_administrador_registrado,
                        id_cargo_administrador,rut,nombres,apellidos,edad,
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

            if (data_bundle!!.containsKey("administrador")) {

                administrador = data_bundle!!.getParcelable("administrador")!!
                id_administrador_registrado = administrador.ID_ADMINISTRADOR

                val ID_CARGO_ADMINISTRADOR = administrador.ID_CARGO_ADMINISTRADOR
                val RUT = administrador.RUT
                val NOMBRES = administrador.NOMBRES
                val APELLIDOS = administrador.APELLIDOS
                val EDAD = administrador.EDAD
                val FECHA_NACIMIENTO = administrador.FECHA_NACIMIENTO
                val SEXO = administrador.SEXO
                val CORREO = administrador.CORREO
                val TELEFONO = administrador.TELEFONO
                val DIRECCION = administrador.DIRECCION
                val URL_FOTO = administrador.URL_FOTO
                val ESTADO = administrador.ESTADO

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
                id_cargo_administrador = ID_CARGO_ADMINISTRADOR
                txt_rut.setText(RUT)
                txt_nombres.setText(NOMBRES)
                txt_apellidos.setText(APELLIDOS)
                txt_edad.setText(EDAD)
                btn_seleccionar_fecha.text = FECHA_NACIMIENTO

                txt_telefono.setText(telefono)
                txt_direccion.setText(direccion)
                txt_correo.setText(correo)

                if (URL_FOTO != "null") {
                    Glide.with(this@RGAdministrador)
                        .load(URL_FOTO)
                        .thumbnail(0.5f).fitCenter()
                        .apply(
                            RequestOptions().override(0, 100).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            )
                        )
                        .into(foto_perfil)
                }

                getCargoXID(id_cargo_administrador)
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
        datePickerDialog.setMaxDate(2000,12,1)

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

            2 -> {
                if (resultCode == RESULT_OK) {
                    cargo_administrador = data!!.getParcelableExtra("cargo")!!
                    id_cargo_administrador = cargo_administrador.ID_CARGO_ADMINISTRADOR

                    if (id_cargo_administrador.isNotEmpty()) {
                        val NOMBRE = cargo_administrador.NOMBRE

                        txt_cargo.setText(NOMBRE)
                        cargo_seleccionado = NOMBRE
                    }
                }
            }
        }
    }

    interface interface_foto_perfil_administrador {
        fun onSuccess(url: String)
        fun onFail(mensaje: String)
    }

    object companion {
        var data_bundle: Bundle? = null
        var administrador: Administrador = Administrador()
        var id = ""

        var url_foto = ""


        var id_cargo_administrador = ""
        var cargo_administrador: CargoAdministrador = CargoAdministrador()

    }


    fun addAdministrador(id_cargo_administrador: String, rut:String, nombres:String, apellidos:String,
                  edad:String,fecha_nacimiento:String,sexo: String,correo:String,telefono:String,
                  direccion: String,url_foto:String,estado:Int) {
        val dialog = cargando(this, "Registrando\nadministrador")
        val call: Call<Administrador> = apiAdm!!.addAdministrador(id_cargo_administrador,rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,correo,telefono,direccion,url_foto,estado)
        call.enqueue(object : Callback<Administrador> {
            override fun onResponse(call: Call<Administrador>, response: Response<Administrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGAdministrador, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGAdministrador,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Administrador>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateAdministrador(id_administrador:String,id_cargo_administrador: String, rut:String, nombres:String, apellidos:String,
                     edad:String,fecha_nacimiento:String,sexo: String,correo:String,telefono:String,
                     direccion: String,url_foto:String) {
        val dialog = cargando(this, "Modificando\nadministrador")
        val call: Call<Administrador> = apiAdm!!.updateAdministrador(id_administrador,id_cargo_administrador,rut,nombres,apellidos,
            edad,fecha_nacimiento, sexo,correo,telefono,direccion,url_foto)
        call.enqueue(object : Callback<Administrador> {
            override fun onResponse(call: Call<Administrador>, response: Response<Administrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGAdministrador, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGAdministrador,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Administrador>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun getCargoXID(id_cargo_administrador: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<CargoAdministrador>> = apiAdm!!.getCargoXID(id_cargo_administrador)
        call.enqueue(object : Callback<ArrayList<CargoAdministrador>> {
            override fun onResponse(call: Call<ArrayList<CargoAdministrador>>, response: Response<ArrayList<CargoAdministrador>>) {
                var cargo = response.body()!!
                for (item in cargo){
                    val nombre = item.NOMBRE
                    cargo_seleccionado = item.NOMBRE
                    txt_cargo.setText(nombre)
                }
                dialog.doDismiss()
            }
            override fun onFailure(call: Call<ArrayList<CargoAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }
}

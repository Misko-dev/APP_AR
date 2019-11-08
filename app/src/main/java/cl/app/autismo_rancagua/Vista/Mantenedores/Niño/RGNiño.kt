package cl.app.autismo_rancagua.Vista.Mantenedores.Niño


import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.graphics.Rect
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
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Api.CLImagenes.galeria
import cl.app.autismo_rancagua.Api.CLImagenes.subirImagenNiño
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.Modelo.Colegio.Colegio
import cl.app.autismo_rancagua.Modelo.Educacion.Educacion
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.*
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.generar_fecha_nacimiento_nino
import cl.app.autismo_rancagua.Utilidades.HelperRegistros.obtener_edad
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.colegio
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.educacion
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.id_colegio
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.id_educacion_niño
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.id_tutor
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.niño
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.tutor
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño.companion.url_foto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_colegio.*
import kotlinx.android.synthetic.main.registrar_persona_activity.*
import kotlinx.android.synthetic.main.form_foto_perfil.*
import kotlinx.android.synthetic.main.form_datos_personales.*
import kotlinx.android.synthetic.main.form_tipo_educacion.*
import kotlinx.android.synthetic.main.form_tutor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class RGNiño : AppCompatActivity() {

    var apiNNS: ApiNNS? = null

    var id_niño_registrado = ""
    var rut = ""
    var nombres = ""
    var apellidos = ""
    var edad = ""
    var sexo = ""
    var fecha_seleccionada: String = ""
    val datos = HashMap<String, String>()

    /*SUBIR IMAGEN*/
    private val PICK_IMAGE_REQUEST = 4
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_persona_activity)

        apiNNS = ApiClient.retrofit!!.create(ApiNNS::class.java)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        bundle()

        form_tutor.visibility = View.VISIBLE
        form_educacion.visibility = View.VISIBLE
        form_colegio.visibility = View.VISIBLE


        txt_rut.addTextChangedListener(Mascara("##.###.###-#"))

        if (cb_hombre.isChecked) {
            sexo = cb_hombre.text.toString()
        }



        listener_sexo()



        btn_seleccionar_imagen.setOnClickListener {
            url_foto = ""
            galeria(4, this@RGNiño)
        }


        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(btn_seleccionar_fecha)
        }


        btn_seleccionar_educacion.setOnClickListener {
            startActivityForResult(Intent(this, SeleccionarEducacion::class.java), 1)
        }

        btn_seleccionar_colegio.setOnClickListener {
            startActivityForResult(Intent(this, SeleccionarColegio::class.java), 2)
        }


        btn_seleccionar_tutor.setOnClickListener {
            startActivityForResult(Intent(this, SeleccionarTutor::class.java), 3)
        }


        btn_guardar.setOnClickListener {
            validar_datos()
        }

        btn_volver.setOnClickListener {
            finish()
        }


        txt_titulo.setOnClickListener {
            val rut_ninos = this.resources.getStringArray(R.array.rut_ninos)
            val nombre_ninos = this.resources.getStringArray(R.array.nombres_ninos)
            val apellidos_ninos = this.resources.getStringArray(R.array.apellidos_ninos)

            val random_rut = rut_ninos[Random().nextInt(rut_ninos.size)]
            val random_nombre = nombre_ninos[Random().nextInt(nombre_ninos.size)]
            val random_apellido = apellidos_ninos[Random().nextInt(apellidos_ninos.size)]

            val random_fecha = generar_fecha_nacimiento_nino()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fecha_formateada = random_fecha.format(formatter)
            fecha_seleccionada = fecha_formateada


            txt_rut.setText(random_rut)
            txt_nombres.setText(random_nombre)
            txt_apellidos.setText(random_apellido)
            txt_edad.setText(obtener_edad(random_fecha))
            btn_seleccionar_fecha.text = (fecha_formateada)
        }
    }

    fun validar_datos() {

        val rut = txt_rut.text.toString().trim()
        val nombre = txt_nombres.text.toString().trim()
        val apellido = txt_apellidos.text.toString().trim()
        edad = txt_edad.text.toString()

        var boolean = false

        if (TextUtils.isEmpty(rut)) {
            Toasty.error(applicationContext, "Campo rut vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (!validarRut(rut)) {
            Toasty.error(this, "Rut invalido.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(nombre)) {
            Toasty.error(applicationContext, "Campo nombres vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        } else if (TextUtils.isEmpty(apellido)) {
            Toasty.error(applicationContext, "Campo apellidos vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        } else if (edad.isEmpty()) {
            Toasty.error(this, "Campo edad vacio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (fecha_seleccionada.isEmpty()) {
            Toasty.error(this, "Seleccione la fecha de nacimiento.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (id_educacion_niño.isEmpty()) {
            Toasty.error(this, "Seleccione un tipo de educacion.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (id_colegio.isEmpty()) {
            Toasty.error(this, "Seleccione un colegio.", Toast.LENGTH_LONG, true).show()
            boolean = true
        } else if (id_tutor.isEmpty()) {
            Toasty.error(this, "Seleccione un tutor.", Toast.LENGTH_LONG, true).show()
            boolean = true
        }


        if (!boolean) {
            if (data_bundle == null) {
                when (filePath) {
                    null -> {
                        mensaje_rojo(this, "Seleccione una foto.")
                    }

                    else -> {
                        subirImagenNiño(
                            this@RGNiño,
                            filePath!!,
                            storageReference!!,
                            object : interface_foto_perfil_niño {
                                override fun onSuccess(url: String) {
                                    addNiño(id_tutor, id_colegio, id_educacion_niño,rut,nombre,apellido,edad,fecha_seleccionada,sexo,url,1)
                                }

                                override fun onFail(mensaje: String) {
                                    mensaje_rojo(this@RGNiño, mensaje)
                                }
                            })
                    }
                }

            } else if (data_bundle != null) {
                if (filePath != null) {
                    subirImagenNiño(
                        this@RGNiño,
                        filePath!!,
                        storageReference!!,
                        object : interface_foto_perfil_niño {
                            override fun onSuccess(url: String) {
                                updateNiño(id_niño_registrado,id_tutor, id_colegio, id_educacion_niño,rut,nombre,apellido,edad,fecha_seleccionada,sexo,url)
                            }

                            override fun onFail(mensaje: String) {
                                mensaje_rojo(this@RGNiño, mensaje)
                            }
                        })
                } else {
                    updateNiño(id_niño_registrado,id_tutor, id_colegio, id_educacion_niño,rut,nombre,apellido,edad,fecha_seleccionada,sexo,url_foto)
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


            if (data_bundle!!.containsKey("niño")) {

                niño = data_bundle!!.getParcelable("niño")!!
                id_niño_registrado = niño.ID_NIÑO

                val ID_TUTOR = niño.ID_TUTOR
                val ID_COLEGIO = niño.ID_COLEGIO
                val ID_EDUCACION_NIÑO = niño.ID_EDUCACION_NIÑO
                val RUT = niño.RUT
                val NOMBRES = niño.NOMBRES
                val APELLIDOS = niño.APELLIDOS
                val EDAD = niño.EDAD
                val FECHA_NACIMIENTO = niño.FECHA_NACIMIENTO
                val SEXO = niño.SEXO
                val URL_FOTO = niño.URL_FOTO

                when (SEXO) {
                    "Hombre" -> {
                        cb_hombre.isChecked = true
                    }

                    "Mujer" -> {
                        cb_mujer.isChecked = true
                    }
                }

                id_educacion_niño = ID_EDUCACION_NIÑO
                id_colegio = ID_COLEGIO
                id_tutor = ID_TUTOR
                fecha_seleccionada = FECHA_NACIMIENTO
                edad = EDAD
                url_foto = URL_FOTO
                filePath = null
                txt_rut.setText(RUT)
                txt_nombres.setText(NOMBRES)
                txt_apellidos.setText(APELLIDOS)
                txt_edad.setText(EDAD)
                btn_seleccionar_fecha.text = FECHA_NACIMIENTO


                if (URL_FOTO != "null") {
                    Glide.with(this@RGNiño)
                        .load(URL_FOTO)
                        .thumbnail(0.5f).fitCenter()
                        .apply(
                            RequestOptions().override(0, 100).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            )
                        )
                        .into(foto_perfil)
                }

                getColegioXID(id_colegio)
                getEducacionXID(id_educacion_niño)
                getTutorXID(id_tutor)

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
                    dia = if (dayOfMonth < 10) {
                        "0${dayOfMonth}"
                    } else {
                        "$dayOfMonth"
                    }
                    mes = if (month + 1 < 10) {
                        "0${month + 1}"
                    } else {
                        (month + 1).toString()
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

        datePickerDialog.setMinDate(2001,1,1)
        datePickerDialog.setMaxDate(2018,10,1)

        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    educacion = data!!.getParcelableExtra("educacion")!!
                    id_educacion_niño = educacion.ID_EDUCACION_NIÑO

                    if (id_educacion_niño != "") {
                        val NOMBRE = educacion.NOMBRE

                        txt_tipo_educacion.setText(NOMBRE)
                    }
                }
            }

            2 -> {
                if (resultCode == RESULT_OK) {
                    colegio = data!!.getParcelableExtra("colegio")!!
                    id_colegio = colegio.ID_COLEGIO

                    if (id_colegio != "") {
                        val NOMBRE = colegio.NOMBRE
                        txt_colegio.setText(NOMBRE)
                    }
                }
            }

            3 -> {
                if (resultCode == RESULT_OK) {
                    tutor = data!!.getParcelableExtra("tutor")!!
                    id_tutor = tutor.ID_TUTOR

                    if (id_tutor != "") {
                        val NOMBRES = tutor.NOMBRES
                        val APELLIDOS = tutor.APELLIDOS
                        txt_tutor.setText("$NOMBRES $APELLIDOS")
                    }
                }
            }


            4 -> {
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

    interface interface_foto_perfil_niño {
        fun onSuccess(url: String)
        fun onFail(mensaje: String)
    }

    object companion {
        var data_bundle: Bundle? = null
        var niño: Niño = Niño()
        var id = ""


        var url_foto = ""

        var id_tutor = ""
        var tutor: Tutor = Tutor()
        var datos_tutor: ArrayList<Tutor> = ArrayList()

        var id_educacion_niño = ""
        var educacion: Educacion = Educacion()
        var datos_educacion: ArrayList<Educacion> = ArrayList()

        var id_colegio = ""
        var colegio: Colegio = Colegio()
        var datos_colegio: ArrayList<Colegio> = ArrayList()
    }


    fun getColegioXID(id_colegio: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<java.util.ArrayList<Colegio>> = apiNNS!!.getColegioXID(id_colegio)
        call.enqueue(object : Callback<java.util.ArrayList<Colegio>> {
            override fun onResponse(call: Call<java.util.ArrayList<Colegio>>, response: Response<java.util.ArrayList<Colegio>>) {
                val colegio = response.body()!!
                for (d in colegio) {
                    val NOMBRE_COLEGIO = d.NOMBRE
                   txt_colegio.setText(NOMBRE_COLEGIO)
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<java.util.ArrayList<Colegio>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getEducacionXID(id_educacion_niño: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<java.util.ArrayList<Educacion>> = apiNNS!!.getEducacionXID(id_educacion_niño)
        call.enqueue(object : Callback<ArrayList<Educacion>> {
            override fun onResponse(call: Call<ArrayList<Educacion>>, response: Response<ArrayList<Educacion>>) {
                val educacion = response.body()!!
                for (d in educacion) {
                    val NOMBRE = d.NOMBRE
                    txt_tipo_educacion.setText(NOMBRE)
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<java.util.ArrayList<Educacion>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun getTutorXID(id_tutor: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<java.util.ArrayList<Tutor>> = apiNNS!!.getTutorXID(id_tutor)
        call.enqueue(object : Callback<ArrayList<Tutor>> {
            override fun onResponse(call: Call<ArrayList<Tutor>>, response: Response<ArrayList<Tutor>>) {
                val tutor = response.body()!!
                for (d in tutor) {
                    val NOMBRES_TUTOR = d.NOMBRES
                    val APELLIDOS_TUTOR = d.APELLIDOS
                    txt_tutor.setText("$NOMBRES_TUTOR $APELLIDOS_TUTOR")
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<java.util.ArrayList<Tutor>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun addNiño(id_tutor: String,id_colegio:String, id_educacion_niño:String, rut:String, nombres:String, apellidos:String,
                       edad:String, fecha_nacimiento:String, sexo: String,url_foto:String, estado:Int) {
        val dialog = cargando(this, "Registrando\nniño")
        val call: Call<Niño> = apiNNS!!.addNiño(id_tutor,id_colegio,id_educacion_niño,rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,url_foto,estado)
        call.enqueue(object : Callback<Niño> {
            override fun onResponse(call: Call<Niño>, response: Response<Niño>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGNiño, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGNiño,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Niño>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateNiño(id_niño:String,id_tutor: String,id_colegio:String, id_educacion_niño:String, rut:String, nombres:String, apellidos:String,
                   edad:String, fecha_nacimiento:String, sexo: String,url_foto:String) {
        val dialog = cargando(this, "Modificando\nniño")
        val call: Call<Niño> = apiNNS!!.updateNiño(id_niño,id_tutor,id_colegio,id_educacion_niño,rut,nombres,apellidos,
            edad,fecha_nacimiento,sexo,url_foto)
        call.enqueue(object : Callback<Niño> {
            override fun onResponse(call: Call<Niño>, response: Response<Niño>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    finish()
                    dialog.doDismiss()
                    mensaje_verde(this@RGNiño, MENSAJE)
                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGNiño,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Niño>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }
}

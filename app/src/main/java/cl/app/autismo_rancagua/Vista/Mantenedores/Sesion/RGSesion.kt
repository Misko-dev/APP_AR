package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Sesion.Sesion
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.descripcion_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.dia_seleccionado
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.duracion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.hora
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.id_profesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.id_sesion_registrada
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.lista_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.nombre_sala
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.nombre_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.RGSesion.companion.tipo_sesion
import com.tapadoo.alerter.Alerter
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_datos_sesion1.*
import kotlinx.android.synthetic.main.form_datos_sesion2.*
import kotlinx.android.synthetic.main.form_descripcion.*
import kotlinx.android.synthetic.main.form_tipo_sesion.*
import kotlinx.android.synthetic.main.registrar_sesiones_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RGSesion : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    var dia_horas_disponibles = mutableMapOf<Int, List<Int>>()

    var lunes = listOf<Int>()
    var martes = listOf<Int>()
    var miercoles = listOf<Int>()
    var jueves = listOf<Int>()
    var viernes = listOf<Int>()
    var sabado = listOf<Int>()
    var domingo = listOf<Int>()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_sesiones_activity)


        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        bundle()

        if (cb_taller.isChecked) {
            tipo_sesion = cb_taller.text.toString()
        }

        listener_tipo()


        btn_guardar.setOnClickListener {
            validar_datos()
        }

        btn_volver.setOnClickListener {
            finish()
        }

        txt_titulo.setOnClickListener {
            lunes = listOf(); martes = listOf(); miercoles = listOf(); jueves = listOf(); viernes = listOf(); sabado = listOf(); domingo = listOf()

            if (data_bundle!!.containsKey("horas_disponibles")) {
                dia_horas_disponibles = intent.getSerializableExtra("horas_disponibles") as MutableMap<Int, List<Int>>

                val horas_totales = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                for (i in 1..7) {
                    dia_horas_disponibles[i]?.let { horas ->

                        /*ME DEVUELVE LAS HORAS DISPONIBLES*/
                        val horas_disponibles = horas_totales.minus(horas)
                        for (element in horas_disponibles) {
                            /*Log.e("chup", "DIA:$i HORAS: $element")*/
                            /*txt_descripcion.setText(txt_descripcion.text.toString() + "DIA:$i HORAS: $element")*/
                            when(i){
                                1->{
                                    lunes = lunes + arrayListOf(element)
                                }

                                2->{
                                    martes = martes + arrayListOf(element)
                                }
                                3->{
                                    miercoles = miercoles + arrayListOf(element)
                                }
                                4->{
                                    jueves = jueves + arrayListOf(element)
                                }
                                5->{
                                    viernes = viernes + arrayListOf(element)
                                }
                                6->{
                                    sabado = sabado + arrayListOf(element)
                                }
                                7->{
                                    domingo = domingo + arrayListOf(element)
                                }
                            }
                        }

                    }
                }
            }



            /*-------------------------------------------------------------------------------------->*/

            var lun = ""
            var mar = ""
            var mie = ""
            var jue = ""
            var vie = ""
            var sab = ""
            var dom = ""
            if(lunes.isNotEmpty()){
                Log.e("chup", "LUNES---> HORAS: $lunes")
                lun = "LUNES---> HORAS: $lunes"
            }else{
                Log.e("chup", "LUNES---> TODAS LAS HORAS DISPONIBLES")
                lun = "LUNES---> TODAS LAS HORAS DISPONIBLES"
            }

            if(martes.isNotEmpty()){
                Log.e("chup", "MARTES---> HORAS: $martes")
                mar = "MARTES---> HORAS: $martes"
            }else{
                Log.e("chup", "MARTES---> TODAS LAS HORAS DISPONIBLES")
                mar = "MARTES---> TODAS LAS HORAS DISPONIBLES"
            }

            if(miercoles.isNotEmpty()){
                Log.e("chup", "MIERCOLES---> HORAS: $miercoles")
                mie = "MIERCOLES---> HORAS: $miercoles"
            }else{
                Log.e("chup", "MIERCOLES---> TODAS LAS HORAS DISPONIBLES")
                mie = "MIERCOLES---> TODAS LAS HORAS DISPONIBLES"
            }

            if(jueves.isNotEmpty()){
                Log.e("chup", "JUEVES---> HORAS: $jueves")
                jue = "JUEVES---> HORAS: $jueves"

            }else{
                Log.e("chup", "JUEVES---> TODAS LAS HORAS DISPONIBLES")
                jue = "JUEVES---> TODAS LAS HORAS DISPONIBLES"
            }

            if(viernes.isNotEmpty()){
                Log.e("chup", "VIERNES---> HORAS: $viernes")
                vie = "VIERNES---> HORAS: $viernes"

            }else{
                Log.e("chup", "VIERNES---> TODAS LAS HORAS DISPONIBLES")
                vie = "VIERNES---> TODAS LAS HORAS DISPONIBLES"
            }

            if(sabado.isNotEmpty()){
                Log.e("chup", "SABADO---> HORAS: $sabado")
                sab = "SABADO---> HORAS: $sabado"

            }else{
                Log.e("chup", "SABADO---> TODAS LAS HORAS DISPONIBLES")
                sab = "SABADO---> TODAS LAS HORAS DISPONIBLES"
            }

            if(domingo.isNotEmpty()){
                Log.e("chup", "DOMINGO---> HORAS: $domingo")
                dom = "DOMINGO---> HORAS: $domingo"
            }else{
                Log.e("chup", "DOMINGO---> TODAS LAS HORAS DISPONIBLES")
                dom = "DOMINGO---> TODAS LAS HORAS DISPONIBLES"
            }

            Alerter.create(this)
                .setTitle("HORAS DISPONIBLES")
                .setText("$lun\n$mar\n$mie\n$jue\n$vie\n$sab\n$dom")
                .setDuration(10000)
                .enableSwipeToDismiss()
                .show()

        }

    }

    private fun listener_tipo() {
        cb_taller.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tipo_sesion = cb_taller.text.toString()
            }
        }

        cb_terapia.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tipo_sesion = cb_terapia.text.toString()
            }
        }
    }

    fun validar_datos() {
        var boolean = false


        nombre_sala = txt_sala.text.toString()
        nombre_sesion = txt_nombre.text.toString()
        dia_seleccionado = txt_dia.text.toString()
        hora = txt_hora.text.toString()
        duracion = txt_duracion.text.toString()
        descripcion_sesion = txt_descripcion.text.toString()




        if (TextUtils.isEmpty(nombre_sala)) {
            Toasty.error(applicationContext, "Campo sala vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(nombre_sesion)) {
            Toasty.error(applicationContext, "Campo nombre vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(dia_seleccionado)) {
            Toasty.error(
                applicationContext,
                "Seleccione un día.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (dia_seleccionado.toInt() > 7) {
            Toasty.error(
                applicationContext,
                "El día ingresado no debe ser mayor a 7.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (dia_seleccionado.toInt() == 0) {
            Toasty.error(
                applicationContext,
                "El día ingresado debe ser mayor a 0.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (TextUtils.isEmpty(hora)) {
            Toasty.error(
                applicationContext,
                "Seleccione una hora inicial.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (hora.toInt() < 8) {
            Toasty.error(
                applicationContext,
                "La hora inicial debe ser mayor a 8.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (hora.toInt() > 19) {
            Toasty.error(
                applicationContext,
                "La hora inicial debe ser menor a 19.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (TextUtils.isEmpty(duracion)) {
            Toasty.error(
                applicationContext,
                "Seleccione una duracion.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (duracion.toInt() < 0) {
            Toasty.error(
                applicationContext,
                "La duracion debe ser mayor a 0.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (duracion.toInt() > 12) {
            Toasty.error(
                applicationContext,
                "La duracion no debe superar las 12 horas.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (TextUtils.isEmpty(descripcion_sesion)) {
            Toasty.error(applicationContext, "Campo descripcion vacio.", Toast.LENGTH_SHORT, true)
                .show()
            boolean = true
        }

        if (hora.isNotEmpty() && duracion.isNotEmpty()) {
            val calculo = (hora.toInt() - 1) + duracion.toInt()
            if (calculo > 19) {
                Toasty.error(
                    applicationContext,
                    "La hora inicial y su duracion \nno deben superar las 19 horas.",
                    Toast.LENGTH_SHORT,
                    true
                )
                    .show()
                boolean = true
            }
        }


        if (!boolean) {
            if (data_bundle!!.containsKey("id_profesional")) {

                addSesion(
                    id_profesional,
                    nombre_sala,
                    nombre_sesion,
                    dia_seleccionado.toInt(),
                    (hora.toInt() - 7),
                    duracion.toInt(),
                    descripcion_sesion,
                    tipo_sesion,
                    1
                )
            } else if (data_bundle!!.containsKey("id_sesion_registrada")) {
                updateSesion(
                    id_sesion_registrada,
                    id_profesional,
                    nombre_sala,
                    nombre_sesion,
                    dia_seleccionado.toInt(),
                    (hora.toInt() - 7),
                    duracion.toInt(),
                    descripcion_sesion,
                    tipo_sesion
                )
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("SimpleDateFormat")
    fun bundle() {
        data_bundle = intent.extras
        if (data_bundle != null) {

            if (data_bundle!!.containsKey("id_profesional")) {
                id_profesional = data_bundle!!.getString("id_profesional")!!
            }

            if (data_bundle!!.containsKey("id_sesion_registrada")) {
                txt_titulo.text = ("Modificar sesion")
                id_sesion_registrada = data_bundle!!.getString("id_sesion_registrada")!!
                getSesion(id_sesion_registrada)
            }




        }

    }

    fun addSesion(
        id_profesional: String,
        sala: String,
        nombre: String,
        dia: Int,
        hora: Int,
        duracion: Int,
        descripcion: String,
        tipo: String,
        estado: Int
    ) {
        val dialog = cargando(this, "Registrando\nsesion")
        val call: Call<Sesion> = apiSSN!!.addSesion(
            id_profesional,
            sala,
            nombre,
            dia,
            hora,
            duracion,
            descripcion,
            tipo,
            estado
        )
        call.enqueue(object : Callback<Sesion> {
            override fun onResponse(call: Call<Sesion>, response: Response<Sesion>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    mensaje_verde(this@RGSesion, MENSAJE)
                    dialog.doDismiss()
                    finish()

                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGSesion, MENSAJE)
                }
            }

            override fun onFailure(call: Call<Sesion>, t: Throwable) {
                dialog.doDismiss()
                Log.e("ERROR", "Error : $t")
            }
        })
    }

    fun updateSesion(
        id_sesion_registrada: String,
        id_profesional: String,
        sala: String,
        nombre: String,
        dia: Int,
        hora: Int,
        duracion: Int,
        descripcion: String,
        tipo: String
    ) {
        val dialog = cargando(this, "Modificando\nsesion")
        val call: Call<Sesion> = apiSSN!!.updateSesion(
            id_sesion_registrada,
            id_profesional,
            sala,
            nombre,
            dia,
            hora,
            duracion,
            descripcion,
            tipo
        )
        call.enqueue(object : Callback<Sesion> {
            override fun onResponse(call: Call<Sesion>, response: Response<Sesion>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    mensaje_verde(this@RGSesion, MENSAJE)
                    dialog.doDismiss()
                    finish()

                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGSesion, MENSAJE)
                }
            }

            override fun onFailure(call: Call<Sesion>, t: Throwable) {
                dialog.doDismiss()
                Log.e("ERROR", "Error : $t")
            }
        })
    }

    fun getSesion(id_sesion_registrada: String) {
        val dialog = cargando(this, "Cargando\ndatos")
        val call: Call<ArrayList<Sesion>> = apiSSN!!.getSesionXIDSSN(id_sesion_registrada)
        call.enqueue(object : Callback<ArrayList<Sesion>> {
            override fun onResponse(
                call: Call<ArrayList<Sesion>>,
                response: Response<ArrayList<Sesion>>
            ) {
                lista_sesion = response.body()!!

                for (item in lista_sesion) {
                    id_profesional = item.ID_PROFESIONAL
                    nombre_sala = item.SALA
                    nombre_sesion = item.NOMBRE
                    dia_seleccionado = item.DIA.toString()
                    hora = (item.HORA + 7).toString()
                    duracion = item.DURACION.toString()
                    descripcion_sesion = item.DESCRIPCION
                    tipo_sesion = item.TIPO

                }


                when (tipo_sesion) {
                    "Taller" -> {
                        cb_taller.isChecked = true
                    }

                    "Terapia" -> {
                        cb_terapia.isChecked = true
                    }
                }

                txt_sala.setText(nombre_sala)
                txt_nombre.setText(nombre_sesion)
                txt_dia.setText(dia_seleccionado)
                txt_hora.setText(hora)
                txt_duracion.setText(duracion)
                txt_descripcion.setText(descripcion_sesion)
                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Sesion>>, t: Throwable) {
                Log.e("ERROR", "Error : $t")
                dialog.doDismiss()
            }
        })
    }


    object companion {
        var data_bundle: Bundle? = null
        var sesion: Sesion = Sesion()
        var id_sesion_registrada = ""
        var id_profesional = ""
        var nombre_sala = ""
        var nombre_sesion = ""
        var dia_seleccionado = ""
        var hora = ""
        var duracion = ""
        var descripcion_sesion = ""
        var tipo_sesion = ""


        var lista_sesion = arrayListOf<Sesion>()

    }

}

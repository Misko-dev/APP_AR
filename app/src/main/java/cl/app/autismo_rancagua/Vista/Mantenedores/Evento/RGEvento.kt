package cl.app.autismo_rancagua.Vista.Mantenedores.Evento


import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import es.dmoral.toasty.Toasty
import java.util.*
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.graphics.Color
import android.util.Log
import android.widget.EditText
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Eventos.ApiEVE
import cl.app.autismo_rancagua.Modelo.Evento.Evento
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.RGEvento.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.RGEvento.companion.evento
import kotlinx.android.synthetic.main.form_botones.*
import kotlinx.android.synthetic.main.form_datos_evento.*
import kotlinx.android.synthetic.main.registrar_eventos_activity.*
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class RGEvento : AppCompatActivity() {

    var apiEVE: ApiEVE? = null

    var id_evento_registrado = ""
    var titulo_evento = ""
    var descripcion_evento = ""
    var fecha_seleccionada: String = ""
    var dia_fecha = ""
    var mes_fecha = ""
    var hora_seleccionada = ""
    var color_seleccionado = ""



    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_eventos_activity)


        apiEVE = ApiClient.retrofit!!.create(ApiEVE::class.java)
        bundle()

        listener_fecha_hora()
        listener_color()


        btn_guardar.setOnClickListener {
            validar_datos()
        }

        btn_volver.setOnClickListener {
            finish()
        }

    }



    fun validar_datos() {
        var boolean = false

        titulo_evento = txt_titulo_evento.text.toString()
        descripcion_evento = txt_descripcion_evento.text.toString()
        hora_seleccionada = txt_hora.text.toString()

        if (TextUtils.isEmpty(titulo_evento)) {
            Toasty.error(applicationContext, "Campo nombre vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(descripcion_evento)) {
            Toasty.error(applicationContext, "Campo descripcion vacio.", Toast.LENGTH_SHORT, true).show()
            boolean = true
        } else if (TextUtils.isEmpty(fecha_seleccionada)) {
            Toasty.error(
                applicationContext,
                "Seleccione una fecha inicial.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        } else if (hora_seleccionada == "Seleccionar hora inicio") {
            Toasty.error(
                applicationContext,
                "Seleccione una hora inicial.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        }else if (color_seleccionado.isEmpty()) {
            Toasty.error(
                applicationContext,
                "Seleccione un color.",
                Toast.LENGTH_SHORT,
                true
            )
                .show()
            boolean = true
        }


        if (!boolean) {
            if (data_bundle == null) {
                addEvento(fecha_seleccionada,hora_seleccionada,titulo_evento,descripcion_evento,color_seleccionado,1)
            } else if (data_bundle != null) {
                updateEvento(id_evento_registrado,fecha_seleccionada,hora_seleccionada,titulo_evento,descripcion_evento,color_seleccionado)
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



    fun listener_fecha_hora() {
        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(txt_fecha, true)
        }

        btn_seleccionar_hora.setOnClickListener {
            cargar_time_picker(txt_hora)
        }

    }

    fun listener_color() {
        btn_seleccionar_color.setOnClickListener {
            ColorPickerDialog()
                .withTitle("Seleccione un color")
                .withPresets(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.CYAN)
                .withListener { _, color ->
                    val cl = String.format("#%08X", color)
                    color_seleccionado = cl
                    txt_color.setText(color_seleccionado)
                    txt_color.setBackgroundColor(Color.parseColor(cl))
                }
                .show(supportFragmentManager, "colorPicker")
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun bundle() {
        data_bundle = intent.extras
        if (data_bundle != null) {

            txt_titulo.text = ("Modificar evento")

            id_evento_registrado = data_bundle!!.getString("id_evento_registrado")!!


            if (data_bundle!!.containsKey("evento")) {

                evento = data_bundle!!.getParcelable("evento")!!

                titulo_evento = evento.TITULO
                descripcion_evento = evento.DESCRIPCION
                hora_seleccionada = evento.HORA
                color_seleccionado = evento.COLOR
                txt_color.setBackgroundColor(Color.parseColor(color_seleccionado))
                txt_color.setText(color_seleccionado)

                val format = SimpleDateFormat("yyyy-MM-dd")
                fecha_seleccionada = format.format(evento.FECHA)

                txt_titulo_evento.setText(titulo_evento)
                txt_descripcion_evento.setText(descripcion_evento)
                txt_fecha.setText(fecha_seleccionada)
                txt_hora.setText(hora_seleccionada)
            }
        }

    }

    fun cargar_date_picker(button: EditText, boolean: Boolean) {
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

                    if (boolean) {
                        fecha_seleccionada = ("$year-$mes-$dia")
                    }

                    button.setText("$year-$mes-$dia")
                    fecha_seleccionada = ("$year-$mes-$dia")
                    dia_fecha = dayOfMonth.toString()
                    mes_fecha = (month + 1).toString()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )

        datePickerDialog.setMinDate(2019,1,1)
        datePickerDialog.setMaxDate(2050,12,1)

        datePickerDialog.show()
    }

    fun cargar_time_picker(button: EditText) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                button.setText("$h : $m ")

            }), hour, minute, false)
        tpd.show()
    }



    object companion {
        var data_bundle: Bundle? = null
        var evento: Evento = Evento()
        var id = ""

    }


    fun addEvento(fecha: String, hora:String, titulo:String,descripcion:String,color:String, estado:Int) {
        val dialog = cargando(this, "Registrando\nevento")
        val call: Call<Evento> = apiEVE!!.addEvento(fecha,hora,titulo,descripcion,color,estado)
        call.enqueue(object : Callback<Evento> {
            override fun onResponse(call: Call<Evento>, response: Response<Evento>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    /*(applicationContext as EventosAdministrador).getTodos("2019-01-01", "2050-12-31")*/
                    mensaje_verde(this@RGEvento, MENSAJE)
                    dialog.doDismiss()
                    finish()

                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGEvento,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Evento>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    fun updateEvento(id_evento: String, fecha: String, hora:String, titulo:String,descripcion:String,color:String) {
        val dialog = cargando(this, "Modificando\nevento")
        val call: Call<Evento> = apiEVE!!.updateEvento(id_evento,fecha,hora,titulo,descripcion,color)
        call.enqueue(object : Callback<Evento> {
            override fun onResponse(call: Call<Evento>, response: Response<Evento>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    mensaje_verde(this@RGEvento, MENSAJE)
                    /*(applicationContext as EventosAdministrador).getTodos("2019-01-01", "2050-12-31")*/
                    dialog.doDismiss()
                    finish()


                } else {
                    dialog.doDismiss()
                    mensaje_rojo(this@RGEvento,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Evento>, t: Throwable){
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

}

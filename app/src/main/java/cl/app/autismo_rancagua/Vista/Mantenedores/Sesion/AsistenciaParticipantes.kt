package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Participante.Asistencia
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.DatePicker.EightFoldsDatePickerDialog
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciaParticipantes.companion.fecha_seleccionada
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciaParticipantes.companion.lista_presentes
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.data
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.id_sesion
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.NiñosParticipantes.companion.niño
import kotlinx.android.synthetic.main.asistencia_activity.*
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.toolbar_mantenedor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class AsistenciaParticipantes : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    var adaptadorAsistenciaParticipantes: AdaptadorAsistenciaParticipantes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.asistencia_activity)


        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        txt_buscar.visibility = View.GONE
        btn_registrar.visibility = View.GONE
        btn_asistencia.visibility = View.GONE


        bundle()

        actualizar.setOnRefreshListener {
            if(txt_fecha.text.toString().isNotEmpty()){
                if (!niño.isNullOrEmpty()) {
                    niño.clear()
                    actualizar.isRefreshing = true
                    adaptadorAsistenciaParticipantes!!.limpiar()
                    getParticipantes(id_sesion)
                    actualizar.isRefreshing = false
                } else {
                    actualizar.isRefreshing = true
                    getParticipantes(id_sesion)
                    actualizar.isRefreshing = false
                }
            }else{
                mensaje_rojo(this,"Debe seleccionar una fecha.")
            }

        }


        btn_seleccionar_fecha.setOnClickListener {
            cargar_date_picker(btn_seleccionar_fecha)
        }


        btn_volver.setOnClickListener {
            adaptadorAsistenciaParticipantes = null
            finish()
        }


        txt_fecha.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isNotEmpty()){
                    getParticipantes(id_sesion)
                }
            }
        })


    }

    fun bundle() {
        data = intent.extras
        if (data != null) {

            if (data!!.containsKey("id_sesion")) {
                id_sesion = data!!.getString("id_sesion")!!
                if(id_sesion.isNotEmpty()){
                    if(txt_fecha.text.toString().isNotEmpty()){
                        getParticipantes(id_sesion)
                    }else{
                        mensaje_rojo(this,"Debe seleccionar una fecha.")
                    }

                }

            }
        }
    }

    override fun onBackPressed() {
        adaptadorAsistenciaParticipantes = null
        finish()
    }

    override fun onResume() {
        super.onResume()
        if(txt_fecha.text.toString().isNotEmpty()){
            getParticipantes(id_sesion)
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

                   /* button.text = ("$year-$mes-$dia")*/
                    fecha_seleccionada = ("$year-$mes-$dia")
                    txt_fecha.setText(fecha_seleccionada)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )

        datePickerDialog.setMinDate(2019,1,1)
        datePickerDialog.setMaxDate(2050,12,31)

        datePickerDialog.show()
    }

    fun getParticipantes(id_sesion:String) {
        val dialog  = cargando(this, "Cargando\nparticipantes")

        val call: Call<ArrayList<Participante>> = apiSSN!!.getParticipantesXIDSSN(id_sesion)
        call.enqueue(object : Callback<ArrayList<Participante>> {
            override fun onResponse(call: Call<ArrayList<Participante>>, response: Response<ArrayList<Participante>>) {
                niño = response.body()!!

                if(niño.isEmpty()){
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    sub_titulo_vacia.text = ("No hay niños participantes en tu lista.\nVuelve y agrega niños a la sesion.")
                    txt_fecha.visibility = View.GONE
                    btn_seleccionar_fecha.visibility = View.GONE
                    textView8.visibility = View.GONE
                    imagen_lista_vacia.startAnimation(stb)

                }else{
                    vacia.visibility = View.GONE
                    btn_asistencia.visibility = View.VISIBLE
                    adaptadorAsistenciaParticipantes =
                        AdaptadorAsistenciaParticipantes(
                            niño,
                            this@AsistenciaParticipantes,
                            apiSSN!!,
                            btn_asistencia,
                            txt_fecha,
                            lista_presentes
                        )
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    lista_recyclerview.layoutManager = mLayoutManager
                    lista_recyclerview.itemAnimator = DefaultItemAnimator()
                    lista_recyclerview.adapter = adaptadorAsistenciaParticipantes
                    adaptadorAsistenciaParticipantes!!.notifyDataSetChanged()

                }
                dialog!!.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Participante>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }

    object companion {
        var data: Bundle? = null
        var niño: ArrayList<Participante> = arrayListOf()
        var id_sesion: String = ""
        var fecha_seleccionada = ""

        var lista_presentes: ArrayList<Asistencia> = arrayListOf()

    }

}

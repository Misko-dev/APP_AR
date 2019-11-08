package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Sesion.Sesion
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.ISchedule
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.Schedule
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.HorarioActivity.companion.data_bundle
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.HorarioActivity.companion.sesiones
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.lista_vacia.*
import kotlinx.android.synthetic.main.horario_activity.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.*


class HorarioActivity : AppCompatActivity() {


    var apiSSN: ApiSSN? = null
    private var weekList: ArrayList<Int>? = null
    var dia_horas_utilizadas = mutableMapOf<Int, List<Int>>()
    var dia_horas_disponibles = mutableMapOf<Int, List<Int>>()
    var horas_disponibles = listOf<Int>()
    var id_profesional: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.horario_activity)

        apiSSN = ApiClient.retrofit!!.create(ApiSSN::class.java)
        txt_buscar.visibility = View.GONE
        bundle()



        btn_registrar_evento.setOnClickListener {
            val intent = Intent(this, RGSesion::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_profesional", id_profesional)
            startActivity(intent)
        }



        timetable.scheduleManager.onItemClickListener =
            ISchedule.OnItemClickListener { v, scheduleList ->
                for (item in scheduleList) {
                    val id_sesion = item.teacher
                    val estado = item.estado
                    cargar_menu(id_sesion, estado)
                }
            }


        btn_volver_tbv.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        /* if(adaptadorMantenedorCuentas != null){
             adaptadorMantenedorCuentas!!.limpiar()*/
        getHorario(id_profesional)
        /* }*/
    }

    private fun bundle() {
        data_bundle = intent.extras
        if (data_bundle != null) {

            if (data_bundle!!.containsKey("id_profesional")) {

                id_profesional = data_bundle!!.getString("id_profesional")!!
                if(id_profesional.isNotEmpty()){
                    getHorario(id_profesional)
                }
            }

        }
    }

    fun getHorario(id_profesional: String) {
        val dialog = cargando(this, "Cargando\nhorario")
        val call: Call<ArrayList<Sesion>> = apiSSN!!.getSesionXID(id_profesional)
        call.enqueue(object : Callback<ArrayList<Sesion>> {
            override fun onResponse(
                call: Call<ArrayList<Sesion>>,
                response: Response<ArrayList<Sesion>>
            ) {
                sesiones = response.body()!!

                if (sesiones.isEmpty()) {
                    val stb = AnimationUtils.loadAnimation(applicationContext, R.anim.stb)
                    vacia.visibility = View.VISIBLE
                    imagen_lista_vacia.startAnimation(stb)

                } else {
                    vacia.visibility = View.GONE

                    for (i in 0..4) {
                        weekList = ArrayList()
                        weekList!!.add(1)
                    }

                    val lista = arrayListOf<Schedule>()
                    dia_horas_utilizadas.clear()


                    for (item in sesiones) {

                        val id_sesion = item.ID_SESION
                        item.ID_PROFESIONAL
                        item.DESCRIPCION

                        val dia = item.DIA
                        val hora = item.HORA
                        val duracion = item.DURACION

                        lista.add(
                            Schedule(
                                item.NOMBRE,
                                item.SALA,
                                id_sesion,
                                item.ESTADO,
                                weekList,
                                hora,
                                duracion,
                                dia,
                                1,
                                ""
                            )
                        )


                        val hora2 = item.HORA + 7

                        for (i in 0 until duracion) {
                            val contains = dia_horas_utilizadas.keys.any { key ->
                                key == dia
                            }
                            if (contains) {
                                dia_horas_utilizadas[dia] =
                                    dia_horas_utilizadas[dia]!!.plus(listOf(hora2 + i))

                            } else {
                                dia_horas_utilizadas[dia] = listOf(hora2 + i)
                            }
                        }


                    }


                    val horas_totales = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                    for (i in 1..7) {
                        /* Log.e("chup", "DIA $i-------------------------------------------------->")*/
                        dia_horas_utilizadas[i]?.let { horas ->
                            /*for (element in horas) {
                                Log.e("chup", "DIA:$i HORAS: $element")
                            }*/

                            horas_disponibles = horas_totales.minus(horas)
                            /*for (element in horas_disponibles) {
                                Log.e("chup", "HORAS: $element")
                            }

                            Log.e("chup", "DIA $i-------------------------------------------------->")*/

                            val contains = dia_horas_disponibles.keys.any { key ->
                                key == i
                            }
                            if (contains) {
                                dia_horas_disponibles[i] =
                                    dia_horas_disponibles[i]!!.plus(horas_disponibles)

                            } else {
                                dia_horas_disponibles[i] = horas_disponibles


                            }
                        }
                    }


                    timetable.setData(lista)
                        .setCurTerm("")//Establecer el semestre
                        .showView()
                }
                dialog.doDismiss()
            }


            override fun onFailure(call: Call<ArrayList<Sesion>>, t: Throwable) {
                Log.e("ERROR", "Error : $t")
                dialog.doDismiss()
            }
        })
    }

    fun updateEstado(id_sesion: String, estado: Int) {
        val dialog = cargando(this, "Modificando\nestado")
        val call: Call<Sesion> = apiSSN!!.updateEstado(id_sesion, estado)
        call.enqueue(object : Callback<Sesion> {
            override fun onResponse(call: Call<Sesion>, response: Response<Sesion>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    getHorario(id_profesional)
                    dialog.doDismiss()
                    mensaje_verde(this@HorarioActivity, MENSAJE)
                } else {
                    getHorario(id_profesional)
                    dialog.doDismiss()
                    mensaje_rojo(this@HorarioActivity, MENSAJE)
                }
            }

            override fun onFailure(call: Call<Sesion>, t: Throwable) {
                getHorario(id_profesional)
                dialog.doDismiss()
                Log.e("ERROR", "Error : $t")
            }
        })
    }

    @SuppressLint("InflateParams")
    fun cargar_menu(id_sesion: String, estado: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.menu_horario_activity, null)
        val dialog = BottomSheetDialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val btn_niños = view.findViewById(R.id.btn_niños) as ConstraintLayout
        val titleitemone = view.findViewById(R.id.titleitemone) as TextView
        val subtitleitemone2 = view.findViewById(R.id.subtitleitemone2) as TextView

        /* getAdministradorXID1(id_profesional,subtitleitemone3)*/
        titleitemone.setText("Editar sesion")

        when (estado) {
            1 -> {
                subtitleitemone2.text = ("Deshabilitar ahora")
                subtitleitemone2.setTextColor(Color.RED)
            }
            0 -> {
                subtitleitemone2.text = ("Habilitar ahora")
                subtitleitemone2.setTextColor(Color.GREEN)
            }
        }

        btn_editar.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, RGSesion::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion_registrada", id_sesion)
            intent.putExtra("horas_disponibles", dia_horas_utilizadas as Serializable)
            startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1 -> {
                    updateEstado(id_sesion, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_sesion, 1)
                    dialog.dismiss()
                }
            }
        }

        btn_niños.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, NiñosParticipantes::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_sesion", id_sesion)
            startActivity(intent)
        }

        val bottomSheet =
            dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    object companion {
        var data_bundle: Bundle? = null
        var sesiones: ArrayList<Sesion> = arrayListOf()

    }


}

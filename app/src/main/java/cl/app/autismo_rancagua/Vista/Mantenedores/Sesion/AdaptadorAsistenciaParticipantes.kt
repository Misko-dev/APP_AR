package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Participante.Asistencia
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.Modelo.Resultado
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño.companion.texto_busqueda
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.AsistenciaParticipantes.companion.fecha_seleccionada
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.tapadoo.alerter.Alerter
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorAsistenciaParticipantes(
    private val listaNiño: ArrayList<Participante>,
    private val context: AppCompatActivity,
    private val apiSSN: ApiSSN,
    private val btn_confirmar: ImageView,
    private val txt_fecha : EditText,
    var lista_presentes: ArrayList<Asistencia>
) : RecyclerView.Adapter<AdaptadorAsistenciaParticipantes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor_asistencia, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_niño = listaNiño[position]
        lista_presentes.clear()


        val ID_PARTICIPANTE = datos_niño.ID_PARTICIPANTE
        val ID_NIÑO = datos_niño.ID_NIÑO

        val RUT = datos_niño.RUT
        val NOMBRES = datos_niño.NOMBRES
        val APELLIDOS = datos_niño.APELLIDOS
        val URL_FOTO = datos_niño.URL_FOTO
        val ESTADO = datos_niño.ESTADO

        var ASISTENCIA = "Ausente"
        for (item in listaNiño) {
            lista_presentes.add(Asistencia(item.ID_PARTICIPANTE, fecha_seleccionada, ASISTENCIA))
        }


        holder.txt_titulo_mantenedor.text = ("$NOMBRES $APELLIDOS")
        holder.txt_subtitulo_mantenedor.text = (RUT)

        if (URL_FOTO != "null") {
            Glide.with(context)
                .load(URL_FOTO)
                .thumbnail(0.5f)
                .apply(
                    RequestOptions().override(0, 225).diskCacheStrategy(
                        DiskCacheStrategy.ALL
                    )
                )
                .into(holder.imagen_mantenedor)
        }


        holder.cb_ausente.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ASISTENCIA = "Presente"
                lista_presentes.removeAll { it.ID_PARTICIPANTE == ID_PARTICIPANTE }
                lista_presentes.add(Asistencia(ID_PARTICIPANTE, fecha_seleccionada, ASISTENCIA))
            } else {
                ASISTENCIA = "Ausente"
                lista_presentes.removeAll { it.ID_PARTICIPANTE == ID_PARTICIPANTE }
                lista_presentes.add(Asistencia(ID_PARTICIPANTE, fecha_seleccionada, ASISTENCIA))

            }
        }



        btn_confirmar.setOnClickListener {
            Alerter.create(context)
                .setTitle("Alerta")
                .setText("¿Desea registrar la asistencia?")
                .setBackgroundDrawable(context.resources.getDrawable(R.drawable.bgitem))
                .setIcon(R.drawable.ic_desconectar)
                .setIconColorFilter(0)
                .enableSwipeToDismiss()
                .addButton("SI", R.style.AlertButton, View.OnClickListener {
                    /*context.finish()*/
                    if(txt_fecha.text.toString().isEmpty()){
                        mensaje_rojo(context, "Para poder registrar las asistencias\ndebe seleccionar una fecha.")
                    }else{
                        for(item in lista_presentes){
                            addAsistencia(item.ID_PARTICIPANTE,item.FECHA,item.OBSERVACION)
                        }
                        context.finish()
                    }
                })
                .addButton("NO", R.style.AlertButton, View.OnClickListener {
                    Alerter.hide()
                })
                .show()
           /* for (item in lista_presentes) {
                Log.e("JAJAJAJAJAJA", item.ID_PARTICIPANTE +item.FECHA+ item.OBSERVACION)
            }
            Log.e("JAJAJAJAJAJA", "--------------------------------------->")*/
        }


    }

    fun limpiar() {
        listaNiño.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listaNiño.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var item_mantenedor: ConstraintLayout
        internal var txt_titulo_mantenedor: TextView
        internal var txt_subtitulo_mantenedor: TextView
        internal var imagen_mantenedor: CircleImageView


        internal var cb_ausente: CheckBox


        init {
            item_mantenedor = view.findViewById(R.id.item_mantenedor)
            txt_titulo_mantenedor = view.findViewById(R.id.txt_titulo_mantenedor)
            txt_subtitulo_mantenedor = view.findViewById(R.id.txt_subtitulo_mantenedor)
            imagen_mantenedor = view.findViewById(R.id.imagen_mantenedor)

            cb_ausente = view.findViewById(R.id.cb_ausente)

        }
    }


    fun addAsistencia(id_participante: String, fecha: String, observacion: String) {
        val dialog = cargando(context, "Registrando\nasistencia")
        val call: Call<Resultado> = apiSSN!!.addAsistencias(id_participante,fecha,observacion)
        call.enqueue(object : Callback<Resultado> {
            override fun onResponse(call: Call<Resultado>, response: Response<Resultado>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    mensaje_verde(context, MENSAJE)
                    dialog.doDismiss()

                } else {
                    dialog.doDismiss()
                    mensaje_rojo(context, MENSAJE)
                }
            }

            override fun onFailure(call: Call<Resultado>, t: Throwable) {
                dialog.doDismiss()
                Log.e("ERROR", "Error : $t")
            }
        })
    }

}

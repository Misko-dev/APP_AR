package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Api.Personas.ApiNNS
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Participante.Asistencia
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño.companion.texto_busqueda
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorAsistenciasParticipantes(
    private val listaAsistencias: ArrayList<Asistencia>,
    private val context: AppCompatActivity,
    private val apiSSN: ApiSSN
) : RecyclerView.Adapter<AdaptadorAsistenciasParticipantes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor_cuenta, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_asistencia = listaAsistencias[position]

        val ID_ASISTENCIA = datos_asistencia.ID_ASISTENCIA
        val ID_PARTICIPANTE = datos_asistencia.ID_PARTICIPANTE
        val FECHA = datos_asistencia.FECHA
        val OBSERVACION = datos_asistencia.OBSERVACION

        holder.txt_titulo_mantenedor.text = (FECHA)
        holder.txt_subtitulo_mantenedor.text = (OBSERVACION)



        holder.item_mantenedor.setOnClickListener {
            cargar_menu(ID_PARTICIPANTE)
        }

        var color = 0
        if (OBSERVACION == "Presente"){
            color = Color.GREEN
        }else{
            color = Color.RED
        }

        val drawable = TextDrawable.builder()
            .buildRound(OBSERVACION.substring(0,1), color)

        holder.imagen_mantenedor.setImageDrawable(drawable)

    }

    fun limpiar() {
        listaAsistencias.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listaAsistencias.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var item_mantenedor: ConstraintLayout
        internal var txt_titulo_mantenedor: TextView
        internal var txt_subtitulo_mantenedor: TextView
        internal var imagen_mantenedor: ImageView

        init {
            item_mantenedor = view.findViewById(R.id.item_mantenedor)
            txt_titulo_mantenedor = view.findViewById(R.id.txt_titulo_mantenedor)
            txt_subtitulo_mantenedor = view.findViewById(R.id.txt_subtitulo_mantenedor)
            imagen_mantenedor = view.findViewById(R.id.imagen_mantenedor)
        }
    }




    @SuppressLint("InflateParams")
    private fun cargar_menu(id_participante: String) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet1, null)
        val dialog = BottomSheetDialog(context)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val titleitemone = view.findViewById(R.id.titleitemone) as TextView

        titleitemone.text = ("Editar registro")


        btn_editar.setOnClickListener {
            
        }


        btn_estado.setOnClickListener {

        }







        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}

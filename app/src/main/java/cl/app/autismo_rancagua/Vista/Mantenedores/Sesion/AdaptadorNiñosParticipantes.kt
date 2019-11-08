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
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.MNiño.companion.texto_busqueda
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


class AdaptadorNiñosParticipantes(
    private val listaNiño: ArrayList<Participante>,
    private val context: AppCompatActivity,
    private val apiSSN: ApiSSN
) : RecyclerView.Adapter<AdaptadorNiñosParticipantes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_niño = listaNiño[position]

        val ID_PARTICIPANTE = datos_niño.ID_PARTICIPANTE
        val ID_NIÑO = datos_niño.ID_NIÑO

        val RUT = datos_niño.RUT
        val NOMBRES = datos_niño.NOMBRES
        val APELLIDOS = datos_niño.APELLIDOS
        val URL_FOTO = datos_niño.URL_FOTO
        val ESTADO = datos_niño.ESTADO

        holder.txt_titulo_mantenedor.text = ("$NOMBRES $APELLIDOS")
        holder.txt_subtitulo_mantenedor.text = (RUT)

        if(URL_FOTO!="null"){
            Glide.with(context)
                .load(URL_FOTO)
                .thumbnail(0.5f)
                .apply(
                    RequestOptions().override(0, 225).diskCacheStrategy(
                    DiskCacheStrategy.ALL))
                .into(holder.imagen_mantenedor)
        }

        holder.item_mantenedor.setOnClickListener {
            cargar_menu(ID_PARTICIPANTE,ESTADO)
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

        init {
            item_mantenedor = view.findViewById(R.id.item_mantenedor)
            txt_titulo_mantenedor = view.findViewById(R.id.txt_titulo_mantenedor)
            txt_subtitulo_mantenedor = view.findViewById(R.id.txt_subtitulo_mantenedor)
            imagen_mantenedor = view.findViewById(R.id.imagen_mantenedor)
        }
    }




    @SuppressLint("InflateParams")
    private fun cargar_menu(id_participante: String,estado: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet1, null)
        val dialog = BottomSheetDialog(context)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val btn_observacion = view.findViewById(R.id.btn_observacion) as ConstraintLayout
        val titleitemone = view.findViewById(R.id.titleitemone) as TextView

        btn_observacion.visibility = View.VISIBLE

        titleitemone.text = ("Ver asistencias")


        btn_editar.setOnClickListener {
            val intent = Intent(context, AsistenciasParticipante::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_participante", id_participante)
            context.startActivity(intent)
            dialog.dismiss()
        }


        btn_estado.setOnClickListener {

        }

        btn_observacion.setOnClickListener {

        }





        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}

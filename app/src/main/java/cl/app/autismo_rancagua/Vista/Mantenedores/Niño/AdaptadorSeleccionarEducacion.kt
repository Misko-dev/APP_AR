package cl.app.autismo_rancagua.Vista.Mantenedores.Niño

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Educacion.Educacion
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


class AdaptadorSeleccionarEducacion(
    private val listaEducacion: ArrayList<Educacion>,
    private var activity: Activity,
    private var context: Context


): RecyclerView.Adapter<AdaptadorSeleccionarEducacion.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mantenedor_cuenta, parent, false)


        return ViewHolder(view)
    }

    @SuppressLint("CheckResult", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val educacion = listaEducacion[position]

        val ID_EDUCACION_NIÑO = educacion.ID_EDUCACION_NIÑO
        val NOMBRE = educacion.NOMBRE

        holder.txt_titulo_mantenedor.text = (NOMBRE)
        holder.txt_subtitulo_mantenedor.visibility = View.GONE


        holder.item_mantenedor.setOnClickListener {
            val intent = Intent()
            intent.putExtra("educacion", educacion)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

        val drawable = TextDrawable.builder()
            .buildRound(NOMBRE.substring(0,2), ColorGenerator.MATERIAL.getColor(NOMBRE.substring(0,2)))

        holder.imagen_mantenedor.setImageDrawable(drawable)

    }

    override fun getItemCount(): Int {
        return listaEducacion.size
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


}

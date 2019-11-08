package cl.app.autismo_rancagua.Vista.Menus.Administrador

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Modelo.Evento.Evento
import cl.app.autismo_rancagua.Modelo.Sesion.Sesion
import cl.app.autismo_rancagua.R
import com.amulyakhare.textdrawable.TextDrawable


class AdaptadorHorarioTaller(
    val listaEventos: MutableList<Evento>,
    val context: Context

) : RecyclerView.Adapter<AdaptadorHorarioTaller.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mantenedor_cuenta, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taller = listaEventos[position]

        val ID_EVENTO = taller.ID_EVENTO
        val TITULO = taller.TITULO
        val FECHA_INICIAL = taller.FECHA
        val HORA = taller.HORA
        val COLOR = taller.COLOR

        holder.txt_texto_item_horario.text = (TITULO)
        holder.txt_segundo_texto_item_horario.text = (HORA)
        /*holder.btn_item_evento.background.setColorFilter(Color.parseColor(COLOR), PorterDuff.Mode.SRC_ATOP)*/

        val drawable = TextDrawable.builder()
            .buildRound(TITULO.substring(0,2), Color.parseColor(COLOR))
        holder.imagen_mantenedor.setImageDrawable(drawable)
    }

    fun limpiar() {
        listaEventos.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var txt_texto_item_horario: TextView
        internal var txt_segundo_texto_item_horario: TextView
        internal var btn_item_evento : ConstraintLayout
        internal var imagen_mantenedor: ImageView


        init {
            txt_texto_item_horario = view.findViewById(R.id.txt_titulo_mantenedor)
            txt_segundo_texto_item_horario = view.findViewById(R.id.txt_subtitulo_mantenedor)
            btn_item_evento= view.findViewById(R.id.item_mantenedor)
            imagen_mantenedor= view.findViewById(R.id.imagen_mantenedor)
        }
    }

}

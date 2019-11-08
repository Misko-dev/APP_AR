package cl.app.autismo_rancagua.Vista.Mantenedores.Niño

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Modelo.Colegio.Colegio
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.SeleccionarColegio.companion.texto_busqueda
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


class AdaptadorSeleccionarColegio(
    private val listaColegio: ArrayList<Colegio>,
    private var activity: Activity,
    private var context: Context


): RecyclerView.Adapter<AdaptadorSeleccionarColegio.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("CheckResult", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val colegio = listaColegio[position]

        val ID_COLEGIO = colegio.ID_COLEGIO
        val ID_TIPO_COLEGIO = colegio.ID_TIPO_COLEGIO
        val NOMBRE = colegio.NOMBRE
        val TELEFONO = colegio.TELEFONO
        val CORREO = colegio.CORREO
        val DIRECCION = colegio.DIRECCION
        val DIRECTOR = colegio.DIRECTOR
        val WEB = colegio.WEB
        val URL_FOTO = colegio.URL_FOTO

        holder.txt_titulo_mantenedor.text = (NOMBRE)
        holder.txt_subtitulo_mantenedor.text = ("WEB: $WEB\nFONO: $TELEFONO")



        holder.item_mantenedor.setOnClickListener {
            texto_busqueda = ""
            val intent = Intent()
            intent.putExtra("colegio", colegio)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }


        if(URL_FOTO!="null"){
            Glide.with(context)
                .load(URL_FOTO)
                .thumbnail(0.5f).fitCenter()
                .apply(
                    RequestOptions().override(0, 100).diskCacheStrategy(
                        DiskCacheStrategy.ALL))
                .into(holder.imagen_mantenedor)
        }

    }

    override fun getItemCount(): Int {
        return listaColegio.size
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


}

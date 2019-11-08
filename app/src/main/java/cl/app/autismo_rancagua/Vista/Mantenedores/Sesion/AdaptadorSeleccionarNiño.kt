package cl.app.autismo_rancagua.Vista.Mantenedores.Sesion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Api.Sesiones.ApiSSN
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Resultado
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tapadoo.alerter.Alerter
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorSeleccionarNiño(
    private val listaNiño: ArrayList<Niño>,
    private val context: AppCompatActivity,
    private val apiSSN: ApiSSN,
    var id_sesion : String
) : RecyclerView.Adapter<AdaptadorSeleccionarNiño.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_niño = listaNiño[position]


        val ID_NIÑO = datos_niño.ID_NIÑO
        val ID_TUTOR = datos_niño.ID_TUTOR
        val ID_COLEGIOS = datos_niño.ID_COLEGIO
        val ID_EDUCACION_NIÑO = datos_niño.ID_EDUCACION_NIÑO


        val RUT = datos_niño.RUT
        val NOMBRES = datos_niño.NOMBRES
        val APELLIDOS = datos_niño.APELLIDOS
        val EDAD = datos_niño.EDAD
        val FECHA_NACIMIENTO = datos_niño.FECHA_NACIMIENTO
        val SEXO = datos_niño.SEXO
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
            Alerter.create(context)
                .setTitle("Alerta")
                .setText("¿Desea agregar el niño seleccionado a la sesion?")
                .setBackgroundDrawable(context.resources.getDrawable(R.drawable.bgitem))
                .setIcon(R.drawable.ic_desconectar)
                .setIconColorFilter(0)
                .enableSwipeToDismiss()
                .addButton("SI", R.style.AlertButton, View.OnClickListener {
                    /*context.finish()*/
                    addNiño(id_sesion,ID_NIÑO)
                })
                .addButton("NO", R.style.AlertButton, View.OnClickListener {
                    Alerter.hide()
                })
                .show()
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


    fun addNiño(
        id_sesion: String,
        id_niño : String

    ) {
        val dialog = cargando(context, "Agregando\nparticipante")
        val call: Call<Resultado> = apiSSN!!.addNiño(id_sesion,id_niño)
        call.enqueue(object : Callback<Resultado> {
            override fun onResponse(call: Call<Resultado>, response: Response<Resultado>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE

                if (VALOR == "1") {
                    mensaje_verde(context, MENSAJE)
                    dialog.doDismiss()
                    context.finish()

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

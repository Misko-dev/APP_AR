package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


class AdaptadorSeleccionarProfesional(
    private val listaProfesionales: ArrayList<Profesional>,
    private var activity: Activity,
    private var context: Context,
    private var vista: View,
    private var circleImageView: CircleImageView,
    var texto1: TextView, var texto2: TextView, var texto3: TextView, var texto4: TextView, var texto5: TextView


): RecyclerView.Adapter<AdaptadorSeleccionarProfesional.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_seleccion_usuarios, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("CheckResult", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val profesional = listaProfesionales[position]

        val NOMBRES = profesional.NOMBRES
        val APELLIDOS = profesional.APELLIDOS
        val RUT = profesional.RUT
        val FECHA_NACIMIENTO = profesional.FECHA_NACIMIENTO
        val EDAD = profesional.EDAD
        val TELEFONO = profesional.TELEFONO
        val DIRECCION = profesional.DIRECCION
        val URL_FOTO = profesional.URL_FOTO


        holder.txt_nombre_usuario.text = ("$NOMBRES $APELLIDOS")
        holder.txt_sub_usuario.text = (RUT)

        holder.btn_item_ver_usuario.setOnClickListener {
            vista.visibility = View.VISIBLE

            texto1.text = ("$NOMBRES $APELLIDOS.")
            texto2.text = ("$RUT.")
            texto3.text = ("$FECHA_NACIMIENTO, $EDAD años.")
            texto4.text = ("$DIRECCION.")
            texto5.text = ("$TELEFONO.")

            if(URL_FOTO!="null"){
                Glide.with(context)
                    .load(URL_FOTO)
                    .thumbnail(0.5f)
                    .apply(
                        RequestOptions().override(0, 225).diskCacheStrategy(
                            DiskCacheStrategy.ALL))
                    .into(circleImageView)
            }
        }

        holder.btn_item_seleccionar_usuario.setOnClickListener {
            val intent = Intent()
            intent.putExtra("profesional", profesional)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

    }

    override fun getItemCount(): Int {
        return listaProfesionales.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var txt_nombre_usuario: TextView
        var txt_sub_usuario: TextView
        var btn_item_seleccionar_usuario : ImageButton
        var btn_item_ver_usuario : ImageButton

        init {
            txt_nombre_usuario = view.findViewById(R.id.txt_nombre_usuario)
            txt_sub_usuario = view.findViewById(R.id.txt_sub_usuario)
            btn_item_seleccionar_usuario = view.findViewById(R.id.btn_item_seleccionar_usuario)
            btn_item_ver_usuario = view.findViewById(R.id.btn_item_ver_usuario)
        }
    }


}

package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


class AdaptadorSeleccionarAdministrador(
    private val listaAdministradores: ArrayList<Administrador>,
    private var activity: Activity,
    private var context: Context,
    private var vista: View,
    private var circleImageView: CircleImageView,
    var texto1: TextView, var texto2: TextView, var texto3: TextView, var texto4: TextView, var texto5: TextView


): RecyclerView.Adapter<AdaptadorSeleccionarAdministrador.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_seleccion_usuarios, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("CheckResult", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val administrador = listaAdministradores[position]

        val NOMBRES = administrador.NOMBRES
        val APELLIDOS = administrador.APELLIDOS
        val RUT = administrador.RUT
        val FECHA_NACIMIENTO = administrador.FECHA_NACIMIENTO
        val EDAD = administrador.EDAD
        val TELEFONO = administrador.TELEFONO
        val DIRECCION = administrador.DIRECCION
        val URL_FOTO = administrador.URL_FOTO


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
            intent.putExtra("administrador", administrador)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

    }

    override fun getItemCount(): Int {
        return listaAdministradores.size
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

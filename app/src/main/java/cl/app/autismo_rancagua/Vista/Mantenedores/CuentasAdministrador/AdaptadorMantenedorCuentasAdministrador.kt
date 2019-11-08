package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador

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
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaAdm
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.MCuentasAdministrador.companion.id_administrador
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator.MATERIAL
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


class AdaptadorMantenedorCuentasAdministrador(
    private val listaCuentas: ArrayList<CuentaAdministrador>,
    private val context: AppCompatActivity,
    private val apiCtaAdm: ApiCtaAdm
) : RecyclerView.Adapter<AdaptadorMantenedorCuentasAdministrador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor_cuenta, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_cuenta = listaCuentas[position]


        val ID_CUENTA_ADMINISTRADOR = datos_cuenta.ID_CUENTA_ADMINISTRADOR
        val ID_ADMINISTRADOR = datos_cuenta.ID_ADMINISTRADOR
        val CORREO = datos_cuenta.CORREO
        val ESTADO = datos_cuenta.ESTADO


        holder.txt_titulo_mantenedor.text = (CORREO)


        if(ESTADO == 1){
            holder.txt_subtitulo_mantenedor.text = ("Habilitada")
        }else{
            holder.txt_subtitulo_mantenedor.text = ("Deshabilitada")
        }

        holder.item_mantenedor.setOnClickListener {
            cargar_menu(ID_CUENTA_ADMINISTRADOR,datos_cuenta,ESTADO)
        }

        val drawable = TextDrawable.builder()
            .buildRound(CORREO.substring(0,2), MATERIAL.getColor(CORREO.substring(0,2)))

        holder.imagen_mantenedor.setImageDrawable(drawable)

    }

    fun limpiar() {
        listaCuentas.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listaCuentas.size
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





    fun updateEstado( id_cuenta_administrador: String, estado: Int) {
        val dialog = cargando(context, "Modificando\nestado")
        val call: Call<CuentaAdministrador> = apiCtaAdm.updateEstado(id_cuenta_administrador, estado)
        call.enqueue(object : Callback<CuentaAdministrador> {
            override fun onResponse(call: Call<CuentaAdministrador>, response: Response<CuentaAdministrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    (context as MCuentasAdministrador).getCuentas(id_administrador)
                    dialog.doDismiss()
                    mensaje_verde(context, MENSAJE)
                } else {
                    (context as MCuentasAdministrador).getCuentas(id_administrador)
                    dialog.doDismiss()
                    mensaje_rojo(context,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaAdministrador>, t: Throwable) {
                (context as MCuentasAdministrador).getCuentas(id_administrador)
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }



    @SuppressLint("InflateParams")
    private fun cargar_menu(id_cuenta_administrador: String,cuentaAdministrador: CuentaAdministrador,estado: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet1, null)
        val dialog = BottomSheetDialog(context)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val subtitleitemone2 = view.findViewById(R.id.subtitleitemone2) as TextView

        when (estado) {
            1-> {
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
            val intent = Intent(context, RGCuentasAdministrador::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_cuenta_registrada", id_cuenta_administrador)
            intent.putExtra("cuenta", cuentaAdministrador)
            context.startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1-> {
                    updateEstado(id_cuenta_administrador, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_cuenta_administrador, 1)
                    dialog.dismiss()
                }
            }
        }

        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}

package cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional

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
import cl.app.autismo_rancagua.Api.Cuentas.ApiCtaPro
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.HorarioActivity
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorMantenedorCuentasProfesional(
    private val listaCuentas: ArrayList<CuentaProfesional>,
    private val context: AppCompatActivity,
    private val apiCtaPro: ApiCtaPro
) : RecyclerView.Adapter<AdaptadorMantenedorCuentasProfesional.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor_cuenta, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_cuenta = listaCuentas[position]


        val ID_CUENTA_PROFESIONAL = datos_cuenta.ID_CUENTA_PROFESIONAL
        val ID_PROFESIONAL = datos_cuenta.ID_PROFESIONAL
        val CORREO = datos_cuenta.CORREO
        val ESTADO = datos_cuenta.ESTADO


        holder.txt_titulo_mantenedor.text = (CORREO)


        if(ESTADO == 1){
            holder.txt_subtitulo_mantenedor.text = ("Habilitada")
        }else{
            holder.txt_subtitulo_mantenedor.text = ("Deshabilitada")
        }

        holder.item_mantenedor.setOnClickListener {
            cargar_menu(ID_CUENTA_PROFESIONAL,datos_cuenta,ESTADO)
        }

        val drawable = TextDrawable.builder()
            .buildRound(CORREO.substring(0,2), ColorGenerator.MATERIAL.getColor(CORREO.substring(0,2)))

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



    fun updateEstado( id_cuenta_profesional: String, estado: Int) {
        val dialog = cargando(context, "Modificando\nestado")
        val call: Call<CuentaProfesional> = apiCtaPro.updateEstado(id_cuenta_profesional, estado)
        call.enqueue(object : Callback<CuentaProfesional> {
            override fun onResponse(call: Call<CuentaProfesional>, response: Response<CuentaProfesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    (context as MCuentasProfesional).getCuentas()
                    dialog.doDismiss()
                    mensaje_verde(context, MENSAJE)
                } else {
                    (context as MCuentasProfesional).getCuentas()
                    dialog.doDismiss()
                    mensaje_rojo(context,MENSAJE)
                }
            }

            override fun onFailure(call: Call<CuentaProfesional>, t: Throwable) {
                (context as MCuentasProfesional).getCuentas()
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }



    @SuppressLint("InflateParams")
    private fun cargar_menu(id_cuenta_profesional: String,cuentaProfesional: CuentaProfesional,estado: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet1, null)
        val dialog = BottomSheetDialog(context)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val btn_horario = view.findViewById(R.id.btn_indicador) as ConstraintLayout
        val btn_cuenta = view.findViewById(R.id.btn_cuenta) as ConstraintLayout
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
            val intent = Intent(context, RGCuentasProfesional::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_cuenta_registrada", id_cuenta_profesional)
            intent.putExtra("cuenta", cuentaProfesional)
            context.startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1-> {
                    updateEstado(id_cuenta_profesional, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_cuenta_profesional, 1)
                    dialog.dismiss()
                }
            }
        }

        btn_cuenta.setOnClickListener {
            dialog.dismiss()
            context.startActivity(Intent(context, MCuentasProfesional::class.java))
        }

        btn_horario.setOnClickListener {
            dialog.dismiss()
            context.startActivity(Intent(context, HorarioActivity::class.java))
        }

        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }
}

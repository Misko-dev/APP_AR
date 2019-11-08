package cl.app.autismo_rancagua.Vista.Mantenedores.Tutor

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
import cl.app.autismo_rancagua.Api.Personas.ApiTTR
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.MTutor.companion.texto_busqueda
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


class AdaptadorMantenedorTutor(
    private val listaTutor: ArrayList<Tutor>,
    private val context: AppCompatActivity,
    private val apiTTR: ApiTTR
) : RecyclerView.Adapter<AdaptadorMantenedorTutor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_tutor = listaTutor[position]


        val ID_TUTOR = datos_tutor.ID_TUTOR
        val RUT = datos_tutor.RUT
        val NOMBRES = datos_tutor.NOMBRES
        val APELLIDOS = datos_tutor.APELLIDOS
        val EDAD = datos_tutor.EDAD
        val FECHA_NACIMIENTO = datos_tutor.FECHA_NACIMIENTO
        val SEXO = datos_tutor.SEXO
        val CORREO = datos_tutor.CORREO
        val TELEFONO = datos_tutor.TELEFONO
        val DIRECCION = datos_tutor.DIRECCION
        val SISTEMA_SALUD = datos_tutor.SISTEMA_SALUD
        val URL_FOTO = datos_tutor.URL_FOTO
        val ESTADO = datos_tutor.ESTADO

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
            cargar_menu(ID_TUTOR,datos_tutor,ESTADO)
        }
    }

    fun limpiar() {
        listaTutor.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listaTutor.size
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


    fun updateEstado(id_tutor: String, estado: Int) {
        val dialog = cargando(context, "Modificando\nestado")
        val call: Call<Tutor> = apiTTR.updateEstado(id_tutor, estado)
        call.enqueue(object : Callback<Tutor> {
            override fun onResponse(call: Call<Tutor>, response: Response<Tutor>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    (context as MTutor).getTutores("tutores","")
                    texto_busqueda = ""
                    dialog.doDismiss()
                    mensaje_verde(context, MENSAJE)
                } else {
                    texto_busqueda = ""
                    (context as MTutor).getTutores("tutores","")
                    dialog.doDismiss()
                    mensaje_rojo(context,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Tutor>, t: Throwable) {
                texto_busqueda = ""
                (context as MTutor).getTutores("tutores","")
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }


    @SuppressLint("InflateParams")
    private fun cargar_menu(id_tutor: String,tutor: Tutor,estado: Int) {
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
            val intent = Intent(context, RGTutor::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_tutor_registrado", id_tutor)
            intent.putExtra("tutor", tutor)
            context.startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1-> {
                    updateEstado(id_tutor, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_tutor, 1)
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

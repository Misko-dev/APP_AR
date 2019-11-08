package cl.app.autismo_rancagua.Vista.Mantenedores.Profesional

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
import cl.app.autismo_rancagua.Api.Personas.ApiPRO
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasProfesional.RGCuentasProfesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.MProfesional.companion.texto_busqueda
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.HorarioActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorMantenedorProfesional(
    private val listaProfesional: ArrayList<Profesional>,
    private val context: AppCompatActivity,
    private val apiPRO: ApiPRO
) : RecyclerView.Adapter<AdaptadorMantenedorProfesional.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_profesional = listaProfesional[position]

        val ID_PROFESIONAL = datos_profesional.ID_PROFESIONAL
        val NOMBRES = datos_profesional.NOMBRES
        val APELLIDOS = datos_profesional.APELLIDOS
        val RUT = datos_profesional.RUT
        val FECHA_NACIMIENTO = datos_profesional.FECHA_NACIMIENTO
        val EDAD = datos_profesional.EDAD
        val TELEFONO = datos_profesional.TELEFONO
        val DIRECCION = datos_profesional.DIRECCION
        val URL_FOTO = datos_profesional.URL_FOTO
        val ESTADO = datos_profesional.ESTADO


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
            cargar_menu(ID_PROFESIONAL,datos_profesional,ESTADO)
        }
    }

    fun limpiar() {
        listaProfesional.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listaProfesional.size
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


    fun getProfesionalXID1(id_profesional: String,subtitleitemone3:TextView){
        val dialog = cargando(context, "Cargando\ndatos")
        val call: Call<ArrayList<CuentaProfesional>> = apiPRO.getCuentaXIDPRO(id_profesional)
        call.enqueue(object : Callback<ArrayList<CuentaProfesional>> {
            override fun onResponse(call: Call<ArrayList<CuentaProfesional>>, response: Response<ArrayList<CuentaProfesional>>){
                val admin = response.body()!!

                if (admin.isEmpty()) {
                    subtitleitemone3.setText("Crear cuenta ahora")
                }else{
                    subtitleitemone3.setText("Modificar cuenta ahora")
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<ArrayList<CuentaProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }


    fun getProfesionalXID2(id_profesional: String){
        val dialog = cargando(context, "Cargando\ndatos")
        val call: Call<ArrayList<CuentaProfesional>> = apiPRO.getCuentaXIDPRO(id_profesional)
        call.enqueue(object : Callback<ArrayList<CuentaProfesional>> {
            override fun onResponse(call: Call<ArrayList<CuentaProfesional>>, response: Response<ArrayList<CuentaProfesional>>){
                val prof = response.body()!!
                if (prof.isEmpty()){
                    val intent = Intent(context, RGCuentasProfesional::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("id_profesional", id_profesional)
                    context.startActivity(intent)

                }else{

                    for (item in prof){
                        val id = item.ID_CUENTA_PROFESIONAL
                        val id_admn = item.ID_PROFESIONAL
                        val correo = item.CORREO
                        val pass = item.CONTRASEÑA
                        val estado = item.ESTADO
                        val intent = Intent(context, RGCuentasProfesional::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("id_cuenta_registrada", item.ID_CUENTA_PROFESIONAL)
                        intent.putExtra("cuenta", CuentaProfesional(id,id_admn,correo,pass,estado))
                        context.startActivity(intent)
                    }

                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<ArrayList<CuentaProfesional>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })

    }

    fun updateEstado(id_profesional: String, estado: Int) {
        val dialog = cargando(context, "Modificando\nestado")
        val call: Call<Profesional> = apiPRO.updateEstado(id_profesional, estado)
        call.enqueue(object : Callback<Profesional> {
            override fun onResponse(call: Call<Profesional>, response: Response<Profesional>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    (context as MProfesional).getProfesionales("profesionales","")
                    texto_busqueda = ""
                    dialog.doDismiss()
                    mensaje_verde(context, MENSAJE)
                } else {
                    (context as MProfesional).getProfesionales("profesionales","")
                    texto_busqueda = ""
                    dialog.doDismiss()
                    mensaje_rojo(context,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Profesional>, t: Throwable) {
                (context as MProfesional).getProfesionales("profesionales","")
                texto_busqueda = ""
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun cargar_menu(id_profesional: String,profesional: Profesional,estado: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet1, null)
        val dialog = BottomSheetDialog(context)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(view)

        val btn_editar = view.findViewById(R.id.btn_editar) as ConstraintLayout
        val btn_estado = view.findViewById(R.id.btn_estado) as ConstraintLayout
        val btn_horario = view.findViewById(R.id.btn_indicador) as ConstraintLayout
        val btn_cuenta = view.findViewById(R.id.btn_cuenta) as ConstraintLayout
        val subtitleitemone2 = view.findViewById(R.id.subtitleitemone2) as TextView
        val subtitleitemone3 = view.findViewById(R.id.subtitleitemone3) as TextView

        btn_horario.visibility = View.VISIBLE
        btn_cuenta.visibility = View.VISIBLE

        getProfesionalXID1(id_profesional,subtitleitemone3)


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
            val intent = Intent(context, RGProfesional::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_profesional_registrado", id_profesional)
            intent.putExtra("profesional", profesional)
            context.startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1-> {
                    updateEstado(id_profesional, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_profesional, 1)
                    dialog.dismiss()
                }
            }
        }

        btn_cuenta.setOnClickListener {
            dialog.dismiss()
            getProfesionalXID2(id_profesional)
        }

        btn_horario.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(context, HorarioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_profesional", id_profesional)
            context.startActivity(intent)
        }

        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}

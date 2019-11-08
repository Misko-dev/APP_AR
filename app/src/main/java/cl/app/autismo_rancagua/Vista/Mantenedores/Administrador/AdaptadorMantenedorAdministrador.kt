package cl.app.autismo_rancagua.Vista.Mantenedores.Administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cl.app.autismo_rancagua.Api.Personas.ApiADM
import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Utilidades.mensaje_verde
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.MAdministrador.companion.texto_busqueda
import cl.app.autismo_rancagua.Vista.Mantenedores.CuentasAdministrador.RGCuentasAdministrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Sesion.HorarioActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdaptadorMantenedorAdministrador(
    private val listaAdministrador: ArrayList<Administrador>,
    private val context: AppCompatActivity,
    private val apiAdm: ApiADM
) : RecyclerView.Adapter<AdaptadorMantenedorAdministrador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mantenedor2, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datos_administrador = listaAdministrador[position]

        val ID_ADMINISTRADOR = datos_administrador.ID_ADMINISTRADOR
        val NOMBRES = datos_administrador.NOMBRES
        val APELLIDOS = datos_administrador.APELLIDOS
        val RUT = datos_administrador.RUT
        val FECHA_NACIMIENTO = datos_administrador.FECHA_NACIMIENTO
        val EDAD = datos_administrador.EDAD
        val TELEFONO = datos_administrador.TELEFONO
        val DIRECCION = datos_administrador.DIRECCION
        val URL_FOTO = datos_administrador.URL_FOTO
        val ESTADO = datos_administrador.ESTADO



        holder.txt_titulo_mantenedor.text = ("$NOMBRES $APELLIDOS")
        holder.txt_subtitulo_mantenedor.text = (RUT)

        holder.item_mantenedor.setOnClickListener {
            cargar_menu(ID_ADMINISTRADOR,datos_administrador,ESTADO)
        }

        if (URL_FOTO != "null") {
            Glide.with(context)
                .load(URL_FOTO)
                .thumbnail(0.5f)
                .apply(
                    RequestOptions().override(0, 225).diskCacheStrategy(
                        DiskCacheStrategy.ALL
                    )
                )
                .into(holder.imagen_mantenedor)
        }

    }

    fun limpiar() {
        listaAdministrador.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listaAdministrador.size
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


    fun getAdministradorXID1(id_administrador: String,subtitleitemone3:TextView){
        val dialog = cargando(context, "Cargando\ndatos")
        val call: Call<ArrayList<CuentaAdministrador>> = apiAdm.getCuentaXID(id_administrador)
        call.enqueue(object : Callback<ArrayList<CuentaAdministrador>> {
            override fun onResponse(call: Call<ArrayList<CuentaAdministrador>>, response: Response<ArrayList<CuentaAdministrador>>){
                val admin = response.body()!!

                if (admin.isEmpty()) {
                    subtitleitemone3.setText("Crear cuenta ahora")
                }else{
                    subtitleitemone3.setText("Modificar cuenta ahora")
                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<ArrayList<CuentaAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })
    }


    fun getAdministradorXID2(id_administrador: String){
        val dialog = cargando(context, "Cargando\ndatos")
        val call: Call<ArrayList<CuentaAdministrador>> = apiAdm.getCuentaXID(id_administrador)
        call.enqueue(object : Callback<ArrayList<CuentaAdministrador>> {
            override fun onResponse(call: Call<ArrayList<CuentaAdministrador>>, response: Response<ArrayList<CuentaAdministrador>>){
                val admin = response.body()!!
                if (admin.isEmpty()){
                    val intent = Intent(context, RGCuentasAdministrador::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("id_administrador", id_administrador)
                    context.startActivity(intent)
                }else{

                    for (item in admin){
                        val id = item.ID_CUENTA_ADMINISTRADOR
                        val id_admn = item.ID_ADMINISTRADOR
                        val correo = item.CORREO
                        val pass = item.CONTRASEÑA
                        val estado = item.ESTADO
                        val intent = Intent(context, RGCuentasAdministrador::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("id_cuenta_registrada", item.ID_CUENTA_ADMINISTRADOR)
                        intent.putExtra("cuenta", CuentaAdministrador(id,id_admn,correo,pass,estado))
                        context.startActivity(intent)
                    }

                }
                dialog.doDismiss()
            }

            override fun onFailure(call: Call<ArrayList<CuentaAdministrador>>, t: Throwable) {
                Log.e("ERROR","Error : $t")
                dialog.doDismiss()
            }
        })

    }

    fun updateEstado( id_administrador: String, estado: Int) {
        val dialog = cargando(context, "Modificando\nestado")
        val call: Call<Administrador> = apiAdm.updateEstado(id_administrador, estado)
        call.enqueue(object : Callback<Administrador> {
            override fun onResponse(call: Call<Administrador>, response: Response<Administrador>) {
                val VALOR: String = response.body()!!.VALOR
                val MENSAJE: String = response.body()!!.MENSAJE
                if (VALOR == "1") {
                    (context as MAdministrador).getAdministradores("administradores","")
                   texto_busqueda = ""
                    dialog.doDismiss()
                    mensaje_verde(context, MENSAJE)
                } else {
                    (context as MAdministrador).getAdministradores("administradores","")
                    texto_busqueda = ""
                    dialog.doDismiss()
                    mensaje_rojo(context,MENSAJE)
                }
            }

            override fun onFailure(call: Call<Administrador>, t: Throwable) {
                (context as MAdministrador).getAdministradores("administradores","")
                texto_busqueda = ""
                dialog.doDismiss()
                Log.e("ERROR","Error : $t")
            }
        })
    }


    @SuppressLint("InflateParams")
    fun cargar_menu(id_administrador: String,administrador: Administrador,estado: Int) {
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


        /*btn_horario.visibility = View.VISIBLE*/
        btn_cuenta.visibility = View.VISIBLE

        getAdministradorXID1(id_administrador,subtitleitemone3)

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
            val intent = Intent(context, RGAdministrador::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id_administrador_registrado", id_administrador)
            intent.putExtra("administrador", administrador)
            context.startActivity(intent)
        }

        btn_estado.setOnClickListener {
            when (estado) {
                1-> {
                    updateEstado(id_administrador, 0)
                    dialog.dismiss()
                }
                0 -> {
                    updateEstado(id_administrador, 1)
                    dialog.dismiss()
                }
            }
        }


        btn_cuenta.setOnClickListener {
            dialog.dismiss()
            getAdministradorXID2(id_administrador)
        }

        btn_horario.setOnClickListener {
            dialog.dismiss()
            context.startActivity(Intent(context, HorarioActivity::class.java))
        }

        val bottomSheet = dialog.window?.decorView!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = STATE_EXPANDED
        dialog.show()
    }

}

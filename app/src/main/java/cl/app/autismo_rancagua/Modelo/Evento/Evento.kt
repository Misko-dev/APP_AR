package cl.app.autismo_rancagua.Modelo.Evento

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Evento (
    val ID_EVENTO: String = "",
    val FECHA : Date = Date(),
    val HORA :String= "",
    val TITULO: String = "",
    val DESCRIPCION: String = "",
    val COLOR: String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
):Parcelable
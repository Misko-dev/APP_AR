package cl.app.autismo_rancagua.Modelo.Sesion

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Sesion (
    val ID_SESION: String = "",
    val ID_PROFESIONAL: String = "",
    val SALA: String = "",
    val NOMBRE: String = "",
    val DIA: Int = 0,
    val HORA: Int = 0,
    val DURACION: Int = 0,
    val DESCRIPCION: String = "",
    val TIPO: String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
):Parcelable
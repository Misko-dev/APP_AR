package cl.app.autismo_rancagua.Modelo.Tutor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Tutor(
    val ID_TUTOR: String = "",
    val RUT: String = "",
    val NOMBRES: String = "",
    val APELLIDOS: String = "",
    val EDAD: String = "",
    val FECHA_NACIMIENTO: String = "",
    val SEXO : String = "",
    val CORREO: String = "",
    val TELEFONO: String = "",
    val DIRECCION: String = "",
    val SISTEMA_SALUD : String = "",
    val URL_FOTO : String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
):Parcelable
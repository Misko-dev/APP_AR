package cl.app.autismo_rancagua.Modelo.Administrador

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Administrador(
    val ID_ADMINISTRADOR: String = "",
    val ID_CARGO_ADMINISTRADOR: String = "",
    val RUT: String = "",
    val NOMBRES: String = "",
    val APELLIDOS: String = "",
    val EDAD: String = "",
    val FECHA_NACIMIENTO: String = "",
    val SEXO: String = "",
    val CORREO: String = "",
    val TELEFONO: String = "",
    val DIRECCION: String = "",
    val URL_FOTO: String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
) : Parcelable



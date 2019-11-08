package cl.app.autismo_rancagua.Modelo.Colegio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Colegio(
    val ID_COLEGIO : String = "",
    val ID_TIPO_COLEGIO: String = "",
    val NOMBRE: String = "",
    val TELEFONO: String = "",
    val CORREO: String = "",
    val DIRECCION : String = "",
    val DIRECTOR : String = "",
    val WEB: String = "",
    val URL_FOTO: String = ""
):Parcelable
package cl.app.autismo_rancagua.Modelo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Resultado(
    val VALOR: String = "",
    val MENSAJE: String = ""
) : Parcelable
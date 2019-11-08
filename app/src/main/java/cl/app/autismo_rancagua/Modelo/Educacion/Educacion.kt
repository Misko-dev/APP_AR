package cl.app.autismo_rancagua.Modelo.Educacion

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Educacion(
    val ID_EDUCACION_NIÑO: String = "",
    val NOMBRE : String = ""
):Parcelable
package cl.app.autismo_rancagua.Modelo.Participante

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Observacion(
    val ID_PARTICIPANTE: String = "",
    val FECHA: String = "",
    var DESCRIPCION: String = ""
) : Parcelable
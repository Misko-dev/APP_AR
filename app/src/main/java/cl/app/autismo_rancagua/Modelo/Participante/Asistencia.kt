package cl.app.autismo_rancagua.Modelo.Participante

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asistencia(
    val ID_ASISTENCIA: String = "",
    val ID_PARTICIPANTE: String = "",
    val FECHA: String = "",
    var OBSERVACION: String = ""
) : Parcelable
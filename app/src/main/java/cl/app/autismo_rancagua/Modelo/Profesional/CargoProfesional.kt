package cl.app.autismo_rancagua.Modelo.Profesional

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CargoProfesional(
    val ID_CARGO_PROFESIONAL : String = "",
    val NOMBRE: String = ""
):Parcelable
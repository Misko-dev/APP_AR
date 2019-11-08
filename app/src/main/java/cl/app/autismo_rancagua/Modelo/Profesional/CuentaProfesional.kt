package cl.app.autismo_rancagua.Modelo.Profesional

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CuentaProfesional(
    val ID_CUENTA_PROFESIONAL: String = "",
    val ID_PROFESIONAL: String = "",
    val CORREO: String = "",
    val CONTRASEÑA: String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
) : Parcelable



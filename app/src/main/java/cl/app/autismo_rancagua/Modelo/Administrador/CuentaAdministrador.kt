package cl.app.autismo_rancagua.Modelo.Administrador

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CuentaAdministrador(
    val ID_CUENTA_ADMINISTRADOR: String = "",
    val ID_ADMINISTRADOR: String = "",
    val CORREO: String = "",
    val CONTRASEÑA: String = "",
    val ESTADO: Int = 0,
    val VALOR: String = "",
    val MENSAJE: String = ""
) : Parcelable



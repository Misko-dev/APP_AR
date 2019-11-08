package cl.app.autismo_rancagua.Modelo.Administrador

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CargoAdministrador(
    val ID_CARGO_ADMINISTRADOR : String = "",
    val NOMBRE: String = ""
):Parcelable
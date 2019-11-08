package cl.app.autismo_rancagua.Modelo.Indicador

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Indicador (
    val TOTAL_HABILITADOS: String = "",
    val TOTAL_REGISTROS :String = "",
    val KPI :String= ""
):Parcelable
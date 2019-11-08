package cl.app.autismo_rancagua.Modelo.Niño

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Niño(val ID_NIÑO: String = "",
                val ID_TUTOR: String = "",
                val ID_COLEGIO: String = "",
                val ID_EDUCACION_NIÑO: String = "",
                val RUT : String = "",
                val NOMBRES : String = "",
                val APELLIDOS: String = "",
                val EDAD : String = "",
                val FECHA_NACIMIENTO: String = "",
                val SEXO: String = "",
                val URL_FOTO : String = "",
                val ESTADO: Int = 0,
                val VALOR: String = "",
                val MENSAJE: String = ""
                ):Parcelable
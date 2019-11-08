package cl.app.autismo_rancagua.Utilidades

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

object HelperRegistros {

    @SuppressLint("NewApi")
    fun generar_fecha_nacimiento_nino(): LocalDate {
        val random = Random()
        val minDay = LocalDate.of(2001, 1, 1).toEpochDay().toInt()
        val maxDay = LocalDate.of(2018, 1, 1).toEpochDay().toInt()
        val randomDay = minDay + random.nextInt(maxDay - minDay).toLong()

        val randomBirthDate = LocalDate.ofEpochDay(randomDay)

        return randomBirthDate

    }

    @SuppressLint("NewApi")
    fun generar_fecha_nacimiento_adulto(): LocalDate {
        val random = Random()
        val minDay = LocalDate.of(1930, 1, 1).toEpochDay().toInt()
        val maxDay = LocalDate.of(2001, 1, 1).toEpochDay().toInt()
        val randomDay = minDay + random.nextInt(maxDay - minDay).toLong()

        val randomBirthDate = LocalDate.ofEpochDay(randomDay)

        return randomBirthDate

    }

    @SuppressLint("NewApi")
    fun obtener_edad(localDate: LocalDate):String{
        val today = LocalDate.now()
        val birthday = localDate
        val p = Period.between(birthday, today)
        return p.years.toString()
    }

    fun generar_telefono():String{
        val rand = Random()
        val num3 = rand.nextInt(10000)
        val num4 = rand.nextInt(10000)
        val df4 = DecimalFormat("0000") // 4 zeros
        val phoneNumber = "+569 "+ df4.format(num3)+df4.format(num4)
        return  phoneNumber
    }


}
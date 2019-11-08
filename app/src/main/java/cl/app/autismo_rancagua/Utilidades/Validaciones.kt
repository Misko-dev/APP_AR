package cl.app.autismo_rancagua.Utilidades

import android.text.TextUtils
import java.util.regex.Pattern

fun validarEmail(target: CharSequence): Boolean {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
}


fun validarRut(rut : String) : Boolean {
    if (!rut.matches(Regex("[0-9]{1,2}(.?[0-9]{3}){2}-?[0-9kK]"))) {
        return false
    }

    val rutf = rut.toLowerCase().replace(Regex("[-.]"), "")
    val rutl = rutf.takeLast(1); var sum = 0; var i = 0
    for (x in rutf.dropLast(1).reversed()){
        sum += (x.toString().toInt() * ((i % 6) + 2)); i+=1
    }

    val div = 11 - (sum % 11)
    return (if (div == 11) 0 else div) == (if (rutl == "k") 10 else rutl.toInt())
}

fun validarNombreApellido(valor: String): Boolean {
    val patron = Pattern.compile("^[a-zA-Z ]+$")
    return patron.matcher(valor).matches() || valor.length > 30
}
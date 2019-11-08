package cl.app.autismo_rancagua.Utilidades

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.v3.TipDialog
import com.kongzue.dialog.v3.WaitDialog
import es.dmoral.toasty.Toasty


fun cargando(context: AppCompatActivity,mensaje: String):TipDialog{
    val dialog =  WaitDialog.show(context, mensaje).setCancelable(false).setTheme(
        DialogSettings.THEME.LIGHT)

    return dialog
}


fun mensaje_verde (context: AppCompatActivity,mensaje:String){
    Toasty.success(context, mensaje, Toast.LENGTH_SHORT, false).show()
}


fun mensaje_azul(context: AppCompatActivity,mensaje:String){
    Toasty.info(context, mensaje, Toast.LENGTH_SHORT, false).show()
}

fun mensaje_rojo(context: AppCompatActivity,mensaje:String){
    Toasty.error(context, mensaje, Toast.LENGTH_SHORT, false).show()
}
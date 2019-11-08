package cl.app.autismo_rancagua.Api

import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import cl.app.autismo_rancagua.Utilidades.cargando
import cl.app.autismo_rancagua.Utilidades.mensaje_rojo
import cl.app.autismo_rancagua.Vista.Mantenedores.Administrador.RGAdministrador
import cl.app.autismo_rancagua.Vista.Mantenedores.Niño.RGNiño
import cl.app.autismo_rancagua.Vista.Mantenedores.Profesional.RGProfesional
import cl.app.autismo_rancagua.Vista.Mantenedores.Tutor.RGTutor
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


object CLImagenes {

    fun solicitarMultiplePermisos(context:Activity,context2:AppCompatActivity) {
        Dexter.withActivity(context)
            .withPermissions(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        /*mensaje_verde(context2,"Todos los permisos son otorgados por el usuario.")*/
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
               mensaje_rojo(context2,it.toString())
            }
            .onSameThread()
            .check()
    }


    fun galeria(codigo:Int,context2: AppCompatActivity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        context2.startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), codigo)
    }


    fun subirImagenNiño(context2: AppCompatActivity,filePath: Uri,storageReference: StorageReference,
                                interface_foto_perfil_niño: RGNiño.interface_foto_perfil_niño){
        if(filePath != null){

            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(context2.contentResolver, filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 40, baos)
            val dts = baos.toByteArray()



            val dialog = cargando(context2,"Subiendo imagen...")
            val ref = storageReference.child("niños/" + UUID.randomUUID().toString())
            val uploadTask = ref.putBytes(dts)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    interface_foto_perfil_niño.onSuccess(downloadUri.toString())
                    dialog.doDismiss()
                } else {
                    mensaje_rojo(context2,"Error al subir la imagen.")
                }
            }.addOnFailureListener{
                interface_foto_perfil_niño.onFail(it.toString())
            }
        }else{
            mensaje_rojo(context2,"Porfavor selecciona una imagen.")
        }
    }




    fun subirImagenTutor(context2: AppCompatActivity,filePath: Uri,storageReference: StorageReference,
                         interface_foto_perfil_tutor: RGTutor.interface_foto_perfil_tutor){
        if(filePath != null){

            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(context2.contentResolver, filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 40, baos)
            val dts = baos.toByteArray()



            val dialog = cargando(context2,"Subiendo imagen...")
            val ref = storageReference.child("tutores/" + UUID.randomUUID().toString())
            val uploadTask = ref.putBytes(dts)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    interface_foto_perfil_tutor.onSuccess(downloadUri.toString())
                    dialog.doDismiss()
                } else {
                    mensaje_rojo(context2,"Error al subir la imagen.")
                }
            }.addOnFailureListener{
                interface_foto_perfil_tutor.onFail(it.toString())
            }
        }else{
            mensaje_rojo(context2,"Porfavor selecciona una imagen.")
        }
    }




    fun subirImagenProfesional(context2: AppCompatActivity,filePath: Uri,storageReference: StorageReference,
                               interface_foto_perfil_profesional: RGProfesional.interface_foto_perfil_profesional){
        if(filePath != null){

            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(context2.contentResolver, filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 40, baos)
            val dts = baos.toByteArray()



            val dialog = cargando(context2,"Subiendo imagen...")
            val ref = storageReference.child("profesionales/" + UUID.randomUUID().toString())
            val uploadTask = ref.putBytes(dts)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    interface_foto_perfil_profesional.onSuccess(downloadUri.toString())
                    dialog.doDismiss()
                } else {
                    mensaje_rojo(context2,"Error al subir la imagen.")
                }
            }.addOnFailureListener{
                interface_foto_perfil_profesional.onFail(it.toString())
            }
        }else{
            mensaje_rojo(context2,"Porfavor selecciona una imagen.")
        }
    }



    fun subirImagenAdministrador(context2: AppCompatActivity,filePath: Uri,storageReference: StorageReference,
                                 interface_foto_perfil_administrador: RGAdministrador.interface_foto_perfil_administrador){
        if(filePath != null){

            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(context2.contentResolver, filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 40, baos)
            val dts = baos.toByteArray()



            val dialog = cargando(context2,"Subiendo imagen...")
            val ref = storageReference.child("administradores/" + UUID.randomUUID().toString())
            val uploadTask = ref.putBytes(dts)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    interface_foto_perfil_administrador.onSuccess(downloadUri.toString())
                    dialog.doDismiss()
                } else {
                    mensaje_rojo(context2,"Error al subir la imagen.")
                }
            }.addOnFailureListener{
                interface_foto_perfil_administrador.onFail(it.toString())
            }
        }else{
            mensaje_rojo(context2,"Porfavor selecciona una imagen.")
        }
    }

}
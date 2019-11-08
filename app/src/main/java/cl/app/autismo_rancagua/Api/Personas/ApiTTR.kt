package cl.app.autismo_rancagua.Api.Personas

import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import retrofit2.Call
import retrofit2.http.*


interface ApiTTR {

    @GET("TTR/getTutores.php")
    fun getTutores(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Tutor>>

    @FormUrlEncoded
    @POST("TTR/getTutorXID.php")
    fun getTutorXID(
        @Field("ID_TUTOR") ID_TUTOR: String
    ): Call<ArrayList<Tutor>>

    @FormUrlEncoded
    @POST("TTR/updateEstado.php")
    fun updateEstado(
        @Field("ID_TUTOR") ID_TUTOR: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Tutor>

    @FormUrlEncoded
    @POST("TTR/addTutor.php")
    fun addTutor(
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("CORREO") CORREO: String,
        @Field("TELEFONO") TELEFONO: String,
        @Field("DIRECCION") DIRECCION: String,
        @Field("SISTEMA_SALUD") SISTEMA_SALUD: String,
        @Field("URL_FOTO") URL_FOTO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Tutor>

    @FormUrlEncoded
    @POST("TTR/updateTutor.php")
    fun updateTutor(
        @Field("ID_TUTOR") ID_TUTOR: String,
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("CORREO") CORREO: String,
        @Field("TELEFONO") TELEFONO: String,
        @Field("DIRECCION") DIRECCION: String,
        @Field("SISTEMA_SALUD") SISTEMA_SALUD: String,
        @Field("URL_FOTO") URL_FOTO: String
    ): Call<Tutor>

}

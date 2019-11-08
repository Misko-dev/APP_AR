package cl.app.autismo_rancagua.Api.Personas

import cl.app.autismo_rancagua.Modelo.Colegio.Colegio
import cl.app.autismo_rancagua.Modelo.Educacion.Educacion
import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Tutor.Tutor
import retrofit2.Call
import retrofit2.http.*


interface ApiNNS {
    @GET("NNS/getNiños.php")
    fun getNiños(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Niño>>

    @FormUrlEncoded
    @POST("NNS/getNiñoXID.php")
    fun getNiñoXID(
        @Field("ID_NIÑO") ID_NIÑO: String
    ): Call<ArrayList<Niño>>

    @FormUrlEncoded
    @POST("NNS/updateEstado.php")
    fun updateEstado(
        @Field("ID_NIÑO") ID_NIÑO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Niño>

    @FormUrlEncoded
    @POST("NNS/addNiño.php")
    fun addNiño(
        @Field("ID_TUTOR") ID_TUTOR: String,
        @Field("ID_COLEGIO") ID_COLEGIO: String,
        @Field("ID_EDUCACION_NIÑO") ID_EDUCACION_NIÑO: String,
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("URL_FOTO") URL_FOTO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Niño>

    @FormUrlEncoded
    @POST("NNS/updateNiño.php")
    fun updateNiño(
        @Field("ID_NIÑO") ID_NIÑO: String,
        @Field("ID_TUTOR") ID_TUTOR: String,
        @Field("ID_COLEGIO") ID_COLEGIO: String,
        @Field("ID_EDUCACION_NIÑO") ID_EDUCACION_NIÑO: String,
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("URL_FOTO") URL_FOTO: String
    ): Call<Niño>


    @GET("NNS/getEducacion.php")
    fun getEducacion(): Call<ArrayList<Educacion>>


    @FormUrlEncoded
    @POST("NNS/getEducacionXID.php")
    fun getEducacionXID(
        @Field("ID_EDUCACION_NIÑO") ID_EDUCACION_NIÑO: String
    ): Call<ArrayList<Educacion>>


    @GET("NNS/getColegios.php")
    fun getColegios(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Colegio>>

    @FormUrlEncoded
    @POST("NNS/getColegioXID.php")
    fun getColegioXID(
        @Field("ID_COLEGIO") ID_COLEGIO: String
    ): Call<ArrayList<Colegio>>


    @GET("NNS/getTutores.php")
    fun getTutores(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Tutor>>

    @FormUrlEncoded
    @POST("NNS/getTutorXID.php")
    fun getTutorXID(
        @Field("ID_TUTOR") ID_TUTOR: String
    ): Call<ArrayList<Tutor>>

}

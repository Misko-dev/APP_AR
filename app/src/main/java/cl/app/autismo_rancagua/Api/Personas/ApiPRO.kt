package cl.app.autismo_rancagua.Api.Personas

import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CargoAdministrador
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import cl.app.autismo_rancagua.Modelo.Profesional.CargoProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import cl.app.autismo_rancagua.Modelo.Profesional.Profesional
import retrofit2.Call
import retrofit2.http.*


interface ApiPRO {
    @GET("PRO/getProfesionales.php")
    fun getProfesionales(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Profesional>>

    @FormUrlEncoded
    @POST("PRO/getProfesionalXID.php")
    fun getProfesionalXID(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String
    ): Call<ArrayList<Profesional>>

    @FormUrlEncoded
    @POST("PRO/updateEstado.php")
    fun updateEstado(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Profesional>

    @FormUrlEncoded
    @POST("PRO/addProfesional.php")
    fun addProfesional(
        @Field("ID_CARGO_PROFESIONAL") ID_CARGO_PROFESIONAL: String,
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("CORREO") CORREO: String,
        @Field("TELEFONO") TELEFONO: String,
        @Field("DIRECCION") DIRECCION: String,
        @Field("URL_FOTO") URL_FOTO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Profesional>

    @FormUrlEncoded
    @POST("PRO/updateProfesional.php")
    fun updateProfesional(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("ID_CARGO_PROFESIONAL") ID_CARGO_PROFESIONAL: String,
        @Field("RUT") RUT: String,
        @Field("NOMBRES") NOMBRES: String,
        @Field("APELLIDOS") APELLIDOS: String,
        @Field("EDAD") EDAD: String,
        @Field("FECHA_NACIMIENTO") FECHA_NACIMIENTO: String,
        @Field("SEXO") SEXO: String,
        @Field("CORREO") CORREO: String,
        @Field("TELEFONO") TELEFONO: String,
        @Field("DIRECCION") DIRECCION: String,
        @Field("URL_FOTO") URL_FOTO: String
    ): Call<Profesional>


    @GET("PRO/getCargos.php")
    fun getCargos(): Call<ArrayList<CargoProfesional>>


    @FormUrlEncoded
    @POST("PRO/getCargoXID.php")
    fun getCargoXID(
        @Field("ID_CARGO_PROFESIONAL") ID_CARGO_PROFESIONAL: String
    ): Call<ArrayList<CargoProfesional>>


    @FormUrlEncoded
    @POST("CTA-PRO/getCuentaXIDPRO.php")
    fun getCuentaXIDPRO(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String
    ): Call<ArrayList<CuentaProfesional>>
}

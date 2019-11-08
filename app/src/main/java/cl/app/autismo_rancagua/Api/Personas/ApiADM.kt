package cl.app.autismo_rancagua.Api.Personas

import cl.app.autismo_rancagua.Modelo.Administrador.Administrador
import cl.app.autismo_rancagua.Modelo.Administrador.CargoAdministrador
import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import retrofit2.Call
import retrofit2.http.*


interface ApiADM {
    @GET("ADM/getAdministradores.php")
    fun getAdministradores(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Administrador>>

    @FormUrlEncoded
    @POST("ADM/getAdministradorXID.php")
    fun getAdministradorXID(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String
    ): Call<ArrayList<Administrador>>

    @FormUrlEncoded
    @POST("ADM/updateEstado.php")
    fun updateEstado(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Administrador>

    @FormUrlEncoded
    @POST("ADM/addAdministrador.php")
    fun addAdministrador(
        @Field("ID_CARGO_ADMINISTRADOR") ID_CARGO_ADMINISTRADOR: String,
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
    ): Call<Administrador>

    @FormUrlEncoded
    @POST("ADM/updateAdministrador.php")
    fun updateAdministrador(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String,
        @Field("ID_CARGO_ADMINISTRADOR") ID_CARGO_ADMINISTRADOR: String,
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
    ): Call<Administrador>


    @GET("ADM/getCargos.php")
    fun getCargos(): Call<ArrayList<CargoAdministrador>>


    @FormUrlEncoded
    @POST("ADM/getCargoXID.php")
    fun getCargoXID(
        @Field("ID_CARGO_ADMINISTRADOR") ID_CARGO_ADMINISTRADOR: String
    ): Call<ArrayList<CargoAdministrador>>



    @FormUrlEncoded
    @POST("CTA-ADM/getCuentaXIDADM.php")
    fun getCuentaXID(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String
    ): Call<ArrayList<CuentaAdministrador>>


}

package cl.app.autismo_rancagua.Api.Cuentas

import cl.app.autismo_rancagua.Modelo.Administrador.CuentaAdministrador
import retrofit2.Call
import retrofit2.http.*


interface ApiCtaAdm {
    @GET("CTA-ADM/getCuentas.php")
    fun getCuentas(): Call<ArrayList<CuentaAdministrador>>

    @FormUrlEncoded
    @POST("CTA-ADM/getCuentaXID.php")
    fun getCuentaXID(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String
    ): Call<ArrayList<CuentaAdministrador>>

    @FormUrlEncoded
    @POST("CTA-ADM/updateEstado.php")
    fun updateEstado(
        @Field("ID_CUENTA_ADMINISTRADOR") ID_CUENTA_ADMINISTRADOR: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<CuentaAdministrador>

    @FormUrlEncoded
    @POST("CTA-ADM/addCuenta.php")
    fun addCuenta(
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String,
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<CuentaAdministrador>

    @FormUrlEncoded
    @POST("CTA-ADM/updateCuenta.php")
    fun updateCuenta(
        @Field("ID_CUENTA_ADMINISTRADOR") ID_CUENTA_ADMINISTRADOR: String,
        @Field("ID_ADMINISTRADOR") ID_ADMINISTRADOR: String,
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String
    ): Call<CuentaAdministrador>


    @FormUrlEncoded
    @POST("CTA-ADM/authAdministrador.php")
    fun authAdministrador(
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String
    ): Call<ArrayList<CuentaAdministrador>>



    /*//for live data search
    @GET("GET/getcontacts.php")
    fun getContact(
            @Query("item_type") item_type: String?,
            @Query("key") keyword: String?
    ): Call<List<Contacts>>*/
}
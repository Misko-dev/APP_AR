package cl.app.autismo_rancagua.Api.Cuentas

import cl.app.autismo_rancagua.Modelo.Profesional.CuentaProfesional
import retrofit2.Call
import retrofit2.http.*


interface ApiCtaPro {
    @GET("CTA-PRO/getCuentas.php")
    fun getCuentas(): Call<ArrayList<CuentaProfesional>>

    @FormUrlEncoded
    @POST("CTA-PRO/getCuentaXID.php")
    fun getCuentaXID(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String
    ): Call<ArrayList<CuentaProfesional>>

    @FormUrlEncoded
    @POST("CTA-PRO/updateEstado.php")
    fun updateEstado(
        @Field("ID_CUENTA_PROFESIONAL") ID_CUENTA_PROFESIONAL: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<CuentaProfesional>

    @FormUrlEncoded
    @POST("CTA-PRO/addCuenta.php")
    fun addCuenta(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<CuentaProfesional>

    @FormUrlEncoded
    @POST("CTA-PRO/updateCuenta.php")
    fun updateCuenta(
        @Field("ID_CUENTA_PROFESIONAL") ID_CUENTA_PROFESIONAL: String,
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String
    ): Call<CuentaProfesional>


    @FormUrlEncoded
    @POST("CTA-PRO/authProfesional.php")
    fun authProfesional(
        @Field("CORREO") CORREO: String,
        @Field("CONTRASEÑA") CONTRASEÑA: String
    ): Call<ArrayList<CuentaProfesional>>

}
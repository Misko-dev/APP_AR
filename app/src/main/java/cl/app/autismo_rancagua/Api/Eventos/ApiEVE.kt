package cl.app.autismo_rancagua.Api.Eventos

import cl.app.autismo_rancagua.Modelo.Evento.Evento
import retrofit2.Call
import retrofit2.http.*


interface ApiEVE {
    @GET("EVE/getEventos.php")
    fun getEventos(
        @Query("item_type") item_type: String?,
        @Query("key") keyword: String?
    ): Call<ArrayList<Evento>>

    @FormUrlEncoded
    @POST("EVE/getEventosXFECHAS.php")
    fun getEventosXFECHAS(
        @Field("FECHA_INICIAL") FECHA_INICIAL: String,
        @Field("FECHA_TERMINO") FECHA_TERMINO: String
    ): Call<ArrayList<Evento>>

    @FormUrlEncoded
    @POST("EVE/updateEstado.php")
    fun updateEstado(
        @Field("ID_EVENTO") ID_EVENTO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Evento>

    @FormUrlEncoded
    @POST("EVE/addEvento.php")
    fun addEvento(
        @Field("FECHA") FECHA: String,
        @Field("HORA") HORA: String,
        @Field("TITULO") TITULO: String,
        @Field("DESCRIPCION") DESCRIPCION: String,
        @Field("COLOR") COLOR: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Evento>

    @FormUrlEncoded
    @POST("EVE/updateEvento.php")
    fun updateEvento(
        @Field("ID_EVENTO") ID_EVENTO: String,
        @Field("FECHA") FECHA: String,
        @Field("HORA") HORA: String,
        @Field("TITULO") TITULO: String,
        @Field("DESCRIPCION") DESCRIPCION: String,
        @Field("COLOR") COLOR: String
    ): Call<Evento>
}

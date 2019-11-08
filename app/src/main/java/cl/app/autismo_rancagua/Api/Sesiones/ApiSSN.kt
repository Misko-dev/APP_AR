package cl.app.autismo_rancagua.Api.Sesiones

import cl.app.autismo_rancagua.Modelo.Niño.Niño
import cl.app.autismo_rancagua.Modelo.Participante.Asistencia
import cl.app.autismo_rancagua.Modelo.Participante.Participante
import cl.app.autismo_rancagua.Modelo.Resultado
import cl.app.autismo_rancagua.Modelo.Sesion.Sesion
import retrofit2.Call
import retrofit2.http.*


interface ApiSSN {
    @GET("SSN/getSesiones.php")
    fun getSesiones(): Call<ArrayList<Sesion>>

    @FormUrlEncoded
    @POST("SSN/getSesionXID.php")
    fun getSesionXID(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String
    ): Call<ArrayList<Sesion>>


    @FormUrlEncoded
    @POST("SSN/getSesionXIDSSN.php")
    fun getSesionXIDSSN(
        @Field("ID_SESION") ID_SESION: String
    ): Call<ArrayList<Sesion>>

    @FormUrlEncoded
    @POST("SSN/updateEstado.php")
    fun updateEstado(
        @Field("ID_SESION") ID_SESION: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Sesion>

    @FormUrlEncoded
    @POST("SSN/addSesion.php")
    fun addSesion(
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("SALA") SALA: String,
        @Field("NOMBRE") NOMBRE: String,
        @Field("DIA") DIA: Int,
        @Field("HORA") HORA: Int,
        @Field("DURACION") DURACION: Int,
        @Field("DESCRIPCION") DESCRIPCION: String,
        @Field("TIPO") TIPO: String,
        @Field("ESTADO") ESTADO: Int
    ): Call<Sesion>

    @FormUrlEncoded
    @POST("SSN/updateSesion.php")
    fun updateSesion(
        @Field("ID_SESION") ID_SESION: String,
        @Field("ID_PROFESIONAL") ID_PROFESIONAL: String,
        @Field("SALA") SALA: String,
        @Field("NOMBRE") NOMBRE: String,
        @Field("DIA") DIA: Int,
        @Field("HORA") HORA: Int,
        @Field("DURACION") DURACION: Int,
        @Field("DESCRIPCION") DESCRIPCION: String,
        @Field("TIPO") TIPO: String
    ): Call<Sesion>


    @FormUrlEncoded
    @POST("SSN/getParticipantesXIDSSN.php")
    fun getParticipantesXIDSSN(
        @Field("ID_SESION") ID_SESION: String
    ): Call<ArrayList<Participante>>

    @FormUrlEncoded
    @POST("SSN/getNiñosXID.php")
    fun getNiñosXID(
        @Field("ID_SESION") ID_SESION: String
        ): Call<ArrayList<Niño>>


    @FormUrlEncoded
    @POST("SSN/addNiño.php")
    fun addNiño(
        @Field("ID_SESION") ID_SESION: String,
        @Field("ID_NIÑO") ID_NIÑO: String
    ): Call<Resultado>

    @FormUrlEncoded
    @POST("SSN/addAsistencias.php")
    fun addAsistencias(
        @Field("ID_PARTICIPANTE") ID_PARTICIPANTE: String,
        @Field("FECHA") FECHA: String,
        @Field("OBSERVACION") OBSERVACION: String
    ): Call<Resultado>

    @FormUrlEncoded
    @POST("SSN/getAsistenciasXID.php")
    fun getAsistenciasXID(
        @Field("ID_PARTICIPANTE") ID_PARTICIPANTE: String
    ): Call<ArrayList<Asistencia>>

}

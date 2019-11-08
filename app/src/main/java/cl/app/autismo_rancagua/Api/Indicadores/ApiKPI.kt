package cl.app.autismo_rancagua.Api.Indicadores

import cl.app.autismo_rancagua.Modelo.Indicador.Indicador
import retrofit2.Call
import retrofit2.http.*


interface ApiKPI {
    @GET("KPI/getKPIADM.php")
    fun getKPIADM(
    ): Call<ArrayList<Indicador>>

    @GET("KPI/getKPIPRO.php")
    fun getKPIPRO(
    ): Call<ArrayList<Indicador>>

    @GET("KPI/getKPINNS.php")
    fun getKPINNS(
    ): Call<ArrayList<Indicador>>

    @GET("KPI/getKPITTR.php")
    fun getKPITTR(
    ): Call<ArrayList<Indicador>>

}

package cl.app.autismo_rancagua.Api

import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    const val BASE_URL = "http://shared14.cl-prueba.site/~aurancag/aurancagua.cl/guillermo.lagos05/"
    var retrofit = Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}





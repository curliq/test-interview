package com.john.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {

    // This would come from a session repository
    private const val TOKEN = "eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_31Uy5KbMBD8lS3Oqy0DAhtuueUH8gGDNNgqC4mShDdbqfx7BiSM8bpy7O559GgG_mTK-6zNYFRM4mA_fACnlTl3YK4fwg7Ze-anjiJyEHiQDbK-KzjjNResK5uCVU2d864_HXhXUjD-HrM2r-tTVdWnhr9nCkIk-PHAZwKEsJMJP62W6H4pOXc_lkXTNw07Qtkx3jQ9O2FesOIkyrrjeZ6faqod7BVNzKgOdd4JfmBlVZEbrHoGZVEwrBvOiRckUgaN9UMI9D5mNYIf6_qYU2xHWYeqYk1RS1bkvKzKgpc5yHlgYUecbUWnTGjrUbYOQb6t3GWxzwwM-FIIX-OToCSaoHqFbs9r5cOOSUBKR8ZblCrcQVRCAHEZ8B654U-nAr7BFC7WKU9rZMpIdVNyAh2DO9BgRLImwEkmrAnO6thoZpJmTa_cAEFZw2zP-slIf5f8vfsKYmsx-WCHdUQcQKXCGsmIObcwjvrrjpaoAYyEgK1EjVRihUlzVwzzIKPDHh2Sd_8_KdqI2qjpZukFAp7dMsdj4ncxpaITF1inGzAAuYFWEFzUhJehRvhCXKUI0hARbEFMDXBOMyWCOjuzriXGP8jBgfEgNtdEs27S13bdLm7U5iDizUTEa4H5RujeBhW2mtoKMvFQYSGYnY_kmU1ZzvZKryPFGXfUEuVQoBrDDvi9FB98WZ4DnUQPN9qpZ2e72dpxabgd9y0zvh8VflViE1_U2sRYVFxQTholS--XaAyB5p3GBEdYvyn6gS63zayTD-337Np3z77IZ_bT3PmAywKFvz1To-wTNXVeOHrc-W7WLo_cEvV4XMv-nq8t-_sPDHBCyxkGAAA.CzwMNwxf6WArW7l7i5_dyDNNl2cfWlW3G30-rJ_J05HoGuWdv1mKwLaHajAlGXaPna-Odp3_tlqcoOTnX21zmkNIPNPMTsizfdP8CztXQ3dwt3ANiX_xU2UXGLKxk9HhdUJXCWIqCtzjeqo_NBPFNFZJtl9bkGAyWakOZJZ3llEVXgF_dv1uLSVicL3JSQEx0c2ksOI1mtgNM1Ooj3XR3CXcSI7RaSIndsh8705WOnci82_HhAqyvX3x7CuafRLH_AmO5RIrcOmyMJjJ00i4fGEU55-47O08RfVoOrcy9XSJGsOiN4vbNLurpo8MVv0_1XWuunk_1kIPc0w2NH27KY5Pko509tJSfiGM_qq6LsM_ihTsxxwcvtbujZC9o8QCfYceO14yuOpk8M0hGZ9GTKGc7Vq_s7gpYu72swC6c_3mpNKjJXQS1LjSaDsRlDU3Mb10ViBACYJbmnOn5tnrsUmHK3FdwXNc22Mjcq8gWMHoVREymiQJggS0fEwFRu3l5bLyPpk3KQL519qes6qRb5e0156ljSY_RPOZ4KlrWXkUZcsRQv99kysIL2C7s2EBNnFSRXTejEJjOeqCSMVxkO5WchH2nrCuoT2d0rz5ujvEyiv6OeFRbYtqxmbqlJe_-luvalnBKSdt4g0aWZoLW_PtC9bPUNp-mZL2dZ0bnvY"

    fun getInstance(): Retrofit {
        val httpClient = OkHttpClient.Builder().apply {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("Accept", "application/json")
                    builder.header("Authorization", "Bearer $TOKEN")
                    return@Interceptor chain.proceed(builder.build())
                }
            )
        }.build()
        return Retrofit.Builder().baseUrl("https://api-sandbox.starlingbank.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

}

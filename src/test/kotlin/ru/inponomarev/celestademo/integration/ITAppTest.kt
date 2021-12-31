package ru.inponomarev.celestademo.integration

import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@Tag("integration")
class ITAppTest {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8088/api/v1/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private val api = retrofit.create(AppApi::class.java)

    @Test
    fun getItems() {
        val response = api.items.execute()
        assertThat(response.isSuccessful).isTrue
        Approvals.verifyJson(response.body())
    }
}
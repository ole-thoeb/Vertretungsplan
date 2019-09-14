package com.example.eloem.vertretungsplan.network

import com.example.eloem.vertretungsplan.util.throwError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class VerPlanConverterFactory private constructor(): Converter.Factory() {
    
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, ResponseModel.VerPlan>? {
        return VerPlanConverter()
    }
    
    private class VerPlanConverter : Converter<ResponseBody, ResponseModel.VerPlan> {
        override fun convert(value: ResponseBody): ResponseModel.VerPlan {
            val str = value.byteStream().bufferedReader(Charsets.ISO_8859_1).readText()
            return ResponseModel.VerPlan.fromString(str).throwError()
        }
    }
    
    companion object {
        fun create(): VerPlanConverterFactory = VerPlanConverterFactory()
    }
}
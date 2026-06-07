package com.david.collegeevents.data.remote

import com.david.collegeevents.utils.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        // runBlocking is fine here as interceptors execute on background threads inside OkHttp
        val token = runBlocking { tokenManager.tokenFlow.first() }
        
        val requestBuilder = chain.request().newBuilder()
        
        // If token exists, append it to headers automatically
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
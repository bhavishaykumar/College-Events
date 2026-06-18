package com.david.collegeevents.di

import android.content.Context
import com.david.collegeevents.data.remote.ApiServices
import com.david.collegeevents.data.remote.AuthInterceptor
import com.david.collegeevents.data.repository.AdminEventRepositoryImpl
import com.david.collegeevents.data.repository.AuthRepositoryImpl
import com.david.collegeevents.data.repository.EventDetailsRepositoryImpl
import com.david.collegeevents.data.repository.EventRepositoryImpl
import com.david.collegeevents.data.repository.UserRepositoryImpl
import com.david.collegeevents.domain.repository.AdminEventRepository
import com.david.collegeevents.domain.repository.AuthRepository
import com.david.collegeevents.domain.repository.EventDetailsRepository
import com.david.collegeevents.domain.repository.EventRepository
import com.david.collegeevents.domain.repository.UserRepository
import com.david.collegeevents.utils.ThemeManager
import com.david.collegeevents.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideThemeManager(@ApplicationContext context: Context): ThemeManager {
        return ThemeManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Auto adds token headers to downstream calls
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)   // ← Server se response padhne ka time
            .writeTimeout(60, TimeUnit.SECONDS)  // ← Image data bhejne ka time (yahi issue tha)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): ApiServices {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient) // Linked client override
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: ApiServices, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideEventRepository(api: ApiServices): EventRepository {
        return EventRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: ApiServices): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAdminEventRepository(api: ApiServices): AdminEventRepository {
        return AdminEventRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideEventDetailsRepository(api: ApiServices): EventDetailsRepository {
        return EventDetailsRepositoryImpl(api)
    }

}
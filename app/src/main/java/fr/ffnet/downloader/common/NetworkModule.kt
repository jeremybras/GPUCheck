package fr.ffnet.downloader.common

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        const val REGULAR_WEBSITE = "regular_website"
        const val MOBILE_WEBSITE = "mobile_website"

        const val CLASSIC_OKHTTP_BUILDER = "classic_okhttp"
        const val REFERER_OKHTTP_BUILDER = "referer_okhttp"
    }

    @Singleton
    @Provides
    @Named(CLASSIC_OKHTTP_BUILDER)
    fun provideClassicOkhttpClient(
        context: Context,
        dispatcher: Dispatcher
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .addInterceptor(ChuckInterceptor(context))
        .addNetworkInterceptor(StethoInterceptor())
        .dispatcher(dispatcher)

    @Singleton
    @Provides
    @Named(REFERER_OKHTTP_BUILDER)
    fun provideRefererOkhttpClient(
        @Named(CLASSIC_OKHTTP_BUILDER) client: OkHttpClient.Builder
    ): OkHttpClient.Builder = client.addInterceptor {
        val newRequest = it.request()
            .newBuilder()
            .addHeader(
                "Referer",
                BuildConfig.API_REGULAR_BASE_URL
            )
            .build()
        it.proceed(newRequest)
    }

    @Provides
    fun provideDispatcher(): Dispatcher {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 3
        return dispatcher
    }

    @Provides
    @Named(MOBILE_WEBSITE)
    fun provideMobileRetrofitMoschi(
        moshiConverterFactory: MoshiConverterFactory,
        @Named(CLASSIC_OKHTTP_BUILDER) client: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_MOBILE_BASE_URL)
            .client(client.build())
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Provides
    @Named(REGULAR_WEBSITE)
    fun provideRegularRetrofitMoschi(
        moshiConverterFactory: MoshiConverterFactory,
        @Named(CLASSIC_OKHTTP_BUILDER) client: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_REGULAR_BASE_URL)
            .client(client.build())
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Provides
    fun provideMoshiConverter(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun providePicasso(
        context: Context,
        @Named(REFERER_OKHTTP_BUILDER) client: OkHttpClient.Builder
    ): Picasso = Picasso
        .Builder(context)
        .downloader(OkHttp3Downloader(client.build()))
        .build()
}

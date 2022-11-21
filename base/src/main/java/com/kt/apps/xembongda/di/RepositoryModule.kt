package com.kt.apps.xembongda.di

import android.annotation.SuppressLint
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.kt.apps.xembongda.api.BinhLuan90PhutApi
import com.kt.apps.xembongda.api.VeBoApi
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.di.mapkey.FootballRepositoryMapKey
import com.kt.apps.xembongda.repository.IFootballMatchRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.repository.config.FootballRepositoryConfig
import com.kt.apps.xembongda.repository.footbalmatch.*
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.inject.Qualifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module
class RepositoryModule {
    companion object {
        private const val SOURCE_91_PHUT = "phut91"
        private const val SOURCE_XOI_LAC_10_PHUT = "xoilac10"
        private const val BinhLuan90Config = "binhluan90"
        private const val VE_BO = "vebo"
        private const val VE_BO_DETAIL = "vebodtail"
        private const val SOURCE_MITOM = "mitom"
        private const val VEBO_REFERER = "vebo_referer"
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Source91Phut

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceXoiLac10

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Regex91Phut

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceMitom

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceBinhLuan


    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceVeBo

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceVeBoDetail

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Source91PhutConfig

    @Qualifier
    @Retention
    annotation class SourceMitomConfig

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceXoiLac10Config

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceBinhLuan90Config

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SourceVeBoConfig

    @Provides
    @Source91Phut
    fun provides91PhutUrl(config: FirebaseRemoteConfig) = config.getString(SOURCE_91_PHUT)

    @Provides
    @SourceXoiLac10
    fun providesXoiLac10(config: FirebaseRemoteConfig) = config.getString(SOURCE_XOI_LAC_10_PHUT)

    @Provides
    @SourceBinhLuan
    fun providesBinhLuan(config: FirebaseRemoteConfig) = config.getString(BinhLuan90Config)

    @Provides
    @SourceVeBo
    fun providesVeBo(config: FirebaseRemoteConfig) = config.getString(VE_BO)

    @Provides
    @Referer.VeBo
    fun providesVeBoReferer(config: FirebaseRemoteConfig) = config.getString(VEBO_REFERER)

    @Provides
    @SourceVeBoDetail
    fun providesVeBoDetail(config: FirebaseRemoteConfig) = config.getString(VE_BO_DETAIL)

    @Provides
    @Regex91Phut
    fun providesRegex91Phut(config: FirebaseRemoteConfig) = "(?<=urlStream\\s=\\s\").*?(?=\")"

    @Provides
    @SourceMitom
    fun providesMiTomPhutUrl(config: FirebaseRemoteConfig) = config.getString(SOURCE_MITOM)

    @Provides
    @BaseScope
    @Source91PhutConfig
    fun providesFootball91PhutConfig(
        @Source91Phut urlFootball90P: String,
        @Regex91Phut regex: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(
        urlFootball90P,
        regex
    )

    @Provides
    @BaseScope
    @SourceXoiLac10Config
    fun providesXoiLac10Config(
        @SourceXoiLac10 urlFootball90P: String,
        @Regex91Phut regex: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(
        urlFootball90P,
        regex
    )

    @Provides
    @BaseScope
    @SourceMitomConfig
    fun providesFootballMiTomConfig(
        @SourceMitom urlFootball90P: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(
        urlFootball90P
    )

    @Provides
    @BaseScope
    @SourceBinhLuan90Config
    fun providesBinhLuanConfig(
        @SourceBinhLuan url: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(url)

    @Provides
    @BaseScope
    @SourceVeBoConfig
    fun providesVeBoConfig(
        @SourceVeBo url: String,
        @Referer.VeBo referer: String
    ): FootballRepositoryConfig = FootballRepositoryConfig(url, referer = referer)

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.Phut91)
    fun providePhut91Repo(repoImpl: Football91Repository): IFootballMatchRepository = repoImpl

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.MiTom)
    fun provideMitomRepo(repoImpl: MitomRepository): IFootballMatchRepository = repoImpl

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.XoiLac10)
    fun provideXoiLacRepo(repoImpl: XoiLac10Repository): IFootballMatchRepository = repoImpl

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.BinhLuan91)
    fun provideBinhLuanRepo(
        repoImpl: BinhLuan90PhutRepositoryImpl,
    ): IFootballMatchRepository = repoImpl

    @Provides
    @IntoMap
    @BaseScope
    @FootballRepositoryMapKey(FootballRepoSourceFrom.VeBo)
    fun provideVeBoRepo(
        repoImpl: VeBoRepositoryImpl,
    ): IFootballMatchRepository = repoImpl

    @Provides
    @BaseScope
    fun providesOkHttpClient(): OkHttpClient {
        var context: SSLContext? = null
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { name, password ->
                return@setDefaultHostnameVerifier true
            }
            context = SSLContext.getInstance("TLS")
            context.init(
                null, arrayOf<X509TrustManager>(@SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }), SecureRandom()
            )
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })
            .apply {
                context?.let {
                    this.sslSocketFactory(it.socketFactory, object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                            return arrayOfNulls(0)
                        }
                    })
                }
            }
            .build()
    }

    @Provides
    @BaseScope
    fun providesRetrofit(okHttpClient: OkHttpClient, @SourceVeBo url: String): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    @Provides
    @BaseScope
    fun providesBinhLuan90PhutApi(
        retrofit: Retrofit,
        @SourceBinhLuan90Config
        config: FootballRepositoryConfig
    ): BinhLuan90PhutApi {
        return retrofit.newBuilder()
            .baseUrl(config.url)
            .build()
            .create(BinhLuan90PhutApi::class.java)

    }

    @Provides
    @BaseScope
    fun providesVeBoApi(
        retrofit: Retrofit,
        @SourceVeBoConfig
        config: FootballRepositoryConfig
    ): VeBoApi {
        return retrofit.newBuilder()
            .baseUrl(config.url)
            .build()
            .create(VeBoApi::class.java)

    }

    @Provides
    @BaseScope
    @Named("DetailVeBo")
    fun providesOtherVeBoApi(
        retrofit: Retrofit,
        @SourceVeBoDetail
        url: String
    ): VeBoApi {
        return retrofit.newBuilder()
            .baseUrl(url)
            .build()
            .create(VeBoApi::class.java)

    }
}
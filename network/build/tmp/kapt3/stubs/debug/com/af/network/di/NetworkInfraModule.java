package com.af.network.di;

@dagger.Module()
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J(\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007b\u0002\b\fb\u0002\b\rJ \u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0005H\u0007b\u0002\b\fb\u0002\b\r\u00ca\u0001\u0002\b\u0012\u00ca\u0001\u0010\b\u0013\u0012\f\b\u0014\u0012\b\b\fJ\u0004\b\t0\u0015\u00a8\u0006\u0011"}, d2 = {"Lcom/af/network/di/NetworkInfraModule;", "", "<init>", "()V", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "config", "Lcom/af/network/di/NetworkConfig;", "authInterceptor", "Lcom/af/network/auth/AuthInterceptor;", "tokenAuthenticator", "Lcom/af/network/auth/TokenAuthenticator;", "Ldagger/Provides;", "Ljavax/inject/Singleton;", "provideRetrofit", "Lretrofit2/Retrofit;", "okHttpClient", "FlashLight:network_debug", "Ldagger/Module;", "Ldagger/hilt/InstallIn;", "value", "Ldagger/hilt/components/SingletonComponent;"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class NetworkInfraModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.af.network.di.NetworkInfraModule INSTANCE = null;
    
    private NetworkInfraModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull()
    com.af.network.di.NetworkConfig config, @org.jetbrains.annotations.NotNull()
    com.af.network.auth.AuthInterceptor authInterceptor, @org.jetbrains.annotations.NotNull()
    com.af.network.auth.TokenAuthenticator tokenAuthenticator) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull()
    com.af.network.di.NetworkConfig config, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
}
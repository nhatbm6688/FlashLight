package com.af.network.di;

import com.af.network.auth.AuthInterceptor;
import com.af.network.auth.TokenAuthenticator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkInfraModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<NetworkConfig> configProvider;

  private final Provider<AuthInterceptor> authInterceptorProvider;

  private final Provider<TokenAuthenticator> tokenAuthenticatorProvider;

  private NetworkInfraModule_ProvideOkHttpClientFactory(Provider<NetworkConfig> configProvider,
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider) {
    this.configProvider = configProvider;
    this.authInterceptorProvider = authInterceptorProvider;
    this.tokenAuthenticatorProvider = tokenAuthenticatorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(configProvider.get(), authInterceptorProvider.get(), tokenAuthenticatorProvider.get());
  }

  public static NetworkInfraModule_ProvideOkHttpClientFactory create(
      Provider<NetworkConfig> configProvider, Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider) {
    return new NetworkInfraModule_ProvideOkHttpClientFactory(configProvider, authInterceptorProvider, tokenAuthenticatorProvider);
  }

  public static OkHttpClient provideOkHttpClient(NetworkConfig config,
      AuthInterceptor authInterceptor, TokenAuthenticator tokenAuthenticator) {
    return Preconditions.checkNotNullFromProvides(NetworkInfraModule.INSTANCE.provideOkHttpClient(config, authInterceptor, tokenAuthenticator));
  }
}

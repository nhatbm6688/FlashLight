package com.af.network.auth;

import com.af.network.di.NetworkConfig;
import dagger.Lazy;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class TokenAuthenticator_Factory implements Factory<TokenAuthenticator> {
  private final Provider<TokenRefresher> tokenRefresherProvider;

  private final Provider<TokenProvider> tokenProvider;

  private final Provider<NetworkConfig> configProvider;

  private TokenAuthenticator_Factory(Provider<TokenRefresher> tokenRefresherProvider,
      Provider<TokenProvider> tokenProvider, Provider<NetworkConfig> configProvider) {
    this.tokenRefresherProvider = tokenRefresherProvider;
    this.tokenProvider = tokenProvider;
    this.configProvider = configProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(DoubleCheck.lazy(tokenRefresherProvider), tokenProvider.get(), configProvider.get());
  }

  public static TokenAuthenticator_Factory create(Provider<TokenRefresher> tokenRefresherProvider,
      Provider<TokenProvider> tokenProvider, Provider<NetworkConfig> configProvider) {
    return new TokenAuthenticator_Factory(tokenRefresherProvider, tokenProvider, configProvider);
  }

  public static TokenAuthenticator newInstance(Lazy<TokenRefresher> tokenRefresher,
      TokenProvider tokenProvider, NetworkConfig config) {
    return new TokenAuthenticator(tokenRefresher, tokenProvider, config);
  }
}

package com.af.network.auth;

import com.af.network.di.NetworkConfig;
import dagger.internal.DaggerGenerated;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<TokenProvider> tokenProvider;

  private final Provider<NetworkConfig> configProvider;

  private AuthInterceptor_Factory(Provider<TokenProvider> tokenProvider,
      Provider<NetworkConfig> configProvider) {
    this.tokenProvider = tokenProvider;
    this.configProvider = configProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(tokenProvider.get(), configProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<TokenProvider> tokenProvider,
      Provider<NetworkConfig> configProvider) {
    return new AuthInterceptor_Factory(tokenProvider, configProvider);
  }

  public static AuthInterceptor newInstance(TokenProvider tokenProvider, NetworkConfig config) {
    return new AuthInterceptor(tokenProvider, config);
  }
}

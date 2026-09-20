package com.af.network.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class NetworkInfraModule_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final Provider<NetworkConfig> configProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  private NetworkInfraModule_ProvideRetrofitFactory(Provider<NetworkConfig> configProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.configProvider = configProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(configProvider.get(), okHttpClientProvider.get());
  }

  public static NetworkInfraModule_ProvideRetrofitFactory create(
      Provider<NetworkConfig> configProvider, Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkInfraModule_ProvideRetrofitFactory(configProvider, okHttpClientProvider);
  }

  public static Retrofit provideRetrofit(NetworkConfig config, OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkInfraModule.INSTANCE.provideRetrofit(config, okHttpClient));
  }
}

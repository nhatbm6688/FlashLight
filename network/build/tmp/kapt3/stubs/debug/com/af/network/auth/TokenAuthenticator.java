package com.af.network.auth;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B+\b\u0007\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u001a\u0002\b\u000b\u00a2\u0006\u0004\b\t\u0010\nJ\u001c\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/af/network/auth/TokenAuthenticator;", "Lokhttp3/Authenticator;", "tokenRefresher", "Ldagger/Lazy;", "Lcom/af/network/auth/TokenRefresher;", "tokenProvider", "Lcom/af/network/auth/TokenProvider;", "config", "Lcom/af/network/di/NetworkConfig;", "<init>", "(Ldagger/Lazy;Lcom/af/network/auth/TokenProvider;Lcom/af/network/di/NetworkConfig;)V", "Ljavax/inject/Inject;", "authenticate", "Lokhttp3/Request;", "route", "Lokhttp3/Route;", "response", "Lokhttp3/Response;", "FlashLight:network_debug"})
public final class TokenAuthenticator implements okhttp3.Authenticator {
    @org.jetbrains.annotations.NotNull()
    private final dagger.Lazy<com.af.network.auth.TokenRefresher> tokenRefresher = null;
    @org.jetbrains.annotations.NotNull()
    private final com.af.network.auth.TokenProvider tokenProvider = null;
    @org.jetbrains.annotations.NotNull()
    private final com.af.network.di.NetworkConfig config = null;
    
    @javax.inject.Inject()
    public TokenAuthenticator(@org.jetbrains.annotations.NotNull()
    dagger.Lazy<com.af.network.auth.TokenRefresher> tokenRefresher, @org.jetbrains.annotations.NotNull()
    com.af.network.auth.TokenProvider tokenProvider, @org.jetbrains.annotations.NotNull()
    com.af.network.di.NetworkConfig config) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public okhttp3.Request authenticate(@org.jetbrains.annotations.Nullable()
    okhttp3.Route route, @org.jetbrains.annotations.NotNull()
    okhttp3.Response response) {
        return null;
    }
}
package com.af.network.auth;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&\u00a8\u0006\u0004\u00c0\u0006\u0003"}, d2 = {"Lcom/af/network/auth/TokenProvider;", "", "getAccessToken", "", "FlashLight:network_debug"})
public abstract interface TokenProvider {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.String getAccessToken();
}
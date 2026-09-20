package com.af.network.auth;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005\u00c0\u0006\u0003"}, d2 = {"Lcom/af/network/auth/TokenRefresher;", "", "refreshToken", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "FlashLight:network_debug"})
public abstract interface TokenRefresher {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refreshToken(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
}
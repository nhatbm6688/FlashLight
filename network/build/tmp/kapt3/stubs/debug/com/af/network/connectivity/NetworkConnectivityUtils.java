package com.af.network.connectivity;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J*\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0016\u0010\u000e\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\t\u00a8\u0006\u0010"}, d2 = {"Lcom/af/network/connectivity/NetworkConnectivityUtils;", "", "<init>", "()V", "isConnected", "", "context", "Landroid/content/Context;", "registerNetworkCallback", "Landroid/net/ConnectivityManager$NetworkCallback;", "onAvailable", "Lkotlin/Function0;", "", "onLost", "unregisterNetworkCallback", "callback", "FlashLight:network_debug"})
public final class NetworkConnectivityUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.af.network.connectivity.NetworkConnectivityUtils INSTANCE = null;
    
    private NetworkConnectivityUtils() {
        super();
    }
    
    public final boolean isConnected(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.net.ConnectivityManager.NetworkCallback registerNetworkCallback(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAvailable, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLost) {
        return null;
    }
    
    public final void unregisterNetworkCallback(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.ConnectivityManager.NetworkCallback callback) {
    }
}
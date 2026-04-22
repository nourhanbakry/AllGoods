package com.example.allgoods.utils.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class NetworkManager {

    private ConnectivityManager.NetworkCallback networkCallback;

    public void register(Context context, NetworkListener listener) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                if (listener != null) listener.onConnected();
            }

            @Override
            public void onLost(@NonNull Network network) {
                if (listener != null) listener.onDisconnected();
            }
        };

        NetworkRequest request = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    public void unregister(Context context) {
        if (networkCallback == null) return;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public boolean isConnected(Context context) {
        return NetworkUtils.isInternetAvailable(context);
    }
}

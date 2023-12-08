package lk.cmh.app.ceylonmarkethub.data.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static Snackbar snackbar;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (isNetworkAvailable(context)) {

            } else {
                Toast.makeText(context, "Network Disconnected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

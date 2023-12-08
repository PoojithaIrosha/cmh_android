package lk.cmh.app.ceylonmarkethub;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import lk.cmh.app.ceylonmarkethub.data.receiver.NetworkStateReceiver;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CMH extends Application {

    private static final String TAG = CMH.class.getSimpleName();
    private static CMH instance;
    private static Intent networkStateReceiverIntent;
    private NotificationManager notificationManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration notificationListener;

    public static CMH getInstance() {
        return instance;
    }

    public static final String NEW_ORDERS_CHANNEL_ID = "NEW_ORDERS";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (networkStateReceiverIntent == null) {
            registerReceiver(new NetworkStateReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        if (auth.contains("accessToken")) {

            // Sign Out
            long expiresIn = auth.getLong("expiresIn", 0);
            boolean after = new Date(System.currentTimeMillis()).after(new Date(expiresIn));

            if (after) {
                auth.edit().clear().apply();
                firebaseAuth.signOut();
            }

            // Notification Broadcasts
            String role = auth.getString("role", "ROLE_CUSTOMER");
            if (role.equals("ROLE_SELLER")) {

                NotificationChannel channel = new NotificationChannel(NEW_ORDERS_CHANNEL_ID, "New Orders", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(true);
                channel.enableLights(true);
                channel.setDescription("New order received notification channel");
                channel.setLightColor(R.color.white);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);

                addNotificationListener(auth);
            }

        }
    }

    public void addNotificationListener(SharedPreferences auth) {
        if (auth.contains("email") && !auth.getString("email", "").isEmpty()) {
            CollectionReference collection = firestore.collection("sellers/" + auth.getString("email", "") + "/notifications");
            if (notificationListener == null) {
                notificationListener = collection.whereEqualTo("seen", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String title = document.getString("title");
                            String body = document.getString("body");

                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), NEW_ORDERS_CHANNEL_ID).setSmallIcon(R.drawable.ic_cart).setAutoCancel(true).setContentTitle(title).setContentText(body).build();

                            int id = new Random().nextInt(100) + 1;
                            notificationManager.notify(id, notification);

                            DocumentReference docRef = document.getReference();
                            docRef.update("seen", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "updated seen");
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        }
    }

    public ListenerRegistration getNotificationListener() {
        return notificationListener;
    }

    public void setNotificationListener(ListenerRegistration notificationListener) {
        this.notificationListener = notificationListener;
    }
}

package com.example.userinterface;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class CheckPlantlistForNotificationService extends Service {

    private static final String TAG_BOOT_EXECUTE_SERVICE = "BOOT_BROADCAST_SERVICE";

    public CheckPlantlistForNotificationService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onCreate() method.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkPlantList();

        String message = "RunAfterBootService onStartCommand() method.";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }
    @Override

    public void onDestroy() {
        super.onDestroy();
    }

    public void checkPlantList(){
        // Retrieve plantList from SharedPrefs
        // Compute daysRem
        // Notify if anyone is zero

    }

}



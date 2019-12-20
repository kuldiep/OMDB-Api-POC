package com.android_poc.moviesfornoon.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.android_poc.moviesfornoon.utils.AppConstants;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CheckInternetTask  extends AsyncTask<String, Void, Boolean> {
    private Context context;
    private boolean showToast = true;
    private boolean isInternetDetected = false;

    public CheckInternetTask(boolean isInternetDetected) {
        this.isInternetDetected = isInternetDetected;
    }

    public CheckInternetTask(Context context, boolean showToast) {
        this.context = context;
        this.showToast = showToast;
    }
    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            int timeoutMs = 2000;
            Socket sock = new Socket();
            SocketAddress sockAddress = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockAddress, timeoutMs);
            sock.close();
            return true;
        } catch (Exception ex) {
            Log.e( AppConstants.Companion.getLOG(), "Exception in CheckInternetTask::doInBackground() creating socket");
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean isInternetAvailable) {
        super.onPostExecute(isInternetAvailable);
        try {
            if (isInternetDetected && isInternetAvailable) {
                Log.d(AppConstants.Companion.getLOG(), "onPostExecute: raising internet detected event");
                EventBus.getDefault().post(new InternetEvent(null,
                        InternetEvent.INTERNET_EVENT_TYPE.DETECTED.ordinal()));
            } else if (!isInternetAvailable) {
                if ((context != null) && showToast) {
                    Toast toast = Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                EventBus.getDefault().post(new InternetEvent(null,
                        InternetEvent.INTERNET_EVENT_TYPE.NOT_AVAILABLE.ordinal()));
            } else if (isInternetAvailable) {
                Log.d(AppConstants.Companion.getLOG(),"raising internet available event");
                EventBus.getDefault().post(new InternetEvent(null,
                        InternetEvent.INTERNET_EVENT_TYPE.AVAILABLE.ordinal()));
            }
        } catch (Exception ex) {
            Log.e( AppConstants.Companion.getLOG(),"Exception in CheckInternetTask::onPostExecute()",ex);
        }
    }
}

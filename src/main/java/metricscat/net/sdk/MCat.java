package metricscat.net.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Alex on 18.12.2014.
 */
public class MCat {
    private static final String META_NAME="mcat_id";
    private static String partnerId;
    private static ExecutorThread smExecutorThread;
    private static ExecutorHandler smHandler;
    private static Gson smGson;
    private static MetricsCatApi smApi;

    public static void start(final Context context){
        final ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                partnerId = (String) ai.metaData.get(META_NAME);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        RequestInterceptor smRequestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("partner_id", partnerId);
                request.addHeader("device_id", Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID));
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ranks.metricscat.net/api/v1")
                .setRequestInterceptor(smRequestInterceptor)
                .build();
        smApi = restAdapter.create(MetricsCatApi.class);

        smGson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        smExecutorThread = new ExecutorThread("executor_thread");
        smExecutorThread.start();
        smHandler = new ExecutorHandler(smExecutorThread.getLooper());
        sendDeviceInfo(context);
    }

    private static void sendDeviceInfo(Context context) {
        final List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_META_DATA);
        final DeviceInfo info = new DeviceInfo();
        info.setup(packs);
        info.brand = Build.BRAND;
        info.manufacturer = Build.MANUFACTURER;
        info.model = Build.MODEL;
        info.product = Build.PRODUCT;
        Locale current = context.getResources().getConfiguration().locale;
        info.locale = current;
        TimeZone tz = TimeZone.getDefault();
        info.timeZone=tz.getID();
        smHandler.post(new Runnable() {
            @Override
            public void run() {
                String json = smGson.toJson(info);
                smApi.setDeviceInfo(json,new Callback<Void>() {

                    @Override
                    public void success(Void arg0, Response arg1) {
                        Log.d("TAG", "ok");
                    }

                    @Override
                    public void failure(RetrofitError arg0) {
                        Log.d("TAG", "failure");
                    }
                });
            }
        });
    }

    public static void stop(Context context) {
        smHandler.stop();
    }

    private static class ExecutorHandler extends android.os.Handler{
        private static final int STOP = 0;

        private ExecutorHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STOP:
                    Looper.myLooper().quit();
                    break;
            }
        }

        public void stop() {
            sendEmptyMessage(STOP);
        }
    }
    private static class ExecutorThread extends HandlerThread{
        public ExecutorThread(String name) {
            super(name);
        }
    }
}

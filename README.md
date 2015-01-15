# MetricscatSDK

How to integrate:

1) add metricscatSDK as  a library
2) add <meta-data android:name="mcat_id" android:value="[your partner's id]" /> to ANdroidManifest.xml
3) add to you Application class

@Override
public void onCreate() {
    super.onCreate();
    MCat.start(this);
}
    
@Override
public void onTerminate() {
    MCat.stop(this);
    super.onTerminate();
}

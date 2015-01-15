# MetricscatSDK

## How to integrate

* add metricscatSDK as  a library

* add ```<meta-data android:name="mcat_id" android:value="[your partner's id]" /> ``` to AndroidManifest.xml

* update your Application class

```
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
```

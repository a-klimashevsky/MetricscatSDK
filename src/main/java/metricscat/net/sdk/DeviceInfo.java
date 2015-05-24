package metricscat.net.sdk;

import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Alex on 20.12.2014.
 */
class DeviceInfo {
    public String manufacturer;
    public String brand;
    public String product;
    public String model;
    public List<PInfo> apps;
    public Locale locale;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("time_zone")
    public String timeZone;

    public void setup(List<PackageInfo> _apps){
        apps = new ArrayList<>();
        for(PackageInfo info : _apps){
            apps.add(new PInfo(info));
        }
    }

    public class PInfo{
        public int versionCode;
        public String versionName;
        public long firstInstallTime;
        public long lastUpdateTime;
        public String packageName;
        public PermissionInfo[] permissions;
        public String[] requestedPermissions;

        public PInfo(PackageInfo info) {
            versionCode = info.versionCode;
            versionName = info.versionName;
            firstInstallTime = info.firstInstallTime;
            lastUpdateTime = info.lastUpdateTime;
            packageName = info.packageName;
            permissions = info.permissions;
            requestedPermissions = info.requestedPermissions;
        }
    }
}

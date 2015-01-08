package metricscat.net.sdk;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Alex on 25.12.2014.
 */
public interface MetricsCatApi {

    @POST("/device")
    public void setDeviceInfo(@Body String info);
}

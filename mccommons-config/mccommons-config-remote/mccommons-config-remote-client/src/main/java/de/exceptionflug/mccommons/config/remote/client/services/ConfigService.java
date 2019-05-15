package de.exceptionflug.mccommons.config.remote.client.services;

import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ConfigService {

    @GET("config")
    Observable<ConfigData> getConfig(@Query("path") final String path);

    @PUT("config")
    Observable<Void> update(@Body ConfigData configData);

}

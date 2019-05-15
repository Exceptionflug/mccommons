package de.exceptionflug.mccommons.config.remote.client;

import de.exceptionflug.mccommons.config.remote.client.services.ConfigService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

public class RemoteConfigClient {

    private final ConfigService configService;

    public RemoteConfigClient(final String baseUrl, final String apiKey) {
        final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).addInterceptor(chain -> chain.proceed(chain.request().newBuilder().addHeader("X-API-KEY", apiKey).build())).build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).build();
        configService = retrofit.create(ConfigService.class);
    }

    public ConfigService getConfigService() {
        return configService;
    }

}

package com.rpos.pos.data.remote.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rpos.pos.Config;
import com.rpos.pos.domain.utils.BooleanSerializerDeserializer;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiGenerator {

    private static BooleanSerializerDeserializer booleanSerializerDeserializer = new BooleanSerializerDeserializer();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = httpClient.build();
            return okHttpClient;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .registerTypeAdapter(Boolean.class, booleanSerializerDeserializer)
            .registerTypeAdapter(boolean.class, booleanSerializerDeserializer)
            .create();


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .client(getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit.Builder licencebuilder =
            new Retrofit.Builder()
                    .baseUrl(Config.LICENCE_SERVER)
                    .client(getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit.Builder zatcaBuilder =
            new Retrofit.Builder()
                    .baseUrl(Config.ZATCA_SERVER)
                    .client(getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson));


    public static <S> S createNoTokenApiService(Class<S> serviceClass) {
        if(!httpClient.interceptors().isEmpty()) {
            httpClient.interceptors().clear();
        }

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });


        if (Config.LOG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient
                .connectTimeout(Config.HTTP_CONNECTION_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .readTimeout(Config.HTTP_CONNECTION_READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .writeTimeout(Config.HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createLicenceKeyApiService(Class<S> serviceClass) {
        if(!httpClient.interceptors().isEmpty()) {
            httpClient.interceptors().clear();
        }

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });


        if (Config.LOG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient
                .connectTimeout(Config.HTTP_CONNECTION_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .readTimeout(Config.HTTP_CONNECTION_READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .writeTimeout(Config.HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = licencebuilder.client(client).build();

        return retrofit.create(serviceClass);
    }


    public static <S> S createApiService(Class<S> serviceClass, String API_KEY, String API_SECRET) {
        try {
            if (!httpClient.interceptors().isEmpty()) {
                httpClient.interceptors().clear();
            }

            if (API_KEY != null && API_SECRET != null) {

                final String apiToken = "Token " + API_KEY + ":" + API_SECRET;

                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json")
                                .header("Authorization", apiToken)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
            }

            if (Config.LOG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

            OkHttpClient client = httpClient
                    .connectTimeout(Config.HTTP_CONNECTION_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                    .readTimeout(Config.HTTP_CONNECTION_READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                    .writeTimeout(Config.HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = builder.client(client).build();

            return retrofit.create(serviceClass);

        }catch (Exception e){

            e.printStackTrace();
            Retrofit retrofit = builder.build();
            return retrofit.create(serviceClass);
        }
    }



    public static <S> S createZatcaApiService(Class<S> serviceClass) {
        if(!httpClient.interceptors().isEmpty()) {
            httpClient.interceptors().clear();
        }


            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });


        if (Config.LOG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient
                .connectTimeout(Config.HTTP_CONNECTION_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .readTimeout(Config.HTTP_CONNECTION_READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .writeTimeout(Config.HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = zatcaBuilder.client(client).build();

        return retrofit.create(serviceClass);
    }


    public static <T> void enqueueApiCall(Call<T> call , APIResponseListener responseListener){
        try{

            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                    try{

                        if(response.isSuccessful()){

                            if(response.body()!= null){

                                responseListener.onSuccess(response);

                            }else {

                                if(responseListener!=null){
                                    responseListener.onFailure(API_ERROR.EMPTY_BODY,"body empty");
                                }
                            }

                        }else {

                            if(responseListener!=null){
                                responseListener.onFailure(API_ERROR.RESPONSE_FAILED,"response unsuccessfull");
                            }
                        }

                    }catch (Exception e){
                        responseListener.onFailure(API_ERROR.UNKNOWN_ERROR,"something went wrong");
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    responseListener.onFailure(API_ERROR.UNKNOWN_ERROR,"something went wrong");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public interface APIResponseListener<T>{
        void onSuccess(retrofit2.Response<T> response);
        void onFailure(API_ERROR api_error, String error);
    }

    public enum API_ERROR{
        BACKEND_NOT_RESPONDING,
        RESPONSE_FAILED,
        EMPTY_BODY,
        UNKNOWN_ERROR
    }
}

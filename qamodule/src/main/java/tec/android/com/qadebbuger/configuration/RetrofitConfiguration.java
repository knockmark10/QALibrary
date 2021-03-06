package tec.android.com.qadebbuger.configuration;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import library.android.com.qamodule.R;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import tec.android.com.qadebbuger.configuration.interfaces.AuthenticationService;
import tec.android.com.qadebbuger.configuration.models.AuthenticationRequest;
import tec.android.com.qadebbuger.configuration.models.AuthenticationResponse;

public class RetrofitConfiguration {

    private Context mContext;
    private OkHttpClient.Builder httpClient;
    private Gson gson;
    private AuthenticationService authenticationService;

    public RetrofitConfiguration(Context context) {
        mContext = context;
        httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);
        httpClient.certificatePinner(providesCertificatePinner());
        gson = new GsonBuilder().setLenient().create();
        setup();
    }

    private CertificatePinner providesCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("calm-island-39702.herokuapp.com", "sha256/Vuy2zjFSPqF5Hz18k88DpUViKGbABaF3vZx5Raghplc=")
                .build();
    }

    private void setup() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.qa_base_endpoint))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        authenticationService = retrofit.create(AuthenticationService.class);
    }

    public Observable<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }

}

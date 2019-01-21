package tec.android.com.qadebbuger.configuration.interfaces;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import tec.android.com.qadebbuger.configuration.models.AuthenticationRequest;
import tec.android.com.qadebbuger.configuration.models.AuthenticationResponse;

public interface AuthenticationService {

    @POST("/v1/firebase/authenticate")
    Observable<AuthenticationResponse> authenticate(
            @Body AuthenticationRequest request
    );
}

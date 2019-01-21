package tec.android.com.qadebbuger.managers;

import android.content.Context;

import rx.Observer;
import rx.schedulers.Schedulers;
import tec.android.com.qadebbuger.configuration.RetrofitConfiguration;
import tec.android.com.qadebbuger.configuration.models.AuthenticationRequest;
import tec.android.com.qadebbuger.configuration.models.AuthenticationResponse;

public class FirebaseManager {

    private Context mContext;
    private FirebaseManagerCallback mListener;

    public FirebaseManager(Context context) {
        this.mContext = context;
    }

    public void setAuthenticationListener(FirebaseManagerCallback listener) {
        mListener = listener;
    }

    public void authenticate(AuthenticationRequest request) {
        checkAuthenticationListener();
        RetrofitConfiguration configuration = new RetrofitConfiguration(mContext);
        configuration.authenticate(request)
                .subscribeOn(Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuthenticationResponse>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   mListener.onAuthenticationFailed();
                               }

                               @Override
                               public void onNext(AuthenticationResponse authenticationResponse) {
                                   mListener.onAuthenticationSucceeded();
                               }
                           }
                );
    }

    private void checkAuthenticationListener() {
        if (mListener == null) {
            throw new NullPointerException("You need to call setAuthenticationListener first.");
        }
    }

    public interface FirebaseManagerCallback {
        void onAuthenticationSucceeded();

        void onAuthenticationFailed();
    }

}

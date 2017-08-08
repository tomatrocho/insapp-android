package fr.insapp.insapp.http.interceptors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.App;
import fr.insapp.insapp.activities.IntroActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.notifications.FirebaseService;
import fr.insapp.insapp.utility.Utils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * Created by thomas on 10/07/2017.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        final SharedPreferences credentialsPreferences = App.getAppContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        final SharedPreferences userPreferences = App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();

        // if user is stored, register firebase token

        if (FirebaseService.SHOULD_REGISTER_TOKEN) {
            if (gson.fromJson(userPreferences.getString("user", ""), User.class) != null) {
                FirebaseService.SHOULD_REGISTER_TOKEN = false;

                final SharedPreferences firebaseCredentialsPreferences = App.getAppContext().getSharedPreferences("FirebaseCredentials", Context.MODE_PRIVATE);
                FirebaseService.registerToken(firebaseCredentialsPreferences.getString("token", ""));
            }
        }

        // add session token in a query parameter

        SessionCredentials sessionCredentials = gson.fromJson(credentialsPreferences.getString("session", ""), SessionCredentials.class);

        if (sessionCredentials != null && sessionCredentials.getSessionToken() != null) {
            final String sessionToken = sessionCredentials.getSessionToken().getToken();

            HttpUrl url = request.url().newBuilder().addQueryParameter("token", sessionToken).build();
            request = request.newBuilder().url(url).build();
        }

        Response response = chain.proceed(request);

        switch (response.code()) {

            // does the token need to be refreshed ? (unauthorized)

            case 401:
                LoginCredentials loginCredentials = new Gson().fromJson(credentialsPreferences.getString("login", ""), LoginCredentials.class);

                Call<SessionCredentials> call = ServiceGenerator.create().logUser(loginCredentials);
                retrofit2.Response<SessionCredentials> res = call.execute();

                // if the user has a new auth token, disconnect him from current device

                if (res.code() == 404) {
                    final Context context = App.getAppContext();
                    context.startActivity(new Intent(context, IntroActivity.class));

                    gson.fromJson(userPreferences.getString("user", ""), User.class).clearData();

                    return response;
                }

                final SessionCredentials refreshedSessionCredentials = res.body();

                final HttpUrl url = request.url().newBuilder().setQueryParameter("token", refreshedSessionCredentials.getSessionToken().getToken()).build();
                request = request.newBuilder().url(url).build();

                return chain.proceed(request);

            default:
                return response;
        }
    }
}
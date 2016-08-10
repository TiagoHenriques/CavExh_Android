package pt.deti.cavexh.applicationclass;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

/**
 * Created by tiago on 16/03/16.
 */
public class ApplicationClass extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Branch.getAutoInstance(this);
    }
}


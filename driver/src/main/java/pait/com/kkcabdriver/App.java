package pait.com.kkcabdriver;

import android.app.Application;

import pait.com.kkcabdriver.helper.AppSignatureHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            /*Following will generate the hash code*/
            AppSignatureHelper appSignature = new AppSignatureHelper(this);
            appSignature.getAppSignatures();
        }
    }
}

package cc.growapp.growapp;


import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(

        formUri = "http://growapp.e-nk.ru/acra/report.php",//Non-password protected.
        customReportContent = { /* */ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text

)
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}

package person.wangchen11.crash;

import android.app.Application;
import android.os.Handler;

public class CrashApplication extends Application {
	@Override
	public void onCreate() {
		final CrashHandler mCrashHandler = new CrashHandler(getApplicationContext());
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
			}
		},1000);
		super.onCreate();
	}
}

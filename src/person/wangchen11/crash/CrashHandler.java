package person.wangchen11.crash;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import person.wangchen11.gnuccompiler.GNUCCompiler;

import android.annotation.SuppressLint;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	@SuppressLint("SdCardPath")
	private static final String mCrashLogFile = GNUCCompiler.getWorkSpaceDir() + "/qeditor_crash.log";
	public CrashHandler() {
	}
	
	private static UncaughtExceptionHandler mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handleUncaughtException(thread,ex))
			mDefaultCrashHandler.uncaughtException(thread, ex);
	}
	
	public boolean handleUncaughtException(Thread thread, Throwable ex) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);
		ex.printStackTrace(printStream);
		printStream.close();
		String msg = arrayOutputStream.toString();
		Log.i(TAG, "handleUncaughtException:"+msg);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(mCrashLogFile);
			try {
				fileOutputStream.write(msg.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}

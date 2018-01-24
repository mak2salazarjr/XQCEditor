package person.wangchen11.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingerThreadPool {
	private static ExecutorService mExecutorService = null;
	public static synchronized ExecutorService getPublicThreadPool(){
		if(mExecutorService==null){
			mExecutorService = Executors.newFixedThreadPool(1);
		}
		return mExecutorService;
	}
}

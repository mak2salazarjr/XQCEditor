package person.wangchen11.questions;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import person.wangchen11.busybox.Busybox;
import person.wangchen11.gnuccompiler.CheckInfo;
import person.wangchen11.gnuccompiler.GNUCCodeCheck;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.gnuccompiler.GNUCCompiler2;
import android.content.Context;

public class CompileTask extends QuestionTask {
	private String mFileIn = null;
	private String mFileOut = null;
	private String mCompileCmd = null;
	private Exception mException = null;
	private boolean mSuccess = false;
	private String mRuselt = "";
	private LinkedList<CheckInfo> mCheckInfos = new LinkedList<CheckInfo>();
	public CompileTask(Context context,String name,OnTaskCompliteListener compliteListener,int timeOut,String fileIn) {
		super(context,name,compliteListener, timeOut);
		mFileIn = fileIn;
		mFileOut = GNUCCompiler.getTempFilePath(context);
		
		mCompileCmd = Busybox.getCmd(context);
		mCompileCmd += GNUCCompiler2.getExportEnvPathCmd(context);
		mCompileCmd += GNUCCompiler.getCompilerCmd(context, new File(mFileIn), new File(mFileOut), "");
	}
	
	public boolean isSuccess(){
		return mSuccess;
	}
	
	public String getFileOut(){
		return mFileOut;
	}
	
	@Override
	public String getCompliteMsg(Context context) {
		if(mException!=null){
			if(mException instanceof TimeoutException){
				return "��ʱ";
			}else{
				return mException.getMessage();
			}
		}
		
		if(isSuccess()){
			int warns = getWarnsNumber();
			return "�ɹ�" + (warns>0?("("+warns+"����)"):"");
		}else{
			int warns = getErrorNumber();
			return "ʧ��" + (warns>0?("("+warns+"����)"):"");
		}
	}
	
	@Override
	public int getMarks() {
		if(mException!=null){
			return 0;
		}
		if(isSuccess()){
			return 0-getWarnsNumber();
		}
		return 0;
	}
	
	@Override
	public int getColor() {
		if(mException!=null){
			return RED;
		}
		if(isComplite()){
			if(isSuccess()){
				if(getWarnsNumber()==0){
					return GREEN;
				}
				return YELLOW;
			}else{
				return RED;
			}
		}
		return super.getColor();
	}
	
	public int getWarnsNumber(){
		int number = 0;
		for(CheckInfo checkInfo:mCheckInfos){
			if(checkInfo.mType == CheckInfo.TYPE_WARN)
				number ++;
		}
		return number;
	}
	
	public int getErrorNumber(){
		int number = 0;
		for(CheckInfo checkInfo:mCheckInfos){
			if(checkInfo.mType == CheckInfo.TYPE_ERROR)
				number ++;
		}
		return number;
	}
	
	
	@Override
	public void inRun() {
		File out = new File(mFileOut);
		try {
			String ret = runCmd(mCompileCmd,getTimeOut(),true);
			mRuselt = ret;
			out = new File(mFileOut);
			if(out.isFile()&&out.length()>0){
				mSuccess = true;
				mCheckInfos = GNUCCodeCheck.dexErrorPutMsg(ret);
			}
		} catch (IOException e) {
			mException = e;
		} catch (TimeoutException e) {
			mException = e;
		}
	}
	
}

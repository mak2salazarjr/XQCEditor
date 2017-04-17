package jackpal.androidterm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import person.wangchen11.busybox.Busybox;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.plugins.PluginsManager;
import person.wangchen11.xqceditor.R;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.TermSession.FinishCallback;
import jackpal.androidterm.util.TermSettings;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class TermFragment extends Fragment implements FinishCallback{
	TermSession mTermSession = null;
	String mInitCmd="cd /sdcard/\nls\n";
	private String mHome="/sdcard/";
	public TermFragment() {
	}

	public TermFragment(String cmd,String home){
		mInitCmd=cmd;
		mHome = home;
	}

	
	public TermFragment(String cmd,boolean runAsSu,String home){
		mInitCmd=cmd;
		mHome = home;
	}
	
	public String getInitCmdEx(String cmd)
	{
		if(cmd == null||cmd.length()<0)
			return "";
		cmd = PluginsManager.getCmd(getActivity())+cmd;
		String ret = "";//"cd $HOME\n";
		File file = getNextRunnableSh(this.getActivity());
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				fileOutputStream.write(cmd.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		file.setExecutable(true, false);
		ret += "source "+file.getAbsolutePath();
		return ret;
	}
	
	private static int mShNumber = 0;
	private static File getNextRunnableSh(Context context)
	{
		String runnablePath = context.getFilesDir().getAbsolutePath();
		File []files = new File(runnablePath).listFiles();
		if(files!=null)
		for (File file2 : files) {
			if(file2.getName().endsWith(".tsh"))
				file2.delete();
		}
		File file = null;
		for(int i=0;i<1000;i++)
		{
			file = new File(runnablePath+"/"+mShNumber+".tsh");
			mShNumber++;
			if(!file.isFile())
				break;
		}
		return file;
	}
	
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_term, null);
		RelativeLayout workSpaceLayout = (RelativeLayout) relativeLayout.findViewById(R.id.layout_work_space);
		mTermSession = createTermSession();
		mTermSession.setFinishCallback(this);
		workSpaceLayout.addView(createEmulatorView(mTermSession), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		return relativeLayout;
	}

    private EmulatorView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView termView = new TermView(getActivity(), session, metrics);
        termView.updatePrefs(mTermSettings);
        EmulatorView emulatorView = termView;
        registerForContextMenu(emulatorView);
        return emulatorView;
    }

    private TermSettings mTermSettings = null;
    private TermSession createTermSession() {
    	TermSettings settings = new TermSettings(getResources(), getActivity().getPreferences(0));
    	mTermSettings = settings;
    	mTermSettings.setAppendPath(Busybox.getWorkDir(getActivity())+":"
    			+GNUCCompiler.getCcPath(getActivity())+":"
    			+GNUCCompiler.getAbiPath(getActivity())+":"
    			+GNUCCompiler.getGccPath(getActivity())
    			);
    	mTermSettings.setHomePath(mHome);
    	TermSession session = createTermSession(getActivity(), settings, getInitCmdEx(mInitCmd) );
        return session;
    }
    
    protected static TermSession createTermSession(Context context, TermSettings settings, String initialCommand) {
        ShellTermSession session = new ShellTermSession(settings, initialCommand);
        session.initializeEmulator(64, 64);
        session.setProcessExitMessage("do you want exit?");
        return session;
    }
    
    public void destory()
    {
    	if(mTermSession.isRunning())
    		mTermSession.finish();
    }

	@Override
	public void onSessionFinish(TermSession session) {
		if(mFinishCallback!=null)
			mFinishCallback.onSessionFinish(session);
	}
	
	private TermSession.FinishCallback mFinishCallback = null;
	public void setFinishCallback(TermSession.FinishCallback callback)
	{
		mFinishCallback = callback;
	}

}

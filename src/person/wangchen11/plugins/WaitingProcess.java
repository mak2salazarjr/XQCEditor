package person.wangchen11.plugins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class WaitingProcess implements Runnable{
	private static ExecutorService mExecutorService = null;
	private AlertDialog mAlertDialog = null;
	private TextView mTextView = null;
	private ProgressBar mProgressBar = null;
	
	public WaitingProcess(Context context) {
		this(context,null);
	}
	
	public WaitingProcess(Context context,int textId){
		this(context,context.getText(textId));
	}
	
	@SuppressLint("InflateParams") 
	public WaitingProcess(Context context,CharSequence title) {
		AlertDialog.Builder builder;
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_process, null);
		mTextView = (TextView) viewGroup.findViewById(R.id.textViewMsg);
		mProgressBar = (ProgressBar) viewGroup.findViewById(R.id.progressBar_process);
		builder=new AlertDialog.Builder(context);
		mAlertDialog=builder.create();
		mAlertDialog.setTitle(title);
		mAlertDialog.getWindow().setContentView(viewGroup);
	}
	
	public void start() {
		if(mExecutorService==null)
			mExecutorService = Executors.newSingleThreadExecutor();
		mAlertDialog.show();
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				// TODO
				this.run();
				mAlertDialog.dismiss();
			}
		});
	}
	
	// [0~1]
	public void setProcess(int process){
		mProgressBar.setProgress(process);
	}
	
	public void setMsg(String msg){
		mTextView.setText(msg);
	}
}

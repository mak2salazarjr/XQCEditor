package person.wangchen11.questions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.ext.CEditor;
import person.wangchen11.xqceditor.R;

public class QuestionFloatWindow {
	private WindowsManager mWindowsManager = null;
	private Question mQuestion = null;
	private FloatBall mFloatBall = null;
	private QuestionDialog mQuestionDialog = null;
	
	private QuestionFloatWindow(WindowsManager windowsManager,Question question) {
		mWindowsManager = windowsManager;
		mQuestion = question;
		mQuestionDialog = new QuestionDialog(mWindowsManager.getContext(),question,mWindowsManager);
		mFloatBall = new FloatBall(getContext()){
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				mQuestionDialog.show();
				return true;
			}
		};
		mFloatBall.setAlpha(0.6f);
	}
	
	public Context getContext(){
		return mWindowsManager.getContext();
	}
	
	private static View findParentViewById(View view,int id){
		if(view.getId()==id)
			return view;
		ViewParent viewParent = view.getParent();
		if(viewParent instanceof View)
			return findParentViewById((View)viewParent,id);
		return null;
	}
	
	private static QuestionFloatWindow mQuestionFloatWindow = null;
	public static void startQuestionMode(WindowsManager windowsManager,Question  question){
		QuestionManager.init(windowsManager.getContext());
		stopQusetionMode();
		mQuestionFloatWindow = new QuestionFloatWindow(windowsManager, question);
		RelativeLayout layout = (RelativeLayout) findParentViewById(mQuestionFloatWindow.mWindowsManager.getTitleListView(),R.id.editor_layout);
		layout.addView(mQuestionFloatWindow.mFloatBall);
		mQuestionFloatWindow.mQuestionDialog.show();
		String codePath = QuestionManager.instance().getQuestionCodeFile(question);
		if(codePath!=null){
			File file = new File(codePath);
			file.getParentFile().mkdirs();
			if(!file.isFile())
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(CCodeTemplate.mRunableCode.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			windowsManager.addWindow(new CEditor(windowsManager,file));
		}
	}
	
	public static void stopQusetionMode(){
		if(mQuestionFloatWindow!=null){
			final RelativeLayout layout = (RelativeLayout) findParentViewById(mQuestionFloatWindow.mWindowsManager.getTitleListView(),R.id.editor_layout);
			final View removeFloatBall = mQuestionFloatWindow.mFloatBall;
			layout.getHandler().post(new Runnable() {
				@Override
				public void run() {
					layout.removeView(removeFloatBall);
				}
			});
			mQuestionFloatWindow.mQuestionDialog.dismiss();
			mQuestionFloatWindow = null;
		}
	}
	
}

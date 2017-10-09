package person.wangchen11.questions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.ext.CEditor;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

@SuppressLint("InflateParams") 
public class QuestionDialog extends AlertDialog implements AlertDialog.OnClickListener {
	private Question mQuestion = null;
	private View mView = null;
	private WindowsManager mWindowsManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.commit_cur_code), this);
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.quit_question_mode), this);
		setButton(BUTTON_NEUTRAL, getContext().getString(R.string.wait), this);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		mView = layoutInflater.inflate(R.layout.dialog_question, null);
		setView(mView);
		configQuestion();
		super.onCreate(savedInstanceState);
	}
	
	protected QuestionDialog(Context context,Question question,WindowsManager windowsManager) {
		super(context);
		mQuestion = question;
		mWindowsManager = windowsManager;
	}
	
	public void configQuestion(){
		if(mView==null)
			return ;
		TextView textViewQuestion = (TextView) mView.findViewById(R.id.textViewQuestion);
		textViewQuestion.setText(mQuestion.getQuestion(getContext()));

		TextView textViewTitle = (TextView) mView.findViewById(R.id.textViewTitle);
		QuestionGroup questionGroup = QuestionManager.instance().getQuestionGroup(mQuestion);
		textViewTitle.setText(questionGroup.getName()+" - "+mQuestion.mTitle);
		

		TextView textViewInput1 = (TextView) mView.findViewById(R.id.textViewInput1);
		TextView textViewInput2 = (TextView) mView.findViewById(R.id.textViewInput2);
		TextView textViewInput3 = (TextView) mView.findViewById(R.id.textViewInput3);
		TextView textViewOutput1 = (TextView) mView.findViewById(R.id.textViewOutput1);
		TextView textViewOutput2 = (TextView) mView.findViewById(R.id.textViewOutput2);
		TextView textViewOutput3 = (TextView) mView.findViewById(R.id.textViewOutput3);

		textViewInput1.setText(""+mQuestion.getInput(getContext(), 0));
		textViewInput2.setText(""+mQuestion.getInput(getContext(), 1));
		textViewInput3.setText(""+mQuestion.getInput(getContext(), 2));

		textViewOutput1.setText(""+mQuestion.getOutput(getContext(), 0));
		textViewOutput2.setText(""+mQuestion.getOutput(getContext(), 1));
		textViewOutput3.setText(""+mQuestion.getOutput(getContext(), 2));

		Button buttonPre =  (Button) mView.findViewById(R.id.buttonPreQuestion);
		Button buttonAnwser =  (Button) mView.findViewById(R.id.buttonAnswer);
		Button buttonNext =  (Button) mView.findViewById(R.id.buttonNextQuestion);
		buttonPre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Question question = QuestionManager.instance().getPreQuestion(mQuestion);
				if(question==null){
					ToastUtil.showToast("没有更多的题目了！", Toast.LENGTH_SHORT);
				}else{
					QuestionFloatWindow.startQuestionMode(mWindowsManager, question);
				}
			}
		});
		
		buttonNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Question question = QuestionManager.instance().getNextQuestion(mQuestion);
				if(question==null){
					ToastUtil.showToast("没有更多的题目了！", Toast.LENGTH_SHORT);
				}else{
					QuestionFloatWindow.startQuestionMode(mWindowsManager, question);
				}
			}
		});
		
		buttonAnwser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WindowPointer windowPointer = mWindowsManager.getSelectWindow();
				if(windowPointer.mWindow instanceof CEditor){
					CEditor ceditor = (CEditor) windowPointer.mWindow;
					ceditor.setText(mQuestion.getAnwser(getContext()));
					QuestionDialog.this.dismiss();
				} else {
					ToastUtil.showToast("请在c/c++代码编辑界面点击此选项!", Toast.LENGTH_SHORT);
				}
			}
		});
		
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		super.show();

		getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_POSITIVE);
			}
		});
		getButton(BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_NEGATIVE);
			}
		});
		getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_NEUTRAL);
			}
		});
		
		Setting.applySettingConfigToAllView(mView);
		mView.setBackgroundDrawable(new ColorDrawable(Setting.mConfig.mEditorConfig.mBackGroundColor));
		
		LayoutParams attributes = getWindow().getAttributes();
		attributes.flags = LayoutParams.FLAG_DIM_BEHIND;
		attributes.dimAmount = 0.4f;
		getWindow().setAttributes(attributes);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			break;
		case BUTTON_NEUTRAL:
			dismiss();
			break;
		case BUTTON_NEGATIVE:
			QuestionFloatWindow.stopQusetionMode();
			dismiss();
			break;

		default:
			break;
		}
	}
}

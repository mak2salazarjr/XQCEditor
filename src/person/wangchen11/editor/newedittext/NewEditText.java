package person.wangchen11.editor.newedittext;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.codeedittext.CodeEditText.CodeType;
import person.wangchen11.editor.codeedittext.ArmAsmCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CPPCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter.CodeStypeAdapterListener;
import person.wangchen11.editor.codeedittext.JavaCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.OnNeedChangeWants;
import person.wangchen11.editor.codeedittext.PHPCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.ShellCodeStyleAdapter;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.editor.edittext.WarnAndError;
import person.wangchen11.gnuccompiler.GNUCCompiler;

import com.editor.text.TextEditorView;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;

public class NewEditText extends TextEditorView implements CodeStypeAdapterListener {
	protected static final String TAG="NewEditText";
	private CodeType mCodeType;
	private OnNeedChangeWants mOnNeedChangeWants;
	public NewEditText(Context context) {
		super(context);
		init();
	}

	public NewEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
	}

	public void setOnNeedChangeWants(OnNeedChangeWants onNeedChangeWants) {
		mOnNeedChangeWants = onNeedChangeWants;
	}

	private static ExecutorService mExecutor = null;

	private AfterTextChangeListener mAfterTextChangeListener = null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener) {
		mAfterTextChangeListener = afterTextChangeListener;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(mAfterTextChangeListener!=null)
			mAfterTextChangeListener.afterTextChange();
		postUpdateCodeStyle();
	}
	
	private Runnable mUpdateChodeStyleRunnable = new Runnable() {
		@Override
		public void run() {
			updateCodeStyle();
		}
	};
	
	private void postUpdateCodeStyle(){
		if(getHandler()!=null){
			getHandler().removeCallbacks(mUpdateChodeStyleRunnable);
			getHandler().postDelayed(mUpdateChodeStyleRunnable,20);
		}
	}
	
	private void updateCodeStyle(){
		Log.i(TAG, "updateCodeStyle");
		if(mExecutor==null){
			mExecutor=Executors.newSingleThreadExecutor();
		}
		try {
			Log.i(TAG, "mExecutor.execute");
			File file=(getTag() instanceof File )?((File)getTag()):null;
			String path="";
			if(file !=null)
				path=file.getParent();
			Runnable runnable=null;
			if(mCodeType==CodeType.TYPE_C){
				runnable=new CCodeStyleAdapter(getText().toString(),getCursor(),path
						,GNUCCompiler.getIncludeDir(),getHandler(),this) ;
			}
			else
			if(mCodeType==CodeType.TYPE_CPP){
				runnable=new CPPCodeStyleAdapter(getText().toString(),getCursor(),path
						,GNUCCompiler.getIncludeDir(),getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_JAVA){
				runnable=new JavaCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_SHELL){
				runnable=new ShellCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_ARM_ASM){
				runnable=new ArmAsmCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_PHP){
				runnable=new PHPCodeStyleAdapter(getHandler(),getText().toString(),getCursor(), this);
			}
			else
			{
				//EditableWithLayout editableWithLayout=getText();
				//editableWithLayout.applyColorSpans(new ArrayList<SpanBody>());
				//postInvalidate();
			}
			if(runnable!=null)
				mExecutor.execute(runnable);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	private int getCursor() {
		return getSelectionEnd();
	}

	@Override
	public int checkLength()
	{
		return getText().length();
	}
	
	@Override
	public void parserComplete(CodeStyleAdapter adapter,List<SpanBody> spanBodies) {
		Log.i(TAG, "runComplete");
		if(checkLength()==adapter.length())
		{
			Log.i(TAG, "applyColorSpans");
			/*
			if(spanBodies==null)
				spanBodies=new ArrayList<SpanBody>();
			editableWithLayout.applyColorSpans(spanBodies);
			*/
			postInvalidate();
		}
	}

	@Override
	public void getWantComplete(CodeStyleAdapter adapter,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants) {
		if(checkLength()==adapter.length())
		{
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(wantChangeStart, wantChangeEnd, wants);
		}
	}
	public void setWarnAndError(LinkedList<WarnAndError> cWarnAndErrors) {
	}

	public void setCodeType(CodeType type) {
		mCodeType = type;
	}
}

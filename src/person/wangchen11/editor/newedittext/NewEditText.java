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
import person.wangchen11.editor.codeedittext.TextCodeStyleAdapter;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.editor.edittext.WarnAndError;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.window.ext.Setting;

import com.editor.text.TextEditorView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class NewEditText extends TextEditorView implements CodeStypeAdapterListener {
	protected static final String TAG="NewEditText";
	private CodeType mCodeType;
	private OnNeedChangeWants mOnNeedChangeWants;
	private int mValidHeadLen = -1;
	private int mValidTailLen = -1;
	private Handler mHandler;
	private LinkedList<WarnAndError> mWarnAndErrors = null;
	
	public NewEditText(Context context) {
		super(context);
		init();
	}

	public NewEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mHandler = new Handler();
	}

	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	public void setOnNeedChangeWants(OnNeedChangeWants onNeedChangeWants) {
		mOnNeedChangeWants = onNeedChangeWants;
	}

	private static ExecutorService mExecutor = null;

	private AfterTextChangeListener mAfterTextChangeListener = null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener) {
		mAfterTextChangeListener = afterTextChangeListener;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(0, 0, null);
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(mAfterTextChangeListener!=null)
			mAfterTextChangeListener.afterTextChange();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				postUpdateCodeStyle();
			}
		});
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int end   = start+count;

		Editable editable = getText();
		ForegroundColorSpan foreSpans[] = editable.getSpans(start,start+count,
													ForegroundColorSpan.class);
		
		for(ForegroundColorSpan foregroundColorSpan:foreSpans){
			start = Math.min(start, editable.getSpanStart(foregroundColorSpan));
			end   = Math.max(end  , editable.getSpanEnd  (foregroundColorSpan));
		}
		if(mValidHeadLen==-1){
			mValidHeadLen = start;
		} else {
			mValidHeadLen = Math.min(start, mValidHeadLen);
		}
		if(mValidTailLen==-1){
			mValidTailLen = end;
		}else{
			mValidTailLen = Math.min(s.length()-end, mValidTailLen);
		}
		super.onTextChanged(s, start, before, count);
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
			getHandler().postDelayed(mUpdateChodeStyleRunnable,100);
		}else{
			Log.i(TAG, "postUpdateCodeStyle getHandler()==null");
		}
	}
	
	private void updateCodeStyle(){
		long startTime = System.currentTimeMillis();
		
		if(mExecutor==null){
			Log.i(TAG, "newSingleThreadExecutor");
			mExecutor=Executors.newSingleThreadExecutor();
		}
		try {
			Log.i(TAG, "mExecutor.execute");
			Log.i(TAG, "updateCodeStyle:"+mCodeType);
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
				runnable=new TextCodeStyleAdapter(getHandler(),getText().length(), this);
				//EditableWithLayout editableWithLayout=getText();
				//editableWithLayout.applyColorSpans(new ArrayList<SpanBody>());
				//postInvalidate();
			}
			if(runnable!=null)
				mExecutor.execute(runnable);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		Log.i(TAG, "updateCodeStyle used:"+(System.currentTimeMillis()-startTime));
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
		Log.i(TAG, "parserComplete");
		long startTime = System.currentTimeMillis();
		if (!Setting.mConfig.mCEditorConfig.mEnableHighLight) {
			Editable editable = getText();
			ForegroundColorSpan foreSpans[] = editable.getSpans(0,
					editable.length(), ForegroundColorSpan.class);
			for (int n = foreSpans.length; n-- > 0;)
				editable.removeSpan(foreSpans[n]);
		}
		else
		if(checkLength()==adapter.length())
		{
			Editable editable = getText();
			
			if(mValidHeadLen==-1||mValidTailLen==-1){
				mValidHeadLen = 0;
				mValidTailLen = 0;
			}
			int invalidStart = mValidHeadLen;
			int invalidEnd   = editable.length()-mValidTailLen;
			ForegroundColorSpan foreSpans[] = editable.getSpans(invalidStart, invalidEnd,
														ForegroundColorSpan.class);

			for (int n = foreSpans.length; n-- > 0;)
				editable.removeSpan(foreSpans[n]);

			for(SpanBody spanBody:spanBodies){
				if(		spanBody.mSpan == CodeStyleAdapter.mCommentsColorSpan ||
						spanBody.mSpan == CodeStyleAdapter.mConstantColorSpan ||
						spanBody.mSpan == CodeStyleAdapter.mKeywordsColorSpan ||
						spanBody.mSpan == CodeStyleAdapter.mProKeywordsColorSpan
						)
				if(spanBody.hasSub(invalidStart, invalidEnd)){
					editable.setSpan(new ForegroundColorSpan( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() )
						, spanBody.mStart, spanBody.mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			mValidHeadLen = -1;
			mValidTailLen = -1;
		}
		Log.i(TAG, "parserComplete used:"+(System.currentTimeMillis()-startTime));
	}

	@Override
	public void getWantComplete(CodeStyleAdapter adapter,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants) {
		if(checkLength()==adapter.length())
		{
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(wantChangeStart, wantChangeEnd, wants);
		}
	}
	
	public void setWarnAndError(LinkedList<WarnAndError> warnAndErrors) {
		mWarnAndErrors = warnAndErrors;
		invalidate();
	}

	public void setCodeType(CodeType type) {
		Log.i(TAG, "setCodeType:"+type);
		mValidHeadLen = 0;
		mValidTailLen = 0;
		mCodeType = type;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				postUpdateCodeStyle();
			}
		});
	}
}

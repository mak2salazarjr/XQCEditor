package person.wangchen11.editor.codeedittext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter.CodeStypeAdapterListener;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.editor.edittext.EditableWithLayout;
import person.wangchen11.editor.edittext.MyEditText;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CodeEditText extends MyEditText implements CodeStypeAdapterListener{
	protected static final String TAG="CCodeEditText";
	public enum CodeType {
		TYPE_NONE,
		TYPE_C,
		TYPE_CPP,
		TYPE_JAVA,
		TYPE_SHELL,
		TYPE_ARM_ASM,
		TYPE_PHP,
	}
	private CodeType mCodeType=CodeType.TYPE_CPP;
	
	public void setCodeType(CodeType type)
	{
		mCodeType = type;
		updateCodeStyle();
	}
	
	public CodeEditText(Context context) {
		super(context);
	}
	
	public CodeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setText(CharSequence charSequence) {
		super.setText(charSequence);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mOnNeedChangeWants!=null && event.getAction() == MotionEvent.ACTION_DOWN )
			mOnNeedChangeWants.onNeedChangeWants(0, 0, null);
		return super.onTouchEvent(event);
	};
	
	private static ExecutorService mExecutor = null;
	
	@Override
	public void afterTextChanged(Editable s) {
		super.afterTextChanged(s);
		if(mAfterTextChangeListener!=null)
			mAfterTextChangeListener.afterTextChange();
		updateCodeStyle();
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
				EditableWithLayout editableWithLayout=(EditableWithLayout)getText();
				editableWithLayout.applyColorSpans(new ArrayList<SpanBody>());
				postInvalidate();
			}
			if(runnable!=null)
				mExecutor.execute(runnable);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	@Override
	public int checkLength()
	{
		return getText().length();
	}
	
	@Override
	public void parserComplete(CodeStyleAdapter adapter,List<SpanBody> spanBodies) {
		Log.i(TAG, "runComplete");
		if(checkLength()==adapter.length()  && getText() instanceof EditableWithLayout )
		{
			Log.i(TAG, "applyColorSpans");
			EditableWithLayout editableWithLayout=(EditableWithLayout)getText();
			if(spanBodies==null)
				spanBodies=new ArrayList<SpanBody>();
			editableWithLayout.applyColorSpans(spanBodies);
			postInvalidate();
		}
	}

	@Override
	public void getWantComplete(CodeStyleAdapter adapter,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants) {
		if(checkLength()==adapter.length()  && getText() instanceof EditableWithLayout )
		{
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(wantChangeStart, wantChangeEnd, wants);
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Editable editable=getText();
		int start=Selection.getSelectionStart(editable);
		int end=Selection.getSelectionEnd(editable);
		if(start==end&&keyCode==KeyEvent.KEYCODE_ENTER){
			if(!insertNewLine(end))
			{
				editable.replace(start, end, "\n");
				setSelection(start+1, start+1);
			}
			return true;
		}else
		return super.onKeyUp(keyCode, event);
	}
	
	public boolean insertNewLine(int position){
		Editable editable=getText();
		int numberOfNull=0; //一个'\t' 4个空格 
		int numberOfK=0;
		for(int index=position-1;index>=0;index--){
			//向前查找 '{' 
			char indexch=editable.charAt(index);
			if(indexch=='\n')
				break;
			if(indexch==' '){
				numberOfNull++;
			}else
			if(indexch=='\t'){
				numberOfNull+=4;
			}else
			if(indexch=='{')
			{
				numberOfK+=4;
			}else{
				numberOfNull=0;
			}
		}
		
		for(int index=position;index<editable.length();index++){
			//向后查找 '}' 
			char indexch=editable.charAt(index);
			if(indexch=='\n')
				break;
			if(indexch==' '){
			}else
			if(indexch=='\t'){
			}else
			if(indexch=='}')
			{
				numberOfK-=4;
			}
		}
		if(numberOfK<0)
			numberOfK=0;
		numberOfNull+=numberOfK;
		if(numberOfNull<0)
			numberOfNull=0;
		int end=position;
		for(;end<editable.length()-1;end++){
			char indexch=editable.charAt(end);
			if( !(indexch == '\t' || indexch == ' ') )
				break;
		}
		String str="\n";
		for(;numberOfNull>=4;numberOfNull-=4){
			str+='\t';
		}
		for(;numberOfNull>0;numberOfNull--){
			str+=' ';
		}
		
		editable.replace(position, end, str);
		setSelection(position+str.length(), position+str.length());
		return true;
	}
	
	public void onWantSelect(int start,int end,String str){
		
	}
	
	private AfterTextChangeListener mAfterTextChangeListener=null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener){
		mAfterTextChangeListener=afterTextChangeListener;
	}
	
	private OnNeedChangeWants mOnNeedChangeWants=null;
	public void setOnNeedChangeWants(OnNeedChangeWants onNeedChangeWants){
		mOnNeedChangeWants=onNeedChangeWants;
	}
}



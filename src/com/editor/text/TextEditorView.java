package com.editor.text;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.Stack;
import java.util.regex.*;

import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.window.ext.Setting;

public class TextEditorView extends EditText {
	private static final String TAG = "TextEditorView";
	private Paint mTextPaint;
	private Handler mUpdateHandler ;
	private int updateDelay,errorLine;
	private boolean modified = true;
	private float mSpaceWidth = 0;
	
	private boolean mSaveToHistory = true;
	private int mMaxSaveHistory = 20;
	private Stack<ReplaceBody> mUndoBodies = new Stack<ReplaceBody>();
	private Stack<ReplaceBody> mRedoBodies = new Stack<ReplaceBody>();

	private Pattern line,number,headfile;
	private Pattern string,keyword;
	private Pattern pretreatment,builtin ;
	private Pattern comment,trailingWhiteSpace ;
	
	private Runnable updateThread = new Runnable() {
		@Override
		public void run() {
			Editable edit = getText();
			highlightWithoutChange(edit);
		}
	};


	public TextEditorView(Context context) {
		super(context, null);
	}

	public TextEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		setGravity(Gravity.TOP);
		getPaint().setTypeface(Typeface.MONOSPACE);
		getPaint().setColor(Setting.mConfig.mEditorConfig.mBaseFontColor);
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(0xff888888);
		setHorizontallyScrolling(true);
		mUpdateHandler = new Handler();
		setFilters(new InputFilter[] {inputFilter});
		addTextChangedListener(watcher);
		setTextScale(Setting.mConfig.mEditorConfig.mFontScale);
		setLineSpacing(0, 1.12f*Setting.mConfig.mEditorConfig.mLineScale);
		initPattern();
	}

	private float mTextScale = 1.0f;
	public void setTextScale(float textScale){
		mTextScale = textScale;
		float textSize = dip2px(getContext(),12)*mTextScale;
		mTextPaint.setTypeface(Typeface.MONOSPACE);
		mTextPaint.setTextSize(textSize);
		mSpaceWidth = mTextPaint.measureText(" ");
		getPaint().setTextSize(textSize);
		setPadding(getBoundOfLeft(), 0, 0, 0);
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		setPadding(getBoundOfLeft(), 0, 0, 0);
		if(mAfterTextChangeListener != null)
			mAfterTextChangeListener.afterTextChange();
	}
	
	public void initPattern() {
		line = Pattern.compile(".*\\n");
		headfile =Pattern.compile("#\\b(include)\\b\\s*<\\w*(/?.*/?)[\\w+|h]>[^\"]");
		number = Pattern.compile("\\b(\\d*[.]?\\d+)\\b");
		string = Pattern.compile("\"(\\\"|.)*?\"");
		keyword = Pattern.compile("[^<,\",|]\\b(auto|int|short|double|float|void|long|signed|unsigned|char|struct|public|protected|private|class|union|bool|string|vector|typename|"
								   + "do|for|while|if|else|switch|case|default|new|delete|true|false|typedef|static|const|register|extern|volatile|"
								   + "goto|return|continue|break|using|namespace|try|catch|import|package)\\b[^>,|\"]");
		pretreatment = Pattern.compile("[^\"]#\\b(ifdef|ifndef|define|undef|if|else|elif|endif|pragma|"
									   + "error|line)\\b[\\s|\\S].*[^\"]");
		builtin = Pattern.compile("[^\"]\\b(printf|scanf|std::|cout|cin|cerr|clog|endl|template|"
								   + "sizeof)\\b[^\"]");		
		comment = Pattern.compile("/\\*(.|[\r\n])*?(\\*/)|/\\*(.|[\r\n])*|[^\"](?<!:)//.*[^\"]");
		trailingWhiteSpace = Pattern.compile("[\\t ]+$", Pattern.MULTILINE);
	}

	public float dip2px(Context context, float dpValue) {
		float scale =context.getResources().getDisplayMetrics().density;
		return dpValue * scale + 0.5f;
	}

	public float px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return pxValue / scale + 0.5f;
	}
	
	private InputFilter inputFilter = new InputFilter(){

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
								   Spanned dest, int dstart, int dend) {
			if (modified && end - start == 1 && start < source.length()
				&& dstart < dest.length()) {
				char c = source.charAt(start);

				if (c == '\n')
					return autoIndent(source, start, end, dest, dstart,dend);
			}
			return source;
		}
	};

    private TextWatcher watcher = new TextWatcher(){
    	
    	int beforeStart = 0;
    	int beforEnd = 0;
    	CharSequence beforCharSequence = "";
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	Log.i(TAG, "beforeTextChanged"+":"+start+":"+count+":"+after);
        	beforeStart = start;
        	beforEnd = start+count;
        	beforCharSequence = s.subSequence(beforeStart, beforEnd);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
    		//saved to history to be undo or redo 
    		if(mSaveToHistory){
    			if(mMaxSaveHistory>0){
    				if(mUndoBodies.size()>mMaxSaveHistory)
    					mUndoBodies.remove(0);
    				ReplaceBody body=new ReplaceBody(beforeStart, beforEnd, beforCharSequence,s.subSequence(start, start+count), 0, count, 0, 0);
    				if(!mUndoBodies.isEmpty() && mUndoBodies.peek().addBody(body))
    				{
    				}
    				else
    					mUndoBodies.push(body);
    			}
    		}
    		
        	Log.i(TAG, "onTextChanged"+":"+start+":"+before+":"+count);
        }

        @Override
        public void afterTextChanged(Editable edit) {
        	Log.i(TAG, "afterTextChanged");
			setPadding(getBoundOfLeft(), 0, 0, 0);
			if(mAfterTextChangeListener != null)
				mAfterTextChangeListener.afterTextChange();
			
			cancelUpdate();
			if (!modified)
				return;
			mUpdateHandler.postDelayed(updateThread, updateDelay);
        }
	};



	public void setTextHighlighted(CharSequence text) {
		cancelUpdate();

		errorLine = 0;
		modified = false;
		setText(highlight(new SpannableStringBuilder(text)));
		modified = true;
	}



	public String getCleanText() {
		return trailingWhiteSpace.matcher(getText()).replaceAll("");
	}

	
	
	public void refresh() {
		highlightWithoutChange(getText());
	}

	
	public void cancelUpdate() {
		mUpdateHandler.removeCallbacks(updateThread);
	}
	

	public void highlightWithoutChange(Editable e) {
		modified = false;
		highlight(e);
		modified = true;
	}

	public Editable highlight(Editable edit) {
		Log.i(TAG, "highlight:"+edit.length());
		try {
			clearSpans(edit);

			if (edit.length() == 0)
				return edit;

			if (errorLine > 0) {
				line.matcher(edit);
			}

			for (Matcher m = headfile.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mConstantColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			for (Matcher m = number.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mConstantColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = string.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mConstantColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = keyword.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mKeywordsColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = pretreatment.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mProKeywordsColor),
							 m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = builtin.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mWordsColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = comment.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(CodeStyleAdapter.mCommentsColor), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception ex) {
		}

		return edit;
	}
	
	public void clearSpans(Editable e) {

		ForegroundColorSpan foreSpan[] = e.getSpans(0, e.length(),
													ForegroundColorSpan.class);

		for (int n = foreSpan.length; n-- > 0;)
			e.removeSpan(foreSpan[n]);

		BackgroundColorSpan backSpan[] = e.getSpans(0, e.length(),
													BackgroundColorSpan.class);

		for (int n = backSpan.length; n-- > 0;)
			e.removeSpan(backSpan[n]);

	}

	public CharSequence autoIndent(CharSequence source, int start, int end,
									Spanned dest, int dstart, int dend) {
		String indent = "";
		int istart = dstart - 1;
		int iend = -1;

		boolean dataBefore = false;
		int pt = 0;

		for (; istart > -1; --istart) {
			char c = dest.charAt(istart);

			if (c == '\n')
				break;

			if (c != ' ' && c != '\t') {
				if (!dataBefore) {
					if (c == '{' || c == '+' || c == '-' || c == '*'
						|| c == '/' || c == '%' || c == '^' || c == '=')
						--pt;

					dataBefore = true;
				}

				if (c == '(')
					--pt;
				else if (c == ')')
					++pt;
			}
		}

		if (istart > -1) {
			char charAtCursor = dest.charAt(dstart);

			for (iend = ++istart; iend < dend; ++iend) {
				char c = dest.charAt(iend);

				if (charAtCursor != '\n' && c == '/' && iend + 1 < dend
					&& dest.charAt(iend) == c) {
					iend += 2;
					break;
				}

				if (c != ' ' && c != '\t')
					break;
			}

			indent += dest.subSequence(istart, iend);
		}

		if (pt < 0)
			indent += "\t";

		return source + indent;
	}

	public boolean gotoLine(int line) {
		--line;
		
		if (line > getLineCount()){
			setSelection(getText().toString().length());
			return false;
		}
			

		Layout layout = getLayout();
		setSelection(layout.getLineStart(line), layout.getLineEnd(line));
		return true;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO: Implement this method
		super.onDraw(canvas);
		canvas.save();
		drawText(canvas);
		canvas.restore();
	}
	
	public void drawText(Canvas canvas) {
		Layout layout = getLayout();
		if (layout!=null) {
			for (int i=0;i < getLineCount();i++) {
				layout.getLineDescent(i);
				float textX = getPaddingLeft() - getWidthOfBit(getBitOfNum(i+1)+1);
				float textY = layout.getLineBaseline(i);//+(i) * getLineHeight();
				canvas.drawText(String.valueOf(i + 1), textX, textY, mTextPaint);
			}
		}
		
		float oldPaintWidth = mTextPaint.getStrokeWidth();
		float newPaintWidth = mSpaceWidth/8;
		mTextPaint.setStrokeWidth(newPaintWidth);
		canvas.drawLine(getPaddingLeft()-mSpaceWidth/4, 0, getPaddingLeft()-mSpaceWidth/4, (getLineCount()) * getLineHeight(), mTextPaint);
		mTextPaint.setStrokeWidth(oldPaintWidth);
	}

	public int getRowHeight() {
		return getLineHeight();
	}

	public int getTotalRows() {
		return getLineCount();
	}

	public int getCurrRow() {
		return getLayout().getLineForOffset(getSelectionStart()) + 1;
	}


	public interface OnTextChangedListener {
		public void onTextChanged(String text);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		long timeStart = System.currentTimeMillis();
		Layout layout = getLayout();
		if(layout==null){
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			setPadding(getBoundOfLeft(), 0, 0, 0);
		}
		else
			setMeasuredDimension(layout.getWidth(), layout.getHeight());
		Log.i(TAG, "onMeasure used:"+(System.currentTimeMillis()-timeStart));
		
	}

	private float getWidthOfBit(int bit)
	{
		return bit * mSpaceWidth ;
	}

	public int getBoundOfLeft(){
		int left=(int) getWidthOfBit(1+getBitOfNum(getLineCount()));
		return (left);
	}

	private int getBitOfNum(int num)
	{
		if(num<10)
			return 1;
		if(num<100)
			return 2;
		if(num<1000)
			return 3;
		if(num<10000)
			return 4;
		if(num<100000)
			return 5;
		if(num<1000000)
			return 6;
		if(num<10000000)
			return 7;
		if(num<100000000)
			return 8;
		return 0;
	}

	public void closeInputMethod() {
		Log.i(TAG, "closeInputMethod");
		View editView=this;
	    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        imm.hideSoftInputFromWindow(editView.getApplicationWindowToken(), 0 );
	    }
	}
	
	public void cleanRedo() {
		mRedoBodies.clear();
	}

	public void cleanUndo() {
		mUndoBodies.clear();
	}

	public void setMaxSaveHistory(int i) {
		mMaxSaveHistory = i;
	}

	public boolean canRedo() {
		return mRedoBodies.size()>0;
	}

	public boolean canUndo() {
		return mUndoBodies.size()>0;
	}

	public boolean redo() {
		if(!canRedo())
			return false;
		ReplaceBody body=mRedoBodies.pop();
		ReplaceBody replaceBody=body.getRedoBody();
		if(mMaxSaveHistory>0){
			if(mUndoBodies.size()>mMaxSaveHistory)
				mUndoBodies.remove(0);
			mUndoBodies.push(body);
		}
		mSaveToHistory = false;
		getEditableText().replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			//mSelectionStart=replaceBody.mSt;
			//mSelectionEnd=mSelectionStart;
		}
		else
		{
			//mSelectionStart=replaceBody.mSt;
			//mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		return true;
	}
	
	public boolean undo() {
		if(!canUndo())
			return false;
		ReplaceBody body=mUndoBodies.pop();
		ReplaceBody replaceBody=body.getUndoBody();
		if(mMaxSaveHistory>0){
			if(mRedoBodies.size()>mMaxSaveHistory)
				mRedoBodies.remove(0);
			mRedoBodies.push(body);
		}
		mSaveToHistory = false;
		getEditableText().replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			//mSelectionStart=replaceBody.mSt;
			//mSelectionEnd=mSelectionStart;
		}
		else
		{
			//mSelectionStart=replaceBody.mSt;
			//mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		return true;
	}

	public void insertText(String string) {
	}

	private AfterTextChangeListener mAfterTextChangeListener = null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener) {
		mAfterTextChangeListener = afterTextChangeListener;
	}


	class ReplaceBody {
		int mSt;
		int mEn;
		CharSequence mSubtext;
		CharSequence mText;
		int mStart;
		int mEnd;
		int mSelectionStart;
		int mSelectionEnd;

		public ReplaceBody(int st, int en, CharSequence subtext,
				CharSequence text, int start, int end, int selectionStart,
				int selectionEnd) {
			mSt = st;
			mEn = en;
			mSubtext = subtext;
			mText = text;
			mStart = start;
			mEnd = end;
			mSelectionStart = selectionStart;
			mSelectionEnd = selectionEnd;
		}

		public ReplaceBody getUndoBody() {
			return new ReplaceBody(mSt, mSt + mEnd - mStart, mText, mSubtext,
					0, mSubtext.length(), mSelectionStart, mSelectionEnd);
		}

		public ReplaceBody getRedoBody() {
			return this;
		}

		public boolean isDelete() {
			if (mEn - mSt > 0 && mStart == 0 && mEnd == 0)
				return true;
			return false;
		}

		public boolean isInsert() {
			if (mSt == mEn && mText != null && mText.length() != 0
					&& mEnd - mStart > 0)
				return true;
			Log.i("isInsert", "false");
			return false;
		}

		public boolean addBody(ReplaceBody body) {
			if (isDelete() && body.isDelete() && mSt == body.mEn) {// 合并相连的删除
				this.mSt = body.mSt;
				this.mSubtext = body.mSubtext.toString() + this.mSubtext;
				this.mSelectionStart = body.mSelectionStart;
				return true;
			}
			Log.i("addBody", "addBody");
			if (isInsert() && body.isInsert()
					&& mSt + mText.length() == body.mSt) {// 合并相连的插入
				Log.i("addBody", "合并相连的插入 ");
				if (body.mText.toString().contains("\n")) {
					return false;
				}
				this.mText = this.mText
						+ body.mText.subSequence(body.mStart, body.mEnd)
								.toString();
				this.mEnd += body.mEnd - body.mStart;
				this.mSelectionEnd = body.mSelectionEnd;
				return true;
			}
			return false;
		}
	}
}

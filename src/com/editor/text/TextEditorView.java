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
import java.util.regex.*;

import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.edittext.AfterTextChangeListener;

public class TextEditorView extends EditText {
	private static final String TAG = "TextEditorView";
	private Paint textPaint;
	private Handler updateHandler ;
	private int updateDelay,errorLine;
	private boolean modified = true;
	private float mSpaceWidth = 0;

	private Pattern line,number,headfile;
	private Pattern string,keyword;
	private Pattern pretreatment,builtin ;
	private Pattern comment,trailingWhiteSpace ;

	private OnTextChangedListener onTextChangedListener;
	
	private Runnable updateThread = new Runnable() {
		@Override
		public void run() {
			Editable edit = getText();

			if (onTextChangedListener != null)
				onTextChangedListener.onTextChanged(edit.toString());

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
		textPaint = new Paint();
		textPaint.setAntiAlias(false);
		textPaint.setTypeface(Typeface.MONOSPACE);
		textPaint.setTextSize(dip2px(context,15));
		textPaint.setColor(0xff888888);
		mSpaceWidth = textPaint.measureText(" ");
		textPaint.getFontMetrics();
		setHorizontallyScrolling(true);
		updateHandler = new Handler();
		setFilters(new InputFilter[] {inputFilter});
		addTextChangedListener(watcher);
		initPattern();
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

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO: Implement this method
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO: Implement this method
        }

        @Override
        public void afterTextChanged(Editable edit) {
			setPadding(getBoundOfLeft(), 0, 0, 0);
			
			cancelUpdate();
			if (!modified)
				return;
			updateHandler.postDelayed(updateThread, updateDelay);
        }
	};



	public void setTextHighlighted(CharSequence text) {
		cancelUpdate();

		errorLine = 0;
		modified = false;
		setText(highlight(new SpannableStringBuilder(text)));
		modified = true;

		if (onTextChangedListener != null)
			onTextChangedListener.onTextChanged(text.toString());
	}



	public String getCleanText() {
		return trailingWhiteSpace.matcher(getText()).replaceAll("");
	}

	
	
	public void refresh() {
		highlightWithoutChange(getText());
	}

	
	public void cancelUpdate() {
		updateHandler.removeCallbacks(updateThread);
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
		if (getText().length() != 0) {
			for (int i=0;i < getLineCount();i++) {
				float textX = getPaddingLeft() - getWidthOfBit(getBitOfNum(i+1)+1);
				float textY = (i + 1) * getLineHeight();
				canvas.drawText(String.valueOf(i + 1), textX, textY, textPaint);
			}
		} else {
			float textX = getPaddingLeft() - getWidthOfBit(getBitOfNum(0+1)+1);
			float textY = (0 + 1) * getLineHeight();
			canvas.drawText(String.valueOf(1), textX, textY, textPaint);
		}
		
		float oldPaintWidth = textPaint.getStrokeWidth();
		float newPaintWidth = mSpaceWidth/8;
		textPaint.setStrokeWidth(newPaintWidth);
		canvas.drawLine(getPaddingLeft()-mSpaceWidth/4, 0, getPaddingLeft()-mSpaceWidth/4, (getLineCount()) * getLineHeight(), textPaint);
		textPaint.setStrokeWidth(oldPaintWidth);
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
	}

	public void cleanUndo() {
	}

	public void setMaxSaveHistory(int i) {
	}

	public boolean canRedo() {
		return true;
	}

	public boolean canUndo() {
		return true;
	}

	public boolean redo() {
		return true;
	}
	
	public boolean undo() {
		return true;
	}

	public void insertText(String string) {
	}

	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener) {
	}

}

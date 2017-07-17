package person.wangchen11.editor.newedittext;

import person.wangchen11.editor.codeedittext.CodeEditText.CodeType;
import person.wangchen11.editor.codeedittext.OnNeedChangeWants;
import person.wangchen11.editor.edittext.AfterTextChangeListener;

import com.editor.text.TextEditorView;

import android.content.Context;
import android.util.AttributeSet;

public class NewEditText extends TextEditorView {
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
	}

	public void setCodeType(CodeType typeNone) {
	}
}

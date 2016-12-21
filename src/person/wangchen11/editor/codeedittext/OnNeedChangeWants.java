package person.wangchen11.editor.codeedittext;

import java.util.List;

public interface OnNeedChangeWants {
	public void onNeedChangeWants(int start,int end,List<String>wants);
}

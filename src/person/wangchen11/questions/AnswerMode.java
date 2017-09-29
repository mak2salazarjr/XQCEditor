package person.wangchen11.questions;

import java.util.List;

import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.R;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class AnswerMode extends Fragment implements Window {
	private WindowsManager mWindowsManager = null;
	private ExpandableListView mExpandableListView = null;
	public AnswerMode(WindowsManager windowsManager) {
		mWindowsManager = windowsManager;
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		QuestionManager.init(inflater.getContext());
		View view = inflater.inflate(R.layout.fragment_answer_and_qusestion, null);
		mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
		mExpandableListView.setAdapter(new QuestionAdapter());
		return view;
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.answer_and_question);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof AnswerMode)
			return false;
		return true;
	}

	@Override
	public boolean onClose() {
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		return null;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		return false;
	}

	@Override
	public String[] getResumeCmd() {
		return null;
	}

	@Override
	public void resumeByCmd(String[] cmd) throws Exception {
	}
}

class QuestionAdapter extends BaseExpandableListAdapter {
	public QuestionAdapter() {
	}
	
	@Override
	public int getGroupCount() {
		return 1;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(groupPosition==0)
			return QuestionManager.instance().getLevel1Questions().size();
		return 0;
	}

	@Override
	public String getGroup(int groupPosition) {
		if(groupPosition==0)
			return "»Î√≈—µ¡∑";
		return null;
	}

	@Override
	public Question getChild(int groupPosition, int childPosition) {
		if(groupPosition==0)
			return QuestionManager.instance().getLevel1Questions().get(childPosition);
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.item_question_group, null);
		
		TextView textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(getGroup(groupPosition));
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.item_question_child, null);
		
		TextView textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(getChild(groupPosition, childPosition).mTitle);
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}
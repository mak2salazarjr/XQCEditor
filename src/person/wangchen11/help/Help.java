package person.wangchen11.help;

import java.util.List;

import person.wangchen11.browser.Browser;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;

@SuppressLint("SetJavaScriptEnabled")
public class Help extends Browser implements Window{

	public Help() {
		super("file:///android_asset/help/index.html");
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.help);
	}

	@Override
	public boolean onBackPressed() {
		return back();
	}

	@Override
	public boolean canAddNewWindow(Window window) {
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
}

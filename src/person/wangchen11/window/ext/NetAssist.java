package person.wangchen11.window.ext;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import person.wangchen11.netassist.NetAssistFragment;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;

public class NetAssist implements Window {
	
	private NetAssistFragment mNetAssistFragment = null;
	
	public NetAssist() {
		mNetAssistFragment = new NetAssistFragment();
	}

	@Override
	public Fragment getFragment() {
		return mNetAssistFragment;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return "ÍøÂçµ÷ÊÔ";
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		return true;
	}

	@Override
	public boolean onClose() {
		mNetAssistFragment.destory();
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
	public void resumeByCmd(String []cmd) {
		
	}

}

package person.wangchen11.browser;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class Browser extends Fragment {

	private WebView mWebView = null;
	public String mUrl="";
	
	public Browser(String url) {
		mUrl=url;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_browser, null);
        mWebView=(WebView) (viewGroup.findViewById(R.id.apiWebView));

        //�Ƿ�����javaScript֧��
        mWebView.getSettings().setJavaScriptEnabled(true);
        //�Ƿ�������
        mWebView.getSettings().setSupportZoom(true);
        //�Ƿ������ŷ���
        mWebView.getSettings().setBuiltInZoomControls(true);
        //�������ҳ������Ϊ�ֱ��ʺ���
        mWebView.getSettings().setUseWideViewPort(false); 
        mWebView.getSettings().setLoadWithOverviewMode(true); 
        //���������С
        mWebView.getSettings().setDefaultFontSize(16);
        //����htmlҳ��
        mWebView.loadUrl(mUrl);
        //����Ϊ���ҵ�mWebWiew��URL
        mWebView.setWebViewClient(new WebViewClient());
        return viewGroup;
	}
	
	public void refresh()
	{
		if(mWebView!=null)
			mWebView.loadUrl(mUrl);
	}
	
	public boolean back() {
		if(mWebView==null)
			return false;
		if(mWebView.canGoBack())
		{
			View view=mWebView;
			//λ�ƶ�����Ч����
			TranslateAnimation translate = new TranslateAnimation(mWebView.getWidth(),0,0,0);
			//���ù���ʱ��
			translate.setDuration(300);
			//��ʼִ��
			view.startAnimation(translate);
			mWebView.goBack();// ����ǰһ��ҳ��
			return true;
		}
		return false;
	}
}
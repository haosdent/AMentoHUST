package org.scauhci.android.mentohust;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockActivity {
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		registerLink();
    }
    
    public void registerLink() {
    	TextView gplAbout = (TextView) findViewById(R.id.gpl_about);
    	TextView googleAbout = (TextView) findViewById(R.id.google_about);
    	TextView githubAbout = (TextView) findViewById(R.id.github_about);
    	TextView haosongAbout = (TextView) findViewById(R.id.haosong_about);
    	TextView jieqiangAbout = (TextView) findViewById(R.id.jieqiang_about);
    	TextView jiawenAbout = (TextView) findViewById(R.id.jiawen_about);
    	TextView hciAbout = (TextView) findViewById(R.id.hci_about);
    	gplAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	googleAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	githubAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	haosongAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	jieqiangAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	jiawenAbout.setMovementMethod(LinkMovementMethod.getInstance());
    	hciAbout.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

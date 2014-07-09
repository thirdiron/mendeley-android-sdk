package com.mendeley.api.network;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.mendeley.api.R;

/**
 * Display the splash screen, with buttons to sign in or create an account.
 *
 * The "create account" button starts a new web browser activity pointed at the
 * Mendeley home page.
 *
 * The "sign in" button starts DialogActivity to obtain an authorization code, which is
 * passed to AuthenticationManager.
 */
public class SignInActivity  extends Activity implements OnClickListener {
	final static int SIGNIN_RESULT = 0;
	final static String CREATE_ACCOUNT_URL = "http://www.mendeley.com/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_layout);
        
        ((Button) findViewById(R.id.signinButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.signupButton)).setOnClickListener(this);
    }

    /**
     * Handling click events on the custom buttons.
     * Will call DialogActivity or open the create account url in a web browser.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signinButton) {
            Intent intent = new Intent(this, DialogActivity.class);
            startActivityForResult(intent, SIGNIN_RESULT);
        } else if (id == R.id.signupButton) {
            openUrlInBrowser(CREATE_ACCOUNT_URL);
        }
    }

    /**
     * Opening a system web browser to load the given url
     * @param url the url to load
     */
    private void openUrlInBrowser(String url) {
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
    	startActivity(intent);
    }
    
    /**
     * Handling on activity result.
     * Will be called after the user logged in in the DialogActivity.
     * If an authorisation code is received will call authenticated method of the AuthenticationManager
     * otherwise will call failedToAuthenticate.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch(requestCode) {
	    		case SIGNIN_RESULT:
	    			String code = null;
	    			Bundle bundle = data.getExtras();
	    			if (bundle != null && bundle.containsKey("authorization_code")) {
	    				code = bundle.getString("authorization_code");
	    			}
	    			if (code != null) {
		    			AuthenticationManager.getInstance().authenticated();
	    			} else {
	    				AuthenticationManager.getInstance().failedToAuthenticate();
	    			}
	    			finish();
	    			break;
    		}
    	}
    }

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
}
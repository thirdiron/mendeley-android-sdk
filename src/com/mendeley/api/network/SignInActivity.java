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
 * This is activity will show when the user is not authenticated and need to enter his username and password. 
 *
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
     * Opening a web browser to load the given url
     * @param url the url to load
     */
    private void openUrlInBrowser(String url) {
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
    	startActivity(intent);
    }
    
    /**
     * Handling on activity result.
     * Will be called after the user logged in in the DialogActivity.
     * If an authorsation code is received will call authenticated method of the AuthenticationManager
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
		    			AuthenticationManager.getInstance().authenticated(code);
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
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
}
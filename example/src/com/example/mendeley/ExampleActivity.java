package com.example.mendeley;

import java.util.Date;
import java.util.List;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.callbacks.MendeleySignInInterface;
import com.mendeley.api.MendeleySdk;
import com.mendeley.api.MendeleySdkFactory;
import com.mendeley.api.callbacks.document.GetDocumentsCallback;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.Page;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ExampleActivity extends Activity implements View.OnClickListener, GetDocumentsCallback, MendeleySignInInterface
{
    // REPLACE THESE STRINGS WITH YOUR APP'S CREDENTIALS:
    private final String CLIENT_ID = "123456";
    private final String CLIENT_SECRET = "hAek7HsjK23jjdDn";
    private final String REDIRECT_URI = "http://example.com/test";

    enum SignInStatus { SIGNED_OUT, SIGNING_IN, SIGNED_IN };
    private SignInStatus signInStatus = SignInStatus.SIGNED_OUT;
	
    private MendeleySdk sdk;

    private Button getDocumentsButton;
	private TextView outputView;
	
	private StringBuilder outputText = new StringBuilder();
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getDocumentsButton = (Button) findViewById(R.id.getDocumentsButton);
        getDocumentsButton.setOnClickListener(this);
        disableControls();
        outputView = (TextView) findViewById(R.id.output);

        sdk = MendeleySdkFactory.getInstance();
        signIn();
    }

    private void signIn() {
        if (sdk.isSignedIn()) {
            setSignInStatus(SignInStatus.SIGNED_IN);
        } else {
            setSignInStatus(SignInStatus.SIGNING_IN);
            ClientCredentials clientCredentials = new ClientCredentials(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
            sdk.signIn(this, this, clientCredentials);
        }
    }

    private void signOut() {
        clearOutput();
        setSignInStatus(SignInStatus.SIGNED_OUT);
        sdk.signOut();
    }

    private void setSignInStatus(SignInStatus status) {
        signInStatus = status;
        invalidateOptionsMenu();
        if (status == SignInStatus.SIGNED_IN) {
            enableControls();
        } else {
            disableControls();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem signInItem = menu.findItem(R.id.action_sign_in);
        MenuItem signOutItem = menu.findItem(R.id.action_sign_out);
        if (signInStatus == SignInStatus.SIGNED_IN) {
            signInItem.setEnabled(false);
            signOutItem.setEnabled(true);
        } else if (signInStatus == SignInStatus.SIGNED_OUT) {
            signInItem.setEnabled(true);
            signOutItem.setEnabled(false);
        } else {
            signInItem.setEnabled(false);
            signOutItem.setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_in:
                signIn();
                break;
            case R.id.action_sign_out:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disableControls() {
        getDocumentsButton.setEnabled(false);
    }

    private void enableControls() {
        getDocumentsButton.setEnabled(true);
    }

    private void clearOutput() {
        outputText.setLength(0);
        outputView.setText(outputText.toString());
    }

    @Override
	public void onClick(View view) {
        if (view == getDocumentsButton) {
            getDocuments();
        }
	}

    @Override
    public void onSignedIn() {
        setSignInStatus(SignInStatus.SIGNED_IN);
    }

    @Override
    public void onSignInFailure() {
        setSignInStatus(SignInStatus.SIGNED_OUT);
    }

    @Override
	public void onDocumentsReceived(List<Document> docs, Page next, Date serverDate) {
		outputText.append("Page received:\n");
		for (Document doc : docs) {
			outputText.append("* " + doc.title + "\n");
		}
		outputText.append("\n");
		outputView.setText(outputText.toString());
		
    	sdk.getDocuments(next, this);
	}

    @Override
	public void onDocumentsNotReceived(MendeleyException mendeleyException) {
		outputText.append(mendeleyException.toString() + "\n");
		outputView.setText(outputText.toString());
	}

    private void getDocuments() {
        DocumentRequestParameters params = new DocumentRequestParameters();
        params.limit = 3;
        outputText.setLength(0);
        sdk.getDocuments(params, this);
    }
}

package tk.friendar.friendar;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriendActivity extends AppCompatActivity {

	private static final String TAG = "AddFriendActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);

		// Action bar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("Add Friend");
		ab.setDisplayHomeAsUpEnabled(true);

		// Field listeners + validators
		((Button)findViewById(R.id.add_friend_submit)).setEnabled(false);

		((EditText)findViewById(R.id.add_friend_username)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { return; }
			@Override
			public void afterTextChanged(Editable editable)  { return; }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				((Button)findViewById(R.id.add_friend_submit))
						.setEnabled(FormValidator.isValidUsername(s.toString()));
			}
		});
	}

	public void submitFriendRequest(View view) {
		String username = ((EditText)findViewById(R.id.add_friend_username)).getText().toString();
		//String email = ((EditText)findViewById(R.id.add_friend_email)).getText().toString();

		Log.d(TAG, "Adding friend: " + username);
		successPopup("Request sent to " + username);
		finish();
	}

	void errorPopup(String error) {
		Snackbar.make(findViewById(R.id.add_friend_layout),
				error, Snackbar.LENGTH_LONG)
				.show();
	}

	void successPopup(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
}

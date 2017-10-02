package tk.friendar.friendar;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class NewMeetingActivity extends AppCompatActivity {

	private static final String TAG = "NewMeetingActivity";
	private static final int MAX_MEETING_NAME_LENGTH = 80;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_meeting);

		// Action bar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("Create Meeting");
		ab.setDisplayHomeAsUpEnabled(true);
	}

	public void submitRequest(View view) {
		String name = ((EditText)findViewById(R.id.new_meeting_name)).getText().toString();

		if (isValidMeetingName(name)) {
			Log.d(TAG, "Created: " + name);
			finish();
		}
		else {
			Snackbar.make(findViewById(R.id.new_meeting_layout),
					"Invalid meeting name", Snackbar.LENGTH_LONG)
					.show();
		}
	}

	public static boolean isValidMeetingName(String name) {
		if (name.length() <= 0 || name.length() > MAX_MEETING_NAME_LENGTH) return false;
		if (name.trim().length() == 0) return false;
		return true;
	}
}

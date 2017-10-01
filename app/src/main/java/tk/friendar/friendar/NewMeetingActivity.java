package tk.friendar.friendar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class NewMeetingActivity extends AppCompatActivity {

	private static final String TAG = "NewMeetingActivity";

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

		Log.d(TAG, "Created: " + name);

		finish();
	}
}

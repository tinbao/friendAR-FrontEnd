package tk.friendar.friendar;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tk.friendar.friendar.testing.DummyData;

public class NewMeetingActivity extends AppCompatActivity {

	UserListAdapter listAdapter;

	private static final String TAG = "NewMeetingActivity";
	private static final int MAX_MEETING_NAME_LENGTH = 80;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_meeting);

		// Action bar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("New Meeting");
		ab.setDisplayHomeAsUpEnabled(true);

		// Friend list
		ArrayList<User> friends = DummyData.getFriends();
		listAdapter = new UserListAdapter(friends);
		ListView listView = (ListView) findViewById(R.id.new_meeting_user_list);
		listView.setAdapter(listAdapter);

		// Meeting name EditText listener + validator
		final Button submitButton = (Button)findViewById(R.id.new_meeting_submit);
		submitButton.setEnabled(false);

		((EditText)findViewById(R.id.new_meeting_name)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { return; }
			@Override
			public void afterTextChanged(Editable editable)  { return; }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				submitButton.setEnabled(isValidMeetingName(s.toString()));
			}
		});
	}

	public void submitRequest(View view) {
		String name = ((EditText)findViewById(R.id.new_meeting_name)).getText().toString();
		ArrayList<User> users = getSelectedUsers();

		if (users.size() == 0) {
			errorPopup("No friends selected");
		}
		else {
			Log.d(TAG, "Created: " + name);
			for (User user : users) {
				Log.d(TAG, "includes: " + user.getName());
			}
			finish();
		}
	}

	public static boolean isValidMeetingName(String name) {
		if (name.length() <= 0 || name.length() > MAX_MEETING_NAME_LENGTH) return false;
		if (name.trim().length() == 0) return false;
		return true;
	}

	ArrayList<User> getSelectedUsers() {
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Boolean> checkboxes = listAdapter.checkboxStates;
		ArrayList<User> friends = listAdapter.users;
		for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i)) {
				users.add(friends.get(i));
			}
		}
		return users;
	}

	void errorPopup(String error) {
		Snackbar.make(findViewById(R.id.new_meeting_layout),
				error, Snackbar.LENGTH_LONG)
				.show();
	}


	// Custom list adapter for the user list
	class UserListAdapter extends BaseAdapter {
		ArrayList<User> users;
		ArrayList<Boolean> checkboxStates;

		public UserListAdapter(ArrayList<User> users) {
			this.users = users;
			checkboxStates = new ArrayList<>(users.size());
			for (int i = 0; i < users.size(); i++) checkboxStates.add(false);
		}

		@Override
		public int getCount() { return users.size(); }
		@Override
		public User getItem(int position) { return users.get(position);	}
		@Override
		public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int position2 = position;

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_new_meeting_list_elem, parent, false);
			}

			// Create listener for checkbox, update state array on click
			((CheckBox)convertView.findViewById(R.id.new_meeting_user_item_checkbox)).
					setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton cb, boolean b) {
							checkboxStates.set(position2, b);
						}
					});

			// Insert user data and checkbox state
			((TextView)convertView.findViewById(R.id.new_meeting_user_item_name))
					.setText(getItem(position).getName());
			((CheckBox)convertView.findViewById(R.id.new_meeting_user_item_checkbox))
					.setChecked(checkboxStates.get(position));

			return convertView;
		}
	}
}

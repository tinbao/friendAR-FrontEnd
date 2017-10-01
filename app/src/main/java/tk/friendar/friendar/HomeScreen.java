package tk.friendar.friendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import tk.friendar.friendar.arscreen.VRActivity;
import tk.friendar.friendar.testing.DummyData;

public class HomeScreen extends AppCompatActivity {
    private GestureDetectorCompat gestureObject;

	private static final String TAG = "HomeScreen";
	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

		// Meeting List
		ArrayList<Meeting> meetings = DummyData.getMeetings();
		ArrayAdapter<Meeting> listAdapter = new ArrayAdapter<Meeting>(this,
				R.layout.home_screen_list_elem, meetings);
		ListView listView = (ListView) findViewById(R.id.home_screen_meeting_list);
		listView.setAdapter(listAdapter);

        // Swipe Gestures
        gestureObject = new GestureDetectorCompat(this, new HomeScreen.LearnGesture());
		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureObject.onTouchEvent(motionEvent);
			}
		});

		// Item Click
		listView.setOnItemClickListener(new ItemClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_screen_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_new_meeting:
				Log.d(TAG, "Creating new meeting");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//Gesture Class
    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        //creating a listener

        //code to swipe

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (event2.getX() > event1.getX()) {
				// swipe right to left
				// TODO maybe support swiping on items
            } else if (event2.getX() < event1.getX()) {
                //swipe left to right open ar screen
				Intent intent = new Intent(HomeScreen.this, VRActivity.class);
				finish();
				startActivity(intent);
            }
            return true;

        }
    }

    // List item click listener
	class ItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Meeting m = (Meeting)parent.getItemAtPosition(position);
			Log.d(TAG, "Clicked: " + m.getName());

			Intent intent = new Intent(HomeScreen.this, screen1.class);
			intent.putExtra(EXTRA_MEETING_ID, m.getId());
			finish();
			startActivity(intent);
		}
	};
}

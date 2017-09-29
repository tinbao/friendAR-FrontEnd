package tk.friendar.friendar;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class screen1 extends AppCompatActivity {
    private GestureDetectorCompat gestureObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());
        //LearnGesture is a class

		int id = getIntent().getIntExtra(HomeScreen.EXTRA_MEETING_ID, -1);
		((TextView)findViewById(R.id.textView4)).setText("meeting id = " + id);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //Gesture Class

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        //creating a listener

        //code to swipe

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (event2.getX() > event1.getX()) {
                //swipe left to right

            } else if (event2.getX() < event1.getX()) {
                //swipe right to left
                Intent intent = new Intent(
                    screen1.this, HomeScreen.class);
                    finish();
                    startActivity(intent);


            }
            return true;

        }
    }
}



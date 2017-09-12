package tk.friendar.friendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import tk.friendar.friendar.arscreen.VRActivity;

public class screen2 extends AppCompatActivity {
    private GestureDetectorCompat gestureObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);
        gestureObject = new GestureDetectorCompat(this, new screen2.LearnGesture());
        //LearnGesture is a class
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

                Intent intent = new Intent(
                        screen2.this, screen1.class);
                finish();
                startActivity(intent);
            } else if (event2.getX() < event1.getX()) {
                //swipe right to left, open ar screen

				Intent intent = new Intent(screen2.this, VRActivity.class);
				finish();
				startActivity(intent);
            }
            return true;

        }
    }
}

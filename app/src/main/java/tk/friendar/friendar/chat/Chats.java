package tk.friendar.friendar.chat;
import java.util.ArrayList;
import java.util.Random;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import tk.friendar.friendar.R;


public class Chats extends AppCompatActivity implements OnClickListener {


    private ArrayList<FriendAR_chat> chats;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FriendAR_chat();
    }
    //

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.home_screen_meeting_list :
                chats.add(new FriendAR_chat()) ;
        }
    }
}

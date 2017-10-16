package tk.friendar.friendar.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tk.friendar.friendar.HomeScreen;
import tk.friendar.friendar.R;

public class FriendAR_chat extends AppCompatActivity implements OnClickListener {

    private EditText msg_edittext;
    private String user1;
    private String user2;
     Random random;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    Handler messageGet;
    Runnable messageGetRunnable;
    private int Get_INTERVAL = 10000;
    public double chatId;
    Timer timer = new Timer();
    public int id;

    private class dummyMessage extends TimerTask {
        public void run() {
            final ChatMessage chatMessage = new ChatMessage("Simon", "Mario",
                    "Eyy Simon, howsita goina?", "" + random.nextInt(1000), false);

            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();

            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    /* set up chat Adapter and chat list*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_layout);



        user1 = "Simon";
        user2 = "Mario";

        msg_edittext = (EditText) findViewById(R.id.messageEditText);
        msgListView = (ListView) findViewById(R.id.msgListView);
        chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatlist);
        ImageButton sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);
        id = getIntent().getIntExtra(HomeScreen.EXTRA_MEETING_ID, -1);
        scheduleGetMessage();



       };


        //messageGet.postDelayed(messageGetRunnable, Get_INTERVAL);
        // Server requests
        public void scheduleGetMessage(){
            messageGet = new Handler();
            messageGet.postDelayed(new Runnable() {
                @Override
                public void run() {

                    // TODO get string message and details from server
                    // get messages based on meeting id in variable id
                    // put code to get json objects here
                    //Simon will loop through them and display them
                    

                    dummyMessage();

                    messageGet.postDelayed(this, Get_INTERVAL);  // loop

                }
            },Get_INTERVAL);
        }





/*
    protected void onResume(){
        super.onResume();
        messageGet.postDelayed(messageGetRunnable,Get_INTERVAL);

    }
*/
/*
    @Override
    protected void onPause() {
        super.onPause();
        messageGet.removeCallbacks(messageGetRunnable);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_friend_ar_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void dummyMessage(){
        msgListView.setAdapter(chatAdapter);
        final ChatMessage chatMessage = new ChatMessage(user2, user1,
                "Eyy Simon, howsita goina?", "" + this.id, false);

        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();

        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
    }


    /*Create current message and add to list and view from User input in EditText element*/

    public void sendMessage(View v) {
        random = new Random();



        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);


        msgListView.setAdapter(chatAdapter);

        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, ""+this.id, true);
            chatMessage.setMsgID();
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();

            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            //TODO Add Server Code to send message to server with meeting Id
            //message to be sent is contained in object chatMessage
            //JSON object parameters
            /*
            params.put("sender", user1);
            params.put("receiver", user2);
            params.put("msgid", this.id);
            params.put("isMine", true);
            params.put("body", message);
            params.put("date", chatMessage.Date);
            params.put("time", chatMessage.Time);
           */
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendMessage(v);

        }
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
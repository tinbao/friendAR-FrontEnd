package tk.friendar.friendar.chat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tk.friendar.friendar.HomeScreen;
import tk.friendar.friendar.R;

public class FriendAR_chat extends AppCompatActivity implements OnClickListener {

    private EditText msg_edittext;
    private Integer user1;
    private Integer user2;
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
            final ChatMessage chatMessage = new ChatMessage(1, 2,
                    "Eyy Simon, howsita goina?", "" + random.nextInt(1000), false, "Mario");

            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();

            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set up chat Adapter and chat list for the first time
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        user1 = 1;
        user2 = 2;

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
                    // Simon will loop through them and display them
                    getMessages();

                    dummyMessage();
                    messageGet.postDelayed(this, Get_INTERVAL);  // loop

                }

            },Get_INTERVAL);
        }

//    protected void onResume(){
//        super.onResume();
//        messageGet.postDelayed(messageGetRunnable,Get_INTERVAL);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        messageGet.removeCallbacks(messageGetRunnable);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_ar_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dummyMessage(){
        msgListView.setAdapter(chatAdapter);
        final ChatMessage chatMessage = new ChatMessage(user2, user1,
                "Eyy Simon, howsita goina?", "" + this.id, false, "Mario");

        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();

        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
    }


    /**
     * Create current message and add to list and view from User input in EditText element
     * @param v is the current view of the device
     */
    public void sendMessage(View v) {
        random = new Random();

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        msgListView.setAdapter(chatAdapter);

        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, ""+this.id, true, ChatMessage.senderName);
            chatMessage.setMsgID();
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();

            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();

            //TODO Add Server Code to send message to server with meeting Id
            /* Message to be sent is contained in object chatMessage */
            final JSONObject params = new JSONObject();
            try {
                // TODO: FORMAT TO CORRECT SCHEMA
                params.put("sender", user1);
                params.put("receiver", user2);
                params.put("msgid", this.id);
                params.put("isMine", true);
                params.put("body", message);
                params.put("date", chatMessage.Date);
                params.put("time", chatMessage.Time);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendMessage(v);
        }
    }

    /**
     * TODO: Does a GET request to the server to get any messages
     */
    private void getMessages() {

    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
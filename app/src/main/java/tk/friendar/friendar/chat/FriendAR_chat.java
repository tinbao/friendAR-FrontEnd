package tk.friendar.friendar.Chat;

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
    private int Get_INTERVAL = 100000;


    /* set up chat Adapter and chat list*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_layout);

        //Chats chat1 = new Chats();
        user1 = "Simon";
        user2 = "Simon1";

        msg_edittext = (EditText) findViewById(R.id.messageEditText);
        msgListView = (ListView) findViewById(R.id.msgListView);
        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(this, chatlist);
        ImageButton sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);

        // Server requests

        messageGet = new Handler();
       messageGetRunnable = new Runnable() {
            @Override
            public void run() {

                // TODO get string message and details from server


                final ChatMessage chatMessage = new ChatMessage("Mario", "Simon",
                        "Eyy Simon, howsita goina?", "" + random.nextInt(1000), false);



                chatMessage.Date = CommonMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();


                chatAdapter.add(chatMessage);
                chatAdapter.notifyDataSetChanged();

               // processUserMessage(chatMessage);

                messageGet.postDelayed(this, Get_INTERVAL);  // loop
            }
        };


    }

/*
    protected void onResume(){
        super.onResume();
        messageGet.postDelayed(messageGetRunnable,Get_INTERVAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        messageGet.removeCallbacks(messageGetRunnable);
    }
    */
    /*
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
    /*Create message from server from other user, called constantly*/
    public void processUserMessage(ChatMessage m){


        /* sample ChatMessage declaration and initialisation:


        ChatMessage chatMessage = new ChatMessage(m.sender,m.receiver,
                m.body, "" + random.nextInt(1000), false);
         */
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        m.setMsgID();
        chatAdapter.add(m);
        chatAdapter.notifyDataSetChanged();

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
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();

            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
        final ChatMessage chatMessage = new ChatMessage("Mario", "Simon",
                "Eyy Simon, howsita goina?", "" + random.nextInt(1000), false);



        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        chatMessage.setMsgID();


        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();

        /*

         */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendMessage(v);
                messageGet.postDelayed(messageGetRunnable,Get_INTERVAL);

        }
    }
}










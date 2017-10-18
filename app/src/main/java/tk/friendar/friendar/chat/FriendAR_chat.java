package tk.friendar.friendar.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import tk.friendar.friendar.HomeScreen;
import tk.friendar.friendar.MapsActivity;
import tk.friendar.friendar.R;
import tk.friendar.friendar.URLs;
import tk.friendar.friendar.VolleyHTTPRequest;

/**
 * FriendAR's chat system to receive and send messages to the users in the meeting group
 */
public class FriendAR_chat extends AppCompatActivity implements OnClickListener {

    private EditText msg_edittext;
    private Integer user1;
    private Integer user2;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    Handler messageGet;
    Runnable messageGetRunnable;
    ChatMessage last_message = null;
    private static final int GET_INTERVAL = 200;

    /** List of all messages from the single meeting group */
    private ArrayList<ChatMessage> meetingChat;

    /** Current instance meeting id */
    public int id;

    /** The last message received */
    private ChatMessage lastMessage;

    private class dummyMessage extends TimerTask {
        public void run() {
            final ChatMessage chatMessage = new ChatMessage(1, 2,
                    "Eyy Simon, howsita goina?", false, "Mario");

            chatMessage.timeStamp = CommonMethods.getCurrentStamp();

            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set up chat Adapter and chat list for the first time
     * @param savedInstanceState current state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        user1 = 1;
        user2 = 2;

        meetingChat = new ArrayList<>();
        msg_edittext = (EditText) findViewById(R.id.messageEditText);
        msgListView = (ListView) findViewById(R.id.msgListView);

        chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatlist);

        ImageButton sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);
        id = getIntent().getIntExtra(HomeScreen.EXTRA_MEETING_ID, -1);
        scheduleGetMessage();

        //TODO: 2ND HALF: possibly within resume rather than here
    }

        String title = title = getIntent().getStringExtra(HomeScreen.EXTRA_MEETING_NAME);
        getSupportActionBar().setTitle(title);

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
                    //dummyMessage();
                    /* Loops and constantly checks for messages for INTERVAL time */
                    messageGet.postDelayed(this, GET_INTERVAL);
                }

            },GET_INTERVAL);
        }


    protected void onResume(){
        super.onResume();
        messageGet.postDelayed(messageGetRunnable,GET_INTERVAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        messageGet.removeCallbacks(messageGetRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_ar_chat, menu);
        return true;
    }

    /**
     * Checks if the message sent is mine
     * @param msg the message
     */
    public void checkMyMessage(ChatMessage msg){
        if(msg.sender == VolleyHTTPRequest.id){
            msg.isMine = true;
        } else {
            msg.isMine = false;
        }
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
        else if (id == R.id.action_open_map) {
			Intent intent = new Intent(this, MapsActivity.class);
			intent.putExtra("id", this.id);
			startActivity(intent);
		}

        return super.onOptionsItemSelected(item);
    }
    public void displayMessages(ArrayList<ChatMessage> chats){
        for (int i = 0; i< chats.size(); i++){
            msgListView.setAdapter(chatAdapter);
            chatAdapter.add(chats.get(i));
            chatAdapter.notifyDataSetChanged();
            last_message = chats.get(i);
            msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            msgListView.setStackFromBottom(true);


        }
    }
    public ChatMessage getLastMessage(ArrayList<ChatMessage> chats){
        return chats.get(chats.size()-1);
    }
    public void displayMessage(ArrayList<ChatMessage> chats){
        msgListView.setAdapter(chatAdapter);
        chatAdapter.add(chats.get(chats.size()-1));
        chatAdapter.notifyDataSetChanged();
        last_message = chats.get(chats.size()-1);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
    }
    public void dummyMessage(){
        msgListView.setAdapter(chatAdapter);
        final ChatMessage chatMessage = new ChatMessage(user2, user1,
                "Eyy Simon, howsita goina?", false, "Mario");

        chatMessage.timeStamp = CommonMethods.getCurrentStamp();

        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendMessage(v);
        }
    }

    /**
     * Create current message and add to list and view from User input in EditText element
     * @param v is the current view of the device
     */
    public void sendMessage(View v) {
        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        msgListView.setAdapter(chatAdapter);

        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, true, VolleyHTTPRequest.getUsername());
            chatMessage.setMsgID();
            chatMessage.timeStamp = CommonMethods.getCurrentStamp();

            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();

            /* Server Code to send message to server with meeting Id
             * Message to be sent is contained in object chatMessage */
            final JSONObject params = new JSONObject();
            try {
                params.put("content", message);
                params.put("userID", VolleyHTTPRequest.getUserID());
                params.put("meetingID", id);
            } catch(JSONException e){
                e.printStackTrace();
            }

            System.out.println(params.toString());

            /* Does a POST request to send the message to the correct meeting group */
            StringRequest req = new StringRequest(Request.Method.POST, URLs.URL_CHAT,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSON Response",response);
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.toString();
                        Log.d("ErrorResponse", msg);
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            /* Tries to make a JSON Object and see if it will incur an error */
                                JSONObject obj = new JSONObject(res);

                            } catch (UnsupportedEncodingException e1) {
                            /* Couldn't properly decode data to string */
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                            /* returned data is not JSONObject */
                                e2.printStackTrace();
                            }
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authorization", VolleyHTTPRequest.makeAutho());
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            req.setShouldCache(false);
            VolleyHTTPRequest.addRequest(req, getApplicationContext());

        }
    }

    public void loopElem(){
           /* Loop over all elements in the array of messages */
        for(int i = 0; i < meetingChat.size(); i++){
            /* Mark those which are send from this user */
            checkMyMessage(meetingChat.get(i));
        }
        System.out.println(meetingChat.toString());

        /* Sort the array list by time stamp (string) */
        Collections.sort(meetingChat);
        System.out.println(meetingChat.toString());

        if (last_message == null){
            displayMessages(meetingChat);
        } else if(last_message.compareTo(getLastMessage(meetingChat)) != 0 && !getLastMessage(meetingChat).isMine){
            displayMessage(meetingChat);
        }
        //displayMessages(meetingChat);
    }

    /**
     * Does a GET request to the server to get all messages from the meeting ID
     * and also displays them as well.
     */
    private void getMessages() {

        String url = URLs.URL_CHAT + "/" + id;
        /* Does a GET request to receive the messages of the meeting group */
        StringRequest req = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("JSON Response",response);

                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray messages = res.getJSONArray("messages: ");

                        for(int i = 0; i < messages.length(); i++){
                            JSONObject message = messages.getJSONObject(i);
                            Log.d("JSON OBJ", message.toString());
                            /* Parses the JSON Object and gets all the needed info */
                            String Smeeting = message.getString("meeting");
                            JSONObject meeting = new JSONObject(Smeeting);
                            Integer meetingId = meeting.getInt("id");

                            String Suser = message.getString("user");
                            JSONObject user = new JSONObject(Suser);
                            Integer userId = user.getInt("id");
                            String userName = user.getString("fullName");

                            String timeSent = message.getString("time sent");
                            String msgBody = message.getString("content");

                            /* Creates a new chat message to add to the array */
                            ChatMessage newMessage = new ChatMessage(
                                    userId, VolleyHTTPRequest.id, msgBody, false, userName);
                            newMessage.timeStamp = timeSent;
                            /* This array will be processed later */
                            meetingChat.add(newMessage);


                        }
                        loopElem();

                        Log.d("ADDED", meetingChat.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    String msg = error.toString();
                    Log.d("ErrorResponse", msg);

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", VolleyHTTPRequest.makeAutho());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        req.setShouldCache(false);
        VolleyHTTPRequest.addRequest(req, getApplicationContext());
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
package tk.friendar.friendar.chat;

/**
 * Created by Simon on 10/3/2017.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import tk.friendar.friendar.R;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chatbubble,null);

		LinearLayout chatbubble = (LinearLayout) vi.findViewById(R.id.chatbubble_root);

        TextView msg = (TextView) chatbubble.findViewById(R.id.chatbubble_message_text);
        msg.setText(message.body);

		TextView sender = (TextView) chatbubble.findViewById(R.id.chatbubble_sender);
		sender.setText(message.sender);

        // if message is mine then align to right
        if (message.isMine) {
			msg.setBackgroundResource(R.drawable.round_box_red);
			chatbubble.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
			msg.setBackgroundResource(R.drawable.round_box_gray);
			chatbubble.setGravity(Gravity.LEFT);
        }
        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}
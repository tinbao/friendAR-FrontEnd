package tk.friendar.friendar.chat;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Simon on 10/3/2017.
 */

public class ChatMessage implements Comparable<ChatMessage> {

    /** The body content of the message */
    public String body;
    /** The sender's name needs to be displayed under the message */
    public static String senderName;
    public String timeStamp;
    /** Each message has a unique identification number */
    public String msgId;
    /** The sender and receiver are identified using their unique IDs */
    public Integer sender, receiver;
    public boolean isMine;// Did I send the message.

    /**
     * Constructor for a new ChatMessage
     * @param sender the message's sender's ID
     * @param receiver the recipient's ID (fixed to the current login)
     * @param body content of the message
     * @param isMine flag to check if the message is from the user
     * @param senderName the sender's username
     */
    public ChatMessage(Integer sender, Integer receiver, String body,
                       boolean isMine, String senderName) {
        this.body = body;
        this.isMine = isMine;
        this.sender = sender;
//        this.msgId = msgId;
        this.receiver = receiver;
        this.senderName = senderName;
    }

    public void setMsgID() {
        msgId += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }

    @Override
    public int compareTo(@NonNull ChatMessage message) {
        return timeStamp.compareTo(message.timeStamp);
    }
}
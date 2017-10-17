package tk.friendar.friendar.chat;

import java.util.Random;

/**
 * Created by Simon on 10/3/2017.
 */

public class ChatMessage {

    /** The body content of the message */
    public String body;
    /** The sender's name needs to be displayed under the message */
    public static String senderName;
    public String Date, Time;
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

    /**
     * Checks if the message sent is mine
     * @param msgSenderID the message's sender's ID
     */
    public void checkMyMessage(Integer msgSenderID){
        if(msgSenderID == sender){
            isMine = true;
        } else {
            isMine = false;
        }
    }

    public void setMsgID() {
        msgId += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }
}
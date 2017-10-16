package tk.friendar.friendar.Chat;

/**
 * Created by Simon on 10/3/2017.
 */

import java.util.Random;

public class ChatMessage {

    public String body;
    public static String senderName;
    public String Date, Time;
    public String msgid;
    public Integer sender, receiver;
    public boolean isMine;// Did I send the message.

    public ChatMessage(Integer Sender, Integer Receiver, String messageString,
                       String ID, boolean isMINE, String senderName) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        this.senderName = senderName;
    }

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }
}
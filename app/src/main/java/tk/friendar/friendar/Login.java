package tk.friendar.friendar;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tk.friendar.friendar.Chat.FriendAR_chat;

/**
 * Created by Simon on 8/22/2017.
 */

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    }
    public void chatTest(View view){
        Intent intent_chat = new Intent(Login.this, FriendAR_chat.class);
        startActivity(intent_chat);
    }

    public void signUp(View view){

        Intent intent_signup = new Intent(this,signUp.class);
        startActivity(intent_signup);

    }

    public void login(View view){
        Intent intent_login = new Intent(this,login_screen.class);
        startActivity(intent_login);

    }




}

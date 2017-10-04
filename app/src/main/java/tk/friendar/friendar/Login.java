package tk.friendar.friendar;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Simon on 8/22/2017.
 */

public class Login extends AppCompatActivity {

    Button signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        signUp = (Button) findViewById(R.id.button);
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    public void signUp(){

        Intent intent_signup = new Intent(this,signUp.class);
        startActivity(intent_signup);



    }
    public void login(){
        Intent intent_login = new Intent(this,login_screen.class);
        startActivity(intent_login);

    }


}

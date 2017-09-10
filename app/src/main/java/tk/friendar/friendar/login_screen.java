package tk.friendar.friendar;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Simon on 8/23/2017.
 */

public class login_screen extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
    }

    public void submitLogin(View view){
        Intent intent_submit = new Intent(this,screen1.class);
        startActivity(intent_submit);

    }

}

package tk.friendar.friendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class friendSearch extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        ListView listview = (ListView)findViewById(R.id.FriendList);
        ArrayList<String> arrayFriend = new ArrayList<>();
        arrayFriend.addAll(Arrays.asList(getResources().getStringArray(R.array.friend_array)));

        adapter = new ArrayAdapter<>(friendSearch.this, android.R.layout.simple_list_item_1,arrayFriend);

        listview.setAdapter(adapter);

    }


}

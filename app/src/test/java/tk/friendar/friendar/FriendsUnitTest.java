package tk.friendar.friendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import tk.friendar.friendar.arscreen.LocationHelper;
import tk.friendar.friendar.User;
import tk.friendar.friendar.arscreen.VRActivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by tinba on 17/10/2017.
 */

/**
 * Unit tests mocking some friends
 */
public class FriendsUnitTest {

    public void makeUsers() throws JSONException{
        JSONArray friends = new JSONArray();

        final JSONObject james = new JSONObject();
        james.put("fullName", "James");
        james.put("username", "JamesTone");
        james.put("email", "jamess@yahoo.com.au");
        // james.setLocation(LocationHelper.fromLatLon(-37.78262, 144.99645)); // tram stop
        //james.setLocation(LocationHelper.fromLatLon(-37.79859, 144.96048)); // south lawn
//        friends.add(james);

        User tin = new User("Tin-Tin", "shintin", "tintin@hotmail.net.au");
        //tin.setLocation(LocationHelper.fromLatLon(-37.78305, 144.99494)); // walker st
        //tin.setLocation(LocationHelper.fromLatLon(-37.79777, 144.96024)); // old arts
//        friends.add(tin);

        User simon = new User("Simon DiCicco", "kamonstone", "simonce@gmail.com");
        //simon.setLocation(LocationHelper.fromLatLon(-37.7846, 145.0043)); // heidelberb road bridge
        //simon.setLocation(LocationHelper.fromLatLon(-37.79852, 144.95938)); // baillieu library
//        friends.add(simon);
    }

    @Test
    public void getallFriends_nullFriends_returnsNull(){

    }

}

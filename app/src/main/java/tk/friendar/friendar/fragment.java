package tk.friendar.friendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Simon on 8/29/2017.
 */

public class fragment extends Fragment{
    public static final String ARG_OBJECT = "object";
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup box, Bundle state) {

        View view = layoutInflater.inflate(R.layout.fragment_collection_object, box, false);
        Bundle arguments = getArguments();
        ((TextView)view.findViewById(android.R.id.text1)).setText(Integer.toString(arguments.getInt(ARG_OBJECT)));

        return view;

    }

}

package de.th_koeln.hgrzesko.virtu.conversation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageBubbleFragment extends Fragment {


    public MessageBubbleFragment() {
        // Required empty public constructor
    }


    public static MessageBubbleFragment newInstance(Message message) {
        MessageBubbleFragment mbf = new MessageBubbleFragment();
        Bundle args = new Bundle();
        args.putSerializable("message", message);
        mbf.setArguments(args);
        return mbf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_bubble_incoming, container, false);
    }

}

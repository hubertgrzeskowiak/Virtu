package de.th_koeln.hgrzesko.virtu.conversationlist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.StoryService;


public class ConversationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversationSummaryListViewAdapter adapter;
    private StoryService storyService;
    private ServiceConnection connection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ConversationSummaryListViewAdapter();
        // set up bound service
        Intent intent = new Intent(this.getContext(), StoryService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                storyService = ((StoryService.StoryBinder) service).getService();
                Log.d("CLF Connection", "service " + storyService.toString() + " connected. attaching observer");
                storyService.addConversationListObserver(adapter);
                Log.d("CLF Connection", "num of conversations: " + String.valueOf(storyService.getConversations().size()));
                adapter.setConversations(storyService.getConversations());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("CLF Connection", "service disconnected");
            }
        };
        this.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        Log.d("CLF", "onStart");


        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_conversation_summary_list, container, false);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        storyService.removeConversationListObserver(adapter);
        getContext().unbindService(connection);
        storyService = null;
        super.onDestroy();
    }
}

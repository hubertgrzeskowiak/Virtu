package de.th_koeln.hgrzesko.virtu.conversation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Contact;
import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;
import de.th_koeln.hgrzesko.virtu.core.ConversationObserver;
import de.th_koeln.hgrzesko.virtu.core.entities.Inquiry;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;
import de.th_koeln.hgrzesko.virtu.core.StoryService;

public class ConversationActivity extends AppCompatActivity implements ConversationObserver {

    private RecyclerView recyclerView;
    private MessageListViewAdapter adapter;
    private StoryService storyService;
    private Conversation conversation;
    private ServiceConnection connection;
    private Inquiry inquiry;
    private View choicesDialog;
    private TextView choice1;
    private TextView choice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CA", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        choicesDialog = findViewById(R.id.choices_dialog);
        choice1 = (TextView) choicesDialog.findViewById(R.id.choice1);
        choice2 = (TextView) choicesDialog.findViewById(R.id.choice2);

        adapter = new MessageListViewAdapter();
    }

    @Override
    protected void onStart() {
        Log.d("CA", "onStart");
        super.onStart();
        Intent intent = new Intent(this, StoryService.class);
        final String conversationContactID = getIntent().getStringExtra("conversationContactID");
        Log.d("CA", "conversation from intent is " + conversationContactID);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("CA", "service connected");
                storyService = ((StoryService.StoryBinder) service).getService();
                Log.d("CA", "story service " + storyService.toString() + " with " +
                        String.valueOf(storyService.getConversations().size()) + " conversations");
                conversation = storyService.getConversationFor(conversationContactID);
                getSupportActionBar().setTitle(conversation.getName());
                adapter.setConversation(conversation);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                conversation.addConversationObserver(ConversationActivity.this); // move to adapter constructor?
                if (storyService.hasInquiryFor(conversation)) {
                    onInquiry(storyService.getInquiryFor(conversation));
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("CA", "service disconnected");
            }
        };
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.d("CA", "onStop");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.d("CA", "onDestroy");
        conversation.removeConversationObserver(this);
        recyclerView.setAdapter(null);
        this.unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onMessage(Message message) {
        adapter.onMessage(message);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
        choicesDialog.setVisibility(View.VISIBLE);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        choice1.setText(inquiry.getChoice1().getText());
        choice2.setText(inquiry.getChoice2().getText());
    }

    public void onChoice(View v) {
        choicesDialog.setVisibility(View.GONE);
        String text = "";
        switch (v.getId()) {
            case R.id.choice1:
                storyService.setInquiryDecision(conversation, inquiry, 1);
                text = choice1.getText().toString();
                break;
            case R.id.choice2:
                storyService.setInquiryDecision(conversation, inquiry, 2);
                text = choice2.getText().toString();
                break;
        }
        Message answer = new Message(Contact.PLAYER, conversation.getContact(), text);
        conversation.addMessage(answer);
        this.inquiry = null;
    }
}

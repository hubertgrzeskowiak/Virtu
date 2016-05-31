package de.th_koeln.hgrzesko.virtu.conversationlist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;
import de.th_koeln.hgrzesko.virtu.core.ConversationListObserver;


public class ConversationSummaryListViewAdapter
        extends RecyclerView.Adapter<ConversationSummaryViewHolder>
        implements ConversationListObserver {

    private final List<Conversation> conversations = new ArrayList<>();

    public ConversationSummaryListViewAdapter() {
        Log.d("CSLVA", "chat summary list view adapter initialized.");
    }

    public void setConversations(Collection<Conversation> conversations) {
        Log.d("CSLVA", "setConversations called with " + String.valueOf(conversations.size()) + " conversations");
        conversations.clear();
        int startSize = this.conversations.size();
        for (Conversation conversation : conversations) {
            this.conversations.add(conversation);
        }
        this.notifyItemRangeInserted(startSize, this.conversations.size()-1);
    }

    @Override
    public ConversationSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation_summary, parent, false);
        return new ConversationSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConversationSummaryViewHolder holder, final int position) {
        holder.setConversation(conversations.get(position));
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Log.d("CSLVA", "onClick" + String.valueOf(position));
//            }
//        };
//        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    @Override
    public void onConversationAdded(Conversation conversation) {
        conversations.add(conversation);
        this.notifyItemInserted(conversations.size()-1);
    }

    @Override
    public void onConversationRemoved(Conversation conversation) {
        int index = conversations.indexOf(conversation);
        if (index != -1) {
            conversations.remove(conversation);
            this.notifyItemRemoved(index);
        }
    }
}

package de.th_koeln.hgrzesko.virtu.conversation;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;

public class MessageListViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    public static enum ViewType {
        INCOMING, OUTGOING
    }

    private Conversation conversation;

    public MessageListViewAdapter() {
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ViewType.OUTGOING.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_message_bubble_outgoing, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_message_bubble_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (conversation.getMessages().get(position).isFromPlayer()) {
            return ViewType.OUTGOING.ordinal();
        } else {
            return ViewType.INCOMING.ordinal();
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.setMessage(conversation.getMessages().get(position));
    }

    @Override
    public int getItemCount() {
        return conversation.getMessages().size();
    }

    public void onMessage(Message message) {
        Log.d("MLVA", "got a new message through the listener");
        this.notifyItemInserted(conversation.getMessages().size() - 1);
    }
}

package de.th_koeln.hgrzesko.virtu.conversationlist;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.conversation.ConversationActivity;
import de.th_koeln.hgrzesko.virtu.core.entities.Contact;
import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;
import de.th_koeln.hgrzesko.virtu.core.ConversationObserver;
import de.th_koeln.hgrzesko.virtu.core.entities.Inquiry;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;

public class ConversationSummaryViewHolder extends RecyclerView.ViewHolder implements ConversationObserver {

    // conversation may change...
    private Conversation conversation;
    // ...these references not.
    private final TextView contactName;
    private final TextView lastMessage;
    private final TextView lastMessageDateTime;
    private final ImageView contactImage;
    private final View contactImageHolder;

    public ConversationSummaryViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conversation == null) {
                    Log.e("CSVH", "conversation null");
                }
                Intent intent = new Intent(view.getContext(), ConversationActivity.class);
                intent.putExtra("conversationContactID", conversation.getContact().getId());
                view.getContext().startActivity(intent);
                Log.d("CSVH", "onclick listener in CSVH " + conversation.toString());
            }
        });
        contactName = (TextView) this.itemView.findViewById(R.id.contact_name);
        lastMessage = (TextView) this.itemView.findViewById(R.id.last_message);
        lastMessageDateTime = (TextView) this.itemView.findViewById(R.id.last_message_datetime);
        contactImage = (ImageView) this.itemView.findViewById(R.id.contact_image);
        contactImageHolder = this.itemView.findViewById(R.id.contact_image_holder);
        contactImageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Make sure to call this method when binding this view holder. It automatically fills in all sub-views with
     * the right values.
     * @param conversation
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        contactName.setText(conversation.getName());
        Contact c = conversation.getContact();
        try {
            InputStream ims = itemView.getContext().getAssets().open(conversation.getContact().getImagePath());
            contactImage.setImageDrawable(Drawable.createFromStream(ims, null));
        }
        catch(IOException ex) {
            Log.e("CSVH", "", ex);
        }

        updateView();
        // TODO: this might be a bad idea in terms of garbage view holder recycling
        conversation.addConversationObserver(this);
    }

    public void updateView() {
        Message lastMsg = conversation.getLastMessage();
        if (lastMsg != null) {
            String dateTime = new SimpleDateFormat("HH:mm").format(lastMsg.getDeliveryTime());
            lastMessageDateTime.setText(dateTime);
            lastMessage.setText(lastMsg.getText());
        } else {
            lastMessageDateTime.setText("");
            lastMessage.setText("empty conversation");
        }
    }

    @Override
    public void onMessage(Message message) {
        updateView();
    }

    @Override
    public void onInquiry(Inquiry inquiry) {

    }
}

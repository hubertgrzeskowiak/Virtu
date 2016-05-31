package de.th_koeln.hgrzesko.virtu.conversation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    // This might change...
    private Message message;

    // ...this not.
    private final TextView text;
    private final TextView datetime;

    public MessageViewHolder(View itemView) {
        super(itemView);
        text = (TextView) this.itemView.findViewById(R.id.message_text);
        datetime = (TextView) this.itemView.findViewById(R.id.message_datetime);
    }

    public void setMessage(Message message) {
        this.message = message;
        this.text.setText(message.getText());
        this.datetime.setText(formatDateTime(message.getDeliveryTime()));
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}

package de.th_koeln.hgrzesko.virtu.core;

import java.io.Serializable;

import de.th_koeln.hgrzesko.virtu.core.entities.Inquiry;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;

/**
 * Created by root on 03.04.2016.
 */
public interface ConversationObserver extends Serializable {
    public void onMessage(Message message);
    public void onInquiry(Inquiry inquiry);
}

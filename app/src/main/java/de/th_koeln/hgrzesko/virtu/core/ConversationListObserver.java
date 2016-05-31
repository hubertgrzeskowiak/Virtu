package de.th_koeln.hgrzesko.virtu.core;

import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;

/**
 * Created by root on 03.04.2016.
 */
public interface ConversationListObserver {
    public void onConversationAdded(Conversation conversation);
    public void onConversationRemoved(Conversation conversation);
}

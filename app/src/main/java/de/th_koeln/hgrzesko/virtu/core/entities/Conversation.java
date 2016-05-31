package de.th_koeln.hgrzesko.virtu.core.entities;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.th_koeln.hgrzesko.virtu.core.ConversationObserver;

/**
 * Single-User Conversation.
 */
public class Conversation implements Serializable {

    public final static String EXTRA_CONVERSATION = "virtu.intent.extra.EXTRA_CONVERSATION";

    private String name;
    private List<Message> messages = new ArrayList<>();
    private Inquiry inquiry;
    private Contact contact;
    private boolean isCurrentlyWriting = false;
    private List<ConversationObserver> conversationObservers = new ArrayList<>();


    public Conversation(Contact contact) {
        this.contact = contact;
    }

    /**
     * Adds a message observer which is used after a message has been added to the conversation.
     * Duplicate entries will fail silently.
     * @param observer
     */
    public void addConversationObserver(ConversationObserver observer) {
        Log.d("Conversation", String.valueOf(this.hashCode()) + " Adding new observer");
        if (!conversationObservers.contains(observer)) {
            conversationObservers.add(observer);
        } else {
            Log.d("Conversation", "Tried to add an existing observer");
        }
    }

    /**
     * Removes a messave observer. Fails silently if observer was not registered.
     * @param observer
     */
    public void removeConversationObserver(ConversationObserver observer) {
        conversationObservers.remove(observer);
    }

    public List<ConversationObserver> getConversationObservers() {
        return conversationObservers;
    }

    /**
     * Adds a new message to the list.
     * @return this
     */
    public void addMessage(Message msg) {
        Log.d("Conversation",
                String.valueOf(this.hashCode()) +
                " adding new message to the list and informing observers: " +
                String.valueOf(conversationObservers.size()));
        messages.add(msg);
        msg.setDeliveryTime();
        for (ConversationObserver observer : conversationObservers) {
            observer.onMessage(msg);
        }
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
        for (ConversationObserver observer : conversationObservers) {
            observer.onInquiry(inquiry);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Contact getContact() {
        return contact;
    }

    public String getName() {
        return contact.getName();
    }

    // TODO: turn into listeners
    public void setIsCurrentlyWriting(boolean isWriting) {
        isCurrentlyWriting = isWriting;
    }
    public boolean getIsCurrentlyWriting() {
        return isCurrentlyWriting;
    }


    public String toString() {
        return String.format("Conversation '%s'", contact.getName());
    }

    public Message getLastMessage() {
        if (messages.size() > 0 ) {
            return messages.get(messages.size() - 1);
        } else {
            return null;
        }
    }
}

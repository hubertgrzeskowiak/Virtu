package de.th_koeln.hgrzesko.virtu.gameparser.dto;

/**
 * Created by root on 19.04.2016.
 */
public class MessageElem {

    private String text;
    private String sender;
    private String recipient;
    private String id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

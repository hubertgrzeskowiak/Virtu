package de.th_koeln.hgrzesko.virtu.core.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * A text message with an author, recipient and optional fields id and status.
 * Recipient defaults to Contact.PLAYER.
 */
public class Message implements Serializable {

    private final Contact author;
    private final Contact recipient;
    private final String text;
    private Date deliveredAt;


    public Message(Contact author, Contact recipient, String text) {
        this.author = author;
        this.recipient = recipient;
        this.text = text;
    }

    public Message(Contact author, String text) {
        this(author, Contact.PLAYER, text);
    }

    public String getText() {
        return text;
    }

    public Contact getAuthor() {
        return author;
    }

    public Contact getRecipient() {
        return recipient;
    }

    public Date getDeliveryTime() {
        return deliveredAt;
    }

    /**
     * Set the date of delivery to now.
     */
    public void setDeliveryTime() {
        this.deliveredAt = new Date();
    }

    public boolean isFromPlayer() {
        return this.getAuthor().equals(Contact.PLAYER);
    }

    public String toString() {
        return String.format("Message '%s'", text);
    }
}

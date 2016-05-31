package de.th_koeln.hgrzesko.virtu.core.entities;

import java.io.Serializable;

/**
 * A contact is a virtual profile which can send and receive messages in a conversation.
 * Instances of this class are usually created in StoryService.
 */
public class Contact implements Serializable {

    public static final Contact PLAYER = new Contact("you", "you");
    private final String id;

    private String name;
    private String imageFileName;

    /**
     * @param id
     * @param name
     */
    public Contact(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id unique string identifying this contact
     * @param name visible name
     * @param imageFileName android resource identifying the profile image
     */
    public Contact(String id, String name, String imageFileName) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImagePath() {
        return imageFileName;
    }

    public void setImagePath(String imageResource) {
        this.imageFileName = imageResource;
    }


    @Override
    public boolean equals(Object another) {
        if (another == null || ! (another instanceof Contact)) {
            return false;
        }
        final Contact other = (Contact) another;
        return other.getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}

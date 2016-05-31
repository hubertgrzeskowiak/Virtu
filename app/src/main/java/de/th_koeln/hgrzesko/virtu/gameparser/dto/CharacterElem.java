package de.th_koeln.hgrzesko.virtu.gameparser.dto;

/**
 * Created by root on 19.04.2016.
 */
public class CharacterElem {

    private String id;
    private String name;
    private String image;
    private String status;

    public String getImage() {
        return image;
    }

    public void setImageFileName(String image) {
        this.image = image;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

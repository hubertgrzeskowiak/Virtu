package de.th_koeln.hgrzesko.virtu.core.entities;

/**
 * Created by root on 01.04.2016.
 */
public class Choice {

    private String text;
    private String result;

    public Choice(String text, String result) {
        this.text = text;
        this.result = result;
    }

    public Choice() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

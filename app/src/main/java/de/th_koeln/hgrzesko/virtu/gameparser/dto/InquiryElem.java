package de.th_koeln.hgrzesko.virtu.gameparser.dto;

/**
 * Created by root on 19.04.2016.
 */
public class InquiryElem {


    private String target = null;
    private ChoiceElem choice1;
    private ChoiceElem choice2;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public ChoiceElem getChoice1() {
        return choice1;
    }

    public void setChoice1(ChoiceElem choice1) {
        this.choice1 = choice1;
    }

    public ChoiceElem getChoice2() {
        return choice2;
    }

    public void setChoice2(ChoiceElem choice2) {
        this.choice2 = choice2;
    }
}

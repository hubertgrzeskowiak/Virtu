package de.th_koeln.hgrzesko.virtu.core.entities;

/**
 * Composition of choices
 */
public class Inquiry {
    private Choice choice1;
    private Choice choice2;

    public Inquiry(Choice choice1, Choice choice2) {
        this.choice1 = choice1;
        this.choice2 = choice2;
    }

    public Choice getChoice1() {
        return choice1;
    }

    public Choice getChoice2() {
        return choice2;
    }

    /**
     * pos can be 1 or 2
     * @param pos
     * @return
     */
    public Choice getChoice(int pos) {
        if (pos == 1) {
            return choice1;
        } else {
            return choice2;
        }
    }
}

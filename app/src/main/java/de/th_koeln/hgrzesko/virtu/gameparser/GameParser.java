package de.th_koeln.hgrzesko.virtu.gameparser;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.th_koeln.hgrzesko.virtu.gameparser.dto.CharacterElem;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.ChoiceElem;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.InquiryElem;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.MessageElem;

/**
 * The Game Loader reads in an XML file from src/main/res/xml/game.xml and tries to parse it.
 * The intended use is as follows:
 * - You can call getCharacters() at any time and save the result.
 * - Call getNextEventType and based upon the response call either getNextMessage(), getNextInquiry() or
 * make the game end.
 * - After a choice has been made, call jumpToID() with the respective ID.
 */
public class GameParser {

    public enum StoryState {
        MESSAGE, INQUIRY, END_OF_STORY
    }

    //    private final File gameFile = new File("src/main/res/xml/game.xml");
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private Document document;

    // Here we save all found message IDs and the corresponding node index
    private Map<String, Integer> idMap = new HashMap<>();

    // intrnal counter over all story-element child nodes
    public int currPos = 0;
    // currPos points to this element
    private Element currElem;

    // remember last used "from" attribute
    private String lastFrom = "you";
    // and the last used "to" attribute
    private String lastTo = "you";

    public GameParser(InputStream gameFile) {
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(gameFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUpIDIndex();
        getNext();
    }

    private void setUpIDIndex() {
        NodeList nodes = getScreenplayElem().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nodes.item(i);
                if (elem.hasAttribute("id")) {
                    idMap.put(elem.getAttribute("id"), i);
                }
            }
        }
    }


    private Element getScreenplayElem() {
        return (Element) document.getElementsByTagName("screenplay").item(0);
    }

    /**
     * Increase counter and set new currElem field to be used by other methods.
     *
     * @throws IOException
     * @throws SAXException
     */
    private void getNext() {
        Node currNode = getScreenplayElem().getChildNodes().item(currPos++);
        while (currNode.getNodeType() != Node.ELEMENT_NODE) {
            currNode = getScreenplayElem().getChildNodes().item(currPos++);
        }
        currElem = (Element) currNode;
    }

    /**
     * Returns the next message from the stack and increases the counter.
     */
    public MessageElem getNextMessage() {
        if (!currElem.getTagName().equals("message")) {
            throw new IllegalStateException("current element is no message");
        }
        // return curr message
        MessageElem message = new MessageElem();
        message.setText(currElem.getTextContent().trim());
        if (currElem.hasAttribute("from")) {
            message.setSender(currElem.getAttribute("from"));
            lastFrom = currElem.getAttribute("from").trim();
        } else {
            message.setSender(lastFrom);
        }
        // if there is a recipient, use it and save for later use...
        if (currElem.hasAttribute("to") && !currElem.getAttribute("to").equals("")) {
            message.setRecipient(currElem.getAttribute("to").trim());
            lastTo = currElem.getAttribute("to").trim();
        // ...otherwise use last author
        } else {
            if (lastFrom.equals(currElem.getAttribute("from").trim())) {
                lastTo = "you";
            }
            message.setRecipient(lastTo);
        }
        if (currElem.hasAttribute("id")) {
            message.setId(currElem.getAttribute("id"));
        }

        // increase counter
        getNext();

        return message;
    }

    /**
     * Returns the next inquiry from the stack. This does not automatically increase the counter.
     */
    public InquiryElem getNextInquiry() {
        if (!currElem.getTagName().equals("inquiry")) {
            throw new IllegalStateException("current element is no inquiry");
        }
        InquiryElem inquiry = new InquiryElem();
        if (currElem.hasAttribute("target")) {
            inquiry.setTarget(currElem.getAttribute("target"));
        } else {
            if (!"you".equals(lastFrom)) {
                inquiry.setTarget(lastFrom);
            } else if (!"you".equals(lastTo)) {
                inquiry.setTarget(lastTo);
            }
        }
        NodeList nodes = currElem.getElementsByTagName("choice");
        if (nodes.getLength() != 2) {
            throw new IllegalStateException("Found an inquiry that had not 2 choices but " + String.valueOf(nodes.getLength()));
        }

        Element choiceElem1 = (Element) nodes.item(0);
        ChoiceElem choice1 = new ChoiceElem();
        choice1.setText(choiceElem1.getTextContent().trim());
        choice1.setResult(choiceElem1.getAttribute("result"));
        inquiry.setChoice1(choice1);

        Element choiceElem2 = (Element) nodes.item(1);
        ChoiceElem choice2 = new ChoiceElem();
        choice2.setText(choiceElem2.getTextContent().trim());
        choice2.setResult(choiceElem2.getAttribute("result"));
        inquiry.setChoice2(choice2);

        return inquiry;
    }

    /**
     * Move the internal counter to a message with the given id.
     *
     * @param id
     */
    public void jumpToID(String id) {
        if (idMap.containsKey(id)) {
            currPos = idMap.get(id);
            getNext();
        } else {
            throw new IllegalStateException(
                    String.format("couldn't find an ID to jump to. Are you sure there is a message with that ID? (%s)", id));
        }
    }

    /**
     * Returns the type of the next event or null in case of unknown or unexpected elements.
     * Call this to determine whether to call getNextMessage or getNextInquiry next.
     * @return
     */
    public StoryState getNextEventType() {
        switch (currElem.getTagName()) {
            case "message":
                return StoryState.MESSAGE;
            case "inquiry":
                return StoryState.INQUIRY;
            case "end":
                return StoryState.END_OF_STORY;
            default:
                return null;
        }
    }

    public List<CharacterElem> getCharacters() {
        List<CharacterElem> characters = new ArrayList<>();
        NodeList nodes = document.getElementsByTagName("character");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element chara = (Element) nodes.item(i);
            CharacterElem character = new CharacterElem();
            character.setId(chara.getAttribute("id"));
            NodeList nameNodeL = chara.getElementsByTagName("name");
            if (nameNodeL.getLength() > 0) {
                character.setName(nameNodeL.item(0).getTextContent().trim());
            }
            NodeList photoNodeL = chara.getElementsByTagName("image");
            if (photoNodeL.getLength() > 0) {
                character.setImageFileName(photoNodeL.item(0).getTextContent().trim());
            }
            NodeList statusNodeL = chara.getElementsByTagName("status");
            if (statusNodeL.getLength() > 0) {
                character.setStatus(statusNodeL.item(0).getTextContent().trim());
            }
            characters.add(character);
        }
        return characters;
    }
}
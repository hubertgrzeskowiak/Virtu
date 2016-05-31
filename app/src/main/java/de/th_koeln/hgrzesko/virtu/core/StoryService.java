package de.th_koeln.hgrzesko.virtu.core;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.th_koeln.hgrzesko.virtu.R;
import de.th_koeln.hgrzesko.virtu.core.entities.Choice;
import de.th_koeln.hgrzesko.virtu.core.entities.Contact;
import de.th_koeln.hgrzesko.virtu.core.entities.Conversation;
import de.th_koeln.hgrzesko.virtu.core.entities.Inquiry;
import de.th_koeln.hgrzesko.virtu.core.entities.Message;
import de.th_koeln.hgrzesko.virtu.gameparser.GameParser;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.CharacterElem;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.InquiryElem;
import de.th_koeln.hgrzesko.virtu.gameparser.dto.MessageElem;
import de.th_koeln.hgrzesko.virtu.main.MainActivity;

/**
 * Core component of Virtú.
 * On creation this loads the game with its current state.
 * Activities and fragments bind to this service to get the current state and add themselves as
 * observers.
 * Messages and Inquiries are sent from and to this class.
 * <p>
 * Contacts writing to the player are done only from inside this class, which gets its messages
 * from a game definition file. Same applies to inquiries. If the player does a choice,
 * the activity or fragment recieving the input should pass it to this service using service binding.
 * <p>
 * Sending messages to the player is done by an AlarmManager which takes a delay and delivers the
 * message back here where observers are informed.
 */
public class StoryService extends Service {

    /**
     * This is incremented every time we send a new message using the AlarmManager.
     */
    private int broadcastID = 1;

    public final static String EXTRA_MESSAGE = "virtu.intent.extra.EXTRA_MESSAGE";

    // used later for MUC
    //public final static String EXTRA_MESSAGE_CONVERSATION = "virtu.intent.extra.EXTRA_MESSAGE_CONVERSATION";

    private AlarmManager am;
    private final IBinder binder = new StoryBinder();
    private GameParser gameParser;

    /**
     * key is contact id
     */
    private Map<String, Contact> contacts = new HashMap<>();
    private List<Conversation> conversations = new ArrayList<>();
    private Map<Conversation, Inquiry> inquiries = new HashMap<>();
    private List<ConversationListObserver> conversationListObservers = new ArrayList<>();

    public StoryService() {
        Log.d("Story Service", "constructor");
    }


    /**
     * Service is created only once no matter how many times it's "started" or bound to.
     */
    @Override
    public void onCreate() {
        Log.d("Story Service", "onCreate");
        super.onCreate();
        am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        gameParser = new GameParser(this.getResources().openRawResource(R.raw.game));
        List<CharacterElem> chars = gameParser.getCharacters();
        for (CharacterElem charElem : chars) {
            addContact(charElem.getId(), charElem.getName(), charElem.getImage());
        }
        nextStoryEvent();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Story Service", "onDestroy");
    }

    /**
     * Simple binder that returns the service itself.
     */
    public class StoryBinder extends Binder {
        public StoryService getService() {
            Log.d("Story Service", "returning a bound instance " + StoryService.this.toString());
            Log.d("Story Service", "service has " + String.valueOf(conversations.size()) + " conversations");
            return StoryService.this;
        }
    }

    private void nextStoryEvent() {
        Log.d("Story Service", "getting next story event");
        switch (gameParser.getNextEventType()) {
            case MESSAGE:
                Log.d("Story Service", "found a message");
                MessageElem msgElem = gameParser.getNextMessage();
                Message msg = messageElem2Message(msgElem);
                scheduleResponse(msg, 0);
                break;
            case INQUIRY:
                Log.d("Story Service", "found an inquiry");
                InquiryElem inquiryElem = gameParser.getNextInquiry();
                Inquiry inquiry = inquiryElem2Inquiry(inquiryElem);
                Conversation targetConv = getConversationFor(inquiryElem.getTarget());
                targetConv.setInquiry(inquiry);
                break;
            case END_OF_STORY:
                Log.d("Story Service", "End Of Story!!!!!!!");
                break;
        }
    }

    /**
     * Start commands are used to deliver delayed messages of contacts and inquiries to the PLAYER.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StoryService", "intent in onStartCommand: " + intent.toString());
        Log.d("StoryService", "game loader state: " + String.valueOf(gameParser.currPos));
        if (intent != null) {
            Message message = (Message) intent.getSerializableExtra(EXTRA_MESSAGE);
            if (message != null) {
                addMessage(message);
            } else {
                Log.w("StoryService", "Discarding intent without EXTRA_MESSAGE");
            }
        }
        nextStoryEvent();
        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Convert a message from the XML game loader to a real message object that can be sent.
     *
     * @param msgElem
     * @return
     */
    private Message messageElem2Message(MessageElem msgElem) {
        Contact sender;
        Contact recipient;
        if ("you".equals(msgElem.getSender())) {
            sender = Contact.PLAYER;
        } else {
            sender = contacts.get(msgElem.getSender());
        }
        if ("you".equals(msgElem.getRecipient())) {
            recipient = Contact.PLAYER;
        } else {
            recipient = contacts.get(msgElem.getRecipient());
        }
        Message msg = new Message(sender, recipient, msgElem.getText());
        return msg;
    }

    private Inquiry inquiryElem2Inquiry(InquiryElem inquiryElem) {
        if (inquiryElem.getTarget() == null) {
            throw new IllegalStateException("Found an inquiry without a valid target!");
        }
        Choice choice1 = new Choice(inquiryElem.getChoice1().getText(), inquiryElem.getChoice1().getResult());
        Choice choice2 = new Choice(inquiryElem.getChoice2().getText(), inquiryElem.getChoice2().getResult());
        Inquiry inquiry = new Inquiry(choice1, choice2);
        Conversation targetConv = getConversationFor(inquiryElem.getTarget());
        inquiries.put(targetConv, inquiry);
        return inquiry;
    }

    /**
     * This adds a message to the corresponding conversation. This method is called for instant and delayed
     * messages (after delay). Here we also look up if the message is to be read in a currently active (registered)
     * activity. If so, we only deliver it, otherwise we also issue a notification.
     *
     * @param message
     */
    private void addMessage(Message message) {
        Log.d("Story Service", "attempting to add a message to corresponding conversation");
        Conversation conversation;
        if (message.getAuthor().equals(Contact.PLAYER)) {
            conversation = getConversationFor(message.getRecipient());
        } else {
            conversation = getConversationFor(message.getAuthor());
        }
        conversation.addMessage(message);

        Log.d("Story Service", "conv observers: " + String.valueOf(conversation.getConversationObservers().size()));
        if (conversation.getConversationObservers().size() == 0) {
//            sendNotification();
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.profile_image_circle)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        // stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Adds a conversation observer. Duplicate entries will fail silently.
     *
     * @param observer
     */
    public void addConversationListObserver(ConversationListObserver observer) {
        Log.d("StoryService", "adding observer");
        if (!conversationListObservers.contains(observer)) {
            conversationListObservers.add(observer);
        }
    }

    /**
     * Remove a conversation observer. Fails silently if observer was not registered.
     *
     * @param observer
     */
    public void removeConversationListObserver(ConversationListObserver observer) {
        Log.d("StoryService", "removing observer");
        conversationListObservers.remove(observer);
    }


    /**
     * Searches for an existing single-user conversation with given contact and returns it
     * if found. Otherwise a new conversation is created and returned.
     *
     * @param contact
     * @return
     */
    public Conversation getConversationFor(Contact contact) {
        Log.d("StoryService", "searching conversation for " + contact.toString() +
                " " + String.valueOf(contact.hashCode()));
        // TODO: this is probably pretty slow. consider ordered hashmap or something like that.
        for (Conversation c : conversations) {
            if (c.getContact().getId().equals(contact.getId())) {
                return c;
            }
        }
        Log.d("StoryService", String.format("creating new conversation for contact %s", contact.getName()));
        Conversation c = new Conversation(contact);
        conversations.add(c);
        for (ConversationListObserver observer : conversationListObservers) {
            observer.onConversationAdded(c);
        }
        return c;
    }

    public Conversation getConversationFor(String contactID) {
        for (Conversation c : conversations) {
            if (c.getContact().getId().equals(contactID)) {
                return c;
            }
        }
        throw new IllegalStateException(
                String.format("We have %s conversations but there is none with contact ID '%s'",
                conversations.size(), contactID));
    }


    public Contact addContact(String id, String name) {
        Contact c = new Contact(id, name);
        contacts.put(id, c);
        return c;
    }

    public Contact addContact(String id, String name, String imageFileName) {
        Contact c = new Contact(id, name, imageFileName);
        contacts.put(id, c);
        return c;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    /**
     * Schedule a response to the player. Default time is 2s + length of message / 4.
     *
     * @param message
     */
    public void scheduleResponse(Message message) {
        int delay = 2;
        if (message.getText().length() > 0) {
            delay += message.getText().length() / 4;
        }
        scheduleResponse(message, delay);
    }

    /**
     * Schedule a response that is delivered after some time.
     * <p>
     * TODO: implement Handler for short delays. AlarmManager is too unreliable
     *
     * @param message
     * @param delaySeconds delay of delivery in seconds
     */
    public void scheduleResponse(Message message, int delaySeconds) {
        Intent intent = new Intent(this, StoryService.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        PendingIntent pendingIntent = PendingIntent.getService(
                this, this.broadcastID++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long wakeTime = System.currentTimeMillis() + 1000 * delaySeconds;
        Log.d("StoryService", "current time: " + String.valueOf(System.currentTimeMillis()));
        Log.d("StoryService", String.format("scheduling response at %s ", wakeTime));
        // Since KITKAT alarm delivery is inexact.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setWindow(AlarmManager.RTC_WAKEUP, wakeTime, 2000, pendingIntent);
            am.setExact(AlarmManager.RTC_WAKEUP, wakeTime, pendingIntent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, wakeTime, pendingIntent);
        }
    }

    public boolean hasInquiryFor(Conversation conversation) {
        return inquiries.containsKey(conversation);
    }

    public Inquiry getInquiryFor(Conversation conversation) {
        return inquiries.get(conversation);
    }

    // TODO: move to inquiry and observe?
    // TODO: save away the choice

    /**
     * @param inquiry
     * @param decision 1 or 2
     */
    public void setInquiryDecision(Conversation conversation, Inquiry inquiry, int decision) {
        String nextID = inquiry.getChoice(decision).getResult();
        gameParser.jumpToID(nextID);
        inquiries.remove(conversation);
        nextStoryEvent();
    }
}
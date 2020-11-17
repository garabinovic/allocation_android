package com.andrijag.allocation.controlers.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static final List<EventItem> EVENTS = new ArrayList<EventItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
        EVENTS.add(new EventItem("01", "10:30 - 15:00", "Group ACB", "Cleaning 1", "Loc 1"));
        EVENTS.add(new EventItem("02", "15:00 - 17:30", "Group DEF", "Cleaning 2", "Beograd"));
        EVENTS.add(new EventItem("03", "17:30 - 19:00", "Group GJK", "Cleaning 3", "Mirijevo"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static class EventItem {
        public final String id;
        public final String time;
        public final String clientName;
        public final String eventTitle;
        public final String location;

        public EventItem(String id, String time, String clientNamem, String eventTitle, String location) {
            this.id = id;
            this.time = time;
            this.clientName = clientNamem;
            this.eventTitle = eventTitle;
            this.location = location;
        }

        @Override
        public String toString() {
            return eventTitle;
        }
    }
}

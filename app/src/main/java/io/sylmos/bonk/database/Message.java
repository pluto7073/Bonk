package io.sylmos.bonk.database;

import java.util.HashMap;

public class Message extends Base {

    public String author, content, id;

    public Message(String id, String author, String content) {
        super(DatabaseManager.INSTANCE, "messages/" + id);
        this.id = id;
        this.content = content;
        this.author = author;
    }

    public Message(HashMap<String, Object> data) {
        this((String) data.get("id"), (String) data.get("author"), (String) data.get("content"));
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("author", author);
        map.put("content", content);
        return map;
    }

    @Override
    public void loadFromHashMap(HashMap<String, Object> map) {
        id = (String) map.get("id");
        content = (String) map.get("content");
        author = (String) map.get("author");
    }

}

package io.sylmos.bonk.database;

import java.util.ArrayList;
import java.util.HashMap;

public class Post extends Base {

    public long id;
    public String author, text;
    public ArrayList<String> files;

    public Post(long id, String author, ArrayList<String> files, String text) {
        super(DatabaseManager.INSTANCE, "posts/" + id);
        this.id = id;
        this.author = author;
        this.files = files;
        this.text = text;
    }

    public Post(HashMap<String, Object> data) {
        this((long) data.get("id"), (String) data.get("author"), (ArrayList<String>) data.get("files"), (String) data.get("text"));
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("author", author);
        map.put("files", files);
        map.put("text", text);
        return map;
    }

    @Override
    public void loadFromHashMap(HashMap<String, Object> map) {
        id = (long) map.get("id");
        author = (String) map.get("author");
        files = (ArrayList<String>) map.get("files");
        text = (String) map.get("text");
    }

}

package sample;

import java.util.HashMap;

public class Message {
    private HashMap<String, String> headers = new HashMap<>();
    private String message = "", subject = "", from = "", to = "";
    private int id;
    private int size = 0;
    private boolean isHTML = true;

    public Message(HashMap<String, String> headers, boolean isHTML, String message) {
        this.headers = headers;
        this.message = message;
        this.isHTML = isHTML;

        this.subject = headers.get("subject");
        this.from = headers.get("from");
        this.to = headers.get("to");
    }

    public Message(int id, HashMap<String, String> headers, boolean isHTML, String message) {
        this(headers, isHTML, message);
        this.id = id;
    }

    public Message(String from, String to, String subject, String message, boolean HTML) {
        this.message = message;
        this.subject = subject;
        this.from = from;
        this.to = to;
        isHTML = HTML;
    }

    public Message(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public boolean isHTML() {
        return isHTML;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name){
        name = name.toLowerCase();

        return headers.get(name);
    }

    @Override
    public String toString() {
        if(from.equals("")){
            return id + " (" + size/1024 + " KB)";
        }else{
            return from + " - " + subject;
        }
    }
}
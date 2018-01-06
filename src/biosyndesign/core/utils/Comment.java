package biosyndesign.core.utils;

import java.awt.*;

public class Comment {

    public int start;
    public int end;
    public String message;
    public Color c;
    public Object tag;

    public Comment(int start, int end, String message, Color c, Object tag) {
        this.start = start;
        this.end = end;
        this.message = message;
        this.c = c;
        this.tag = tag;
    }
}
package biosyndesign.core.utils;

import java.awt.*;
import java.io.Serializable;

public class Comment implements Serializable, Cloneable {

    public int start;
    public int end;
    public String message;
    public Color c;
   // public Object tag;

    public Comment(int start, int end, String message, Color c) {
        this.start = start;
        this.end = end;
        this.message = message;
        this.c = c;
        //this.tag = tag;
    }

    public Comment clone(){
        return new Comment(start, end, message,  c) ;
    }
}
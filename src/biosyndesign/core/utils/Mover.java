package biosyndesign.core.utils;

import java.awt.*;

/**
 * Created by Umarov on 1/19/2017.
 */
public class Mover {
    private int x, y;
    private int max;
    private int cdist;
    private int cdir;
    private int step;
    private int t;

    public Mover(int step) {
        this.step = step;
    }

    public void move() {
        if (cdist > max) {
            t++;
            if (t==2) {
                max = cdist;
                t=0;
            }
            cdir = (cdir + 1) % 4;
            cdist = 0;
        }
        if (cdir == 0) {
            x += step;
        } else if (cdir == 1) {
            y += step;
        } else if (cdir == 2) {
            x -= step;
        } else if (cdir == 3) {
            y -= step;
        }
        cdist++;
    }

    public static int max(int size){
        Mover m = new Mover(1);
        for(int i =0; i<size;i++){
            m.move();
        }
        return m.max;
    }
    public int x(){
        return x;
    }
    public int y(){
        return y;
    }
}

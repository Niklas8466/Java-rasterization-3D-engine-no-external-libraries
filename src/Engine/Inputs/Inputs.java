package Engine.Inputs;

import java.awt.event.*;
import java.util.HashMap;

public class Inputs implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private HashMap<Character, Boolean> keys = new HashMap<>();


    public boolean isPressed(char c){
        if(keys.get(c) == null || !keys.get(c))
            return false;
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(Character.toLowerCase(e.getKeyChar()), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(Character.toLowerCase(e.getKeyChar()), false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}

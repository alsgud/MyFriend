import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyComponent extends JComponent implements MouseListener {
    boolean clicked = false;
    boolean pressed = false;
    boolean released = false;


    @Override
    public void mouseClicked(MouseEvent e) {
        setClicked(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setPressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setReleased(true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void setClicked(boolean clicked){
        this.clicked = clicked;
    }
    public void setPressed(boolean pressed){
        this.pressed = pressed;
    }
    public void setReleased(boolean released){
        this.released = released;
    }
    public boolean isClicked() {
        return clicked;
    }
    public boolean isPressed() {
        return pressed;
    }
    public boolean isReleased() {
        return released;
    }
}

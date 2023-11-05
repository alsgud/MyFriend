import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static java.lang.Math.abs;


/**
 * Practice project desktop pet
 * @author Min-hyong Lee
 */

public class Friend extends JFrame {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int FPS = 60;
    private final static int WINDOW_WIDTH = (int) screenSize.getWidth();
    private final static int WINDOW_HEIGHT = (int) screenSize.getHeight();
    private final static ImageIcon CAPOO_WALK_LEFT = new ImageIcon(Friend.class.getResource("res/capoowalkleft.gif"));
    private final static ImageIcon CAPOO_WALK_RIGHT = new ImageIcon(Friend.class.getResource("res/capoowalkright.gif"));
    private final static ImageIcon CAPOO_LICK = new ImageIcon(Friend.class.getResource("res/capoolick.gif")); // 14 frames
    private final static ImageIcon CAPOO_SLEEP = new ImageIcon(Friend.class.getResource("res/capoosleep.gif")); // 21 frames
    private final static ImageIcon CAPOO_HMPH = new ImageIcon(Friend.class.getResource("res/capoohmph.gif")); // 11 frames
    private final static ImageIcon CAPOO_BOING = new ImageIcon(Friend.class.getResource("res/capooboing.gif")); // 20 frames
    private final static ImageIcon CAPOO_EXCITED = new ImageIcon(Friend.class.getResource("res/capooexcited.gif")); // 15 frames
    private final static ImageIcon CAPOO_ROLL = new ImageIcon(Friend.class.getResource("res/capooroll.gif"));
    private final static ImageIcon CAPOO_ROLL_READY = new ImageIcon(Friend.class.getResource("res/capoorollready.gif"));
    private static ImageIcon imageIcon = CAPOO_ROLL;
    private static JLabel label = new JLabel(imageIcon);
    private final static JFrame f = new JFrame("My Friend");
    private final static MyComponent mouseClick = new MyComponent();
    private static int FRIEND_WIDTH = imageIcon.getIconWidth();
    private static int FRIEND_HEIGHT = imageIcon.getIconHeight();
    private static final Random rand = new Random();
    private final static double ACCELERATION = 0.5;
    private final static double BOUNCE_COEFF = 0.6;
    private final static double AIR_RES_CONST = 0.99;
    private static int clickedState = 0;
    private static int clickedTimer = 0;
    private static int idleAction = 0;
    private static int direction;
    private static int idleActionCooldown = 0;
    private static int idleActionTimer = 0;
    private static double velocityY;
    private static double velocityX;
    private static double prevMousePositionX;
    private static double prevMousePositionY;
    private JPanel panelMain; // not used (default panel from Friend.form)

    public static void main(String[] args) {
        Friend friend = new Friend();
        friend.runFriend();
    }

    /** initialise friend **/
    public void runFriend() {

        f.setBounds(((WINDOW_WIDTH - FRIEND_WIDTH))/2, ((WINDOW_HEIGHT - FRIEND_HEIGHT)/2),
                FRIEND_WIDTH, FRIEND_HEIGHT);
        f.setUndecorated(true);
        f.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f)); // 'a' is opacity
        f.addMouseListener(mouseClick);
        f.getContentPane().add(label);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        run();

    }

    /** state update for friend every frame **/
    public void update() {

        // physics
        if (f.getY() > WINDOW_HEIGHT - FRIEND_HEIGHT) {
            setY(WINDOW_HEIGHT - FRIEND_HEIGHT);
        }
        doPhysics();

        // idle action
        if (velocityY == 0 && f.getY() == WINDOW_HEIGHT - FRIEND_HEIGHT) {
            velocityX = 0;
            if (clickedTimer == 0 && idleActionTimer == 0 && idleActionCooldown == 0 && clickedState == 0) {
                changeState(CAPOO_BOING);
                idleAction = rand.nextInt(2) + 1; // 1 is walk, 2 is sleep
                direction = rand.nextInt(2); // 0 is left, 1 is right
            }
        }

        // idle behaviour
        if (clickedState == 0 && idleAction == 1) {
            if (direction == 0 && f.getX() > 150) { // magic number left wall
                changeState(CAPOO_WALK_LEFT);
                setX(f.getX() - 3);
            }
            else if (direction == 1 && f.getX() < WINDOW_WIDTH - FRIEND_WIDTH) {
                changeState(CAPOO_WALK_RIGHT);
                setX(f.getX() + 3);
            }
            else {
                direction = abs(direction - 1);
            }
            idleActionTimer += 1;
            if (idleActionTimer == rand.nextInt(100) + 50 || idleActionTimer > 150) { // magic number timer
                idleAction = 0;
                idleActionTimer = 0;
                idleActionCooldown += 1;
            }
        }
        if (clickedState == 0 && idleAction == 2) {
            changeState(CAPOO_SLEEP);
            idleActionTimer += 1;
            if (idleActionTimer == rand.nextInt(100) + 250 || idleActionTimer > 350) { // magic number timer
                idleAction = 0;
                idleActionTimer = 0;
                idleActionCooldown += 1;
            }
        }

        // idle action cooldown
        if (idleActionCooldown != 0) {
            idleActionCooldown += 1;
            if (idleActionCooldown == rand.nextInt(200) + 200 || idleActionCooldown > 400) { // magic number timer
                idleActionCooldown = 0;
            }
        }

        // dragging friend into the air
        if (mouseClick.isPressed() && clickedState == 0) {
            idleAction = 0;
            idleActionTimer = 0;
            if (MouseInfo.getPointerInfo().getLocation().getY() < 950) { // magic number min height for grab
                velocityY = 0;
                f.setLocation((int) (MouseInfo.getPointerInfo().getLocation().getX() - (FRIEND_WIDTH / 2)),
                        (int) (MouseInfo.getPointerInfo().getLocation().getY() - (FRIEND_HEIGHT / 2)));
                changeState(CAPOO_ROLL_READY);
            }
        }
        if (mouseClick.isReleased()) {
            idleActionCooldown = 1;
            changeState(CAPOO_ROLL);
            mouseClick.setPressed(false);
            mouseClick.setReleased(false);
            calculateThrow(prevMousePositionX, prevMousePositionY,
                    MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
        }
        if (velocityY == 0 && f.getY() == WINDOW_HEIGHT - FRIEND_HEIGHT && clickedTimer == 0 && idleAction == 0) {
            clickedState = 0;
            changeState(CAPOO_BOING);
        }

        // clicked behaviour
        if (mouseClick.isClicked()) {
            clickedState = rand.nextInt(3) + 1;
            mouseClick.setClicked(false);
        }
        if (clickedState == 1 && clickedTimer < 60) {
            changeState(CAPOO_EXCITED);
            clickedTimer += 1;
            if (clickedTimer == 60) {
                clickedState = 0;
                clickedTimer = 0;
            }
        }
        else if (clickedState == 2 && clickedTimer < 75) {
            changeState(CAPOO_HMPH);
            clickedTimer += 1;
            if (clickedTimer == 75) {
                clickedState = 0;
                clickedTimer = 0;
            }
        }
        else if (clickedState == 3 && clickedTimer < 60) {
            changeState(CAPOO_LICK);
            clickedTimer += 1;
            if (clickedTimer == 60) {
                clickedState = 0;
                clickedTimer = 0;
            }
        }

        prevMousePositionX = MouseInfo.getPointerInfo().getLocation().getX();
        prevMousePositionY = MouseInfo.getPointerInfo().getLocation().getY();
        mouseClick.setClicked(false);
        repaint();
    }

    /** apply update for friend as per frame rate **/
    public void run() {
        long timeStart = System.nanoTime();
        long secondInNano = 1000000000;
        long oneFrame = secondInNano/ FPS;
        long lastFpsTime = 0;

        while (true) {
            long timeNow = System.nanoTime();
            long timeLength = timeNow - timeStart;
            timeStart = timeNow;
            lastFpsTime += timeLength;

            // called every frame
            if (lastFpsTime >= oneFrame) {
                lastFpsTime = 0;
                update();
            }
        }
    }

    public void doPhysics() {
        if (f.getY() < WINDOW_HEIGHT - FRIEND_HEIGHT) {
            velocityY = velocityY + ACCELERATION;
            setY((int) (f.getY() + velocityY));
        }
        else if (velocityY - velocityY * BOUNCE_COEFF > 4) { // magic number 3 idk how to fix bug
            setY((int) (f.getY() - velocityY * BOUNCE_COEFF));
            velocityY = -(velocityY * BOUNCE_COEFF);
            setY((int) (f.getY() + velocityY));
        }
        else {
            velocityY = 0;
        }

        setX((int) (f.getX() - velocityX));
        if (f.getX() <= 0 || f.getX() >= WINDOW_WIDTH - FRIEND_WIDTH){
            velocityX *= -1;
        }
        if (abs(velocityX) > 0){
            velocityX *= AIR_RES_CONST;
        }
    }

    /** allows changing of friend's state/sprite **/
    public void changeState(ImageIcon icon) {
        imageIcon = icon;
        label = new JLabel(imageIcon);
        f.setLocation(f.getX() + (FRIEND_WIDTH - imageIcon.getIconWidth())/2,
                f.getY() + (FRIEND_HEIGHT - imageIcon.getIconHeight()));
        FRIEND_WIDTH = imageIcon.getIconWidth();
        FRIEND_HEIGHT = imageIcon.getIconHeight();
        f.setSize(FRIEND_WIDTH, FRIEND_HEIGHT);
        f.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
        f.getContentPane().removeAll();

        // bugfix cos gif sizing
        if (mouseClick.isPressed() && MouseInfo.getPointerInfo().getLocation().getY() < 950 && clickedState == 0) {
            velocityY = 0;
            f.setLocation((int) (MouseInfo.getPointerInfo().getLocation().getX() - (FRIEND_WIDTH / 2)),
                    (int) (MouseInfo.getPointerInfo().getLocation().getY() - (FRIEND_HEIGHT / 2)));
        }
    }

    public void calculateThrow(double prevX, double prevY, double x, double y) {
        velocityY = (y - prevY)/3;
        velocityX = -(x - prevX)/3;
    }

    public void repaint() {
        f.getContentPane().add(label);
        f.revalidate();
        f.repaint();
    }

    public void setY(int y) {
        f.setLocation(f.getX(), y);
    }

    public void setX(int x) {
        f.setLocation(x, f.getY());
    }
}

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

/**
 * An example of what I had in mind by a more "intuitive" touch focus indicator.
 */
public class CirclePointer
{
    private final static class FocusPoint
    {
        final Point location;
        final long startedAt;
        long cancelledAt;
        int r;

        FocusPoint(Point location)
        {
            this.location = location;
            this.startedAt = System.currentTimeMillis();
        }

        public boolean isActive()
        {
            return cancelledAt == 0;
        }
    }

    /** 25 frames per second? */
    private static final int ANIMATION_DELAY = 1000 / 25;

    /** How long does it take to place the marker? */
    private static final int PLACE_MARKER_TIME = 1000;

    /** Fadeout time. */
    private static final int FADEOUT_TIME = 500;

    /** Animation timer callback. */
    private final ActionListener timerListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            panel.repaint();
        }
    };

    /**
     * A queue of touch points.
     */
    private final Deque<FocusPoint> trackers = new ArrayDeque<FocusPoint>();

    /**
     * Placed markers (fixed). 
     */
    private final Deque<Point> markers = new ArrayDeque<Point>();

    /**
     * Canvas on which we paint the trackers.
     */
    private JComponent panel;

    /*
     * 
     */
    private void start()
    {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.getContentPane().add(panel = createCanvas());
        frame.setSize(400, 400);
        frame.setVisible(true);

        final Timer timer = new Timer(ANIMATION_DELAY, timerListener);
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent e)
            {
                timer.stop();
            }
        });
    }

    /*
     * 
     */
    @SuppressWarnings("serial")
    private JComponent createCanvas()
    {
        final JPanel panel = new JPanel()
        {
            public void update(Graphics g)
            {
                // Don't bother with updates.
            }
            
            public void paint(Graphics g)
            {
                final Graphics2D g2d = (Graphics2D) g;
                animateTrackers(g2d);
            }
        };
        panel.setDoubleBuffered(true);

        panel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                addFocusTracker(e.getPoint());
            }

            public void mouseReleased(MouseEvent e)
            {
                if (!trackers.isEmpty() && trackers.getLast().isActive())
                {
                    trackers.getLast().cancelledAt = System.currentTimeMillis();
                }
            }
        });

        return panel;
    }

    /*
     * 
     */
    protected void animateTrackers(Graphics2D g2d)
    {
        assert SwingUtilities.isEventDispatchThread();

        // Advance animation states.
        final long now = System.currentTimeMillis();
        final Iterator<FocusPoint> i = trackers.descendingIterator();
        while (i.hasNext())
        {
            final FocusPoint fp = i.next();
            if (fp.isActive())
            {
                if ((now - fp.startedAt) >= PLACE_MARKER_TIME)
                {
                    placeMarker(fp.location);
                    i.remove();
                }
            }
            else
            {
                if ((now - fp.cancelledAt) >= FADEOUT_TIME)
                {
                    i.remove();
                }
            }
        }

        // Clear clipping area.
        final Rectangle cb = g2d.getClipBounds();
        g2d.setColor(g2d.getBackground());
        g2d.fillRect(cb.x, cb.y, cb.width, cb.height);

        // Repaint markers.
        g2d.setColor(Color.RED);
        for (Point p : markers)
        {
            g2d.fillOval(p.x - 3, p.y - 3, 6, 6);
        }

        // Repaint the focus points.
        for (FocusPoint fp : trackers)
        {
            g2d.setColor(fp.isActive() ? Color.BLUE : Color.GRAY);

            if (fp.isActive())
            {
                final float e = (now - fp.startedAt) / (float) PLACE_MARKER_TIME;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, e));

                fp.r = easing(e, 100, 0);
                drawTracker(g2d, fp, fp.r);
            }
            else
            {
                final float e = (now - fp.cancelledAt) / (float) FADEOUT_TIME; 
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - e));

                int r = easing((now - fp.cancelledAt) / (float) FADEOUT_TIME, fp.r, fp.r + 50);
                drawTracker(g2d, fp, r);
            }
        }
    }

    /*
     * 
     */
    private void drawTracker(Graphics2D g2d, FocusPoint fp, int r)
    {
        int [] thickness = new int []    {   5,   3,    2,   1};
        float [] distance = new float [] {0.5f,  1f, 1.5f,  2f};

        for (int i = 0; i < thickness.length; i++)
        {
            g2d.setStroke(new BasicStroke(thickness[i]));
            int rs = (int) (r * distance[i]);
            g2d.drawOval(fp.location.x - rs / 2, fp.location.y - rs / 2, rs, rs);
        }
    }

    /*
     * 
     */
    private int easing(float current, int fromValue, int toValue)
    {
        current = (float) (1 - Math.sin(Math.PI / 2 * (1 + current)));
        final int difference = toValue - fromValue;
        return (int) (fromValue + (difference * current));
    }

    /*
     * 
     */
    private void placeMarker(Point location)
    {
        markers.addLast(location);
    }

    /*
     * 
     */
    private void addFocusTracker(Point point)
    {
        trackers.addLast(new FocusPoint(point));
    }

    /*
     * 
     */
    public static void main(String [] args)
    {
        new CirclePointer().start();
    }
}

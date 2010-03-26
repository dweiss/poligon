import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.sound.sampled.*;
import javax.swing.JFrame;




public class ClipPlayer
{
    private Clip m_clip;

    public static void main(String [] args)
        throws Exception
    {
        AudioInputStream is = AudioSystem.getAudioInputStream(
            ClipPlayer.class.getResource("bomb.wav"));

        AudioFormat format = is.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        
        final Clip m_clip = (Clip) AudioSystem.getLine(info);
        m_clip.addLineListener(new LineListener() {
            public void update(LineEvent event)
            {
                final LineEvent.Type t = event.getType();
                System.out.println(t);
            }
         });

        m_clip.open(is);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
            new KeyEventDispatcher()
            {
                public boolean dispatchKeyEvent(KeyEvent e)
                {
                    System.out.println(e);
                    if (e.getKeyChar() == ' ' && e.getID() == KeyEvent.KEY_PRESSED)
                    {
                        if (m_clip.isActive())
                        {
                            m_clip.stop();
                        }
                        m_clip.setFramePosition(1000);
                        m_clip.start();
                    }
                    return false;
                }
            });
        
        new JFrame().setVisible(true);
    }
}
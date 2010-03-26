import java.io.FileOutputStream;
import java.io.OutputStream;


public class Test
{
    public static void main(String [] args) throws Exception
    {
        char [] b = new char [256];
        for (int i = 0; i < 256; i++) 
            b[i] = (char)(i & 0xff);
        
        OutputStream os = new FileOutputStream("/home/dweiss/www/default/binary-js/out.bin");
        os.write(new String(b).getBytes("UTF-8"));
    }
}

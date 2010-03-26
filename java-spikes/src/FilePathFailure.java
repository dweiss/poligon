import java.io.*;


public class FilePathFailure
{
    public static void main(String [] args) throws IOException
    {
        for (int i = 0; i < 100000; i++)
        {
            int [] a = new int [1024 * 1024];
        }
    }
}

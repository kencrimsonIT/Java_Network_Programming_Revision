package IO;

import java.io.*;

/**
 * This is example of splitting & joining scheme like WinRAR or .zip file
 * Hope it will be useful to you ^^!
 */

public class Splitter_Joiner {
    // Splitting method
    public static void split(String source, long pSize) {
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(source));
            OutputStream fos;
            String dest;
            int count = 1; //files number

            while (true) {
                dest = source + createExt(count);
                fos = new FileOutputStream(dest);
                boolean hasMoreData = copyFile(fis, fos, pSize);
                if (!hasMoreData) break;
                fos.close(); // Close FileOutStream
                count++;
            }

            fis.close(); //Close FileInputStream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Joining method (reversed splitting method)
    public static void join(String source) {
        File file = new File(source);
        if (!file.exists()) {
            System.out.println("File already exists");
            return;
        }

        try {
            OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
            InputStream fis;
            String partFileName;
            int count = 1;

            while (true) {
                partFileName = source + createExt(count);
                File partFile = new File(partFileName);
                if (!partFile.exists()) break; //Check part file existence
                fis = new BufferedInputStream(new FileInputStream(partFile));
                copyFile(fis, fos, (int) partFile.length());
                fis.close();
                count++;
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Copy file when it has more data
    private static boolean copyFile(InputStream fis, OutputStream fos, long pSize) throws IOException {
        byte[] buffer = new byte[102400]; //Create a buffer contains 102400 bytes
        long remain = pSize;
        int byteMustRead, byteRead;

        while (remain > 0) {
            byteMustRead = (int) (remain > buffer.length ? buffer.length : remain);
            byteRead = fis.read(buffer, 0, byteMustRead);
            if (byteRead == -1) return false; //Read file done
            fos.write(buffer, 0, byteRead);
            remain -= byteRead;
        }

        return true;
    }

    // Create extension for destination file
    private static String createExt(int count) {
        if (count < 10) return ".00" + count; //.001 -> .009
        if (count < 100) return ".0" + count; //.010 -> .099;
        return "." + count; // over .099
    }

    public static void main(String[] args) {
        String source = "D:\\Network Programming\\Download.mp4";
        int pSize = 79 * 1024;

//        split(source, pSize);
        join(source);
    }
}

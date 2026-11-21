package IO;

import java.io.*;

public class PackFile {
    public static void pack(String folder, String packedFile) throws IOException {
        File sourceFolder = new File(folder);
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) return; //Check folder

        //List files inside the folder
        File[] allFiles = sourceFolder.listFiles();
        if (allFiles == null) return;

        File pFile = new File(packedFile);
        File packedCanonical = pFile.getCanonicalFile();

        //Filter valid files
        int fileNum = 0;
        for (File file : allFiles) {
            if (file.isFile() && !file.getCanonicalFile().equals(packedCanonical)) {
                fileNum++;
            }
        }
        File[] files = new File[fileNum];
        int k = 0;
        for (File file : allFiles) {
            if (file.isFile() && !file.getCanonicalFile().equals(packedCanonical)) {
                files[k++] = file;
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(packedCanonical, "rw")) {
            raf.setLength(0);

            /**
             * HEADER
             */

            raf.writeInt(fileNum);
            //FileInfo
            long[] position = new long[fileNum];
            for (int i = 0; i < fileNum; i++) {
                position[i] = raf.getFilePointer();   //Get FileInfo position
                raf.writeLong(0);                  //FileInfo position
                raf.writeLong(files[i].length());     //FileInfo size
                raf.writeUTF(files[i].getName());     //FileInfo name
            }

            /**
             * BODY
             */
            byte[] buffer = new byte[102400];
            int byteRead;
            long[] dataPosition = new long[fileNum];

            for (int i = 0; i < fileNum; i++) {
                dataPosition[i] = raf.getFilePointer();
                try (InputStream fis = new BufferedInputStream(new FileInputStream(files[i]))) {
                    while ((byteRead = fis.read(buffer)) != -1) {
                        raf.write(buffer, 0, byteRead);
                    }
                }
            }

            //Return to header, write the actual position
            for (int i = 0; i < fileNum; i++) {
                raf.seek(position[i]);
                raf.writeLong(dataPosition[i]);
            }
        }
    }

    public static void unpack(String packedFile, String extractFile, String destFile) throws IOException {
        File pFile = new File(packedFile);
        if (!pFile.exists() || !pFile.isFile()) return;

        try (RandomAccessFile raf = new RandomAccessFile(packedFile, "r")){
            /**
             * HEADER
             */
            int fileNum = raf.readInt();

            for (int i = 0; i < fileNum; i++) {
                long filePosition = raf.readLong();    // Position
                long fileSize = raf.readLong();              // Size
                String fileName = raf.readUTF();             // Name

                if (fileName.equalsIgnoreCase(extractFile)) { // 'fileName' is 'extractName' -> execute extraction
                    /**
                     * BODY
                     */
                    raf.seek(filePosition);
                    File dest = new File(destFile);
                    try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(dest))) {
                        copyFiles(raf, fos, fileSize);
                    }
                    return;
                }
            }
        }
    }

    private static boolean copyFiles(RandomAccessFile raf, OutputStream fos, long pSize) throws IOException {
        byte[] buffer = new byte[102400];
        long remain = pSize;
        int byteMustRead, byteRead;
        while (remain > 0) {
            byteMustRead = (int) (remain > buffer.length ? buffer.length : remain);
            byteRead = raf.read(buffer, 0, byteMustRead);
            if (byteRead == -1) return false; // All bytes are read
            fos.write(buffer, 0, byteRead);
            remain -= byteRead;
        }
        return true;
    }


    public static void main(String[] args) throws IOException {
        String folder = "D:\\Documents\\OS";
        String packedFile = "D:\\Documents\\packed.zip";
        String extractFile = "abc.txt";
        String destFile = "D:\\Documents\\abc_dest.txt";

        /*pack(folder, packedFile);*/
        unpack(packedFile, extractFile, destFile);
    }
}

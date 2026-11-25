package Test_1.Task1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Pack_Unpack {
    private static final int FILE_NAME_SIZE = 3000;

    public static void pack(String folder, String packedFile, String extension) {
        File dir = new File(folder);
        if (!dir.exists()) { //Check directory existence
            System.out.println("Directory does not exist");
            return;
        }

        String ext = extension.startsWith(".") ? extension : "." + extension;
        ext = ext.toLowerCase(); //.pdf, .docx, .txt,...
        final String finalExt = ext;

        File[] detectedFiles = dir.listFiles(file -> file.getName().endsWith(finalExt)); //Detect file with a given extension

        List<File> files = new ArrayList<>(); //List of detected files
        if (detectedFiles != null) {
            for (File file : detectedFiles) {
                if (!file.getAbsolutePath().equalsIgnoreCase(new File(packedFile).getAbsolutePath())) {
                    files.add(file);
                }
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(packedFile, "rw")) {
            raf.setLength(0);

            //HEADER
            raf.writeInt(files.size()); //TotalFileNumber
            raf.writeInt(FILE_NAME_SIZE); //FileNameSize
            for (File file : files) {
                raf.writeLong(file.length());
                writeFixedFile(raf, file.getName());
            }

            //DATA
            byte[] buffer = new byte[102400];
            for (File file : files) {
                try (RandomAccessFile fileIn = new RandomAccessFile(file, "r")) {
                    int n;
                    while ((n = fileIn.read(buffer)) != -1) {
                        raf.write(buffer, 0, n);
                    }
                }
            }
            System.out.println("Packed file: " + packedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean unPack(String packedFile, String extractFile, String destFile) {
        File file = new File(packedFile);
        if (!file.exists()) return false;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            //Read header
            int total = raf.readInt();
            int nameSize = raf.readInt();
            int nameChar = nameSize / 2;

            long[] sizes = new long[total];
            String[] names = new String[total];
            for (int i = 0; i < total; i++) {
                sizes[i] = raf.readLong();
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < nameChar; c++) {
                    sb.append(raf.readChar());
                }
                names[i] = sb.toString().trim();
            }


            //Read data
            long offset = raf.getFilePointer(); //File pointer
            for (int i = 0; i < total; i++) {
                if (names[i].equalsIgnoreCase(extractFile)) {
                    raf.seek(offset);
                    try (RandomAccessFile fileOut = new RandomAccessFile(file, "rw")) { //To extract file - need read & write permission
                        fileOut.setLength(0);
                        byte[] buffer = new byte[102400];
                        long remain = sizes[i];
                        while (remain > 0) {
                            int n = fileOut.read(buffer, 0, (int) Math.min(buffer.length, remain));
                            if (n == -1) return false;
                            fileOut.write(buffer, 0, n);
                            remain -= n;
                        }
                    }
                    return true;
                }
                offset += sizes[i];
            }
            return false; //File cannot be found
        } catch (IOException e) {
            return false;
        }
    }

    private static void writeFixedFile(RandomAccessFile raf, String name) throws IOException {
        int maxChar = FILE_NAME_SIZE / 2;
        for (int i = 0; i < maxChar; i++) {
            raf.writeChar(i < name.length() ? name.charAt(i) : ' ');
        }
    }



    static void main(String[] args) {
        String folder = "D:\\Documents\\SlideLapTrinhFrontEnd";
        String packedFile = "D:\\Documents\\packed.bin";
        String extension = ".pdf";
        String extractFile = "D:\\Documents\\SlideLapTrinhFrontEnd\\L07 - ReactJS_ Introduction.pdf";
        String destFile = "D:\\Documents\\SlideLapTrinhFrontEnd\\L07 - ReactJS_ Introduction_test.pdf";

//        pack(folder, packedFile, extension);
        System.out.println(unPack(packedFile, extractFile, destFile));
    }
}

package FileClass;

import java.io.File;

public class BasicFileClass {
    public static boolean delete(String path) { //use recursion to delete files
        /*File file = new File(path);

        if (!file.exists()) return false; //File doesn't exist in folder -> Cannot delete
        if (file.isDirectory()) { //Current file is directory
            File[] childFiles = file.listFiles(); //Empty child files list
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    delete(childFile.getAbsolutePath()); //True -> Get the absolute path to delete
                }
            }
        }

        return file.delete(); //False -> Cannot delete files*/

        /**
         * Delete files inside the path without changing folder structure
         */

        File file = new File(path);
        if (!file.exists()) return false; //Check file existence

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null) return false; //Child file list is null -> False

            boolean allDeleted = true; //Initialize 'allDeleted' variable as a flag
            for (File child : childFiles) {
                boolean ok = delete(child.getAbsolutePath());
                allDeleted = allDeleted && ok; //update flag value (use AND)
            }
        }

        return file.delete();
    }

    public static void findAllByExts(String path, String... exts) {
        if (path == null) return; //path is null -> return

        File file = new File(path);
        if (!file.exists()) return; //file doesn't exist -> return

        //Get file by a given ext
        if (file.isFile()) {
            for (String ext : exts) {
                if (file.getName().endsWith(ext)) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        }

        //Get files in current folder by a given ext
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File child : childFiles) {
                    findAllByExts(child.getAbsolutePath(), exts);
                }
            }
        }
    }

    public static void main(String[] args) {
        String path = "D:\\Documents\\OS";
        System.out.println();

//        boolean result = delete(path);
//        System.out.println(result);

        String ext = ".txt";
        findAllByExts(path, ext);
    }
}

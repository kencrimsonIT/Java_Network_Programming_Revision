package FileClass;

import java.io.*;
import java.util.StringTokenizer;

public class BasicCMD {
    private File defaultDir;
    private BufferedReader userIn;
    private PrintStream userOut;

    public BasicCMD(String defaultDir) {
        this.defaultDir = new File(defaultDir);
        this.userIn = new BufferedReader(new InputStreamReader(System.in));
        this.userOut = System.out;
    }

    public void run() throws IOException {
        String line, syntax, param, response; //Shell command's components
        StringTokenizer st; //Command's token

        while (true) { //Run CLI shell until interrupted by user
            line = "";
            syntax = "";
            param = "";
            response = "";
            getDefaultDir();

            line = this.userIn.readLine();
            if (line.isBlank()) continue; //Blank command line -> Continue running CLI shell

            //Initialize StringTokenizer to check command syntax
            st = new StringTokenizer(line);
            syntax = st.nextToken();
            if (st.hasMoreTokens()) param = line.substring(syntax.length()).trim();

            switch (syntax.toUpperCase()) { //command cases
                case "EXIT":
                    return;
                case "CD":
                    response = changeDirectory(param);
                    break;
                case "DIR":
                    response = listDirectory();
                    break;
                case "DEL":
                    response = del(param);
                default:
                    response = "\'" +  syntax + "\'" + " is not a valid command";
                    break;
            }
        }
    }

    private String changeDirectory(String param) throws IOException {
        if (param.isBlank()) return this.defaultDir.getAbsolutePath() + "\n";

        File cdFile = new File(param);
        if (!cdFile.isAbsolute()) cdFile = new File(this.defaultDir, param);

        cdFile = cdFile.getCanonicalFile();
        if (cdFile.exists() && cdFile.isDirectory()) {
            this.defaultDir = cdFile;
            return "";
        }

        return "Cannot change directory";
    }

    private String listDirectory() {
        File[] files = this.defaultDir.listFiles();
        StringBuilder fileNames = new StringBuilder();
        StringBuilder folderNames = new StringBuilder();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) folderNames.append(file.getName().toUpperCase()).append("\n");
                if (file.isFile()) fileNames.append(file.getName().toLowerCase()).append("\n");
            }
        }

        return fileNames.toString();
    }

    private String del(String param) {
        File delFile = new File(this.defaultDir, param);
        if (!delFile.exists()) return "Could not find " + this.defaultDir.getAbsolutePath() + "\\" + param;
        boolean result = deleteFunction(delFile);
        return result ? "Delete successfully" : "Could not delete " + param;
    }

    private boolean deleteFunction(File delFile) {
        File[] files = delFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!deleteFunction(file)) return false;
            }
        }
        return delFile.delete();
    }

    private void getDefaultDir() {
        this.userOut.print(this.defaultDir.getAbsolutePath() + ">");
    }

    public static void main(String[] args) throws IOException {
        String defaultDir = System.getProperty("user.dir");
        BasicCMD cli = new BasicCMD(defaultDir);
        cli.run();
    }
}

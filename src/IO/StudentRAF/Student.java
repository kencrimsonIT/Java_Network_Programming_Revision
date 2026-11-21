package IO.StudentRAF;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

public class Student {
    private int sid;
    private String name;
    private int bYear;
    private double grade;

    public Student() {
    }

    public Student(int sid, String name, int bYear, double grade) {
        this.sid = sid;
        this.name = name;
        this.bYear = bYear;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return sid + " | " + name + " | " + bYear + " | " + grade;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getbYear() {
        return bYear;
    }

    public void setbYear(int bYear) {
        this.bYear = bYear;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public void writeRAFStudent(RandomAccessFile raf, int nameLength) throws IOException {
        raf.writeInt(sid);                     //ID
        writeName(raf, nameLength);            //Name
        raf.writeInt(bYear);                   //Birth year
        raf.writeDouble(grade);                //Grade
    }

    private void writeName(RandomAccessFile raf, int nameLength) throws IOException {
        char c;
        for (int i = 0; i < nameLength; i++) {
            c = i < name.length() ? name.charAt(i) : 0;
            raf.writeChar(c);
        }
    }

    public void readRAFStudent(RandomAccessFile raf, int nameLength) throws IOException {
        this.sid = raf.readInt();
        this.name = readName(raf, nameLength);
        this.bYear = raf.readInt();
        this.grade = raf.readDouble();
    }

    private String readName(RandomAccessFile raf, int nameLength) throws IOException {
        String name = "";
        char c;
        for (int i = 0; i < nameLength; i++) {
            c = raf.readChar();
            if (c != 0) name += c;
        }

        return name;
    }

    public void fromLine(String line, String del) {
        StringTokenizer st = new StringTokenizer(line, del);
        if (st.countTokens() != 3) return;
        this.sid = Integer.parseInt(st.nextToken());
        this.name = st.nextToken();
        this.bYear = Integer.parseInt(st.nextToken());
    }
}

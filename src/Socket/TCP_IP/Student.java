package Socket.TCP_IP;

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

    @Override
    public String toString() {
        return sid + " | " + name + " | " + bYear + " | " + grade;
    }
}

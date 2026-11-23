package IO.StudentRAF;

import java.io.*;
import java.util.*;

public class StudentData {
    private static final int NAME_LENGTH = 50;

    public static void addStudent(String path, Student student) throws IOException {
        if (student == null) return; // Check null

        try (RandomAccessFile raf = new RandomAccessFile(path, "rw")) {
            int studentNumber = 0;
            if (raf.length() > 0) studentNumber = raf.readInt();
            int recordSize = 4 + NAME_LENGTH * 2 + 4 + 8;

            raf.seek(0);
            raf.writeInt(studentNumber + 1); //Add student into file
            raf.writeInt(recordSize);

            //Write student data
            raf.seek(8 + (long) recordSize * studentNumber);
            student.writeRAFStudent(raf, NAME_LENGTH);
        }
    }

    public Student getStudent(String path, int index) throws IOException {
        if (index < 0) return null; //Index must be start at 0

        File file = new File(path);
        if (!file.exists()) return null; //Check file existence

        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            //Read header
            int studentNumber = raf.readInt();
            int recordSize = 4 + NAME_LENGTH * 2 + 4 + 8;

            //Read records (data)
            int recordPosition = 8 + recordSize * index;
            if (index < studentNumber) {
                raf.seek(recordPosition);
                Student student = new Student();
                student.readRAFStudent(raf, NAME_LENGTH);
                return student;
            }
        }

        return null;
    }

    public static void updateStudent(String path, int index, Student newStudent) throws IOException {
        if (index < 0) return;

        File file = new File(path);
        if (!file.exists()) return;

        try (RandomAccessFile raf = new RandomAccessFile(path, "rw")) {
            //Read header
            int studentNumber = raf.readInt();
            int recordSize = 4 + NAME_LENGTH * 2 + 4 + 8;

            //Read records (data)
            int recordPosition = 8 + recordSize * index;
            if (index < studentNumber) {
                raf.seek(recordPosition);
                newStudent.writeRAFStudent(raf, NAME_LENGTH);
            }
        }
    }

    public static Student findById(String path, int id) throws IOException {
        File file = new File(path);
        if (!file.exists()) return null;

        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            //Read header
            int studentNumber = raf.readInt();
            int recordSize = 4 + NAME_LENGTH * 2 + 4 + 8;

            //Find student ID in records
            for (int i = 0; i < studentNumber; i++) {
                int recordPosition = 8 + recordSize * i;
                raf.seek(recordPosition);
                int stuId = raf.readInt();
                if (stuId == id) { // stuId = id -> return Student
                    Student student = new Student();
                    student.readRAFStudent(raf, NAME_LENGTH);
                    return student;
                }
            }
        }

        return null;
    }

    public List<Student> loadStudents(String stFile, String gradeFile) throws IOException {
        List<Student> students = new ArrayList<Student>();
        String del = "\t";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stFile)))) {
            br.read();
            String line;
            Student student;
            while ((line = br.readLine()) != null) {
                student = new Student();
                student.fromLine(line, del);
                students.add(student);
            }
        }

        Map<Integer, Double> map = new HashMap<Integer, Double>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(gradeFile)))) {
            br.read();
            String line;
            StringTokenizer st;
            int id;
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line, del);
                id = Integer.parseInt(st.nextToken());
                double total = 0;
                int count = 0;
                while (st.hasMoreTokens()) {
                    total += Double.parseDouble(st.nextToken());
                    count++;
                }
                double avg = total / count;
                map.put(id, avg);
            }
        }

        for (Student student : students) {
            double average = map.get(student.getSid());
            student.setGrade(average);
        }

        return students;
    }

    public static void main(String[] args) throws IOException {
        String path = "D:\\Network Programming\\Revision\\src\\IO\\StudentRAF\\student.txt";
        String gradePath = "";

        //Student records
        Student st1 = new Student(101, "Nguyen Van A", 2001, 7.1);
        Student st2 = new Student(102, "Dinh Duc C", 2002, 7.4);
        Student st3 = new Student(103, "Tran Thi B", 2003, 8.7);
        Student st4 = new Student(104, "Trinh Huu D", 2004, 8.1);
        Student st5 = new Student(105, "Banh Thi E", 2005, 7.9);

        StudentData data = new StudentData();

        //Add students info file
        addStudent(path, st1);
        addStudent(path, st2);
        addStudent(path, st3);
        addStudent(path, st4);
        addStudent(path, st5);

        //Find 'Tran Thi B'
        Student student = data.getStudent(path, 2);
        System.out.println(student);

        //Update student 'Trinh Huu D'
        updateStudent(path, 3, new Student(106, "Huynh Hello", 2003, 8.4));

        //Find student with id 101
        Student student2 = data.findById(path, 101);
        System.out.println(student2);

        //Load file data
        List<Student> students = data.loadStudents(path, gradePath);
        for (Student stu : students) {
            System.out.println(stu);
        }
    }
}

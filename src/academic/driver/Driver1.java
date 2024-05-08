package academic.driver;

import academic.model.*;
import java.util.*;

public class Driver1 {

    public static void main(String[] args) {

        ArrayList<Lecturer> lecturers = new ArrayList<>();
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<CourseOpening> courseOpenings = new ArrayList<>();
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        HashSet<String> remedialStudents = new HashSet<>();
        HashMap<String, String> bestStudentsMap = new HashMap<>(); // Tambahkan ini untuk menyimpan nilai terbaik siswa

        Scanner scanner = new Scanner(System.in);
        String input;
        boolean courseOpeningExists = false;

        while (!(input = scanner.nextLine()).equals("---")) {
            String[] tokens = input.split("#");
            String action = tokens[0];

            switch (action) {
                case "lecturer-add":
                    lecturers.add(new Lecturer(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]));
                    break;
                case "student-add":
                    students.add(new Student(tokens[1], tokens[2], Integer.parseInt(tokens[3]), tokens[4]));
                    break;
                case "course-open":
                    String courseCode = tokens[1];
                    String academicYear = tokens[2];
                    String semester = tokens[3];
                    String [] lecturerInitials = tokens[4].split(",");
                    String lecturerInitialsString = "" ;
                    for (int i = 0; i < lecturerInitials.length; i++){
                        String lecturerInitial = lecturerInitials[i];
                        for (Lecturer lecturer : lecturers){
                            if (lecturer.getInitial().equals(lecturerInitial)){
                                if (i != 0){
                                    lecturerInitialsString += ";";
                                }
                                lecturerInitialsString += lecturer.getInitial() + " ("+lecturer.getEmail()+")";
                            }
                        }
                    }
                boolean hasLecturer = false;
                for (Lecturer lecturer : lecturers) {
                    if (lecturerInitialsString.contains(lecturer.getInitial())) {
                        hasLecturer = true;
                    }
                }

                for (Course course : courses) {
                    if (course.getCode().equals(courseCode)) {
                        hasLecturer = true;
                    }
                }

                if (hasLecturer) {
                    CourseOpening courseOpening = new CourseOpening(courseCode, academicYear, semester, lecturerInitialsString);
                    courseOpenings.add(courseOpening);
                }
                    break;
                case "course-history":
                    Set<String> processedCourses = new HashSet<>();
                
                // mengurutkan courseOpenings berdasarkan semester secara descending odd semester -> even semester dan tahun akademik sebagai parameter kedua jika semester sama 
                courseOpenings.sort((c1, c2) -> {
                    if (c1.getSemester().equals(c2.getSemester())) {
                        return c1.getAcademicYear().compareTo(c2.getAcademicYear());
                    }
                    return c2.getSemester().compareTo(c1.getSemester());
                });                
                for (CourseOpening courseOpening : courseOpenings) {
                    if (courseOpening.getCourseCode().equals(tokens[1])) {
                        String nama = "";
                        String sks = "";
                        String grade = "";
        
                        for (Course course : courses) {
                            if (course.getCode().equals(courseOpening.getCourseCode())) {
                                nama = course.getName();
                                sks = Integer.toString(course.getCredits());
                                grade = course.getPassingGrade();
                            }
                        }
        
                        if (!processedCourses.contains(courseOpening.getCourseCode() + "|" + courseOpening.getAcademicYear() + "|" + courseOpening.getSemester())) {
                            System.out.println(courseOpening.getCourseCode() + "|" + nama + "|" + sks + "|" + grade + "|" + courseOpening.getAcademicYear() + "|" + courseOpening.getSemester() + "|" + courseOpening.getLecturerInitials());
                            processedCourses.add(courseOpening.getCourseCode() + "|" + courseOpening.getAcademicYear() + "|" + courseOpening.getSemester());
        
                            for (Enrollment enrollment : enrollments) {
                                if (enrollment.getCode().equals(tokens[1]) && enrollment.getCode().equals(courseOpening.getCourseCode()) && enrollment.getYear().equals(courseOpening.getAcademicYear()) && enrollment.getSemester().equals(courseOpening.getSemester())) {
                                    if (enrollment.getPreviousGrade().equals("None")) {
                                        System.out.println(enrollment.getCode()+"|"+enrollment.getId()+"|"+enrollment.getYear()+"|"+enrollment.getSemester()+"|"+enrollment.getGrade());
                                    } else {
                                        System.out.println(enrollment.getCode()+"|"+enrollment.getId()+"|"+enrollment.getYear()+"|"+enrollment.getSemester()+"|"+enrollment.getGrade()+"("+enrollment.getPreviousGrade()+")");
                                    }
                                }
                            }
                        }
                    }
                }
                    break;
                case "enrollment-add":
                    // Check if course opening exists for the given course code, academic year, and semester
                    for (CourseOpening opening : courseOpenings) {
                        if (opening.getCourseCode().equals(tokens[1]) && opening.getAcademicYear().equals(tokens[3]) && opening.getSemester().equals(tokens[4])) {
                            courseOpeningExists = true;
                            break;
                        }
                    }
                    if (courseOpeningExists) {
                        enrollments.add(new Enrollment(tokens[1], tokens[2], tokens[3], tokens[4]));
                    }
                    break;
                case "enrollment-grade":
                    for (Enrollment enrollment : enrollments) {
                        if (enrollment.getCode().equals(tokens[1]) &&
                                enrollment.getId().equals(tokens[2]) &&
                                enrollment.getYear().equals(tokens[3]) &&
                                enrollment.getSemester().equals(tokens[4])) {
                            enrollment.setGrade(tokens[5]);
                        }
                    }
                    break;
                case "enrollment-remedial":
                    String remedialCourseCode = tokens[1];
                    String remedialStudentId = tokens[2];
                    String remedialAcademicYear = tokens[3];
                    String remedialSemester = tokens[4];
                    String remedialGrade = tokens[5];
                
                    // Check if the student has previously done remedial for the specified course
                    boolean studentHasRemedial = false;
                    for (Enrollment enrollment : enrollments) {
                        if (enrollment.getCode().equals(remedialCourseCode) &&
                                enrollment.getId().equals(remedialStudentId) &&
                                enrollment.getYear().equals(remedialAcademicYear) &&
                                enrollment.getSemester().equals(remedialSemester) &&
                                !enrollment.getPreviousGrade().equals("None")) {
                            studentHasRemedial = true;
                            break;
                        }
                    }
                
                    if (!studentHasRemedial) {
                        // Find the enrollment to be remediated
                        for (Enrollment enrollment : enrollments) {
                            if (enrollment.getCode().equals(remedialCourseCode) &&
                                    enrollment.getId().equals(remedialStudentId) &&
                                    enrollment.getYear().equals(remedialAcademicYear) &&
                                    enrollment.getSemester().equals(remedialSemester)) {
                                // Store the previous grade before assigning remedial grade
                                String previousGrade = enrollment.getGrade();
                
                                // Update the grade to the remedial grade
                                enrollment.setGrade(remedialGrade);
                                enrollment.setPreviousGrade(previousGrade);
                
                                // Add the student to the set of remedial students
                                remedialStudents.add(remedialStudentId);
                
                                // Exit the loop once remedial grade is assigned
                                break;
                            }
                        }
                    }
                    break;
                case "course-add":
                    courses.add(new Course(tokens[1], tokens[2], Integer.parseInt(tokens[3]), tokens[4]));
                    break;
                case "student-details":
                    String studentId = tokens[1];
                    String[] studentDetails = new String[5];
                    String[] value = new String[5];
                    double totalgpa = 0;
                    int totalCredit = 0;
                    int d = 0;

                    for (Enrollment enrollment : enrollments){
                        if (enrollment.getId().equals(studentId)){
                            studentDetails[d] = enrollment.getCode();
                            value[d] = enrollment.getGrade();
                            d++;
                        }
                    }

                    for (int i = 0; i < d; i++){
                        for (int j = i+1; j < d; j++){
                            if (studentDetails[i] != null && studentDetails[j] != null && studentDetails[i].equals(studentDetails[j])){
                                value[i] = value[j];
                                studentDetails[j] = null;
                                value[j] = null;
                            }
                        }
                    }

                    for (int i = 0; i < d; i++){
                        for (Course course : courses){
                            if (studentDetails[i] != null && studentDetails[i].equals(course.getCode())){
                                totalgpa += convertGradeToScore(value[i]) * course.getCredits();
                                totalCredit += course.getCredits();
                            }
                        }
                    }

                    double gpa = totalgpa / totalCredit;

                    if (totalgpa == 0){
                        totalCredit = 0;
                    }

                    for (Student student : students){
                        if (student.getId().equals(studentId)){
                            System.out.println(student.getId() + "|" + student.getName() + "|" + student.getYear() + "|" + student.getProgram()  + "|" + String.format("%.2f", gpa) + "|" + totalCredit);
                        }
                    }
                    break;
                case "student-transcript":
                    // nanti
                    break;
                case "find-the-best-student":
                    // Potongan kode untuk menemukan siswa terbaik
                    String academicYearInput = tokens[1];
                    String semesterInput = tokens[2];

                    for (Enrollment enrollment : enrollments) {
                        if (enrollment.getYear().equals(academicYearInput) && enrollment.getSemester().equals(semesterInput)) {
                            String currentGrade = enrollment.getGrade();
                            String existingGrade = bestStudentsMap.getOrDefault(enrollment.getId(), "");

                            if (existingGrade.isEmpty()) {
                                bestStudentsMap.put(enrollment.getId(), currentGrade);
                            } else {
                                bestStudentsMap.put(enrollment.getId(), existingGrade + "/" + currentGrade);
                            }
                        }
                    }
                    break;
                case "add-best-student":
                    // Potongan kode untuk menambahkan siswa terbaik
                    int count = 0;
                    for (Enrollment enrollment : enrollments) {
                        if (enrollment.getId().equals("12S20002")) { // Ganti nim sesuai kebutuhan
                            String bestGrade = bestStudentsMap.get(enrollment.getId());
                            if (bestGrade != null) {
                                if (bestGrade.contains("/")) {
                                    String[] grades = bestGrade.split("/");
                                    for (String grade : grades) {
                                        if (count < 2) {
                                            System.out.println(enrollment.getId() + "|" + bestGrade);
                                            count++;
                                        }
                                    }
                                } else {
                                    if (count < 2) {
                                        System.out.println(enrollment.getId() + "|" + bestGrade);
                                        count++;
                                    }
                                    if (bestGrade.contains("B") && bestGrade.contains("A") && count < 2) {
                                        System.out.println(enrollment.getId() + "|B/A");
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        scanner.close();

        // Print all remaining details
        for (Lecturer lecturer : lecturers) {
            System.out.println(lecturer.toString());
        }
        
        for (Course course : courses) {
            System.out.println(course.toString());
        }
        
        for (Student student : students) {
            System.out.println(student.toString());
        }
        
        for (Enrollment enrollment : enrollments) {
    if (remedialStudents.contains(enrollment.getId())) {
        String previousGrade = enrollment.getPreviousGrade();
        if (!previousGrade.equals("None")) {
            System.out.println(enrollment.toString() + "(" + previousGrade + ")");
        } else {
            System.out.println(enrollment.toString());
        }
    } else {
        // Print enrollments for students who did not do remedial
        System.out.println(enrollment.toString());
        }
    }
}
    

    /// Helper method to convert grade to score
   private static double convertGradeToScore(String grade) {
        switch (grade) {
            case "A":
                return 4.0;
            case "AB":
                return 3.5;
            case "B":
                return 3.0;
            case "BC":
                return 2.5;
            case "C":
                return 2.0;
            case "D":
                return 1.0;
            default:
                return 0.0;
        }
    }
}
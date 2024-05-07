package academic.model;

import java.util.Objects;

public class Course {
    private final String code;
    private final String name;
    private final int credits;
    private final String passingGrade;

    public Course(String code, String name, int credits, String passingGrade) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty");
        }
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.passingGrade = passingGrade;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public String getPassingGrade() {
        return passingGrade;
    }

    @Override
    public String toString() {
        return String.format("%s|%s|%d|%s", code, name, credits, passingGrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(code, course.code);
    }
}
// Name: Braden Meuth et al.
// Date: 2/7/2025
// Description: Display a custom image as a "map" with zoom and pan functionality.
import java.awt.Color;

/**
 * Classroom --- Represents a classroom with details like room number, subject, teacher, and color.
 * @author Braden Meuth
 */
public class Classroom {
    private int roomNumber; // Classroom number
    private String subject; // Subject taught in the classroom
    private String teacher; // Teacher of the classroom
    private String description; // Additional description
    private int red, green, blue; // RGB color values

    public Classroom(int roomNumber, String subject, String teacher, String description, int red, int green, int blue) {
        this.roomNumber = roomNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.description = description;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Gets the room number.
     * @return Room number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Gets the subject taught in the classroom.
     * @return Subject name
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the teacher's name.
     * @return Teacher name
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * Gets the description of the classroom.
     * @return Classroom description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the Color of the classroom.
     * @return Classroom Color
     */
    public Color getColor() {
        return new Color(red, green, blue);
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "roomNumber='" + roomNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", teacher='" + teacher + '\'' +
                ", description='" + description + '\'' +
                ", RGB=(" + red + ", " + green + ", " + blue + ")" +
                '}';
    }
}
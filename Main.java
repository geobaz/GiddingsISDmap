// Name: Braden Meuth et al.
// Date: 2/7/2025
// Description: Display a custom image as a "map" with zoom and pan functionality.
import processing.core.PImage;
import processing.core.PApplet;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

/**
 * Main --- Program to display a custom image as a map with zoom and pan functionality.
 * @author Braden Meuth
 */
public class Main extends PApplet {

    private PImage img; // Map image
    private PImage ColoredImg; // Colored map image
    private float offsetX, offsetY; // Panning offsets
    private float scaleFactor = 0.4f; // Initial zoom level
    private float minZoom = 0.4f, maxZoom = 4.0f; // Zoom limits
    private int lastMouseX, lastMouseY; // Coordinates for dragging
    private int floor = 1; // Current floor level
    private List<Classroom> classrooms; // List of classrooms loaded from Excel
    private boolean showPopup = false; // Flag to show popup
    private Classroom selectedClassroom = null; // Currently selected classroom
    private float popupX, popupY; // Popup position
    private int popupWidth = 0; // Width of popup
    private int popupHeight = 0; // Height of popup
    private int pressedX, pressedY = 0; // Initial mouse press coordinates

    /**
     * Main method to launch the application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        PApplet.main("Main");
    }

    /**
     * Sets up the Processing application window size.
     */
    @Override
    public void settings() {
        size(800, 600); // Set window size
    }

    /**
     * Initializes images and loads classroom data from an Excel file.
     */
    @Override
    public void setup() {
        img = loadImage("data/Newest 1st floor.png"); // Loading image
        ColoredImg = loadImage("data/Newest Colored 1st floor.png"); // Loading image

        offsetX = 0; // Initialize panning offsets
        offsetY = 0;

        try {
            // Load classrooms from the Excel file
            classrooms = loadClassroomsFromExcel("data/Giddings High School Map Data.xlsx");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Draws the map, handles zooming and panning, and displays the popup if necessary.
     */
    @Override
    public void draw() {
        background(255); // Clear the background

        // Apply zoom and pan transformations
        translate(offsetX, offsetY);
        scale(scaleFactor);

        // Draw the image
        image(img, 0, 0);

        // Draw the popup if applicable
        if (showPopup) {
            drawPopup(popupX, popupY);
        }

        // Ensure controls stay fixed on the screen
        drawControls();
    }

    /**
     * Draws a popup with classroom information.
     * @param x X-coordinate for the popup
     * @param y Y-coordinate for the popup
     */
    private void drawPopup(float x, float y) {
        
        // Draw the room information
        int textSize = 12;
        popupWidth = 0;
        fill(0);
        textSize(textSize);
        textAlign(LEFT, TOP);

        ArrayList<String> popupTextArry = new ArrayList<String>();

        // Populate popupTextArry ArrayList with lines of inforation for classroom.
        if (selectedClassroom != null) {
            if (selectedClassroom.getRoomNumber() != 0) {
                popupTextArry.add("Room: " + selectedClassroom.getRoomNumber());
            }
            if (!selectedClassroom.getSubject().isEmpty()) {
                popupTextArry.add("Subject(s):");
                popupTextArry.addAll(Arrays.asList(selectedClassroom.getSubject().split(", ")));
            }
            if (!selectedClassroom.getTeacher().isEmpty()) {
                popupTextArry.add("Teacher(s):");
                popupTextArry.addAll(Arrays.asList(selectedClassroom.getTeacher().split("&")));
            }
            if (!selectedClassroom.getDescription().isEmpty()) {
                popupTextArry.add("Description:");
                popupTextArry.add(selectedClassroom.getDescription());
            }
            
            // Determine box height from lines in the ArrayList
            AffineTransform affinetransform = new AffineTransform();     
            FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
            Font font = new Font("Monaco", Font.PLAIN, 13);
            popupHeight = popupTextArry.size()*(textSize+textSize/8);
            
            // Get the longest line to set the width of the box
            for(int i = 0; i < popupTextArry.size(); i++)
            {
                String str = popupTextArry.get(i);
                if((int)(font.getStringBounds(str, frc).getWidth()) > popupWidth)
                {
                    popupWidth = (int)(font.getStringBounds(str, frc).getWidth());
                }
            }

            // Convert Array Of lines to format for box
            String popupText = "";
            for(int i = 0; i < popupTextArry.size(); i++)
            {
                String str = popupTextArry.get(i);
                popupText+=str;
                if(i<popupTextArry.size()-1)
                    popupText+="\n";
            }

            // Draw box and put text inside
            fill(200,200,200);
            rect(x, y, popupWidth + 5, popupHeight+10);
            fill(0);
            text(popupText, x+5, y+5);
        }

        // Draw the close button (X) 
        fill(255, 0, 0); // Red color for the X button
        rect(x + popupWidth - 10, y, 15, 15); // Draw X button box based on the pop up position
        fill(255);
        textSize(13);
        textAlign(CENTER, CENTER);
        text("X", x + popupWidth - 3, y + 7);
    }


    private void drawControls() {
        // Prevent transformations from affecting the controls
        pushMatrix();
        resetMatrix();

        // Draw + button
        fill(234);
        rect(10, 10, 30, 30);
        fill(0);
        textSize(20);
        textAlign(CENTER, CENTER);
        text("+", 25, 25);

        // Draw - button
        fill(234);
        rect(10, 50, 30, 30);
        fill(0);
        textSize(20);
        textAlign(CENTER, CENTER);
        text("-", 25, 65);

        // Draw up or down arrows for floor switch
        if (floor == 2) {
            fill(234);
            rect(10, 90, 30, 30);
            fill(0);
            textSize(20);
            textAlign(CENTER, CENTER);
            pushMatrix(); // Save the current transformation state
            translate(25, 105); // Move the origin to the arrow's position
            rotate(PI); // Rotate 180 degrees (flip the arrow)
            text("^", 0, 0); // Draw text at the new origin
            popMatrix(); // Restore the previous transformation state
        } else {
            fill(234);
            rect(10, 90, 30, 30);
            fill(0);
            textSize(20);
            textAlign(CENTER, CENTER);
            text("^", 25, 110); // Draw up arrow normally
        }
        // Restore the previous transformation state
        popMatrix();
    }

    // Method to load the classrooms from the Excel file
    private static List<Classroom> loadClassroomsFromExcel(String excelFilePath) throws IOException {
        List<Classroom> classrooms = new ArrayList<>();

        FileInputStream fis = new FileInputStream(new File(excelFilePath));
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            if (row.getCell(0).getNumericCellValue() == 0) break;

            // Check and handle null cells
            int roomNumber = (int) (row.getCell(0) != null ? row.getCell(0).getNumericCellValue() : 0);
            String subject = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
            String teacher = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
            String description = (row.getCell(3) != null) ? row.getCell(3).getStringCellValue() : "";

            int red = (int) (row.getCell(4) != null ? row.getCell(4).getNumericCellValue() : 0);
            int green = (int) (row.getCell(5) != null ? row.getCell(5).getNumericCellValue() : 0);
            int blue = (int) (row.getCell(6) != null ? row.getCell(6).getNumericCellValue() : 0);

            classrooms.add(new Classroom(roomNumber, subject, teacher, description, red, green, blue));
        }

        workbook.close();
        fis.close();

        return classrooms;
    }

    @Override
    public void mousePressed() {
        int mouseWorldX = (int)((mouseX - offsetX) / scaleFactor);
        int mouseWorldY = (int)((mouseY - offsetY) / scaleFactor);

        // Check if the mouse is inside the + button
        if(mouseX >= 10 && mouseX <= 40 && mouseY >= 10 && mouseY <= 40) {
            zoomIn(width / 2, height / 2); // Zoom to the center
        }
        // Check if the mouse is inside the - button
        else if(mouseX >= 10 && mouseX <= 40 && mouseY >= 50 && mouseY <= 80) {
            zoomOut(width / 2, height / 2); // Zoom to the center
        }
        // Check if the mouse is inside the ^ or * button
        else if(mouseX >= 10 && mouseX <= 40 && mouseY >= 90 && mouseY <= 120) {
            if(floor == 1)
            {
                img = loadImage("data/Newest 2nd floor.png");
                ColoredImg = loadImage("data/Newest Colored 2nd floor.png");
                floor = 2;
            }
            else
            {
                img = loadImage("data/Newest 1st floor.png");
                ColoredImg = loadImage("data/Newest Colored 1st floor.png");
                floor = 1;
            }
        }
        else if(mouseWorldX >= popupX + popupWidth - 15 && mouseWorldX <= popupX + popupWidth && mouseWorldY >= popupY && mouseWorldY <= popupY + 15) {
            showPopup = false;
        }
        else{
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    // Check if two colors are close enough based on the threshold
    private boolean isColorClose(Color clickedColor, Color classroomColor) {
        int threshold = 5;
        int rDiff = Math.abs(clickedColor.getRed() - classroomColor.getRed());
        int gDiff = Math.abs(clickedColor.getGreen() - classroomColor.getGreen());
        int bDiff = Math.abs(clickedColor.getBlue() - classroomColor.getBlue());

        return rDiff < threshold && gDiff < threshold && bDiff < threshold;
    }


    
    // Method to track mouse dragging
    @Override
    public void mouseDragged() {
        offsetX += mouseX - lastMouseX;
        offsetY += mouseY - lastMouseY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public void mouseReleased() {
        int mouseWorldX = (int)((mouseX - offsetX) / scaleFactor);
        int mouseWorldY = (int)((mouseY - offsetY) / scaleFactor);

        if(Math.abs(mouseX-pressedX) > 10 || Math.abs(mouseY-pressedY) > 10)
        {
            int rgb = ColoredImg.get(mouseWorldX, mouseWorldY);
            Color clickedColor = new Color(rgb, true);

            // Check if the clicked color is close enough to a classroom's color
            for (Classroom classroom : classrooms) {
                if (isColorClose(clickedColor, classroom.getColor())) {
                    selectedClassroom = classroom;
                    popupX = mouseWorldX + 10; // Position popup near click
                    popupY = mouseWorldY - 110;
                    showPopup = true;
                    return;
                }
            }
        }
    }
    

    @Override
    public void mouseWheel(processing.event.MouseEvent event) {
        // Zoom in or out based on mouse wheel movement
        float e = event.getCount();
        float zoomFactor = (e > 0) ? 0.9f : 1.1f;

        // Gets the mouse position relative to the current zoom and pan
        float mouseWorldX = (mouseX - offsetX) / scaleFactor;
        float mouseWorldY = (mouseY - offsetY) / scaleFactor;

        // Apply zoom
        scaleFactor *= zoomFactor;
        scaleFactor = constrain(scaleFactor, minZoom, maxZoom);

        // Adjust offsets to zoom into the mouse position
        offsetX = mouseX - mouseWorldX * scaleFactor;
        offsetY = mouseY - mouseWorldY * scaleFactor;

    }

    @Override
    public void keyPressed() {
        // Pan controls with arrow keys
        if (keyCode == LEFT) {
            offsetX += 10; // Move right
        } else if (keyCode == RIGHT) {
            offsetX -= 10; // Move left
        } else if (keyCode == UP) {
            offsetY += 10; // Move down
        } else if (keyCode == DOWN) {
            offsetY -= 10; // Move up
        }
    }

    public void zoomIn(float zoomCenterX, float zoomCenterY) {
        float zoomFactor = 1.1f;

        // Get the zoom center position relative to the current zoom and pan
        float worldX = (zoomCenterX - offsetX) / scaleFactor;
        float worldY = (zoomCenterY - offsetY) / scaleFactor;

        // Apply zoom
        scaleFactor *= zoomFactor;
        scaleFactor = constrain(scaleFactor, minZoom, maxZoom);

        // Adjust offsets to zoom into the center
        offsetX = zoomCenterX - worldX * scaleFactor;
        offsetY = zoomCenterY - worldY * scaleFactor;

    }

    public void zoomOut(float zoomCenterX, float zoomCenterY) {
        float zoomFactor = 0.9f;

        // Get the zoom center position relative to the current zoom and pan
        float worldX = (zoomCenterX - offsetX) / scaleFactor;
        float worldY = (zoomCenterY - offsetY) / scaleFactor;

        // Apply zoom
        scaleFactor *= zoomFactor;
        scaleFactor = constrain(scaleFactor, minZoom, maxZoom);

        // Adjust offsets to zoom into the center
        offsetX = zoomCenterX - worldX * scaleFactor;
        offsetY = zoomCenterY - worldY * scaleFactor;

    }
}
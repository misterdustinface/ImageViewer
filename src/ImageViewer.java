
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This program will allow you to imagesInDirectory through images
 * @author Dustin
 */
public class ImageViewer {

    /////////////////////
    //    CONSTANTS    //
    /////////////////////
    
    final public static String PROGRAM_TITLE     = "Viewer - ";
    final public static String DEFAULT_FILE_NAME = "No Image";
    
    final public static int DEFAULT_WIDTH  = 600;
    final public static int DEFAULT_HEIGHT = 400;
    
    
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String JPG = "JPG";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    
    /////////////////////
    //    VARIABLES    //
    /////////////////////
    
    // DISPLAY STUFF /////////////////
    private static JFrame       frame; // THE DISPLAY
    private static JFileChooser jfc;   // For file choosing
    private static JOptionPane  dialog;// For error messages
    //////////////////////////////////
    
    // THE TOP MENU BAR //////////////
    private static MenuBar   menuBar;  // The menu bar itself
    private static Menu      fileMenu; // The "File" section
    private static MenuItem  miOpen;   // open option
    private static MenuItem  miShuffle; // Shuffle images
    //////////////////////////////////
    
    // IMAGE STUFF ////////////////////
    private static ImageReader   in;           // Used for reading images
    private static BufferedImage currentImage; // The image that we're viewing
    private static File          currentFile; // The FILE that we're getting the image from
    
    private static ArrayList<File> imagesInDirectory;
    private static int             currentIndex;
    
    private static FileNameExtensionFilter filter;
    private static FilenameFilter          fnfilter;
    //////////////////////////////////
    
    // Viewer stuff //////////////////
    private static JPanel      viewArea;    // FOR VIEWING
    private static JScrollPane sp;          // Scrolling for notepad
    ///////////////////////////////////
    
    private static JButton next;
    private static JButton previous;
    
    public static void main(String[] args) {

        // Setup the display
        initDisplay();
        
        // Setup what the buttons do
        initActionListeners();
        
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////    
    
    private static void initDisplay()
    {
        // CREATING FRAME WITH ALL OF IT'S GOODIES [DISPLAY]
        frame = new JFrame(PROGRAM_TITLE + DEFAULT_FILE_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setLocation((int)(Math.random() * 800), (int)(Math.random() * 400));
        
        // INITIALIZING OTHER DISPLAY STUFF
        jfc    = new JFileChooser();
        dialog = new JOptionPane();
        
        // SET FILE TYPE FILTERS
        filter = new FileNameExtensionFilter(jpeg, jpg, JPG, gif, png, tiff, tif);  
        fnfilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(jpeg) ||  name.contains(jpg) || name.contains(JPG)
                ||  name.contains(gif)  ||  name.contains(png)
                || name.contains(tiff)  ||  name.contains(tif))
                {
                    return true;
                }
                
                return false;
            }
        };
        // Put the filter on the file chooser
        jfc.setFileFilter(filter);
        
        ////////////////////////////////////////////////////////////////////////
        
        // SETTING THE CURRENT IMAGE TO DEFAULT
        currentImage = null;
        imagesInDirectory        = new ArrayList();
        
        ////////////////////////////////////////////////////////////////////////
        
        // INITIALIZING THE MENU BAR
        menuBar  = new MenuBar();
        
        // INITIALIZING THE FILE SECTION OF THE MENU BAR
        fileMenu = new Menu(); 
        fileMenu.setLabel("File"); // setting the name
        
        // INITIALIZING THE OPTIONS IN THE FILE SECTION
        miOpen    = new MenuItem("Open");
        miShuffle = new MenuItem("Shuffle");
        
        // ADDING THE OPTIONS TO THE FILE SECTION
        fileMenu.add(miOpen);
        fileMenu.add(miShuffle);
        
        // ADDING THE FILE SECTION TO THE MENU BAR
        menuBar.add(fileMenu);
        
        JPanel buttonArea = new JPanel();
        buttonArea.setBackground(Color.LIGHT_GRAY);
        next     = new JButton("Next");
        previous = new JButton("Previous");
        
        buttonArea.add(previous);
        buttonArea.add(next);
        
        // ADD THE MenuBar to the display
        frame.setMenuBar(menuBar);
        
        ////////////////////////////////////////////////////////////////////////
        
        // INITIALIZING THE VIEW AREA (area we type in)
        viewArea = new JPanel();
        viewArea.setBackground(Color.LIGHT_GRAY);
        viewArea.setLayout(new BorderLayout());
        
        // INITIALIZING THE SCROLLING STUFF (having it hold the text area) 
        // - It's a Notepad now!
        sp = new JScrollPane(viewArea);
        
        // ADD THE Notepad to the display
        frame.add(sp, BorderLayout.CENTER);
        frame.add(buttonArea, BorderLayout.NORTH);
        
        // REFRESH
        frame.validate();
        frame.repaint();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////    
        
    private static void initActionListeners()
    {

        // Add a reaction to the OPEN button
        miOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Open up the file selection screen, if "Open" is pressed
                if(jfc.showDialog(frame, "View") == JFileChooser.APPROVE_OPTION)
                {
                    currentFile = jfc.getSelectedFile();
                }
                
                if(currentFile != null)
                {
                    // Attempt to read in data [inFile all at once]
                    try {
                        currentImage = ImageIO.read(currentFile);
                        
                        // Clear the viewArea
                        viewArea.removeAll();
                        // Put the image on the viewArea [Centered]
                        viewArea.add( (new JLabel(new ImageIcon(currentImage))), BorderLayout.CENTER );

                        // Set the top title to the name of the document
                        frame.setTitle(PROGRAM_TITLE + currentFile.getName());

                        frame.setSize(currentImage.getWidth() + 20, currentImage.getHeight() + 80);
                        
                    // If the attempt fails, display the error message - then give up.    
                    } catch (IOException ex) {
                        dialog.showMessageDialog(frame, "PROBLEM READING FILE");
                    }
                    
                    // Refresh
                    frame.validate();
                    frame.repaint();
                    
                    ///////////////////////////////////////////////////////////
                    // SET UP A CYCLE - RETRIEVE ALL IMAGES FROM DIRECTORY
                    imagesInDirectory = new ArrayList();

                    String directory = currentFile.getAbsolutePath();

                    directory = directory.substring(0, directory.lastIndexOf('\\'));

                    imagesInDirectory = new ArrayList<File>();
                    File[] files = new File(directory).listFiles(fnfilter);
                    
                    for (File file : files) {
                        if (file.isFile()) {
                            imagesInDirectory.add(file);
                        }
                    }
                    
                    currentIndex = getIndexOfCurrentImage();
                }
            }
        });
        
        miShuffle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                int  p;
                File temp;
                
                for(int i = 0; i < imagesInDirectory.size(); i++)
                {
                    p = (int)(Math.random() * imagesInDirectory.size());
                    
                    temp = imagesInDirectory.get(i);
                    imagesInDirectory.set(i, imagesInDirectory.get(p));
                    imagesInDirectory.set(p, temp);
                }   

                if(currentFile != null)
                {
                    currentIndex = getIndexOfCurrentImage();
                    getDirectoryImages(currentIndex);
                }
            }
        });
        
        next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // If there are multiple images in the directory
                if(imagesInDirectory.size() > 1)
                {
                    currentIndex++;

                    if(currentIndex >= imagesInDirectory.size())
                    {
                        currentIndex = 0;
                    }

                    getDirectoryImages(currentIndex);
                }
            }
        });
        
        previous.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // If there are multiple images in the directory
                if(imagesInDirectory.size() > 1)
                {
                    currentIndex--;

                    if(currentIndex < 0)
                    {
                        currentIndex = imagesInDirectory.size() - 1;
                    }

                    getDirectoryImages(currentIndex);
                }
            }
        });
        
    }
    
    private static void getDirectoryImages(int index)
    {
        try {
            currentImage = ImageIO.read(imagesInDirectory.get(currentIndex));

            // Clear the viewArea
            viewArea.removeAll();
            // Put the image on the viewArea [Centered]
            viewArea.add( (new JLabel(new ImageIcon(currentImage))), BorderLayout.CENTER );

            // Set the top title to the name of the document
            frame.setTitle(PROGRAM_TITLE + imagesInDirectory.get(currentIndex).getName());

            frame.setSize(currentImage.getWidth() + 20, currentImage.getHeight() + 80);

        // If the attempt fails, display the error message - then give up.    
        } catch (IOException ex) {
            dialog.showMessageDialog(frame, "PROBLEM READING FILE");
        }

        // Refresh
        frame.validate();
        frame.repaint();
    }
    
    private static int getIndexOfCurrentImage()
    {
        return imagesInDirectory.indexOf(currentFile);
    }
}

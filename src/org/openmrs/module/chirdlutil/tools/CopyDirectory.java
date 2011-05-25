package org.openmrs.module.chirdlutil.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class CopyDirectory {
	
	private JTextField copyDirField = new JTextField(30);
    private JTextField targDirField = new JTextField(30);
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Copy Directory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getGlassPane().addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent e) {
        		// Do nothing while the glass pane is visible.
        	}
        });

        //Set up the content pane.
        Container contentPane = frame.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);

        //Create and add the components.
        JLabel label = new JLabel("Directory To Copy: ");
        JLabel label2 = new JLabel("Target Directory: ");
        
        JButton copyFromButton = new JButton("Browse...");
        setCopyBrowseAction(copyFromButton, frame);
        JButton targetButton = new JButton("Browse...");
        setTargetBrowseAction(targetButton, frame);
        JButton goButton = new JButton("Go");
        setGoAction(goButton, frame);
        JButton closeButton = new JButton("Close");
        setCloseButtonAction(closeButton);
        goButton.setPreferredSize(closeButton.getPreferredSize());
        
        JPanel panel = new JPanel(layout);
        label.setLabelFor(copyDirField);
        label2.setLabelFor(targDirField);
        panel.add(label);
        panel.add(copyDirField);
        panel.add(copyFromButton);
        panel.add(label2);
        panel.add(targDirField);
        panel.add(targetButton);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(goButton);
        buttonPanel.add(closeButton);
        panel.add(new JLabel());
        panel.add(buttonPanel);
        panel.add(new JLabel());
        
        //Lay out the panel.
        SpringUtilities.makeCompactGrid(panel,
                                        3, 3, //rows, cols
                                        6, 6, //initX, initY
                                        6, 6);//xPad, yPad
        
        //Set up the content pane.
        panel.setOpaque(true);  //content panes must be opaque
        frame.setContentPane(panel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private void setCopyBrowseAction(JButton copyBrowseButton, final JFrame frame) {
    	copyBrowseButton.addActionListener(new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(copyDirField.getText());
	            fileChooser.setDialogTitle("Choose Directory to Copy");
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int returnVal = fileChooser.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File selectedDir = fileChooser.getSelectedFile();
		            copyDirField.setText(selectedDir.getAbsolutePath());
	            }
            }
    		
    	});
    }
    
    private void setTargetBrowseAction(JButton targetBrowseButton, final JFrame frame) {
    	targetBrowseButton.addActionListener(new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(targDirField.getText());
	            fileChooser.setDialogTitle("Choose Target Directory");
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int returnVal = fileChooser.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File selectedDir = fileChooser.getSelectedFile();
		            targDirField.setText(selectedDir.getAbsolutePath());
	            }
            }
    		
    	});
    }
    
    private void setGoAction(JButton goButton, final JFrame frame) {
    	goButton.addActionListener(new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
				String dirToCopyStr = copyDirField.getText();
				String copyToStr = targDirField.getText();
				File dirToCopy = new File(dirToCopyStr);
				File copyTo = new File(copyToStr);
				
				if (!dirToCopy.exists()) {
					JOptionPane.showMessageDialog(frame, "The directory to copy does not exist.", "Error", 
						JOptionPane.ERROR_MESSAGE);
				} else if (!copyTo.exists()) {
					JOptionPane.showMessageDialog(frame, "The target directory does not exist.", "Error", 
						JOptionPane.ERROR_MESSAGE);
				} else if (!dirToCopy.isDirectory()) {
					JOptionPane.showMessageDialog(frame, "The directory to copy is not a directory.", "Error", 
						JOptionPane.ERROR_MESSAGE);
				} else if (!copyTo.isDirectory()) {
					JOptionPane.showMessageDialog(frame, "The target directory is not a directory.", "Error", 
						JOptionPane.ERROR_MESSAGE);
				} else {
					Thread copyThread = new CopyThread(frame, dirToCopy, copyTo);
					Component glassPane = frame.getGlassPane();
					glassPane.setVisible(true);
					glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					copyThread.start();
				}
            }
    		
    	});
    }
    
    private void setCloseButtonAction(JButton closeButton) {
    	closeButton.addActionListener(new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
	            System.exit(0);
            }
    		
    	});
    }
    
	private void copyDirectory(File dirToCopy, File destDir) {
		String dirName = dirToCopy.getName();
		File newDir = new File(destDir, dirName);
		newDir.mkdirs();
		File[] files = dirToCopy.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				dirName = file.getName();
				createDirectory(file, new File(newDir, dirName));
			}
		}
	}
	
	private void createDirectory(File dirToCopy, File destDir) {
		destDir.mkdirs();
		File[] files = dirToCopy.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				createDirectory(file, new File(destDir, dirName));
			}
		}
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	CopyDirectory copyDir = new CopyDirectory();
                copyDir.createAndShowGUI();
            }
        });
    }
    
    private class CopyThread extends Thread {
    	
    	private JFrame frame;
    	private File dirToCopy;
    	private File destDir;
    	
    	public CopyThread(JFrame frame, File dirToCopy, File destDir) {
    		this.frame = frame;
    		this.dirToCopy = dirToCopy;
    		this.destDir = destDir;
    	}
    	
    	public void run() {
    		copyDirectory(dirToCopy, destDir);
    		javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	Component glassPane = frame.getGlassPane();
        			glassPane.setVisible(false);
            		glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
    	}
    }
}
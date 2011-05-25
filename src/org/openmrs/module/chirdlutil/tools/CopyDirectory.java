package org.openmrs.module.chirdlutil.tools;
import java.io.File;

/**
 * This class is used to copy a directory and it's sub-directories, but not any 
 * files to a target destination.  It takes two parameters: the first is the 
 * directory (and sub-directories) you want to copy.  The second is the target 
 * area where you want it copied to.
 *
 * @author Steve McKee
 */
public class CopyDirectory {
	
	public static void copyDirectory(File dirToCopy, File destDir) {
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
	
	private static void createDirectory(File dirToCopy, File destDir) {
		destDir.mkdirs();
		File[] files = dirToCopy.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				createDirectory(file, new File(destDir, dirName));
			}
		}
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please specify two parameters:");
			System.out.println("1. The directory (and it's sub-directories) to be copied.");
			System.out.println("2. The target destination of the directory.");
			System.out.println("Example: java CopyDirectory F:\\chica\\images C:\\temp");
			System.exit(1);
		}
		
		String dirToCopyStr = args[0];
		String copyToStr = args[1];
		File dirToCopy = new File(dirToCopyStr);
		File copyTo = new File(copyToStr);
		
		if (!dirToCopy.exists()) {
			System.out.println("The following directory does not exist: " + dirToCopyStr);
			System.exit(1);
		} else if (!copyTo.exists()) {
			System.out.println("The following directory does not exist: " + copyToStr);
			System.exit(1);
		} else if (!dirToCopy.isDirectory()) {
			System.out.println("The following is not a directory: " + dirToCopyStr);
			System.exit(1);
		} else if (!copyTo.isDirectory()) {
			System.out.println("The following is not a directory: " + copyToStr);
			System.exit(1);
		}
		
		copyDirectory(dirToCopy, copyTo);
		
		System.exit(0);
	}
	
}

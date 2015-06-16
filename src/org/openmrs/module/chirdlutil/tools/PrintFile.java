/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chirdlutil.tools;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 * Utility used to print a file to a specific printer.
 * 
 * @author Steve McKee
 */
public class PrintFile {
	
	/**
	 * Prints the specified file to the specified printer.  This method does perform any rendering for specialized file formats.  
	 * Please test this method with your file type and printer.
	 * 
	 * @param printerName The name of the printer to send the print job.
	 * @param fileToPrint The file to print.
	 * @throws IOException
	 * @throws PrintException
	 */
    public boolean printFile(String printerName, File fileToPrint) throws IOException, PrintException, PrinterException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		System.out.println("A valid printerName parameter was not provided: " + printerName);
    		return false;
    	} else if (fileToPrint == null || !fileToPrint.exists() || !fileToPrint.canRead()) {
    		System.out.println("A valid fileToPrint parameter was not provided: " + fileToPrint);
    		return false;
    	}
    	
    	if (fileToPrint.getAbsolutePath().toLowerCase().endsWith(ChirdlUtilConstants.FILE_EXTENSION_PDF)) {
    		return printPDFFile(printerName, fileToPrint);
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	System.out.println("No printers found for " + printerName);
        	return false;
        }
        
        PrintService selectedService = printServices[0];
        FileInputStream psStream = null;  
        psStream = new FileInputStream(fileToPrint);  
        
        DocPrintJob printJob = selectedService.createPrintJob();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(psStream, flavor, null);
        printJob.print(document, null);
        return true;
    }
    
    /**
     * Prints a specified PDF file to a specified printer.
     * 
     * @param printerName The name of the printer to use to print the PDF file.
     * @param pdfFile The PDF File to print.
     * @throws IOException
     * @throws PrinterException
     */
    public boolean printPDFFile(String printerName, File pdfFile) throws IOException, PrinterException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		System.out.println("A valid printerName parameter was not provided: " + printerName);
    		return false;
    	} else if (pdfFile == null || !pdfFile.exists() || !pdfFile.canRead()) {
    		System.out.println("A valid fileToPrint parameter was not provided (or unable to read): " + pdfFile);
    		return false;
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	System.out.println("No printers found for " + printerName);
        	return false;
        }
        
        PrintService selectedService = printServices[0];
        PrinterJob printJob = PrinterJob.getPrinterJob();
	    printJob.setPrintService(selectedService);
	    PDDocument document = PDDocument.load(pdfFile);
	    document.silentPrint(printJob);
	    
	    return true;
    }
	
	/**
	 * Prints the usage for this command line tool.
	 */
	private void printUsage() {
		System.out.println("Arguments:");
		System.out.println("Arg 1: The file to print");
		System.out.println("Arg 2: The name of the printer");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("PrintFile C:\\temp\\test.pdf Clinic_Printer");
		System.out.println("This will print the test.pdf file to the Clinic_Printer printer");
	}
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PrintFile printFile = new PrintFile();
		if (args.length != 2) {
			printFile.printUsage();
			System.exit(1);
		}
		
		String fileStr = args[0];
		String printerName = args[1];
		try {
	        boolean completed = printFile.printFile(printerName, new File(fileStr));
	        if (!completed) {
	        	System.exit(1);
	        }
        }
        catch (IOException e) {
	        e.printStackTrace();
	        System.exit(1);
        }
        catch (PrintException e) {
	        e.printStackTrace();
	        System.exit(1);
        }
		catch (PrinterException e) {
	        e.printStackTrace();
	        System.exit(1);
        }
		
		System.exit(0);
	}
	
	
}

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
package org.openmrs.module.chirdlutil.util;

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
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.Defs;
import org.icepdf.core.views.DocumentViewController;
import org.icepdf.ri.common.PrintHelper;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

/**
 * Utility class for printing needs.
 *
 * @author Steve McKee
 */
public class PrintServices {

	private static Log log = LogFactory.getLog(PrintServices.class);
	
	static {
        Defs.setProperty("java.awt.headless", "true");
        Defs.setProperty("org.icepdf.core.scaleImages", "false");
        Defs.setProperty("org.icepdf.core.print.disableAlpha", "true");
        Defs.setProperty("org.icepdf.core.print.alphaInterpolation", "VALUE_ALPHA_INTERPOLATION_SPEED");
        Defs.setProperty("org.icepdf.core.print.antiAliasing", "VALUE_ANTIALIAS_ON");
        Defs.setProperty("org.icepdf.core.print.textAntiAliasing", "VALUE_TEXT_ANTIALIAS_OFF");
        Defs.setProperty("org.icepdf.core.print.colorRender", "VALUE_COLOR_RENDER_SPEED");
        Defs.setProperty("org.icepdf.core.print.dither", "VALUE_DITHER_DEFAULT");
        Defs.setProperty("org.icepdf.core.print.fractionalmetrics", "VALUE_FRACTIONALMETRICS_OFF");
        Defs.setProperty("org.icepdf.core.print.interpolation", "VALUE_INTERPOLATION_NEAREST_NEIGHBOR");
        Defs.setProperty("org.icepdf.core.print.render", "VALUE_RENDER_SPEED");
        Defs.setProperty("org.icepdf.core.print.stroke", "VALUE_STROKE_PURE");
    }
    
	/**
	 * Prints the specified file to the specified printer.  This method does perform any rendering for specialized file formats.  
	 * Please test this method with your file type and printer.
	 * 
	 * @param printerName The name of the printer to send the print job.
	 * @param fileToPrint The file to print.
	 * @throws IOException
	 * @throws PrintException
	 */
    public static void printFile(String printerName, File fileToPrint) throws IOException, PrintException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		log.error("A valid printerName parameter was not provided: " + printerName);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + printerName);
    	} else if (fileToPrint == null || !fileToPrint.exists() || !fileToPrint.canRead()) {
    		log.error("A valid fileToPrint parameter was not provided: " + fileToPrint);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + fileToPrint);
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	log.error("No printers found for " + printerName);
        	throw new IllegalArgumentException("No printers found for " + printerName);
        }
        
        PrintService selectedService = printServices[0];
        FileInputStream psStream = null;  
        psStream = new FileInputStream(fileToPrint);  
        
        DocPrintJob printJob = selectedService.createPrintJob();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(psStream, flavor, null);
        printJob.print(document, null);
    }
    
    /**
     * Prints a specified PDF file to a specified printer.
     * 
     * @param printerName The name of the printer to use to print the PDF file.
     * @param pdfFile The PDF File to print.
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFException
     * @throws PrintException
     */
    public static void printPDFFile(String printerName, File pdfFile) throws IOException, PrintException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		log.error("A valid printerName parameter was not provided: " + printerName);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + printerName);
    	} else if (pdfFile == null || !pdfFile.exists() || !pdfFile.canRead()) {
    		log.error("A valid fileToPrint parameter was not provided (or unable to read): " + pdfFile);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + pdfFile);
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	log.error("No printers found for " + printerName);
        	throw new IllegalArgumentException("No printers found for " + printerName);
        }
        
        PrintService selectedService = printServices[0];
    	Document pdf = new Document();
    	try {
	        pdf.setFile(pdfFile.getAbsolutePath());
        }
        catch (PDFException e) {
	        log.error("Error printing PDF", e);
	        throw new PrintException(e);
        }
        catch (PDFSecurityException e) {
	        log.error("Error printing PDF", e);
	        throw new PrintException(e);
        }
    	
    	SwingController sc = new SwingController();
    	DocumentViewController vc = new DocumentViewControllerImpl(sc);
    	vc.setDocument(pdf);
    	
    	// create a new print helper with a specified paper size and print
    	// quality
    	PrintHelper printHelper = new PrintHelper(vc, pdf.getPageTree(),
    	        MediaSizeName.NA_LETTER, PrintQuality.NORMAL);
    	// try and print pages 1 - 10, 1 copy, scale to fit paper.
    	printHelper.setupPrintService(selectedService, 0, pdf.getNumberOfPages() - 1, 1, false);
    	// print the document
    	printHelper.print();
    }
}

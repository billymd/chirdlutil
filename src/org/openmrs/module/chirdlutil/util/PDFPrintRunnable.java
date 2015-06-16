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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;


/**
 * Thread used to do auto-printing of PDF files.
 * 
 * @author Steve McKee
 */
public class PDFPrintRunnable implements Runnable {
	
	private static Log log = LogFactory.getLog(PDFPrintRunnable.class);
	private String printerName;
	private File pdfFile;
	
	/**
	 * Constructor method
	 * 
	 * @param printerName The printer where the print job will be sent.
	 * @param pdfFile The PDF file to print.
	 */
	public PDFPrintRunnable(String printerName, File pdfFile) {
		this.printerName = printerName;
		this.pdfFile = pdfFile;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
    public void run() {
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	log.error("No printers found for " + printerName);
        	throw new IllegalArgumentException("No printers found for " + printerName);
        }
        
        PrintService selectedService = printServices[0];
        PrinterJob printJob = PrinterJob.getPrinterJob();
	    try {
            printJob.setPrintService(selectedService);
            PDDocument document = PDDocument.load(pdfFile);
		    document.silentPrint(printJob);
        }
        catch (PrinterException e) {
            log.error("Error printing PDF file " + pdfFile.getAbsolutePath() + " to printer " + printerName, e);
        } 
	    catch (IOException e) {
            log.error("Error loading PDF file to print " + pdfFile.getAbsolutePath(), e);
        }
    }
}

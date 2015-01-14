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
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for printing needs.
 *
 * @author Steve McKee
 */
public class PrintServices {

	private static Log log = LogFactory.getLog(PrintServices.class);
    
	/**
	 * Prints the specified file to the specified printer.
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
}

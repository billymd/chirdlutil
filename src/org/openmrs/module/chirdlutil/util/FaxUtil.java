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
import java.io.FileWriter;
import java.util.UUID;

import org.jfree.util.Log;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;


/**
 * Utility class for handling faxes.
 * 
 * @author Steve McKee
 */
public class FaxUtil {

	/**
	 * Faxes a file.
	 * 
	 * @param fileToFax The file to fax.
	 * @param from Who the fax is from.  This will be displayed on the cover letter.
	 * @param to Who the fax is to.  This will be displayed on the cover letter.
	 * @param faxNumber The number where the fax will be sent.
	 * @param patient The patient for whom the fax is being sent.  This can be null.
	 * @param formName The name of the form being sent.  This will be displayed on the cover letter.  This can be null.
	 * @throws Exception
	 */
	public static void faxFile(File fileToFax, String from, String to, String faxNumber, Patient patient, String formName) 
	throws Exception {
		if (fileToFax == null) {
			throw new IllegalArgumentException("The fileToFax parameter cannot be null.");
		} else if (from == null) {
			throw new IllegalArgumentException("The from parameter cannot be null.");
		} else if (to == null) {
			throw new IllegalArgumentException("The to parameter cannot be null.");
		} else if (faxNumber == null) {
			throw new IllegalArgumentException("The faxNumber parameter cannot be null.");
		}
		
		// Get the fax directory
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String faxDirectory = adminService.getGlobalProperty("chirdlutil.outgoingFaxDirectory");
			if (faxDirectory == null || faxDirectory.trim().length() == 0) {
				String message = "Cannot fax form " + formName
				        + ".  The chirdlutil.outgoingFaxDirectory global property is not set.";
				throw new Exception(message);
			} else if (!(new File(faxDirectory).exists())) {
				String message = "Cannot fax form: " + formName
				        + ".  The chirdlutil.outgoingFaxDirectory cannot be found: " + faxDirectory;
				throw new Exception(message);
			}
			
			// copy the image file to the fax directory
			String name = fileToFax.getName();
			String destination = faxDirectory + File.separator + name;
			IOUtil.copyFile(fileToFax.getAbsolutePath(), destination);
			
			// create the control file
			String controlFilename = UUID.randomUUID().toString() + ".col";
			File controlFile = new File(faxDirectory, controlFilename);
			FileWriter writer = new FileWriter(controlFile);
			String lineSeparator = System.getProperty("line.separator");
			StringBuffer data = new StringBuffer("##filename ");
			data.append(name);
			data.append(lineSeparator);
			data.append("##covername Generic.doc");
			data.append(lineSeparator);
			data.append("##cover");
			data.append(lineSeparator);
			data.append("##from ");
			data.append(from);
			data.append(lineSeparator);
			data.append("##to ");
			data.append(to);
			data.append(lineSeparator);
			if (formName != null) {
				data.append("##message1 Form: " + formName);
				data.append(lineSeparator);
			}
			
			if (patient != null) {
				data.append("##message2 Patient: " + patient.getGivenName() + " " + patient.getFamilyName());
				data.append(lineSeparator);
				PatientIdentifier ident = patient.getPatientIdentifier();
				if (ident != null) {
					data.append("##message3 MRN: " + ident.getIdentifier());
					data.append(lineSeparator);
				}
			}
			
			data.append("##dial ");
			data.append(faxNumber);
			
			try {
				writer.write(data.toString());
			}
			finally {
				writer.flush();
				writer.close();
			}
		}
		catch (Exception e) {
			Log.error("Error faxing file: " + fileToFax, e);
			throw e;
		}
	}
}

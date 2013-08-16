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
package org.openmrs.module.chirdlutil.util.voice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.HttpUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;


/**
 * Utility class for communicating with the voice system.
 *
 * @author Steve McKee
 */
public class VoiceSystemUtil {
	
	private static Log log = LogFactory.getLog(VoiceSystemUtil.class);
	private static final String APPOINTMENT_FILE_NAME = "Appointments";
	private static final String APPOINTMENT_FILE_EXTENSION = ".csv";

	/**
	 * Uploads entries to the voice system service.
	 * 
	 * @param callRequests The information to upload to the voice system service.
	 * @return String containing the result of the HTTP POST to the voice system service or null if the service call failed 
	 * due to missing global properties or invalid patient credentials.
	 * @throws IOException
	 */
	public static String uploadCallData(List<VoiceCallRequest> callRequests) throws IOException {
		AdministrationService adminService = Context.getAdministrationService();
		String data = "";
		String userId;
		String password;
		String filename;
		String host;
		String url;
		int port;
		int connectionTimeout = 5000;
		int readTimeout = 5000;
		
		// Get the username
		userId = adminService.getGlobalProperty("chirdlutil.voiceUsername");
		if (userId == null || userId.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceUsername");
			return null;
		}
		
		// Get the password
		password = adminService.getGlobalProperty("chirdlutil.voicePassword");
		if (password == null || password.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voicePassword");
			return null;
		}
		
		// Get the filename
		filename = adminService.getGlobalProperty("chirdlutil.voiceFilename");
		if (filename == null || filename.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceFilename");
			return null;
		}
		
		// Get the host
		host = adminService.getGlobalProperty("chirdlutil.voiceHost");
		if (host == null || host.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceHost");
			return null;
		}
		
		// Get the URL
		url = adminService.getGlobalProperty("chirdlutil.voiceUrl");
		if (url == null || url.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceUrl");
			return null;
		}
		
		// Get the port
		String portStr = adminService.getGlobalProperty("chirdlutil.voicePort");
		if (portStr == null || portStr.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voicePort.");
			return null;
		}
		
		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			log.error("Invalid number format for global property chirdlutil.voicePort.");
			return null;
		}
		
		// Get the connection timeout
		String connectionTimeoutStr = adminService.getGlobalProperty("chirdlutil.voiceConnectionTimeout");
		if (connectionTimeoutStr == null || connectionTimeoutStr.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceConnectionTimeout.  A default of 5 seconds will " +
					"be used.");
			connectionTimeoutStr = "5";
		}
		
		try {
			connectionTimeout = Integer.parseInt(connectionTimeoutStr);
			connectionTimeout = connectionTimeout * 1000;
		} catch (NumberFormatException e) {
			log.error("Invalid number format for global property chirdlutil.voiceConnectionTimeout.  A default of 5 " +
					"seconds will be used.");
			connectionTimeout = 5000;
		}
		
		// Get the read timeout
		String readTimeoutStr = adminService.getGlobalProperty("chirdlutil.voiceReadTimeout");
		if (readTimeoutStr == null || readTimeoutStr.trim().length() == 0) {
			log.error("No value set for global property: chirdlutil.voiceReadTimeout.  A default of 5 seconds will " +
					"be used.");
			readTimeoutStr = "5";
		}
		
		try {
			readTimeout = Integer.parseInt(readTimeoutStr);
			readTimeout = readTimeout * 1000;
		} catch (NumberFormatException e) {
			log.error("Invalid number format for global property chirdlutil.voiceReadTimeout.  A default of 5 " +
					"seconds will be used.");
			readTimeout = 5000;
		}
		
		String fileText = "PatientID\tFirstName\tLastName\tHomePhone\tAppointment Date\tAppointment Time\t" +
				"Appointment Location";
		String patientFileText = "";
		DateFormat dateFormatter = new SimpleDateFormat("M/dd/yyyy");
		DateFormat timeFormatter = new SimpleDateFormat("h:mmaa");
		Map<Integer, String> locationNameMap = new HashMap<Integer, String>();
		
		// Process each request
		for (VoiceCallRequest callRequest : callRequests) {
			Person patient = callRequest.getPatient();
			Integer locationId = callRequest.getLocationId();
			Integer personId = patient.getPersonId();
			String firstName = patient.getGivenName();
			String lastName = patient.getFamilyName();
			String phoneNumber = callRequest.getPhoneNumber();
			if (phoneNumber == null || phoneNumber.trim().length() == 0) {
				// The phone number is required, so we cannot request a phone call for the patient.
				log.error("Patient " + personId + " does not have a phone number and will not be forwarded to the " +
						"voice system service to be called.");
				continue;
			}
			
			String locationName = locationNameMap.get(locationId);
			if (locationName == null) {
				locationName = getLocationName(locationId);
				if (locationName == null || locationName.trim().length() == 0) {
					// The location name is required, so we cannot request a phone call for the patient.
					log.error("Location " + locationId + " does not have a displayName location attribute defined.  Patient " +
							personId + " will not be referred to the voice system service to be called.");
					continue;
				}
				
				locationNameMap.put(locationId, locationName);
			}
			
			Date apptDate = callRequest.getAppointmentDate();
			String apptDateStr = dateFormatter.format(apptDate);
			String apptTime = timeFormatter.format(apptDate);
			
			patientFileText += "\n" + personId + "\t" + firstName + "\t" + lastName + "\t" + phoneNumber + "\t" + 
				apptDateStr + "\t" + apptTime + "\t" + locationName;
		}
		
		locationNameMap.clear();
		if (patientFileText.trim().length() == 0) {
			log.error("There was not enough information for the call request provided to forward the information on to " +
					"the voice system service.");
			return null;
		}
		
		data = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
		data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
		data += "&" + URLEncoder.encode("filename", "UTF-8") + "=" + URLEncoder.encode(filename, "UTF-8");
		data += "&" + URLEncoder.encode("filetext", "UTF-8") + "=" + URLEncoder.encode(fileText + patientFileText, "UTF-8");
		
		// Perform the POST
		String result = HttpUtil.postSecure("SSLv3", host, url, data, connectionTimeout, port);
		return result;
	}
	
	/**
	 * Get the list of appointments for the next business day.
	 * 
	 * @return List of Appointment objects
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<Appointment> getAppointments() throws FileNotFoundException, IOException {
		String csvFileLocStr = Context.getAdministrationService().getGlobalProperty("chirdlutil.appointmentCsvFileLocation");
		if (csvFileLocStr == null || csvFileLocStr.trim().length() == 0) {
			log.error("No global property value set for chirdlutil.appointmentCsvFileLocation.  Appointments " +
					"cannot be located.");
			return new ArrayList<Appointment>();
		}
		
		File csvLocFile = new File(csvFileLocStr);
		if (!csvLocFile.exists() || !csvLocFile.canRead()) {
			log.error("Cannot find/read appointments file from location " + csvFileLocStr);
			return new ArrayList<Appointment>();
		}
		
		Calendar cal = Calendar.getInstance();
		// Add a day to the date for tomorrow's appointments.
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date date = cal.getTime();
		DateFormat formatter = new SimpleDateFormat("MMddyyyy");
		String dateStr = formatter.format(date);
		String filename = APPOINTMENT_FILE_NAME + " " + dateStr + APPOINTMENT_FILE_EXTENSION;
		File csvFile = new File(csvLocFile, filename);
		if (!csvFile.exists() || !csvFile.canRead()) {
			log.error("Cannot find/read appointments file: " + csvFileLocStr + File.separator + filename);
			return new ArrayList<Appointment>();
		}
		
		InputStreamReader inStreamReader = new InputStreamReader(new FileInputStream(csvFile), "UTF-16");
		CSVReader reader = new CSVReader(inStreamReader, '|');
		HeaderColumnNameTranslateMappingStrategy<Appointment> strat = 
			new HeaderColumnNameTranslateMappingStrategy<Appointment>();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("encounterID", "encounterId");
		map.put("First Name", "firstName");
		map.put("Last Name", "lastName");
		map.put("Phone Number", "phoneNumber");
		map.put("Appt. Date", "apptDate");
		map.put("Appt. Time", "apptTime");
		map.put("Clinic Location", "clinicLocation");
		map.put("MRN", "mrn");
		map.put("Status", "status");
		
		strat.setType(Appointment.class);
		strat.setColumnMapping(map);
		
		CsvToBean<Appointment> csv = new CsvToBean<Appointment>();
		List<Appointment> list = csv.parse(strat, reader);
		
		if (list == null) {
			return new ArrayList<Appointment>();
		}
		
		return list;
	}
	
	/**
	 * Utility method for getting a location's display name based on a location ID.
	 * 
	 * @param locationId Location ID used to find the location display name.
	 * @return The clinic display name or null if one cannot be found.
	 */
	private static String getLocationName(Integer locationId) {
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		LocationAttributeValue lav = service.getLocationAttributeValue(locationId, "clinicDisplayName");
		if (lav == null) {
			return null;
		}
		
		return lav.getValue();
	}
}

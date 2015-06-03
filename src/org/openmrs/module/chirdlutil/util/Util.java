/**
 * 
 */
package org.openmrs.module.chirdlutil.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

/**
 * This class contains several utility methods
 * and other modules that depend on it.
 * 
 * @author Tammy Dugan
 */
public class Util
{
	protected static final Log log = LogFactory.getLog(Util.class);

	
	public static final String MEASUREMENT_LB = "lb";
	public static final String MEASUREMENT_IN = "in";
	public static final String MEASUREMENT_CM = "cm";
	public static final String MEASUREMENT_KG = "kg";
	
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	private static final String APPOINTMENT_FILE_NAME = "Appointments";
	private static final String APPOINTMENT_FILE_EXTENSION = ".csv";
	
	private static ServerConfig serverConfig = null;
	private static long lastUpdatedServerConfig = System.currentTimeMillis();
	private static final long SERVER_CONFIG_UPDATE_CYCLE = 900000; // fifteen minutes
	
	/**
	 * Converts specific measurements in English units to metric
	 * 
	 * @param measurement measurement to be converted
	 * @param measurementUnits units of the measurement
	 * @return double metric value of the measurement
	 */
	public static double convertUnitsToMetric(double measurement,
			String measurementUnits)
	{
		if (measurementUnits == null)
		{
			return measurement;
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_IN))
		{
			measurement = measurement * 2.54; // convert inches to centimeters
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_LB))
		{
			measurement = measurement * 0.45359237; // convert pounds to kilograms
		}
		return measurement; // truncate to one decimal
												  // place
	}
	
	/**
	 * Converts specific measurements in metric units to English
	 * 
	 * @param measurement measurement to be converted
	 * @param measurementUnits units of the measurement
	 * @return double English value of the measurement
	 */
	public static double convertUnitsToEnglish(double measurement,
			String measurementUnits)
	{
		if (measurementUnits == null)
		{
			return measurement;
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_CM))
		{
			measurement = measurement * 0.393700787; // convert centimeters to inches
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_KG))
		{
			measurement = measurement * 2.20462262; // convert kilograms to pounds
		}
		return measurement; // truncate to one decimal
												  // place
	}
	
	/**
	 * Returns the numeric part of a string input as a string
	 * @param input alphanumeric input
	 * @return String all numeric
	 */
	public static String extractIntFromString(String input)
	{
		if(input == null)
		{
			return null;
		}
		String[] tokens = Pattern.compile("\\D").split(input);
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < tokens.length; i++)
			result.append(tokens[i]);
		return result.toString();
	}
	
	/**
	 * Adds period if necessary to a package prefix
	 * @param packagePrefix a java package prefix
	 * @return String formatted package prefix
	 */
	public static String formatPackagePrefix(String packagePrefix)
	{
		if (packagePrefix!=null&&!packagePrefix.endsWith("."))
		{
			packagePrefix += ".";
		}
		return packagePrefix;
	}
	
	/**
	 * Parses a giving string into a list of package prefixes based on the delimiter provided.  This will also 
	 * add a period (if necessary) to each of the package prefixes.  This will not return null.
	 * 
	 * @param packagePrefixes one or more java package prefix
	 * @param delimiter the delimiter that separates the package prefixes in the packagePrefixes parameter.
	 * @return List of Strings containing formatted package prefixes
	 */
	public static List<String> formatPackagePrefixes(String packagePrefixes, String delimiter)
	{
		List<String> packagePrefixList = new ArrayList<String>();
		if (packagePrefixes == null) {
			return packagePrefixList;
		}
		
		StringTokenizer tokenizer = new StringTokenizer(packagePrefixes, delimiter, false);
		while (tokenizer.hasMoreTokens()) {
			String packagePrefix = tokenizer.nextToken().trim();
			if (packagePrefix.length() == 0) {
				continue;
			}
			
			packagePrefix = formatPackagePrefix(packagePrefix);			
			packagePrefixList.add(packagePrefix);
		}
		
		return packagePrefixList;
	}
	
	public static String toProperCase(String str)
	{
		if(str == null || str.length()<1)
		{
			return str;
		}
		
		StringBuffer resultString = new StringBuffer();
		String delimiter = " ";
		
		StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
		
		String currToken = null;
		
		while(tokenizer.hasMoreTokens())
		{
			currToken = tokenizer.nextToken();
			
			if(!currToken.equals(delimiter))
			{
				if(currToken.length()>0)
				{
					currToken = currToken.substring(0, 1).toUpperCase()
						+ currToken.substring(1).toLowerCase();
				}
			}
			
			resultString.append(currToken);
		}
		
		return resultString.toString();
	}
	
	public static double getFractionalAgeInUnits(Date birthdate, Date today, String unit)
	{
		int ageInUnits = getAgeInUnits(birthdate,today,unit);
		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.setTime(birthdate);
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.setTime(today);
		
		if (unit.equalsIgnoreCase(MONTH_ABBR))
		{
			int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
			int birthdateDayOfMonth = birthdateCalendar.get(Calendar.DAY_OF_MONTH);
			
			double dayDiff = todayDayOfMonth - birthdateDayOfMonth;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			double daysInMonth = 0;
			
			if(dayDiff > 0)
			{
				daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}else{
				todayCalendar.add(Calendar.MONTH, -1);
				daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				dayDiff = daysInMonth+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInMonth);
		}
		if (unit.equalsIgnoreCase(YEAR_ABBR))
		{
			int todayDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR);
			int birthdateDayOfYear = birthdateCalendar.get(Calendar.DAY_OF_YEAR);
			
			double dayDiff = todayDayOfYear - birthdateDayOfYear;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			//code to handle leap years
			Integer daysInYear = 365; 
			if(birthdateCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365||
					todayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365){
				dayDiff--;
			}
			
			if(dayDiff < 0)
			{
				todayCalendar.add(Calendar.YEAR, -1);
				dayDiff = daysInYear+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInYear);
		}
		if (unit.equalsIgnoreCase(WEEK_ABBR))
		{
			int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);
			int birthdateDayOfWeek = birthdateCalendar.get(Calendar.DAY_OF_WEEK);
			
			int dayDiff = todayDayOfWeek - birthdateDayOfWeek;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			int daysInWeek = 0;
			
			if(dayDiff > 0)
			{
				daysInWeek = todayCalendar.getActualMaximum(Calendar.DAY_OF_WEEK);
			}else{
				todayCalendar.add(Calendar.WEEK_OF_YEAR, -1);
				daysInWeek = todayCalendar.getActualMaximum(Calendar.DAY_OF_WEEK);
				dayDiff = daysInWeek+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInWeek);
		}
		return ageInUnits;
	}
	
	/**
	 * Returns a person's age in the specified units (days, weeks, months,
	 * years)
	 * 
	 * @param birthdate person's date of birth
	 * @param today date to calculate age from
	 * @param unit unit to calculate age in (days, weeks, months, years)
	 * @return int age in the given units
	 */
	//Note: this does not handle leap years for age in days
	public static int getAgeInUnits(Date birthdate, Date today, String unit)
	{
		if (birthdate == null)
		{
			return 0;
		}

		if (today == null)
		{
			today = new Date();
		}

		int diffMonths = 0;
		int diffDayOfMonth = 0;
		int diffDayOfYear = 0;
		int years = 0;
		int months = 0;
		int days = 0;

		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.setTime(birthdate);
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.setTime(today);

		// return 0 if the birthdate is after today
		if (birthdate.compareTo(today) > 0)
		{
			return 0;
		}

		years = todayCalendar.get(Calendar.YEAR)
				- birthdateCalendar.get(Calendar.YEAR);
		
		diffMonths = todayCalendar.get(Calendar.MONTH)
				- birthdateCalendar.get(Calendar.MONTH);
		diffDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR)
				- birthdateCalendar.get(Calendar.DAY_OF_YEAR);

		diffDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH)
				- birthdateCalendar.get(Calendar.DAY_OF_MONTH);

		months = years * 12;
		months += diffMonths;

		days = years * 365;
		days += diffDayOfYear;

		if (unit.equalsIgnoreCase(YEAR_ABBR))
		{
			if (diffMonths < 0)
			{
				years--;
			}
			else if (diffMonths == 0 && diffDayOfYear < 0)
			{
				years--;
			}
			return years;
		}

		if (unit.equalsIgnoreCase(MONTH_ABBR))
		{
			if (diffDayOfMonth < 0)
			{
				months--;
			}
			return months;
		}

		if (unit.equalsIgnoreCase(WEEK_ABBR))
		{
			return days/7;
		}

		if (days < 0)
		{
			days = 0;
		}
		return days;
	}
	
	public static Double round(Double value,int decimalPlaces)
	{
		if(decimalPlaces<0||value == null)
		{
			return value;
		}
		
		double intermVal = value*Math.pow(10, decimalPlaces);
		intermVal = Math.round(intermVal);
		return intermVal/(Math.pow(10, decimalPlaces));
	}
	
	public static String getStackTrace(Exception x) {
		OutputStream buf = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(buf);
		x.printStackTrace(p);
		return buf.toString();
	}
	
	public static String archiveStamp()
	{
		Date currDate = new java.util.Date();
		String dateFormat = "yyyyMMdd-kkmmss-SSS";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String formattedDate = formatter.format(currDate);
		return formattedDate;
	}
	
	public static boolean isToday(Date date) {
		if (date != null) {
			Calendar today = Calendar.getInstance();
			Calendar dateToCompare = Calendar.getInstance();
			dateToCompare.setTime(date);
			return (today.get(Calendar.ERA) == dateToCompare.get(Calendar.ERA)
					&& today.get(Calendar.YEAR) == dateToCompare.get(Calendar.YEAR) && today
					.get(Calendar.DAY_OF_YEAR) == dateToCompare
					.get(Calendar.DAY_OF_YEAR));

		}
		return false;
	}
	
	public static String removeTrailingZeros(String str1)
	{

		char[] chars = str1.toCharArray();
		int index = chars.length-1;
		for (; index >=0; index--)
		{
			if (chars[index] != '0')
			{
				break;
			}
		}
		if (index > -1)
		{
			return str1.substring(0,index+1);
		}
		return str1;
	}
	
	public static String removeLeadingZeros(String mrn)
	{

		char[] chars = mrn.toCharArray();
		int index = 0;
		for (; index < chars.length; index++)
		{
			if (chars[index] != '0')
			{
				break;
			}
		}
		if (index > -1)
		{
			return mrn.substring(index);
		}
		return mrn;
	}
	
	public static Obs saveObs(Patient patient, Concept currConcept, int encounterId, String value,
	                          Date obsDatetime) {
		if (value == null || value.length() == 0) {
			return null;
		}
		
		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		String datatypeName = currConcept.getDatatype().getName();
		
		if (datatypeName.equalsIgnoreCase("Numeric")) {
			try {
				obs.setValueNumeric(Double.parseDouble(value));
			}
			catch (NumberFormatException e) {
				log.error("Could not save value: " + value + " to the database for concept "
				        + currConcept.getName().getName());
			}
		} else if (datatypeName.equalsIgnoreCase("Coded")) {
			ConceptService conceptService = Context.getConceptService();
			Concept answer = conceptService.getConceptByName(value);
			if (answer == null) {
				log.error(value + " is not a valid concept name. " + value + " will be stored as text.");
				obs.setValueText(value);
			} else {
				obs.setValueCoded(answer);
			}
		} else if (datatypeName.equalsIgnoreCase("Date")) {
			Date valueDatetime = new Date(Long.valueOf(value));
			obs.setValueDatetime(valueDatetime);
		} else {
			obs.setValueText(value);
		}
		
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		Location location = encounter.getLocation();
		
		obs.setPerson(patient);
		obs.setConcept(currConcept);
		obs.setLocation(location);
		obs.setEncounter(encounter);
		obs.setObsDatetime(obsDatetime);
		obsService.saveObs(obs, null);
		return obs;
	}
	
	/**
	 * Calculates age to a precision of days, weeks, months, or years based on a
	 * set of rules
	 * 
	 * @param birthdate patient's birth date
	 * @param cutoff date to calculate age from
	 * @return String age with units 
	 */
	public static String adjustAgeUnits(Date birthdate, Date cutoff)
	{
		int years = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, YEAR_ABBR);
		int months = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, MONTH_ABBR);
		int weeks = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, WEEK_ABBR);
		int days = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, DAY_ABBR);

		if (years >= 2)
		{
			return years + " " + YEAR_ABBR;
		}

		if (months >= 2)
		{
			return months + " " + MONTH_ABBR;
		}

		if (days > 30)
		{
			return weeks + " " + WEEK_ABBR;
		}

		return days + " " + DAY_ABBR;
	}
	
	public static String computeMD5(String strToMD5) throws DigestException
	{
		try
		{
			//get md5 of input string
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(strToMD5.getBytes());
			byte[] bytes = md.digest();
			
			//convert md5 bytes to a hex string
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < bytes.length; i++)
			{
				hexString.append(Integer.toHexString(0xFF & bytes[i]));
			}
			return hexString.toString();
		} catch (Exception e)
		{
			throw new DigestException("couldn't make digest of partial content");
		}
	}
	
	/**
	 * Returns an array of barcodes found in the specified PDF file.
	 * 
	 * @param pdfFile The PDF document to search for barcodes.
	 * @param hints Map of barcode types to find.  One key should be DecodeHintType.POSSIBLE_FORMATS.  The value should be a 
	 * Collection of possible format types found in the BarcodeFormat class.  Another key that is optional is 
	 * DecodeHintType.TRY_HARDER with a boolean value.
	 * @param regexPattern A pattern used to match barcodes.  If this parameter is specified, only barcodes that match this 
	 * pattern will be returned.  All others will be ignored.
	 * @return Array of barcodes found in a PDF document.
	 * @throws Exception
	 */
	public static String[] getPdfFormBarcodes(File pdfFile, Map<DecodeHintType, Object> hints, String regexPattern) 
	throws Exception {    
		if (pdfFile == null || !pdfFile.exists() || !pdfFile.canRead()) {
			throw new IllegalArgumentException("Please specify a valid PDF file.  Make sure the file is readable as well.");
		}
		
		BufferedImage image;     
		image = convertPdfPageToImage(pdfFile, 0, 0.0f);     
		
		if (image == null) {       
			throw new IllegalArgumentException("Could not decode image from PDF file " + pdfFile.getAbsolutePath() + ".");  
		}
		
		LuminanceSource source = new BufferedImageLuminanceSource(image);     
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));     
		MultiFormatReader barcodeReader = new MultiFormatReader();
		MultipleBarcodeReader reader = new GenericMultipleBarcodeReader(barcodeReader);
		Result[] results;     
		Set<String> matches =  new HashSet<String>();     
		if (hints != null && ! hints.isEmpty()) {            
			results = reader.decodeMultiple(bitmap, hints); 
		}
		else {       
			results = reader.decodeMultiple(bitmap); 
		}
		
		if (results != null) {
			for (Result result : results) {
				String text = result.getText();
				if (regexPattern != null) {
					if (!text.matches(regexPattern)) {
						continue;
					}
				}
				
				matches.add(text);
			}
		}
		
		String[] returnArry = new String[matches.size()];
		return matches.toArray(returnArry);
	}
	
	/**
	 * Creates a BufferedImage of a particular page in a PDF document.
	 * 
	 * @param pdfFile The PDF document containing the page for which the image will be created.
	 * @param pageNumber The page of the PDF document that will be generated into an image. This
	 *            counter is zero based.
	 * @param rotation The rotation of the image. If null it will be set to no rotation (0.0f).
	 * @return BufferedImage object containing the specified PDF page as an image.
	 */
	public static BufferedImage convertPdfPageToImage(File pdfFile, int pageNumber, Float rotation) throws Exception {
		if (rotation == null) {
			rotation = 0.0f;
		}
		
		// open the file
		Document document = new Document();
		try {
			document.setFile(pdfFile.getAbsolutePath());
		}
		catch (PDFException ex) {
			log.error("Error parsing PDF document", ex);
			throw ex;
		}
		catch (PDFSecurityException ex) {
			log.error("Error encryption not supported", ex);
			throw ex;
		}
		catch (FileNotFoundException ex) {
			log.error("Error file not found", ex);
			throw ex;
		}
		catch (IOException ex) {
			log.error("Error IOException", ex);
			throw ex;
		}
		
		// save page capture to file.
		float scale = 1.5f;
		String scaleStr = Context.getAdministrationService().getGlobalProperty("chirdlutil.pdfToImageScaleValue");
		if (scaleStr != null && scaleStr.trim().length() > 0) {
			try {
				scale = Float.parseFloat(scaleStr);
			}
			catch (NumberFormatException e) {
				log.error("Invalid value for global property chirdlutil.pdfToImageScaleValue.  Default value of "
				        + "1.5f will be used");
				scale = 1.5f;
			}
		} else {
			log.error("Value for global property chirdlutil.pdfToImageScaleValue is not set.  Default value of "
			        + "1.5f will be used");
		}
		
		// Paint the page content to an image
		BufferedImage image = (BufferedImage) document.getPageImage(pageNumber, GraphicsRenderingHints.PRINT,
		    Page.BOUNDARY_CROPBOX, rotation, scale);
		
		// clean up resources
		document.dispose();
		
		return image;
	}
	
	/**
	 * Returns the server configuration.
	 * 
	 * @return ServerConfig object.
	 * @throws JiBXException
	 * @throws FileNotFoundException
	 */
	public static ServerConfig getServerConfig() throws JiBXException, FileNotFoundException {
		long currentTime = System.currentTimeMillis();
    	if (serverConfig == null || (currentTime - lastUpdatedServerConfig) > SERVER_CONFIG_UPDATE_CYCLE) {
    		lastUpdatedServerConfig = currentTime;
			String configFileStr = Context.getAdministrationService().getGlobalProperty("chirdlutil.serverConfigFile");
			if (configFileStr == null) {
				log.error("You must set a value for global property: chirdlutil.serverConfigFile");
				return null;
			}
			
			File configFile = new File(configFileStr);
			if (!configFile.exists()) {
				log.error("The file location specified for the global property "
					+ "chirdlutil.serverConfigFile does not exist.");
				return null;
			}
			
			IBindingFactory bfact = BindingDirectory.getFactory(ServerConfig.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			serverConfig = (ServerConfig)uctx.unmarshalDocument(new FileInputStream(configFile), null);
    	}
    	
    	return serverConfig;
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
		
		InputStreamReader inStreamReader = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
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
	 * Method to return the JSP page associated with a specific form.
	 * 
	 * @param formId The form ID.
	 * @return The page URL for the form or null if one can't be found.
	 */
	public static String getFormUrl(Integer formId) {
		String url = null;
		try {
	        ServerConfig config = Util.getServerConfig();
	        String username = Context.getAuthenticatedUser().getUsername();
	        MobileClient client = config.getMobileClient(username);
	        if (client == null) {
	        	return null;
	        }
	        
	        Form form = Context.getFormService().getForm(formId);
    		String formName = form.getName();
	        String primaryFormId = client.getPrimaryFormId();
	        MobileForm primaryForm = config.getMobileFormById(primaryFormId);
	        if (primaryForm != null && formName.equals(primaryForm.getName())) {
        		return primaryForm.getPageUrl();
	        }
	        
	        String[] secondaryFormIds = client.getSecondaryFormIds();
	        if (secondaryFormIds == null) {
	        	return null;
	        }
	        
	        for (String secondaryFormId : secondaryFormIds) {
	        	MobileForm secondaryForm = config.getMobileFormById(secondaryFormId);
	        	if (secondaryForm != null && formName.equals(secondaryForm.getName())) {
	        		return secondaryForm.getPageUrl();
	        	}
	        }
	        
	        return null;
        }
        catch (FileNotFoundException e) {
	        log.error("Error finding server config file", e);
        }
        catch (JiBXException e) {
	        log.error("Error parsing server config file", e);
        }
        
        return url;
	}
	
	/**
	 * Creates a new concept.
	 * 
	 * @param conceptName The name of the concept.
	 * @param conceptClass The concept class.
	 * @param conceptDatatype The concept data type.
	 * @return Newly created Concept object.
	 */
	public static Concept createNewConcept(String conceptName, String conceptClass, String conceptDatatype) {
		Concept concept = new Concept();
		concept.setDateCreated(new Date());
		concept.setUuid(UUID.randomUUID().toString());
		
		ConceptName conceptNameObj = new ConceptName();
		conceptNameObj.setLocale(Context.getLocale());
		conceptNameObj.setName(conceptName);
		conceptNameObj.setDateCreated(new Date());
		conceptNameObj.setUuid(UUID.randomUUID().toString());
		concept.addName(conceptNameObj);
		
		ConceptService conceptService = Context.getConceptService();
		ConceptClass conceptClassObj = conceptService.getConceptClassByName(conceptClass);
		concept.setConceptClass(conceptClassObj);
		
		ConceptDatatype conceptDatatypeObj = conceptService.getConceptDatatypeByName(conceptDatatype);
		concept.setDatatype(conceptDatatypeObj);
		
		return conceptService.saveConcept(concept);
	}
}

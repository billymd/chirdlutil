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


/**
 * Constants class for dependent modules
 * 
 * @author Steve McKee
 */
public final class ChirdlUtilConstants {
	
	/*
	 * User property constants
	 */
	public static final String USER_PROPERTY_LOCATION = "location";
	
	public static final String USER_PROPERTY_LOCATION_TAGS = "locationTags";
	/*
	 * 
	 */
	
	/*
	 * Identifier type constants
	 */
	public static final String IDENTIFIER_TYPE_MRN = "MRN_OTHER";
	public static final String IDENTIFIER_TYPE_SSN = "SSN";
	public static final String IDENTIFIER_TYPE_IMMUNIZATION_REGISTRY = "Immunization Registry";

	/*
	 * 
	 */

	/*
	 * Person attribute type constants
	 */
	public static final String PERSON_ATTRIBUTE_RELIGION = "Religion";
	public static final String PERSON_ATTRIBUTE_MARITAL_STATUS = "Civil Status";
	public static final String PERSON_ATTRIBUTE_MAIDEN_NAME = "Mother's maiden name";
	public static final String PERSON_ATTRIBUTE_NEXT_OF_KIN = "Next of Kin";
	public static final String PERSON_ATTRIBUTE_TELEPHONE = "Telephone Number";
	public static final String PERSON_ATTRIBUTE_CITIZENSHIP = "Citizenship";
	public static final String PERSON_ATTRIBUTE_RACE = "Race";
	public static final String PERSON_ATTRIBUTE_SSN = "SSN";

	/*
	 * 
	 */

	/*
	 * Chirdlutilbackports error category constants
	 */
	public static final String ERROR_QUERY_KITE_CONNECTION = "Query Kite Connection";
	public static final String ERROR_GENERAL = "General Error";
	public static final String ERROR_PSF_SCAN = "PSF Scan";
	public static final String ERROR_PWS_SCAN = "PWS Scan";
	public static final String ERROR_HL7_PARSING = "Hl7 Parsing";
	public static final String ERROR_MRN_VALIDITY = "MRN Validity";
	public static final String ERROR_XML_PARSING = "XML Parsing";
	public static final String ERROR_ID_VALIDITY = "ID Validity";
	public static final String ERROR_QUERY_MED_LIST_CONNECTION = "Query Medication List Connection";
	public static final String ERROR_HL7_EXPORT = "Hl7 Export";
	public static final String ERROR_SUPPORT_PAGE = "Support Page";
	public static final String ERROR_BAD_SCANS = "Bad Scans";
	public static final String ERROR_QUERY_IMMUNIZATION_CONNECTION = "Query Immunization List Connection";
	public static final String ERROR_GIS_CLINIC_ADDRESS_USED = "GIS Clinic Address Used";
	public static final String ERROR_MEDICAL_LEGAL_PAGE = "Medical Legal Page";
	public static final String ERROR_DIABETES_PAGE = "Diabetes Page";
	public static final String ERROR_WEB_SERVICE = "Web Service Error";

	
	/*
	 * Location tag attribute constants
	 */
	public static final String LOC_TAG_ATTR_ACTIVE_PRINTER_STATION = "ActivePrinterStation";
	public static final String LOC_TAG_ATTR_AGE_LIMIT_AT_CHECKIN = "CheckinAgeLimit";
	/*
	 * 
	 */
	
	/* Unit constants
	 * 
	 */
	public static final String MEASUREMENT_LB = "lb";
	public static final String MEASUREMENT_IN = "in";
	public static final String MEASUREMENT_CM = "cm";
	public static final String MEASUREMENT_KG = "kg";
	
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	
	/*
	 * Global property constants
	 */
	public static final String GLOBAL_PROPERTY_SCHEDULER_USERNAME = "scheduler.username";
	public static final String GLOBAL_PROPERTY_SCHEDULER_PASSWORD = "scheduler.password";
	
	
	/*
	 * State constants
	 */
	
	public static final String STATE_EXPORT_JIT = "JIT_export";
	public static final String STATE_EXPORT_VITALS = "Export Vitals";
	public static final String STATE_EXPORT_POC = "Export POC";
	public static final String STATE_JIT = "JIT";
	public static final String STATE_CHECKIN = "CHECKIN";
	public static final String STATE_FINISHED = "FINISHED";
	public static final String STATE_ERROR = "ErrorState";
	public static final String STATE_PSF_PRINTED = "PSF_printed";
	public static final String STATE_PSF_WAIT_TO_SCAN = "PSF_wait_to_scan";
	public static final String STATE_PWS_PRINTED = "PWS_printed";
	public static final String STATE_PWS_WAIT_TO_SCAN = "PWS_wait_to_scan";
	public static final String STATE_PWS_PROCESS = "PWS_process";
	public static final String STATE_RANDOMIZE = "Randomize";
	public static final String STATE_PSF_PROCESS = "PSF_process";
	public static final String STATE_QUERY_KITE_PWS = "QUERY KITE PWS";
	public static final String STATE_QUERY_KITE_PSF = "QUERY KITE PSF";
	public static final String STATE_PSF_REPRINT = "PSF_reprint";
	public static final String STATE_PWS_REPRINT = "PWS_reprint";
	public static final String STATE_PSF_RESCAN = "PSF_rescan";
	public static final String STATE_PWS_RESCAN = "PWS_rescan";
	public static final String STATE_PSF_CREATE = "PSF_create";
	public static final String STATE_PWS_CREATE = "PWS_create";
	public static final String STATE_QUERY_KITE = "QUERY KITE Alias";
	public static final String STATE_CLINIC_REGISTRATION = "Clinic Registration";
	public static final String STATE_PROCESS_CHECKIN_HL7 = "Process Checkin HL7";
	public static final String STATE_QUERY_MEDICATION_LIST = "Query medication list";
	public static final String STATE_JIT_CREATE = "JIT_create";
	public static final String STATE_JIT_PRINTED = "JIT_printed";
	public static final String STATE_JIT_WAIT_TO_SCAN = "JIT_wait_to_scan";
	public static final String STATE_JIT_PROCESS = "JIT_process";
	public static final String STATE_JIT_RESCAN = "JIT_rescan";
	public static final String STATE_JIT_FINISHED = "JIT_FINISHED";
	public static final String STATE_JIT_REPRINT = "JIT_reprint";
	public static final String STATE_JIT_INCOMPLETE = "JIT_incomplete";
	public static final String STATE_CLEAR_IN_MEMORY_OBS_PWS = "CLEAR INMEMORY OBS PWS";
	public static final String STATE_CLEAR_IN_MEMORY_OBS_PSF = "CLEAR INMEMORY OBS PSF";
	public static final String STATE_JIT_FAX = "JIT_fax";
	public static final String STATE_QUERY_IMMUNIZATION_FORECAST = "Query Immunization Forecast";
	public static final String STATE_PSF_MOBILE_CREATE = "PSF_mobile_create";
	public static final String STATE_JIT_MOBILE_CREATE = "JIT_mobile_create";
	public static final String STATE_PSF_WAIT_FOR_ELECTORONIC_SUBMISSION = "PSF WAIT FOR ELECTRONIC SUBMISSION";
	public static final String STATE_JIT_WAIT_FOR_ELECTRONIC_SUBMISSION = "JIT WAIT FOR ELECTRONIC SUBMISSION";

}


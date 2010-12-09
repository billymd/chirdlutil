package org.openmrs.module.chirdlutil.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;

/**
 * Defines implementations of services used by this module
 * 
 * @author Tammy Dugan
 */
public class ChirdlUtilServiceImpl implements ChirdlUtilService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private ChirdlUtilDAO dao;
	
	/**
	 * Empty constructor
	 */
	public ChirdlUtilServiceImpl() {
	}
	
	/**
	 * @return ChirdlUtilDAO
	 */
	public ChirdlUtilDAO getChirdlUtilDAO() {
		return this.dao;
	}
	
	/**
	 * Sets the DAO for this service. The dao allows interaction with the database.
	 * 
	 * @param dao
	 */
	public void setChirdlUtilDAO(ChirdlUtilDAO dao) {
		this.dao = dao;
	}
	
	public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId, String locationTagAttributeName,
	                                                              Integer locationId) {
		return getChirdlUtilDAO().getLocationTagAttributeValue(locationTagId, locationTagAttributeName, locationId);
	}
	
	public LocationAttributeValue getLocationAttributeValue(Integer locationId, String locationAttributeName) {
		return getChirdlUtilDAO().getLocationAttributeValue(locationId, locationAttributeName);
	}
	
	public LocationTagAttributeValue getLocationTagAttributeValueById(Integer location_tag_attribute_value_id) {
		return getChirdlUtilDAO().getLocationTagAttributeValueById( location_tag_attribute_value_id);
	}
}

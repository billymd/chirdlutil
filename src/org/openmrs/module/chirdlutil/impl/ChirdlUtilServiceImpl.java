package org.openmrs.module.chirdlutil.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttribute;
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
	
	public LocationTagAttribute getLocationTagAttribute(Integer locationTagAttributeId) {
		return getChirdlUtilDAO().getLocationTagAttribute(locationTagAttributeId);
	}
	
	public LocationTagAttribute getLocationTagAttribute(String locationTagAttributeName) {
		return getChirdlUtilDAO().getLocationTagAttribute(locationTagAttributeName);
	}
	
	public LocationTagAttribute saveLocationTagAttribute(LocationTagAttribute value) {
		return getChirdlUtilDAO().saveLocationTagAttribute(value);
	}

    public LocationTagAttributeValue saveLocationTagAttributeValue(LocationTagAttributeValue value) {
	    return getChirdlUtilDAO().saveLocationTagAttributeValue(value);
    }

    public LocationAttributeValue saveLocationAttributeValue(LocationAttributeValue value) {
	    return getChirdlUtilDAO().saveLocationAttributeValue(value);
    }

    public void deleteLocationTagAttribute(LocationTagAttribute value) {
	    getChirdlUtilDAO().deleteLocationTagAttribute(value);
    }

    public void deleteLocationTagAttributeValue(LocationTagAttributeValue value) {
	    getChirdlUtilDAO().deleteLocationTagAttributeValue(value);
    }

    public EventLog logEvent(EventLog eventLog) {
	    return getChirdlUtilDAO().logEvent(eventLog);
    }

    public List<EventLog> getEventLogs(Integer eventId, Integer locationId, Integer formId, Integer studyId, String event,
                                       Date startDate, Date endDate, Integer userId, String description) {
	    return getChirdlUtilDAO().getEventLogs(eventId, locationId, formId, studyId, event, startDate, 
	    	endDate, userId, description);
    }
}

package org.openmrs.module.chirdlutil.service;

import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines services used by this module
 * 
 * @author Tammy Dugan
 */
@Transactional
public interface ChirdlUtilService {
	
	public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId, String locationTagAttributeName,
	                                                              Integer locationId);
	
	public LocationAttributeValue getLocationAttributeValue(Integer locationId, String locationAttributeName);
	
	public LocationTagAttributeValue getLocationTagAttributeValueById(Integer location_tag_attribute_value_id); 
		
}

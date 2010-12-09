package org.openmrs.module.chirdlutil.db;

import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;

/**
 * ChirdlUtil related database functions
 * 
 * @author Tammy Dugan
 */
public interface ChirdlUtilDAO {
	
	public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId, String locationTagAttributeName,
	                                                              Integer locationId);
	
	public LocationAttributeValue getLocationAttributeValue(Integer locationId, String locationAttributeName);
	
	public LocationTagAttributeValue getLocationTagAttributeValueById(Integer location_tag_attribute_value_id); 
}

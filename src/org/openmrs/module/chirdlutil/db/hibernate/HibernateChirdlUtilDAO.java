package org.openmrs.module.chirdlutil.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttribute;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttribute;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;

/**
 * Hibernate implementations of ChirdlUtil related database functions.
 * 
 * @author Tammy Dugan
 */
public class HibernateChirdlUtilDAO implements ChirdlUtilDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Empty constructor
	 */
	public HibernateChirdlUtilDAO() {
	}
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId, String locationTagAttributeName,
	                                                              Integer locationId) {
		try {
			LocationTagAttribute locationTagAttribute = this.getLocationTagAttributeByName(locationTagAttributeName);
			
			if (locationTagAttribute != null) {
				Integer locationTagAttributeId = locationTagAttribute.getLocationTagAttributeId();
				
				String sql = "select * from chirdlutil_location_tag_attribute_value where location_tag_id=? and location_id=? and location_tag_attribute_id=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
				
				qry.setInteger(0, locationTagId);
				qry.setInteger(1, locationId);
				qry.setInteger(2, locationTagAttributeId);
				qry.addEntity(LocationTagAttributeValue.class);
				
				List<LocationTagAttributeValue> list = qry.list();
				
				if (list != null && list.size() > 0) {
					return list.get(0);
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private LocationTagAttribute getLocationTagAttributeByName(String locationTagAttributeName) {
		try {
			String sql = "select * from chirdlutil_location_tag_attribute " + "where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
			qry.setString(0, locationTagAttributeName);
			qry.addEntity(LocationTagAttribute.class);
			
			List<LocationTagAttribute> list = qry.list();
			
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LocationAttributeValue getLocationAttributeValue(Integer locationId, String locationAttributeName) {
		try {
			LocationAttribute locationAttribute = this.getLocationAttributeByName(locationAttributeName);
			
			if (locationAttribute != null) {
				Integer locationAttributeId = locationAttribute.getLocationAttributeId();
				
				String sql = "select * from chirdlutil_location_attribute_value where location_id=? and location_attribute_id=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
				
				qry.setInteger(0, locationId);
				qry.setInteger(1, locationAttributeId);
				qry.addEntity(LocationAttributeValue.class);
				
				List<LocationAttributeValue> list = qry.list();
				
				if (list != null && list.size() > 0) {
					return list.get(0);
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private LocationAttribute getLocationAttributeByName(String locationAttributeName) {
		try {
			String sql = "select * from chirdlutil_location_attribute " + "where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
			qry.setString(0, locationAttributeName);
			qry.addEntity(LocationAttribute.class);
			
			List<LocationAttribute> list = qry.list();
			
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LocationTagAttributeValue getLocationTagAttributeValueById(Integer location_tag_attribute_value_id){
		try {
			String sql = "select * from chirdlutil_location_tag_attribute_value " + 
			"where location_tag_attribute_value_id=?";
			
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
			qry.setInteger(0, location_tag_attribute_value_id);
			qry.addEntity(LocationTagAttributeValue.class);
			List<LocationTagAttributeValue> list = qry.list();
			
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
    public LocationTagAttribute getLocationTagAttribute(Integer locationTagAttributeId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTagAttribute.class).add(
		    Expression.eq("locationTagAttributeId", locationTagAttributeId));
		
		List<LocationTagAttribute> locations = criteria.list();
		if (null == locations || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
    }

	@Override
    public LocationTagAttribute getLocationTagAttribute(String locationTagAttributeName) {		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTagAttribute.class).add(
		    Expression.eq("name", locationTagAttributeName));
		
		List<LocationTagAttribute> locations = criteria.list();
		if (null == locations || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
    }
	
	@Override
	public LocationTagAttribute saveLocationTagAttribute(LocationTagAttribute value) {
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
	}

	@Override
    public LocationTagAttributeValue saveLocationTagAttributeValue(LocationTagAttributeValue value) {
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
    }

	@Override
    public LocationAttributeValue saveLocationAttributeValue(LocationAttributeValue value) {
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
    }

	@Override
    public void deleteLocationTagAttribute(LocationTagAttribute value) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTagAttributeValue.class).add(
		    Expression.eq("locationTagAttributeId", value.getLocationTagAttributeId()));
		
		List<LocationTagAttributeValue> locations = criteria.list();
		if (null != locations) {
			for (LocationTagAttributeValue attr : locations) {
				deleteLocationTagAttributeValue(attr);
			}
		}
		
		sessionFactory.getCurrentSession().delete(value);
    }

	@Override
    public void deleteLocationTagAttributeValue(LocationTagAttributeValue value) {
		sessionFactory.getCurrentSession().delete(value);
    }
	
}


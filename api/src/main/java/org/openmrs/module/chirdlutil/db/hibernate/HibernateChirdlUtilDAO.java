package org.openmrs.module.chirdlutil.db.hibernate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;

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
	
	public EventLog logEvent(EventLog eventLog) {
		sessionFactory.getCurrentSession().saveOrUpdate(eventLog);
		return eventLog;
    }

    public List<EventLog> getEventLogs(Integer eventId, Integer locationId, Integer formId, Integer studyId, String event,
                                       Date startDate, Date endDate, Integer userId, String description) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventLog.class).addOrder(Order.desc("eventTime"));
    	if (eventId != null) {
    		criteria.add(Expression.eq("eventId", eventId));
    	}
    	if (locationId != null) {
    		criteria.add(Expression.eq("locationId", locationId));
    	}
    	if (formId != null) {
    		criteria.add(Expression.eq("formId", formId));
    	}
    	if (studyId != null) {
    		criteria.add(Expression.eq("studyId", studyId));
    	}
    	if (startDate != null && endDate != null) {
    		criteria.add(Expression.between("eventTime", startDate, endDate));
    	} else {
    		if (startDate != null) {
    			criteria.add(Expression.ge("eventTime", startDate));
    		} else if (endDate != null) {
    			criteria.add(Expression.le("eventTime", endDate));
    		}
    	}
    	if (event != null) {
    		criteria.add(Expression.like("event", event));
    	}
    	if (userId != null) {
    		criteria.add(Expression.eq("userId", userId));
    	}
    	if (description != null) {
    		criteria.add(Expression.like("description", description, MatchMode.ANYWHERE));
    	}
		
		List<EventLog> logs = criteria.list();
		if (logs == null) {
			logs = new ArrayList<EventLog>();
		}
		
		return logs;
    }
	public boolean tableExists(String tableName)
	{
		try
		{
			Connection con = this.sessionFactory.getCurrentSession()
					.connection();
			DatabaseMetaData dbmd = con.getMetaData();

			// Check if table exists

			ResultSet rs = dbmd.getTables(null, null, tableName, null);

			if (rs.next())
			{
				return true;
			}
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(e);
		}
		return false;
	}

	public void executeSql(String sql)
	{
		Connection con = this.sessionFactory.getCurrentSession().connection();
		try
		{
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			con.commit();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(e);
		}
	}
}


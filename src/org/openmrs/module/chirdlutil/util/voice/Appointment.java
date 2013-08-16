package org.openmrs.module.chirdlutil.util.voice;


public class Appointment {

	private String encounterId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String apptDate;
	private String apptTime;
	private String clinicLocation;
	private String mrn;
	private String status;
	
	public Appointment() {
	}

	
    /**
     * @return the encounterId
     */
    public String getEncounterId() {
    	return encounterId;
    }

	
    /**
     * @param encounterId the encounterId to set
     */
    public void setEncounterId(String encounterId) {
    	this.encounterId = encounterId;
    }

	
    /**
     * @return the firstName
     */
    public String getFirstName() {
    	return firstName;
    }

	
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
    	this.firstName = firstName;
    }

	
    /**
     * @return the lastName
     */
    public String getLastName() {
    	return lastName;
    }

	
    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }

	
    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
    	return phoneNumber;
    }

	
    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
    	this.phoneNumber = phoneNumber;
    }

	
    /**
     * @return the apptDate
     */
    public String getApptDate() {
    	return apptDate;
    }

	
    /**
     * @param apptDate the apptDate to set
     */
    public void setApptDate(String apptDate) {
    	this.apptDate = apptDate;
    }

	
    /**
     * @return the apptTime
     */
    public String getApptTime() {
    	return apptTime;
    }

	
    /**
     * @param apptTime the apptTime to set
     */
    public void setApptTime(String apptTime) {
    	this.apptTime = apptTime;
    }

	
    /**
     * @return the clinicLocation
     */
    public String getClinicLocation() {
    	return clinicLocation;
    }

	
    /**
     * @param clinicLocation the clinicLocation to set
     */
    public void setClinicLocation(String clinicLocation) {
    	this.clinicLocation = clinicLocation;
    }

	
    /**
     * @return the mrn
     */
    public String getMrn() {
    	return mrn;
    }

	
    /**
     * @param mrn the mrn to set
     */
    public void setMrn(String mrn) {
    	this.mrn = mrn;
    }

	
    /**
     * @return the status
     */
    public String getStatus() {
    	return status;
    }

	
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
    	this.status = status;
    }

	
}

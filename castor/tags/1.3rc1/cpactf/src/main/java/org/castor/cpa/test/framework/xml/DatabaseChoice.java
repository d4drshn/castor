/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id$
 */

package org.castor.cpa.test.framework.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class DatabaseChoice.
 * 
 * @version $Revision$ $Date$
 */
public class DatabaseChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _driver.
     */
    private org.castor.cpa.test.framework.xml.Driver _driver;

    /**
     * Field _dataSource.
     */
    private org.castor.cpa.test.framework.xml.DataSource _dataSource;

    /**
     * Field _jndi.
     */
    private org.castor.cpa.test.framework.xml.Jndi _jndi;


      //----------------/
     //- Constructors -/
    //----------------/

    public DatabaseChoice() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'dataSource'.
     * 
     * @return the value of field 'DataSource'.
     */
    public org.castor.cpa.test.framework.xml.DataSource getDataSource(
    ) {
        return this._dataSource;
    }

    /**
     * Returns the value of field 'driver'.
     * 
     * @return the value of field 'Driver'.
     */
    public org.castor.cpa.test.framework.xml.Driver getDriver(
    ) {
        return this._driver;
    }

    /**
     * Returns the value of field 'jndi'.
     * 
     * @return the value of field 'Jndi'.
     */
    public org.castor.cpa.test.framework.xml.Jndi getJndi(
    ) {
        return this._jndi;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'dataSource'.
     * 
     * @param dataSource the value of field 'dataSource'.
     */
    public void setDataSource(
            final org.castor.cpa.test.framework.xml.DataSource dataSource) {
        this._dataSource = dataSource;
    }

    /**
     * Sets the value of field 'driver'.
     * 
     * @param driver the value of field 'driver'.
     */
    public void setDriver(
            final org.castor.cpa.test.framework.xml.Driver driver) {
        this._driver = driver;
    }

    /**
     * Sets the value of field 'jndi'.
     * 
     * @param jndi the value of field 'jndi'.
     */
    public void setJndi(
            final org.castor.cpa.test.framework.xml.Jndi jndi) {
        this._jndi = jndi;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * org.castor.cpa.test.framework.xml.DatabaseChoice
     */
    public static org.castor.cpa.test.framework.xml.DatabaseChoice unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (org.castor.cpa.test.framework.xml.DatabaseChoice) Unmarshaller.unmarshal(org.castor.cpa.test.framework.xml.DatabaseChoice.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}

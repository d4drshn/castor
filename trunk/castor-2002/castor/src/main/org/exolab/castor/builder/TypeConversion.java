/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2000 (C) Intalio Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.builder;

import org.exolab.castor.builder.types.*;
import org.exolab.javasource.*;
import org.exolab.castor.util.OrderedMap;

import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.JavaXMLNaming;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.SimpleTypesFactory;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class TypeConversion {


    private static OrderedMap sjNameMap = iCreateNameMap();

    /**
     * Returns the Java type name based on the given Schema
     * type name
    **/
    public static String getJavaTypeName(String schemaTypeName) {
        if (schemaTypeName == null) return null;
        String mappedName = (String) sjNameMap.get(schemaTypeName);
        if (mappedName != null) return mappedName;
        else return schemaTypeName;
    } //-- getJavaTypeName

    /*
    public static XSType createXSType(String schemaType) {

        XSType xsType = null;

        //-- string
        if ("string".equals(schemaType)) {
            xsType = new XSString();
        }
        //-- integer
        else if ("integer".equals(schemaType)) {
            xsType = new XSInteger();
        }
        else if ("binary".equals(schemaType)) {
            xsType = new XSBinary();
        }
        else if ("boolean".equals(schemaType)) {
            xsType = new XSBoolean();
        }
        //-- positive-integer
        else if ("negative-integer".equals(schemaType)) {
            xsType = new XSNegativeInteger();
        }
        //-- positive-integer
        else if ("positive-integer".equals(schemaType)) {
            xsType = new XSPositiveInteger();
        }
        //-- real
        else if ("real".equals(schemaType)) {
            xsType = new XSReal();
        }
        else if ("NCName".equals(schemaType)) {
            xsType = new XSNCName();
        }
        //-- NMTOKEN
        else if ("NMTOKEN".equals(schemaType)) {
            xsType = new XSNMToken();
        }
        else if ("timeInstant".equals(schemaType)) {
            xsType = new XSTimeInstant();
        }
        else {
            xsType = new XSClass(new JClass(getJavaTypeName(schemaType)));
        }
        return xsType;
    } //-- createXSType
    */


    /**
     * Converts the given Simpletype to the appropriate XSType.
     * @return the XSType which represets the given Simpletype
    **/
    public static XSType convertType(SimpleType simpleType) {

        if (simpleType == null) return null;

        XSType xsType = null;


        //-- enumerated types
        if (simpleType.hasFacet("enumeration")) {
            String className = JavaXMLNaming.toJavaClassName(simpleType.getName());
			className = SourceGenerator.getQualifiedClassName(
							simpleType.getSchema().getTargetNamespace(),
							"types."+className);
            XSClass xsClass = new XSClass(new JClass(className));
            xsClass.setAsEnumertated(true);
            return xsClass;
        }

        //-- determine base type
        SimpleType base = simpleType;

        while ((base != null) && (!base.isBuiltInType())) {
            base = (SimpleType)base.getBaseType();
        }
        if (base == null) {
            String className
                = JavaXMLNaming.toJavaClassName(simpleType.getName());
            xsType = new XSClass(new JClass(className));
        }
        else {
            switch ( base.getTypeCode() ) {

                //-- ID
                case SimpleTypesFactory.ID_TYPE:
                    return new XSId();
                //-- IDREF
                case SimpleTypesFactory.IDREF_TYPE:
                    return new XSIdRef();
                //-- IDREFS
                case SimpleTypesFactory.IDREFS_TYPE:
                    return new XSList(new XSIdRef());
                //--URIREFERENCE
                case SimpleTypesFactory.URIREFERENCE_TYPE:
                    return new XSUriReference();
                //-- NCName
                case SimpleTypesFactory.NCNAME_TYPE:
                    return new XSNCName();
                //-- NMTOKEN
                case SimpleTypesFactory.NMTOKEN_TYPE:
                    return new XSNMToken();
                //-- binary
                case SimpleTypesFactory.BINARY_TYPE:
                    return new XSBinary();
                //-- boolean
                case SimpleTypesFactory.BOOLEAN_TYPE:
                    return new XSBoolean();
                //-- date
                case SimpleTypesFactory.DATE_TYPE:
                    return new XSDate();
                //-- double
                case SimpleTypesFactory.DOUBLE_TYPE:
                    return new XSReal();
                //-- integer
                case SimpleTypesFactory.INTEGER_TYPE:
                {
                    XSInteger xsInteger = new XSInteger();
                    if (!simpleType.isBuiltInType())
                        readIntegerFacets(simpleType, xsInteger);
                    return xsInteger;
                }
                //-- int
				case SimpleTypesFactory.INT_TYPE:
				{
					XSInt xsInt = new XSInt();
					if (!simpleType.isBuiltInType())
					    readIntFacets(simpleType, xsInt);
                    return xsInt;
                }
                //-- long
                case SimpleTypesFactory.LONG_TYPE:
                {
                    XSLong xsLong = new XSLong();
                    if (!simpleType.isBuiltInType())
                        readLongFacets(simpleType,xsLong);
                    return xsLong;
                }
                //-- negative-integer
                case SimpleTypesFactory.NEGATIVE_INTEGER_TYPE:
                {
                    XSInteger xsInteger = new XSNegativeInteger();
                    readIntegerFacets(simpleType, xsInteger);
                    return xsInteger;
                }
                //-- positive-integer
                case SimpleTypesFactory.POSITIVE_INTEGER_TYPE:
                {
                    XSInteger xsInteger = new XSPositiveInteger();
                    readIntegerFacets(simpleType, xsInteger);
                    return xsInteger;
                }
                //-- string
                case SimpleTypesFactory.STRING_TYPE:
                    return toXSString(simpleType);
                //-- time
                case SimpleTypesFactory.TIME_TYPE:
                    return new XSTime();
                //-- timeInstant
                case SimpleTypesFactory.TIME_INSTANT_TYPE:
                    return new XSTimeInstant();
                //-- Time duration
                case SimpleTypesFactory.TIME_DURATION_TYPE:
                    return new XSTimeDuration();
                    //return new XSLong();
                //-- decimal
                case SimpleTypesFactory.DECIMAL_TYPE:
                    return new XSDecimal();
                //-- short
                case SimpleTypesFactory.SHORT_TYPE:
                {
					XSShort xsShort = new XSShort();
					readShortFacets(simpleType, xsShort);
                    return xsShort;
                }
                default:
                    //-- error
                    String className
                        = JavaXMLNaming.toJavaClassName(simpleType.getName());
                    xsType = new XSClass(new JClass(className));
                    break;

            }
        }
        return xsType;

    } //-- convertType


    /**
     * Determines if the given type is a built in Schema simpletype
    **/
    public static boolean isBuiltInType(String type) {
        return (sjNameMap.get(type) != null);
    } //-- isBuiltInType

    public static String getSchemaTypeName(String javaTypeName) {
        return sjNameMap.getNameByObject(javaTypeName);
    } //-- getSchemaTypeNam

      //-------------------/
     //- Private Methods -/
    //-------------------/

    /**
     * Reads and sets the facets for XSInteger
     * @param simpletype the SimpleType containing the facets
     * @param xsInt the XSInteger to set the facets of
    **/
    private static void readIntegerFacets
        (SimpleType simpleType, XSInteger xsInteger)
    {

        //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            //-- maxExclusive
            if (Facet.MAX_EXCLUSIVE.equals(name))
                xsInteger.setMaxExclusive(facet.toInt());
            //-- maxInclusive
            else if (Facet.MAX_INCLUSIVE.equals(name))
                xsInteger.setMaxInclusive(facet.toInt());
            //-- minExclusive
            else if (Facet.MIN_EXCLUSIVE.equals(name))
                xsInteger.setMinExclusive(facet.toInt());
            //-- minInclusive
            else if (Facet.MIN_INCLUSIVE.equals(name))
                xsInteger.setMinInclusive(facet.toInt());
            else if (Facet.PATTERN.equals(name))
                xsInteger.setPattern(facet.getValue());

        }

    } //-- toXSInteger


    /**
     * Reads and sets the facets for XSInt
     * @param simpletype the SimpleType containing the facets
     * @param xsInt the XSInt to set the facets of
    **/
    private static void readIntFacets
        (SimpleType simpleType, XSInt xsInt)
    {

        //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            //-- maxExclusive
            if (Facet.MAX_EXCLUSIVE.equals(name))
                xsInt.setMaxExclusive(facet.toInt());
            //-- maxInclusive
            else if (Facet.MAX_INCLUSIVE.equals(name))
                xsInt.setMaxInclusive(facet.toInt());
            //-- minExclusive
            else if (Facet.MIN_EXCLUSIVE.equals(name))
                xsInt.setMinExclusive(facet.toInt());
            //-- minInclusive
            else if (Facet.MIN_INCLUSIVE.equals(name))
                xsInt.setMinInclusive(facet.toInt());
            else if (Facet.PATTERN.equals(name)) {
                xsInt.setPattern(facet.getValue());
            }

        }

    } //-- toXSInt

    /**
     * Reads the facets for an XSLong
     * @param simpleType the SimpleType containing the facets
     * @param xsLong the XSLong to set the facets of
    **/
    private static void readLongFacets
        (SimpleType simpleType, XSLong xsLong)
    {

        //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            //-- maxExclusive
            if (Facet.MAX_EXCLUSIVE.equals(name))
                xsLong.setMaxExclusive(facet.toLong());
            //-- maxInclusive
            else if (Facet.MAX_INCLUSIVE.equals(name))
                xsLong.setMaxInclusive(facet.toLong());
            //-- minExclusive
            else if (Facet.MIN_EXCLUSIVE.equals(name))
                xsLong.setMinExclusive(facet.toLong());
            //-- minInclusive
            else if (Facet.MIN_INCLUSIVE.equals(name))
                xsLong.setMinInclusive(facet.toLong());
            //-- pattern
            else if (Facet.PATTERN.equals(name))
                xsLong.setPattern(facet.getValue());
        }

    } //-- readLongFacets

    /**
     * Reads and sets the facets for XSShort
     * @param simpletype the Simpletype containing the facets
     * @param xsShort the XSShort to set the facets of
    **/
    private static void readShortFacets
        (SimpleType simpleType, XSShort xsShort)
    {
        //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            //-- maxExclusive
            if (Facet.MAX_EXCLUSIVE.equals(name))
                xsShort.setMaxExclusive(facet.toShort());
            //-- maxInclusive
            else if (Facet.MAX_INCLUSIVE.equals(name))
                xsShort.setMaxInclusive(facet.toShort());
            //-- minExclusive
            else if (Facet.MIN_EXCLUSIVE.equals(name))
                xsShort.setMinExclusive(facet.toShort());
            //-- minInclusive
            else if (Facet.MIN_INCLUSIVE.equals(name))
                xsShort.setMinInclusive(facet.toShort());
            //-- pattern
            else if (Facet.PATTERN.equals(name))
                xsShort.setPattern(facet.getValue());

        }

    } //-- toXSShort

	/**
	 * Returns a list of Facets from the simpleType
	 *	(duplicate facets due to extension are filtered out)
     * @param simpletype the Simpletype we want the facets for
     * @return Unique list of facets from the simple type
	 */
	private static Enumeration getFacets(SimpleType simpleType)
	{
		Hashtable hashTable = new Hashtable();
        Enumeration enum = simpleType.getFacets();
		while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();
			hashTable.put(name, facet);
		}
		return hashTable.elements();
	}

    /**
     * Converts the given simpletype to an XSString
     * @param simpletype the Simpletype to convert
     * @return the XSString representation of the given Simpletype
    **/
    private static XSString toXSString(SimpleType simpleType) {
        XSString xsString = new XSString();
        //-- copy valid facets
        Enumeration enum = simpleType.getFacets();
        while (enum.hasMoreElements()) {
            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();
            //-- maxlength
            if (Facet.MAX_LENGTH.equals(name))
                xsString.setMaxLength(facet.toInt());
            else if (Facet.MIN_LENGTH.equals(name))
                xsString.setMinLength(facet.toInt());
            else if (Facet.PATTERN.equals(name)) {
                xsString.setPattern(facet.getValue());
            }
        }
        return xsString;
    } //-- toXSString

    /**
     * Creates the naming table for type conversion
    **/
    private static OrderedMap iCreateNameMap() {

        OrderedMap nameMap = new OrderedMap(20);

        //-- #IDREF...temporary this will be changed, once
        //-- I add in the Resolver code
        nameMap.put("IDREF",        "java.lang.String");

        // Choose the right collection
        nameMap.put("IDREFS",       "java.lang.Vector");

        //-- type mappings
        nameMap.put("ID",                  "java.lang.String");
        nameMap.put("NCName",              "java.lang.String");
        nameMap.put("NMTOKEN",             "java.lang.String");
        nameMap.put("uriReference",         "java.lang.String");
        nameMap.put("binary",              "byte[]");
        nameMap.put("boolean",             "boolean");
        //nameMap.put("date",                "java.util.date");
        nameMap.put("date",                "org.exolab.castor.types.DateType");
        nameMap.put("integer",             "int");
        nameMap.put("negativeInteger",     "int");
        nameMap.put("positiveInteger",     "int");
        nameMap.put("real",                "double");
        nameMap.put("string",              "java.lang.String");
        //nameMap.put("time",                "java.sql.Time");
        nameMap.put("time",                "org.exolab.castor.types.Time");
        nameMap.put("timeDuration",        "org.exolab.castor.types.TimeDuration");
       // nameMap.put("timeDuration",        "long");
        nameMap.put("timeInstant",         "java.util.Date");
        nameMap.put("decimal",             "java.util.BigDecimal");
        nameMap.put("short",               "short");
        nameMap.put("int",		           "int");

        return nameMap;
    } //-- iCreateNameMap

} //-- TypeConversion

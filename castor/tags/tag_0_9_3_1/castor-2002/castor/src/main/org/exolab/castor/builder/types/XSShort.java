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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.builder.types;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.builder.SourceGenerator;
import org.exolab.javasource.*;

import java.util.Enumeration;

/**
 * The XML Schema Short type
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public final class XSShort extends XSPatternBase {

    //- Constraints for Short type
    Short maxInclusive = null;
    Short maxExclusive = null;
    Short minInclusive = null;
    Short minExclusive = null;

    /**
     * The JType represented by this XSType
    **/
    private static JType jType = JType.Short;


    public XSShort() {
        this(SourceGenerator.usePrimitiveWrapper());
    }

    public XSShort(boolean asWrapper) {
        super(XSType.SHORT_TYPE);
        if (asWrapper)
             this.jType = new JClass("java.lang.Short");
    } //-- XSShort


    /**
     * Returns the JType that this XSType represents
     * @return the JType that this XSType represents
    **/
    public JType getJType() {
        return this.jType;
    }

    /**
     * Returns the maximum exclusive value that this XSShort can hold.
     * @return the maximum exclusive value that this XSShort can hold. If
     * no maximum exclusive value has been set, Null will be returned
     * @see getMaxInclusive
    **/
    public Short getMaxExclusive() {
        return maxExclusive;
    } //-- getMaxExclusive

    /**
     * Returns the maximum inclusive value that this XSShort can hold.
     * @return the maximum inclusive value that this XSShort can hold. If
     * no maximum inclusive value has been set, Null will be returned
     * @see getMaxExclusive
    **/
    public Short getMaxInclusive() {
        return maxInclusive;
    } //-- getMaxInclusive


    /**
     * Returns the minimum exclusive value that this XSShort can hold.
     * @return the minimum exclusive value that this XSShort can hold. If
     * no minimum exclusive value has been set, Null will be returned
     * @see getMinInclusive
     * @see setMaxInclusive
    **/
    public Short getMinExclusive() {
        return minExclusive;
    } //-- getMinExclusive

    /**
     * Returns the minimum inclusive value that this XSShort can hold.
     * @return the minimum inclusive value that this XSShort can hold. If
     * no minimum inclusive value has been set, Null will be returned
     * @see getMinExclusive
    **/
    public Short getMinInclusive() {
        return minInclusive;
    } //-- getMinInclusive

    public boolean hasMaximum() {
        return ((maxInclusive != null) || (maxExclusive != null));
    } //-- hasMaximum

    public boolean hasMinimum() {
        return ((minInclusive != null) || (minExclusive != null));
    } //-- hasMinimum


    //public String toString() {
    //    return value.toString();
    //}


    /**
     * Sets the maximum exclusive value that this XSShort can hold.
     * @param max the maximum exclusive value this XSShort can be
     * @see setMaxInclusive
    **/
    public void setMaxExclusive(short max) {
        maxExclusive = new Short(max);
        maxInclusive = null;
    } //-- setMaxExclusive

    /**
     * Sets the maximum exclusive value that this XSShort can hold.
     * @param max the maximum exclusive value this XSShort can be
     * @see setMaxInclusive
    **/
    public void setMaxExclusive(Short max) {
        maxExclusive = max;
        maxInclusive = null;
    } //-- setMaxExclusive

    /**
     * Sets the maximum inclusive value that this XSShort can hold.
     * @param max the maximum inclusive value this XSShort can be
     * @see setMaxExclusive
    **/
    public void setMaxInclusive(short max) {
        maxInclusive = new Short(max);
        maxExclusive = null;
    } //-- setMaxInclusive

    /**
     * Sets the maximum inclusive value that this XSShort can hold.
     * @param max the maximum inclusive value this XSShort can be
     * @see setMaxExclusive
    **/
    public void setMaxInclusive(Short max) {
        maxInclusive = max;
        maxExclusive = null;
    } //-- setMaxInclusive


    /**
     * Sets the minimum exclusive value that this XSShort can hold.
     * @param max the minimum exclusive value this XSShort can be
     * @see setMinInclusive
    **/
    public void setMinExclusive(short min) {
        minExclusive = new Short(min);
        minInclusive = null;
    } //-- setMinExclusive

    /**
     * Sets the minimum exclusive value that this XSShort can hold.
     * @param max the minimum exclusive value this XSShort can be
     * @see setMinInclusive
    **/
    public void setMinExclusive(Short min) {
        minExclusive = min;
        minInclusive = null;
    } //-- setMinExclusive

    /**
     * Sets the minimum inclusive value that this XSShort can hold.
     * @param max the minimum inclusive value this XSShort can be
     * @see setMinExclusive
    **/
    public void setMinInclusive(short min) {
        minInclusive = new Short(min);
        minExclusive = null;
    } //-- setMinInclusive

    /**
     * Sets the minimum inclusive value that this XSShort can hold.
     * @param max the minimum inclusive value this XSShort can be
     * @see setMinExclusive
    **/
    public void setMinInclusive(Short min) {
        minInclusive = min;
        minExclusive = null;
    } //-- setMinInclusive

    /**
     * Reads and sets the facets for XSShort
     * @param simpletype the Simpletype containing the facets
     **/
    public void setFacets(SimpleType simpleType)
    {
        //-- copy valid facets
        Enumeration enum = getFacets(simpleType);
        while (enum.hasMoreElements()) {

            Facet facet = (Facet)enum.nextElement();
            String name = facet.getName();

            //-- maxExclusive
            if (Facet.MAX_EXCLUSIVE.equals(name))
                setMaxExclusive(facet.toShort());
            //-- maxInclusive
            else if (Facet.MAX_INCLUSIVE.equals(name))
                setMaxInclusive(facet.toShort());
            //-- minExclusive
            else if (Facet.MIN_EXCLUSIVE.equals(name))
                setMinExclusive(facet.toShort());
            //-- minInclusive
            else if (Facet.MIN_INCLUSIVE.equals(name))
                setMinInclusive(facet.toShort());
            //-- pattern
            else if (Facet.PATTERN.equals(name))
                setPattern(facet.getValue());

        }

    } //-- toXSShort
    /**
     * Returns the String necessary to convert an instance of this XSType
     * to an Object. This method is really only useful for primitive types
     * @param variableName the name of the instance variable
     * @return the String necessary to convert an instance of this XSType
     * to an Object
    **/
    public String createToJavaObjectCode(String variableName) {
        if (SourceGenerator.usePrimitiveWrapper())
            return super.createToJavaObjectCode(variableName);
        else {
            StringBuffer sb = new StringBuffer("new Short(");
            sb.append(variableName);
            sb.append(")");
            return sb.toString();
        }
    } //-- toJavaObject

    /**
     * Returns the String necessary to convert an Object to
     * an instance of this XSType. This method is really only useful
     * for primitive types
     * @param variableName the name of the Object
     * @return the String necessary to convert an Object to an
     * instance of this XSType
    **/
    public String createFromJavaObjectCode(String variableName) {
         StringBuffer sb = new StringBuffer("((Short)");
         sb.append(variableName);
         sb.append(").shortValue()");
         return sb.toString();
    } //-- fromJavaObject

} //-- XSShort

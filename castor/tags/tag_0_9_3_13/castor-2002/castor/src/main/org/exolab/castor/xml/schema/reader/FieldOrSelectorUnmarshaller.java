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
 * Copyright 2001 (C) Intalio Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema.reader;

//-- imported classes and packages
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.schema.*;
import org.xml.sax.*;

/**
 * A class for Unmarshalling Selector or Field elements for
 * identity-constraints
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class FieldOrSelectorUnmarshaller extends SaxUnmarshaller {

      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * The current SaxUnmarshaller
    **/
    private SaxUnmarshaller _unmarshaller;

    /**
     * The current branch depth
    **/
    private int _depth = 0;

    /**
     * The Field or Selector we are unmarshalling
    **/
    private Annotated _fieldOrSelector = null;

    private boolean _foundAnnotation   = false;

    private String _elementName = null;
    
      //----------------/
     //- Constructors -/
    //----------------/

    /**
     * Creates a new FieldOrSelectorUnmarshaller
     *
     * @param elementName the name of the element being unmarshalled.
     * @param atts the AttributeList.
    **/
    public FieldOrSelectorUnmarshaller
        (String elementName, AttributeList atts)
        throws SAXException
    {
        super();
        
        _elementName = elementName;

        String xpath = atts.getValue(SchemaNames.XPATH_ATTR);
        if (xpath == null) {
            error("The 'xpath' attribute for '" + _elementName + "' must exist.");
        }
        
        String id   = atts.getValue(SchemaNames.ID_ATTR);
        
        //-- selector
        if (SchemaNames.SELECTOR.equals(elementName)) {
            _fieldOrSelector = new IdentitySelector(xpath);
            if (id != null) {
                ((IdentitySelector)_fieldOrSelector).setId(id);
            }
        }
        //-- field
        else {
            _fieldOrSelector = new IdentityField(xpath);
            if (id != null) {
                ((IdentityField)_fieldOrSelector).setId(id);
            }
        }

    } //-- FieldOrSelectorUnmarshaller

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the name of the element that this SaxUnmarshaller
     * handles
     * @return the name of the element that this SaxUnmarshaller
     * handles
    **/
    public String elementName() {
        return _elementName;
    } //-- elementName

    /**
     * Returns the Object created by this SaxUnmarshaller
     *
     * @return the Object created by this SaxUnmarshaller
    **/
    public Object getObject() {
        return _fieldOrSelector;
    } //-- getObject

    public void finish() throws SAXException {
        //-- do nothing
    } //-- finish

    /**
     * @param name
     * @param atts
     * @see org.xml.sax.DocumentHandler
    **/
    public void startElement(String name, AttributeList atts)
        throws org.xml.sax.SAXException
    {
        //-- Do delagation if necessary
        if (_unmarshaller != null) {
            _unmarshaller.startElement(name, atts);
            ++_depth;
            return;
        }

        if (SchemaNames.ANNOTATION.equals(name)) {

            if (_foundAnnotation)
                error("Only one (1) annotation may appear as a child of '" +
                    _elementName + "'.");
            _foundAnnotation = true;
            _unmarshaller = new AnnotationUnmarshaller(atts);
        }
        else illegalElement(name);

    } //-- startElement

    /**
     *
     * @param name
    **/
    public void endElement(String name)
        throws org.xml.sax.SAXException
    {

        //-- Do delagation if necessary
        if ((_unmarshaller != null) && (_depth > 0)) {
            _unmarshaller.endElement(name);
            --_depth;
            return;
        }

        //-- have unmarshaller perform any necessary clean up
        _unmarshaller.finish();

        if (SchemaNames.ANNOTATION.equals(name)) {
            Annotation annotation = (Annotation)_unmarshaller.getObject();
            _fieldOrSelector.addAnnotation(annotation);
        }

        _unmarshaller = null;
    } //-- endElement

    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        //-- Do delagation if necessary
        if (_unmarshaller != null) {
            _unmarshaller.characters(ch, start, length);
        }
    } //-- characters

} //-- FieldOrSelectorUnmarshaller

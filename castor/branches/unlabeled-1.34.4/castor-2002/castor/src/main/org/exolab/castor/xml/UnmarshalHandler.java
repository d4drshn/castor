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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml;

//-- Castor imports
import org.exolab.castor.util.MimeBase64Decoder;
import org.exolab.castor.xml.util.*;
import org.exolab.castor.mapping.FieldHandler;

//-- xml related imports
import org.xml.sax.*;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.ParserFactory;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * An unmarshaller to allowing unmarshalling of XML documents to
 * Java Objects. The Class must specify
 * the proper access methods (setters/getters) in order for instances
 * of the Class to be properly unmarshalled.
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class UnmarshalHandler implements DocumentHandler {
    
    
    //----------------------------/
    //- Private Member Variables -/
    //----------------------------/
    
    private static final Class[]  EMPTY_CLASS_ARGS  = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARGS = new Object[0];
    private static final String   EMPTY_STRING      = "";
    
    private Stack            _stateInfo    = null;
    private UnmarshalState   _topState     = null;
    private Class            _topClass     = null;
    
    /**
     * A StringBuffer used to created Debug/Log messages
    **/
    private StringBuffer     buf           = null;
    
    /**
     * A flag to indicate whether or not to generate debug information
    **/
    private boolean          debug         = false;
    
    /**
     * A flag to indicate we need to kill the _logWriter when
     * debug mode is false
    **/
    private boolean          killWriter    = false;
    
    /**
     * The SAX Document Locator
    **/
    private Locator          _locator      = null;
    
    /**
     * The PrintWriter to print log information to
    **/
    private PrintWriter      _logWriter    = null;

    /**
     * The ClassDescriptorResolver which is used to "resolve"
     * or find ClassDescriptors
    **/
    private ClassDescriptorResolver _cdResolver = null;
    
    /**
     * A flag indicating whether or not to perform validation
    **/
    private boolean          _validate     = true;
    
    private Hashtable _idReferences = null;
    
    private Hashtable _resolveTable = null;
    
    private ClassLoader _loader = null;
    
    //----------------/
    //- Constructors -/
    //----------------/
    
    /**
     * Creates a new UnmarshalHandler
     * The "root" class will be obtained by looking into the mapping
     * for a descriptor that matches the root element.
    **/
    protected UnmarshalHandler() {
        this(null);
    } //-- UnmarshalHandler
    
    /**
     * Creates a new UnmarshalHandler
     * @param _class the Class to create the UnmarshalHandler for
    **/
    protected UnmarshalHandler(Class _class) {
        super();
        _stateInfo    = new Stack();
        _idReferences = new Hashtable();
        _resolveTable = new Hashtable();
        buf           = new StringBuffer();
        _topClass     = _class;
    } //-- UnmarshalHandler(Class)
    
    protected Object getObject() {
        if (_topState != null) return _topState.object;
        return null;
    } //-- getObject
    
    
    /**
     * Sets the ClassLoader to use when loading classes
     *
     * @param loader the ClassLoader to use
    **/
    public void setClassLoader(ClassLoader loader) {
        _loader = loader;
    } //-- setClassLoader
    
    /**
     * Sets the ClassDescriptorResolver to use for loading and
     * resolving ClassDescriptors
     * 
     * @param cdResolver the ClassDescriptorResolver to use
    **/
    public void setResolver(ClassDescriptorResolver cdResolver) {
        this._cdResolver = cdResolver;
    } //-- setResolver
    
    /**
     * Turns debuging on or off. If no Log Writer has been set, then
     * System.out will be used to display debug information
     * @param debug the flag indicating whether to generate debug information.
     * A value of true, will turn debuggin on. 
     * @see #setLogWriter.
    **/
    public void setDebug(boolean debug) {
        this.debug = debug;
        
        if (this.debug && (_logWriter == null)) {
            _logWriter = new PrintWriter(System.out, true);
            killWriter = true;
        }
        if ((!this.debug) && killWriter) {
            _logWriter = null;
            killWriter = false;
        }
    } //-- setDebug
    
    /**
     * Sets the PrintWriter used for printing log messages
     * @param printWriter the PrintWriter to use when printing
     * log messages
    **/
    public void setLogWriter(PrintWriter printWriter) {
        this._logWriter = printWriter;
        killWriter = false;
    } //-- setLogWriter
    
    /**
     * Sets the flag for validation
     * @param validate, a boolean to indicate whether or not 
     * validation should be done during umarshalling. <br />
     * By default validation will be performed.
    **/
    public void setValidation(boolean validate) {
        this._validate = validate;
    } //-- setValidation
    
    //-----------------------------------/
    //- SAX Methods for DocumentHandler -/
    //-----------------------------------/
    
    public void characters(char[] ch, int start, int length) 
        throws SAXException
    {
        //System.out.println("#characters");
        
        if (_stateInfo.empty()) {
            return;
        }
        UnmarshalState state = (UnmarshalState)_stateInfo.peek();
        
        if (state.buffer == null) state.buffer = new StringBuffer();
        state.buffer.append(ch, start, length);
    } //-- characters
    
    
    public void endDocument() 
        throws org.xml.sax.SAXException
    {
        //-- I've found many application don't always call
        //-- #endDocument, so I usually never put any 
        //-- important logic here
        
    } //-- endDocument
    
    
    public void endElement(String name) 
        throws org.xml.sax.SAXException
    {
        
        if (_stateInfo.empty()) {
            throw new SAXException("missing start element: " + name);
        }
        
        if (hasNameSpace(name)) {
            name = getLocalPart(name);
        }
        
        UnmarshalState state = (UnmarshalState) _stateInfo.pop();
        //-- make sure we have the correct closing tag
        XMLFieldDescriptor descriptor = state.fieldDesc;
        if (!state.elementName.equals(name)) {            
            String err = "error in xml, expecting </" + state.elementName;
            err += ">, but recieved </" + name + "> instead.";
            throw new SAXException(err);
        }        
        
        //-- clean up current Object
        Class type = state.type;

        if ( type == null ) {
            message("Ignoring " + state.elementName + " no descriptor was found");
            return;
        }
        
        //-- check for special cases
        boolean byteArray = false;
        
        if (type.isArray())
            byteArray = (type.getComponentType() == Byte.TYPE);
        
        //-- If we don't have an instance object and the Class type
        //-- is not a primitive or a byte[] we must simply return
        if ((state.object == null) && (!state.primitiveOrImmutable))
            return;
            
            
        if (state.primitiveOrImmutable) {
            
            String str = null;
            
            if (state.buffer != null) {
                str = state.buffer.toString();
                state.buffer.setLength(0);
            }
            
            if (type == String.class) {
                if (str != null)
                    state.object = str;
                else
                    state.object = "";
            }
            //-- special handling for byte[]
            else if (byteArray) {
                if (str == null)
                    state.object = new byte[0];
                else {
                    //-- Base64 decoding
                    char[] chars = str.toCharArray();
                    MimeBase64Decoder decoder = new MimeBase64Decoder();
                    decoder.translate(chars, 0, chars.length);
                    state.object = decoder.getByteArray();
                }
            }
            else state.object = MarshalHelper.toPrimitiveObject(type,str);
        }
               
        //-- check for character content
        if ((state.buffer != null) && 
            (state.buffer.length() > 0) &&
            (state.classDesc != null)) {
            
            XMLFieldDescriptor cdesc = state.classDesc.getContentDescriptor();
            if (cdesc != null) {
                try {
                    FieldHandler handler = cdesc.getHandler();
                    handler.setValue(state.object, state.buffer.toString());
                }
                catch(java.lang.IllegalStateException ise) {
                    String err = "unable to add text content to ";
                    err += descriptor.getXMLName();
                    err += " due to the following error: " + ise;
                    throw new SAXException(err);
                }
            }
        }
        
        //-- if we are at root....just validate and we are done
        if (_stateInfo.empty()) {
            if (_validate) {
                try {
                    Validator.validate(state.object, _cdResolver);
                }
                catch(ValidationException vEx) {
                    throw new SAXException(vEx);
                }
            }
            return;
        }
        
        
        //-- Add object to parent if necessary
        
        if (descriptor.isIncremental()) return; //-- already added
        
        Object val = state.object;
        
        //-- get target object
        state = (UnmarshalState) _stateInfo.peek();
        
        //-- check to see if we have already read in
        //-- an element of this type
        if (!descriptor.isMultivalued()) {
            if (state.isUsed(descriptor)) {
                
                String err = "element \"" + name;
                err += "\" occurs more than once.";
                ValidationException vx = 
                    new ValidationException(err);
                throw new SAXException(vx);
            }
            state.markAsUsed(descriptor);
        }
        
        try {
            FieldHandler handler = descriptor.getHandler();
            handler.setValue(state.object, val);
        }
        /*
        catch(java.lang.reflect.InvocationTargetException itx) {
            
            Throwable toss = itx.getTargetException();
            if (toss == null) toss = itx;
            
            String err = "unable to add '" + name + "' to <";
            err += state.descriptor.getXMLName();
            err += "> due to the following exception: " + toss;
            throw new SAXException(err);
        }
        */
        catch(Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter  pw = new PrintWriter(sw); 
            ex.printStackTrace(pw);
            pw.flush();
            String err = "unable to add '" + name + "' to <";
            err += state.fieldDesc.getXMLName();
            err += "> due to the following exception: \n";
            err += ">>>--- Begin Exception ---<<< \n";
            err += sw.toString();
            err += ">>>---- End Exception ----<<< \n";
            throw new SAXException(err);
        }
        
    } //-- endElement
    
    public void ignorableWhitespace(char[] ch, int start, int length) 
        throws org.xml.sax.SAXException
    {
        //-- ignore
    } //-- ignorableWhitespace
    
    public void processingInstruction(String target, String data) 
        throws org.xml.sax.SAXException 
    {
        //-- do nothing for now
    } //-- processingInstruction
    
    
    public void setDocumentLocator(Locator locator) {
        this._locator = locator;
    } //-- setDocumentLocator
    
    public Locator getDocumentLocator() {
        return _locator;
    } //-- getDocumentLocator

    public void startDocument() 
        throws org.xml.sax.SAXException 
    {
        
        //-- I've found many application don't always call
        //-- #startDocument, so I usually never put any 
        //-- important logic here
        
    } //-- startDocument
    
    public void startElement(String name, AttributeList atts) 
        throws org.xml.sax.SAXException
    {
        
        //-- handle namespaces
        
        String namespace = null;
        
        if (hasNameSpace(name)) {
            name = getLocalPart(name);
        }
        
        UnmarshalState state = null;
        
        if (_stateInfo.empty()) {
            //-- Initialize since this is the first element
            
            if (_cdResolver == null) {
                
                if (_topClass == null) {
                    String err = "The class for the root element '" +
                        name + "' could not be found.";
                    throw new SAXException(err);
                }
                else 
                    _cdResolver = new ClassDescriptorResolverImpl(_loader);
            }
            
            _topState = new UnmarshalState();
            _topState.elementName = name;
            
            
            XMLClassDescriptor classDesc = null;
            
            //-- If _topClass is null, then we need to search
            //-- the resolver for one
            if (_topClass == null) {
                classDesc = _cdResolver.resolveByXMLName(name, null);
                
                if (classDesc != null) 
                    _topClass = classDesc.getJavaClass();
                    
                if (_topClass == null) {
                    String err = "The class for the root element '" +
                        name + "' could not be found.";
                    throw new SAXException(err);
                }
            }
            
            //-- create a "fake" FieldDescriptor for the root element
            XMLFieldDescriptorImpl fieldDesc
                = new XMLFieldDescriptorImpl(_topClass, 
                                             name, 
                                             name, 
                                             NodeType.Element);
            
            _topState.fieldDesc = fieldDesc;
            
            //-- look for XMLClassDescriptor if null
            if (classDesc == null)
                classDesc = getClassDescriptor(_topClass);
                
            fieldDesc.setClassDescriptor(classDesc);
            
            if (classDesc == null) {
                //-- report error
			    if ((!_topClass.isPrimitive()) &&
                    (!Serializable.class.isAssignableFrom( _topClass )))
                    throw new SAXException(MarshalException.NON_SERIALIZABLE_ERR);
                else {
                    String err = "unable to create XMLClassDescriptor " +
                                 "for class: " + _topClass.getName();
                    throw new SAXException(err);
                }
            }
            _topState.classDesc = classDesc;
            _topState.type = _topClass;

            // Retrieving the xsi:type attribute, if present
            String instanceClassname = getInstanceType(atts);
            if (instanceClassname != null) {
                Class instanceClass = null;
                Object instance = null;
                try {
                    instanceClass = _cdResolver.resolve(instanceClassname)
                        .getJavaClass();
                    if (!_topClass.isAssignableFrom(instanceClass)) {
                        String err = instanceClass + " is not a subclass of "
                            + _topClass;
                        throw new SAXException(err);
                    }
                    
                } catch(Exception ex) {
                    String msg = "unable to instantiate " + 
                        instanceClassname + "; ";
                    throw new SAXException(msg + ex);
                }
                
                //-- try to create instance of the given Class
                try {
                    _topState.object = instanceClass.newInstance();
                }
                catch(Exception ex) {
                    String msg = "unable to instantiate " + 
                        instanceClass.getName() + "; ";
                    throw new SAXException(msg + ex);
                }
                
            } else {
                
                //-- try to create instance of the given Class
                try {
                    _topState.object = _topClass.newInstance();
                }
                catch(Exception ex) {
                    String msg = "unable to instantiate " + 
                        _topClass.getName() + "; ";
                    throw new SAXException(msg + ex);
                }
            }
            _stateInfo.push(_topState);
            processAttributes(atts, classDesc);
            return;
        }
        
        
        //-- get MarshalDescriptor for the given element
            
        UnmarshalState parentState = (UnmarshalState)_stateInfo.peek();
        

        //-- create new state object
        state = new UnmarshalState();
        state.elementName = name;
        _stateInfo.push(state);
        
        //-- make sure we should proceed
        if (parentState.object == null) return;
        
        Class _class = null;
            
        XMLClassDescriptor classDesc 
            = (XMLClassDescriptor)parentState.fieldDesc.getClassDescriptor();

        if (classDesc == null)
           classDesc = getClassDescriptor(parentState.object.getClass());
        
        //-- find Descriptor
        XMLFieldDescriptor[] descriptors = classDesc.getElementDescriptors();
        XMLFieldDescriptor descriptor = null;
        for (int i = 0; i < descriptors.length; i++) {
            //-- check for null here, since I actually ran into this
            //-- problem once
            if (descriptors[i] == null) continue;
            
            if (descriptors[i].matches(name)) {
                descriptor = descriptors[i];
                break;
            }
        }


        /*
          If descriptor is null, we need to handle possible inheritence.
          This can be a slow process...for speed use the match attribute
          of the xml element in the mapping file
        */
        XMLClassDescriptor cdInherited = null;
        if (descriptor == null) {
            cdInherited = _cdResolver.resolveByXMLName(name, null);
            if (cdInherited != null) {
                Class subclass = cdInherited.getJavaClass();
                for (int i = 0; i < descriptors.length; i++) {
                    if (descriptors[i] == null) continue;
                    Class superclass = descriptors[i].getFieldType();
                    if (superclass.isAssignableFrom(subclass)) {
                        descriptor = descriptors[i];
                        break;
                    }
                }
            }
        }

        //-- Find object type and create new Object of that type
        if (descriptor != null) {
            
            state.fieldDesc = descriptor;
            
            /* <update>
            if (!descriptor.getAccessRights().isWritable()) {
                if (debug) {
                    buf.setLength(0);
                    buf.append("The field for element '");
                    buf.append(name);
                    buf.append("' is read-only.");
                    message(buf.toString());
                }
                return;
            }
            */
            
            //-- Find class to instantiate
            //-- check xml names to see if we should look for a more specific
            //-- ClassDescriptor, otherwise just use the one found in the
            //-- descriptor
            classDesc = null;
            if (cdInherited != null) classDesc = cdInherited;
            else if (!name.equals(descriptor.getXMLName()))
                classDesc = _cdResolver.resolveByXMLName(name, null);
            
            if (classDesc == null)
                classDesc = (XMLClassDescriptor)descriptor.getClassDescriptor();
            
            FieldHandler handler = descriptor.getHandler();
            
            /*
            Method creator = descriptor.getCreateMethod();
            if (creator == null) {
            
                _class = descriptor.getFieldType();
                
                if (_class == Object.class) {
                    
                    //-- create class name
                    String cname = MarshalHelper.toJavaName(name,true);
                    
                    //-- use parent to package information
                    String pkg = parentState.object.getClass().getName();
                    int idx = pkg.lastIndexOf('.');
                    if (idx > 0) {
                        pkg = pkg.substring(0,idx);
                        cname = pkg + cname;
                    }
                    mInfo = MarshalHelper.getMarshalInfo(cname);
                    if (mInfo != null) {
                        _class = mInfo.getClassType();
                    }
                }
                
                state.type = _class;
                
                //-- instantiate class
                try {
                    
                    if (_class.isArray())
                        byteArray = (_class.getComponentType() == Byte.TYPE);
                    
                    if ((!_class.isPrimitive()) && (!byteArray)) {
                        state.object = _class.newInstance();
                    }
                }
                catch(java.lang.NoSuchMethodError nsme) {
                    String err = "no default constructor for class: "; 
                    err += className(_class);
                    throw new SAXException(err);
                }
                catch(java.lang.Exception ex) {
                    String err = "unable to instantiate a new type of: ";
                    err += className(_class);
                    throw new SAXException(err);
                }
            }
            
            //-- use creator method to create a new object
            else {
                
            */
            try {
                    
                //-- Get Class type...first use ClassDescriptor,
                //-- since it could be more specific than 
                //-- the FieldDescriptor
                if (classDesc != null) {
                    _class = classDesc.getJavaClass();
                    //-- XXXX this is a hack I know...but we
                    //-- XXXX can't use the handler in this case
                    state.derived = true;
                }
                else
                    _class = descriptor.getFieldType();
                
                // Retrieving the xsi:type attribute, if present
                String instanceType = getInstanceType(atts);
                if (instanceType != null) {
                    Class instanceClass = null;
                    try {
                        
                        XMLClassDescriptor instanceDesc 
                            = _cdResolver.resolve(instanceType);
                        
                        if (instanceDesc != null)
                            instanceClass = instanceDesc.getJavaClass();
                        else
                            instanceClass = loadClass(instanceType, null);
                            
                        if (!_class.isAssignableFrom(instanceClass)) {
                            String err = instanceClass 
                                + " is not a subclass of " + _class;
                            throw new SAXException(err);
                        }
                        _class = instanceClass;
                    } 
                    catch(Exception ex) {
                        String msg = "unable to instantiate " + instanceType;
                        throw new SAXException(msg + "; " + ex);
                    }
                    
                }
                
                //-- Handle support for "Any" type
                if (_class == Object.class) {
                    
                    Class pClass = parentState.classDesc.getJavaClass();
                    ClassLoader loader = pClass.getClassLoader();
                    
                    //-- first look for a descriptor based
                    //-- on the XML name
                    
                    classDesc = _cdResolver.resolveByXMLName(name, loader);
                    
                    //-- if null, create classname, and try resolving
                    if (classDesc == null) {
                        //-- create class name
                        String cname = MarshalHelper.toJavaName(name,true);
                        
                        //-- use parent to get package information
                        String pkg = pClass.getName();
                        int idx = pkg.lastIndexOf('.');
                        if (idx > 0) {
                            pkg = pkg.substring(0,idx);
                            cname = pkg + cname;
                        }
                        classDesc = getClassDescriptor(cname, loader);
                    }
                    
                    if (classDesc != null) {
                        _class = classDesc.getJavaClass();
                    }
                }
                
                boolean byteArray = false;
                if (_class.isArray())
                    byteArray = (_class.getComponentType() == Byte.TYPE);
                    
                //-- check for immutable
                if (isPrimitive(_class) || 
                    descriptor.isImmutable() || 
                    byteArray) 
                {
                    state.object = null; 
                    state.primitiveOrImmutable = true;
                }
                else {
                    //-- XXXX should remove this test once we can
                    //-- XXXX come up with a better solution
                    if (!state.derived) 
                        state.object = handler.newInstance(parentState.object);
                    //-- reassign class in case there is a conflict
                    //-- between descriptor#getFieldType and
                    //-- handler#newInstance...I should hope not, but
                    //-- who knows
                    if (state.object != null) 
                        _class = state.object.getClass();
                    else {
                        try {
                            state.object = _class.newInstance();
                        }
                        catch(java.lang.Exception ex) {
                            String err = "unable to instantiate a new type of: ";
                            err += className(_class);
                            throw new SAXException(err);
                        }
                    }
                }
                state.type = _class;
            }
            catch (java.lang.IllegalStateException ise) {
                message(ise.toString());
            }
            ///////} //-- end if (creator)           
            
            
            //-- At this point we should have a new object, unless
            //-- we are dealing with a primitive type, or a special
            //-- case such as byte[]
            if (classDesc == null) {
                classDesc = getClassDescriptor(_class);
            }
            state.classDesc = classDesc;
            
            if ((state.object == null) && (!state.primitiveOrImmutable))
            {
                String err = "unable to unmarshal: " + name + "\n";
                err += " - unable to instantiate: " + className(_class);
                throw new SAXException(err);
            }
            
            //-- assign object, if incremental

            if (descriptor.isIncremental()) {
                
                if (debug) {
                    buf.setLength(0);
                    buf.append("debug: Processing incrementally for element: ");
                    buf.append(name);
                    message(buf.toString());
                }
                
                try {
                    handler.setValue(parentState.object, state.object);
                }
                catch(java.lang.IllegalStateException ise) {
                    String err = "unable to add \"" + name + "\" to ";
                    err += parentState.fieldDesc.getXMLName();
                    err += " due to the following error: " + ise;
                    throw new SAXException(err);
                }
            }
        }
        else {
            //System.out.println("descriptor is null");
            message("unable to find FieldDescriptor for: " + name);
        }
        
        if (state.object != null)
            processAttributes(atts, classDesc);
        else if ((state.type != null) && (!state.primitiveOrImmutable)) {
            buf.setLength(0);
            buf.append("The current object for element '");
            buf.append(name);
            buf.append("\' is null, ignoring attributes.");
            message(buf.toString());
        }
        
    } //-- void startElement(String, AttributeList) 
    
    
      //-------------------/
     //- Private Methods -/
    //-------------------/
    
    /**
     * Returns the instance type attribute.
     * @return String the value of the xsi:type attribute, or null if there is
     * no xsi:type attribute
     */
    private String getInstanceType(AttributeList atts) {
        String xsiTypeAttribute = atts.getValue("xsi:type");
        if (xsiTypeAttribute != null) {
            if (xsiTypeAttribute.startsWith("java:")) {
                return xsiTypeAttribute.substring(5);
            }
            // Retrieve the type corresponding to the schema name and
            // return it.
            XMLClassDescriptor classDesc = 
                _cdResolver.resolveByXMLName(xsiTypeAttribute, null);
            
            if (classDesc != null) 
                return classDesc.getJavaClass().getName();
        }
        return null;
    } //-- getInstanceType
    
    /**
     * Returns true if the given NCName (element name) is qualified
     * with a namespace prefix
     * @return true if the given NCName is qualified with a namespace
     * prefix
    **/
    private boolean hasNameSpace(String ncName) {
        return (ncName.indexOf(':')>0 );
    } //-- hasNameSpace
    
    /**
     * Returns the local part of the given NCName. The local part is anything
     * following the namespace prefix. If there is no namespace prefix
     * the returned name will be the same as the given name.
     * @return the local part of the given NCName.
    **/
    private String getLocalPart(String ncName) {
        int idx = ncName.indexOf(':');
        if (idx >= 0) return ncName.substring(idx+1);
        return ncName;
    } //-- getLocalPart
    
    
    /**
     * Processes the given attribute list, and attempts to add each 
     * Attribute to the current Object on the stack
    **/
    private void processAttributes
        (AttributeList atts, XMLClassDescriptor classDesc) 
        throws org.xml.sax.SAXException
    {
        
        if (atts == null) {
            
            if ((classDesc != null) 
                && (classDesc.getAttributeDescriptors().length > 0)
                && (debug))
            {
                UnmarshalState state = (UnmarshalState)_stateInfo.peek();
                buf.setLength(0);
                buf.append("warning: the AttributeList for '");
                buf.append(state.elementName);
                buf.append("' is null, but attribute descriptors exist.");
                message(buf.toString());
            }
            return;
        }
        
        UnmarshalState state = (UnmarshalState)_stateInfo.peek();
        Object object = state.object;
        
        if (classDesc == null) {
            classDesc = state.classDesc;
            if (classDesc == null) {
                buf.setLength(0);
                buf.append("No ClassDescriptor for '");
                buf.append(state.elementName);
                buf.append("', cannot process attributes.");
                message(buf.toString());
                return;
            }
        }
        
        
        XMLFieldDescriptor[] descriptors = classDesc.getAttributeDescriptors();
        
        for (int i = 0; i < descriptors.length; i++) {
            
            XMLFieldDescriptor descriptor = descriptors[i];
            
            /* <update>
            if (!descriptor.getAccessRights().isWritable()) {
                if (debug) {
                    buf.setLength(0);
                    buf.append("Attribute '");
                    buf.append(descriptor.getXMLName());
                    buf.append("' is read-only - skipping.");
                    message(buf.toString());
                }
                continue;
            }
            */
            
            String attName = descriptor.getXMLName();
            String attValue = atts.getValue(attName);
            if (attValue == null) {
                //-- error handling
                /*
                if (debug) {
                    buf.setLength(0);
                    buf.append("no attribute value with name \'");
                    buf.append(attName);
                    buf.append("\' exists in element \'");
                    buf.append(state.elementName);
                    buf.append("\'");
                    message(buf.toString());
                }
                */
                
                if (descriptor.isRequired()) {
                    String err = classDesc.getXMLName() + " is missing " +
                        "required attribute: " + attName;
                    if (_locator != null) {
                        err += "\n  - line: " + _locator.getLineNumber() +
                            " column: " + _locator.getColumnNumber();
                    }
                    throw new SAXException(err);
                }
                else continue;
            }
            
            Object value = attValue;
            
            //-- if this is the identity then save id
            if (classDesc.getIdentity() == descriptor) {
                _idReferences.put(attValue, state.object);
                //-- resolve waiting references
                resolveReferences(attValue, state.object);
            }
            else if (descriptor.isReference()) {
                value = _idReferences.get(attValue);
                if (value == null) {
                    //-- save state to resolve later 
                    ReferenceInfo refInfo 
                        = new ReferenceInfo(attValue, object, descriptor);
                    refInfo.next 
                        = (ReferenceInfo)_resolveTable.remove(attValue);
                    _resolveTable.put(attValue, refInfo);
                    continue;
                }
            }
            else {
                //-- check for proper type and do type
                //-- conversion
                Class type = descriptor.getFieldType();
                if (isPrimitive(type))
                    value = MarshalHelper.toPrimitiveObject(type, attValue);
            }
                
            try {
                
                
                FieldHandler handler = descriptor.getHandler();
                
                if (handler != null)
                    handler.setValue(object, value);
            }
            catch(java.lang.IllegalStateException ise) {
                String err = "unable to add attribute \"" + attName + "\" to ";
                err += state.fieldDesc.getXMLName();
                err += "due to the following error: " + ise;
                throw new SAXException(err);
            }
        }
        
    } //-- processAttributes
    
    /**
     * Sends a message to all observers. Currently the only observer is
     * the logger.
     * @param msg the message to send to all message observers
    **/
    private void message(String msg) {
        if (_logWriter != null) {
            _logWriter.println(msg);
            _logWriter.flush();
        }
    } //-- message
    
    /**
     * Finds and returns an XMLClassDescriptor for the given class. If
     * a ClassDescriptor could not be found one will attempt to 
     * be generated. 
     * @param _class the Class to get the ClassDescriptor for
    **/
    private XMLClassDescriptor getClassDescriptor(Class _class) 
        throws SAXException
    {
        if (_class == null) return null;
        if (_class.isArray()) return null;
        
        if (_cdResolver == null) 
            _cdResolver = new ClassDescriptorResolverImpl();
            
        XMLClassDescriptor classDesc = _cdResolver.resolve(_class);
        
        if (classDesc != null) return classDesc;
        
        //-- we couldn't create a ClassDescriptor, check for
        //-- error message
        if (_cdResolver.error()) {
            message(_cdResolver.getErrorMessage());
        }
        else {
            buf.setLength(0);
            buf.append("unable to find or create a ClassDescriptor for class: ");
            buf.append(_class.getName());
            message(buf.toString());
        }
        return classDesc;
    } //-- getMarshalInfo

    
    /**
     * Finds and returns a ClassDescriptor for the given class. If
     * a ClassDescriptor could not be found one will attempt to 
     * be generated. 
     * @param className the name of the class to get the Descriptor for
    **/
    private XMLClassDescriptor getClassDescriptor
        (String className, ClassLoader loader) 
    {
        if (_cdResolver == null) 
            _cdResolver = new ClassDescriptorResolverImpl();
            
        XMLClassDescriptor classDesc 
            = _cdResolver.resolve(className, loader);
        
        if (classDesc != null) return classDesc;
        
        //-- we couldn't create a ClassDescriptor, check for
        //-- error message
        if (_cdResolver.error()) {
            message(_cdResolver.getErrorMessage());
        }
        else {
            buf.setLength(0);
            buf.append("unable to find or create a ClassDescriptor for class: ");
            buf.append(className);
            message(buf.toString());
        }
        return classDesc;
    } //-- getClassDescriptor
    
    
    /**
     * Returns the name of a class, handles array types
     * @return the name of a class, handles array types
    **/
    private String className(Class type) {
        if (type.isArray()) {
            return className(type.getComponentType()) + "[]";
        }
        return type.getName();
    } //-- className
    
    /**
     * Returns true if the given class should be treated as a primitive
     * type
     * @return true if the given class should be treated as a primitive
     * type
    **/
    private boolean isPrimitive(Class type) {
        
        if (type.isPrimitive()) return true;
        
        //-- we treat strings as primitives
        if (type == String.class) return true;
        
        if ((type == Boolean.class)   ||
            (type == Byte.class)      ||
            (type == Character.class) ||
            (type == Double.class)    ||
            (type == Float.class)     ||
            (type == Integer.class)   ||
            (type == Long.class)      ||
            (type == Short.class)) 
            return true;
            
       return false;
       
    } //-- isPrimitive
    
    /**
     * Loads and returns the class with the given class name using the 
     * given loader.
     * @param className the name of the class to load
     * @param loader the ClassLoader to use, this may be null.
    **/
    private Class loadClass(String className, ClassLoader loader) 
        throws ClassNotFoundException
    {
        Class c = null;
        
        //-- use passed in loader
	    if ( loader != null )
		    return loader.loadClass(className);
		//-- use internal loader
		else if (_loader != null)
		    return _loader.loadClass(className);
		//-- no loader available use Class.forName
		return Class.forName(className);
    } //-- loadClass
    
    /**
     * Resolves the current set of waiting references for the given Id
     * @param id the id that references are waiting for
     * @param value, the value of the resolved id
    **/
    private void resolveReferences(String id, Object value) 
        throws org.xml.sax.SAXException
    {
        if ((id == null) || (value == null)) return;
        
        ReferenceInfo refInfo = (ReferenceInfo)_resolveTable.remove(id);
        while (refInfo != null) {
            try {
                FieldHandler handler = refInfo.descriptor.getHandler();
                if (handler != null)
                    handler.setValue(refInfo.target, value);
            }
            catch(java.lang.IllegalStateException ise) {
                        
                String err = "Attempting to resolve an IDREF: " +
                        id + "resulted in the following error: " +
                        ise.toString();
                throw new SAXException(err);
            }
            refInfo = refInfo.next;
        }
    } //-- resolveReferences
    
    /**
     * Internal class used to save state for reference resolving
    **/
    class ReferenceInfo {
        String id = null;
        Object target = null;
        XMLFieldDescriptor descriptor = null;
        ReferenceInfo next = null;
        
        public ReferenceInfo() {
            super();
        }
        
        public ReferenceInfo
            (String id, Object target, XMLFieldDescriptor descriptor) 
        {
            this.id = id;
            this.target = target;
            this.descriptor = descriptor;
        }
    }
    
} //-- Unmarshaller


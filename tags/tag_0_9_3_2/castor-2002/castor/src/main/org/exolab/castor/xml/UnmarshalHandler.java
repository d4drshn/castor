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
import org.exolab.castor.util.List;
import org.exolab.castor.xml.descriptors.StringClassDescriptor;
import org.exolab.castor.xml.util.*;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;

//-- xml related imports
import org.xml.sax.*;
import org.xml.sax.helpers.ParserFactory;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;


/**
 * An unmarshaller to allowing unmarshalling of XML documents to
 * Java Objects. The Class must specify
 * the proper access methods (setters/getters) in order for instances
 * of the Class to be properly unmarshalled.
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public final class UnmarshalHandler extends MarshalFramework
    implements DocumentHandler, ErrorHandler
{


    //----------------------------/
    //- Private Member Variables -/
    //----------------------------/

    private static final Class[]  EMPTY_CLASS_ARGS  = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARGS = new Object[0];
    private static final String   EMPTY_STRING      = "";


    // constants used for namespace declaration.
   /**
    * Attribute name for default namespace declaration
    */
    private static final String   XMLNS             = "xmlns";
   /**
    * Attribute prefix for prefixed namespace declaration
    */
    private final static String XMLNS_PREFIX        = "xmlns:";
    private final static int    XMLNS_PREFIX_LENGTH = XMLNS_PREFIX.length();
    /**
     * The name of the QName type
     */
    private static final String QNAME_NAME = "QName";

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
     * The IDResolver for resolving IDReferences
    **/
    private IDResolverImpl _idResolver = null;

    /**
     * A flag indicating whether or not to perform validation
    **/
    private boolean          _validate     = true;


    private Hashtable _resolveTable = null;

    private ClassLoader _loader = null;

    private static final StringClassDescriptor _stringDescriptor
        = new StringClassDescriptor();

    /**
     * A SAX2ANY unmarshaller in case we are dealing with <any>
     */
     private SAX2ANY _anyUnmarshaller = null;

    /**
     * The any branch depth
     */
    private int _depth = 0;

    /**
     * The AnyNode to add (if any)
     */
     private org.exolab.castor.types.AnyNode _node = null;

     /**
      * The namespace stack
      */
     private Namespaces _namespaces = null;

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
        _idResolver   = new IDResolverImpl();
        _resolveTable = new Hashtable();
        buf           = new StringBuffer();
        _topClass     = _class;
        _namespaces   = new Namespaces();
    } //-- UnmarshalHandler(Class)

    public Object getObject() {
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
     * @see #setLogWriter
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
     * Sets the IDResolver to use when resolving IDREFs for
     * which no associated element may exist in XML document.
     *
     * @param idResolver the IDResolver to use when resolving
     * IDREFs for which no associated element may exist in the
     * XML document.
    **/
    public void setIDResolver(IDResolver idResolver) {
        _idResolver.setResolver(idResolver);
    } //-- idResolver

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
     * By default, validation will be performed.
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
        if (_anyUnmarshaller != null)
           _anyUnmarshaller.characters(ch, start, length);
        else {
             UnmarshalState state = (UnmarshalState)_stateInfo.peek();

             if (state.buffer == null) state.buffer = new StringBuffer();
             state.buffer.append(ch, start, length);
        }
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

        //-- Do delagation if necessary

        if (_anyUnmarshaller != null) {
            _anyUnmarshaller.endElement(name);
            --_depth;
            //we are back to the starting node
            if (_depth == 0) {
               _node = _anyUnmarshaller.getStartingNode();
               _anyUnmarshaller = null;
            }
            else return;
        }

        if (_stateInfo.empty()) {
            throw new SAXException("missing start element: " + name);
        }

        //-- * Begin Namespace Handling
        //-- XXX Note: This code will change when we update the XML event API

         int idx = name.indexOf(':');
         if (idx >= 0) {
             name = name.substring(idx+1);
         }
        //-- * End Namespace Handling

        UnmarshalState state = (UnmarshalState) _stateInfo.pop();

        //-- make sure we have the correct closing tag
        XMLFieldDescriptor descriptor = state.fieldDesc;
        if (!state.elementName.equals(name)) {
            String err = "error in xml, expecting </" + state.elementName;
            err += ">, but received </" + name + "> instead.";
            throw new SAXException(err);
        }

        //-- clean up current Object
        Class type = state.type;

        if ( type == null ) {
            //-- this message will only show up if debug
            //-- is turned on...how should we handle this case?
            //-- should it be a fatal error?
            message("Ignoring " + state.elementName + " no descriptor was found");
            //-- remove current namespace scoping
            _namespaces = _namespaces.getParent();
            return;
        }

        //-- check for special cases
        boolean byteArray = false;

        if (type.isArray())
            byteArray = (type.getComponentType() == Byte.TYPE);

        //-- If we don't have an instance object and the Class type
        //-- is not a primitive or a byte[] we must simply return
        if ((state.object == null) && (!state.primitiveOrImmutable)) {
            //-- remove current namespace scoping
            _namespaces = _namespaces.getParent();
            return;
        }

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
            else state.object = toPrimitiveObject(type,str);
        }

        //-- check for character content
        if ((state.buffer != null) &&
            (state.buffer.length() > 0) &&
            (state.classDesc != null)) {
           XMLFieldDescriptor cdesc = state.classDesc.getContentDescriptor();
            if (cdesc != null) {
               Object value = state.buffer.toString();
               if (isPrimitive(cdesc.getFieldType()))
                  value = toPrimitiveObject(cdesc.getFieldType(), (String)value);

                try {
                    FieldHandler handler = cdesc.getHandler();
                    handler.setValue(state.object, value);
                }
                catch(java.lang.IllegalStateException ise) {
                    String err = "unable to add text content to ";
                    err += descriptor.getXMLName();
                    err += " due to the following error: " + ise;
                    throw new SAXException(err);
                }
           }
           else {
                //-- check for non-whitespace...and report error
                if (!isWhitespace(state.buffer)) {
                    String err = "Illegal Text data found as child of: "
                        + name;
                    err += "\n  value: \"" + state.buffer + "\"";
                    throw new SAXException(err);
                }
           }
        }

        //-- if we are at root....just validate and we are done
         if (_stateInfo.empty()) {
             if (_validate) {
                try {
                    Validator validator = new Validator();
                    validator.validate(state.object, _cdResolver);
                }
                catch(ValidationException vEx) {
                    throw new SAXException(vEx);
                }
            }


            // As we are at the root, we check that we have no unresolved
            // references left.
//             if ( ! _resolveTable.isEmpty()) {
//                 String unresolvedRefs = new String();
//                 for (Enumeration e = _resolveTable.elements(); e.hasMoreElements() ;) {
//                     ReferenceInfo unresolved = (ReferenceInfo)e.nextElement();
//                     unresolvedRefs += unresolved.descriptor.getXMLName() + "=\"" +
//                                       unresolved.id + "\" in element <" +
//                                       ((XMLClassDescriptor)unresolved.descriptor.getContainingClassDescriptor()).getXMLName() + " ...>\n";
//                 }
//                 throw new SAXException("Unable to resolve the following IDREF(s) during unmarshalling:\n" + unresolvedRefs);
//             }

            return;
        }

        //-- Add object to parent if necessary

        if (descriptor.isIncremental()) {
            //-- remove current namespace scoping
           _namespaces = _namespaces.getParent();
           return; //-- already added
        }

        Object val = state.object;

        //--special code for AnyNode handling
        if (_node != null) {
           val = _node;
           _node = null;
        }

        //-- get target object
        state = (UnmarshalState) _stateInfo.peek();

        //-- check to see if we have already read in
        //-- an element of this type
        if (!descriptor.isMultivalued() && (state.container == null)) {
            if (state.isUsed(descriptor)) {

                FieldHandler handler = descriptor.getHandler();

                String err = "element \"" + name;
                err += "\" occurs more than once. (" + descriptor + ")";
                ValidationException vx =
                    new ValidationException(err);
                throw new SAXException(vx);
            }
            state.markAsUsed(descriptor);
        }

        try {
            FieldHandler handler = descriptor.getHandler();
            //check if the value is a QName that needs to
            //be resolved (ns:value -> {URI}value
            String valueType = descriptor.getSchemaType();
            if ((valueType != null) && (valueType.equals(QNAME_NAME)))
                 val = resolveNamespace(val);

            if (state.container == null) {

                handler.setValue(state.object, val);
            }
            else {
                state.ContainerFieldDesc.getHandler().setValue(state.container, val);
                state.container = null;
                state.ContainerFieldDesc = null;
            }
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

         //-- remove current namespace scoping
        _namespaces = _namespaces.getParent();

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

        //-- if we are in an <any> section
        //-- we delegate the event handling
        if (_anyUnmarshaller != null) {
            _depth++;
           _anyUnmarshaller.startElement(name,atts);
           return;
        }

        //-- The namespace of the given element
        String namespace = null;

        //-- Begin Namespace Handling :
        //-- XXX Note: This code will change when we update the XML event API

        _namespaces = _namespaces.createNamespaces();
        processNamespaces(atts);

        String prefix = "";
        int idx = name.indexOf(':');
        if (idx >= 0) {
             prefix = name.substring(0,idx);
             name = name.substring(idx+1);
        }

        namespace = _namespaces.getNamespaceURI(prefix);
        //-- End Namespace Handling

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
                classDesc = _cdResolver.resolveByXMLName(name, namespace, null);

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

                    XMLClassDescriptor xcd =
                        getClassDescriptor(instanceClassname);

                    if (xcd != null)
                        instanceClass = xcd.getJavaClass();

                    if (instanceClass == null) {
                        throw new SAXException("Class not found: " +
                            instanceClassname);
                    }

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
        } //--rootElement


        //-- get MarshalDescriptor for the given element

        UnmarshalState parentState = (UnmarshalState)_stateInfo.peek();
        //-- create new state object
        state = new UnmarshalState();
        state.elementName = name;
        _stateInfo.push(state);

        //-- make sure we should proceed
        if (parentState.object == null) return;

        Class _class = null;

        //-- Find ClassDescriptor for Parent
        XMLClassDescriptor classDesc = parentState.classDesc;
        if (classDesc == null) {
            classDesc = (XMLClassDescriptor)parentState.fieldDesc.getClassDescriptor();
            if (classDesc == null)
                classDesc = getClassDescriptor(parentState.object.getClass());
        }

        //-- Find FieldDescriptor for the element
        //-- we wish to unmarshal
        XMLFieldDescriptor descriptor = null;
        descriptor = classDesc.getFieldDescriptor(name, NodeType.Element);

        /*
          XXXX Search for container, hopefully this is a temporary fix
        */
        if (descriptor == null) {
            descriptor = searchContainers(name, classDesc);
        }

        /* end of container fix */

        /*
          If descriptor is null, we need to handle possible inheritence,
          which might not be described in the current ClassDescriptor.
          This can be a slow process...for speed use the match attribute
          of the xml element in the mapping file. This logic might
          not be completely necessary, and perhaps we should remove it.
        */
        XMLClassDescriptor cdInherited = null;
        if (descriptor == null) {
             Vector inheritanceList = searchInheritance(name, namespace, classDesc, _cdResolver);
             if (inheritanceList.size() != 0) {
                 InheritanceMatch match = (InheritanceMatch)inheritanceList.get(0);
                 descriptor  = match._parentFieldDesc;
                 cdInherited = match._inheritedClassDesc;
             }
        }


        //the field descriptor is still null ->problem
        if (descriptor == null) {
            String msg = "unable to find FieldDescriptor for '" + name;
            msg += "' in ClassDescriptor of " + classDesc.getXMLName();
            //if we have no field descriptor and
            //the class descriptor was introspected
            //just log it
            if (Introspector.introspected(classDesc)) {
               message(msg);
               return;
            }
            //but if we could not find the field descriptor
            //whereas a class descriptor has been provided (using the
            //Source Generator for instance)
            else throw new SAXException(msg);
        }

        //-- Handle Container field (handle container in container too)
        Object object = parentState.object;
        while (descriptor.isContainer()) {

            // Check if the container object has already been instantiated
            Object containerObject = null;
            if (!descriptor.isMultivalued()) {
                FieldHandler handler = descriptor.getHandler();
                containerObject = handler.getValue(object);

                if (containerObject == null) {
                    containerObject = handler.newInstance(object);
                    handler.setValue(object, containerObject);
                }
            }
            else {
                Class containerClass = descriptor.getFieldType();
                try {
                    containerObject = containerClass.newInstance();
                    FieldHandler handler = descriptor.getHandler();
                    handler.setValue(object, containerObject);
                }
                catch(Exception ex) {
                    throw new SAXException(ex);
                }
            }


            XMLClassDescriptor containerClassDesc
                = (XMLClassDescriptor)descriptor.getClassDescriptor();

            if (containerClassDesc == null) {
                Class fieldType = descriptor.getFieldType();
                containerClassDesc = getClassDescriptor(fieldType);
                if (containerClassDesc == null) {
                    throw new SAXException("unable to resolve descriptor for class: " + fieldType);
                }
                if (descriptor instanceof XMLFieldDescriptorImpl) {
                    ((XMLFieldDescriptorImpl)descriptor).setClassDescriptor(containerClassDesc);
                }
            }

            descriptor = containerClassDesc.getFieldDescriptor(name, NodeType.Element);

            if (descriptor == null) {
                String msg = "unable to find field descriptor for '" + name;
                msg += "' in element '" + parentState.elementName + "'.";
                throw new SAXException(msg);
            }
            parentState.container = containerObject;
            parentState.ContainerFieldDesc = descriptor;
            object = containerObject;
        } //-- End Container Support


        //-- Find object type and create new Object of that type

        state.fieldDesc = descriptor;

        /* <update>
            *  we need to add this code back in, to make sure
            *  we have proper access rights.
            *
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
            classDesc = _cdResolver.resolveByXMLName(name, namespace, null);

        if (classDesc == null)
            classDesc = (XMLClassDescriptor)descriptor.getClassDescriptor();

        FieldHandler handler = descriptor.getHandler();
        boolean useHandler = true;

        try {

            //-- Get Class type...first use ClassDescriptor,
            //-- since it could be more specific than
            //-- the FieldDescriptor
            if (classDesc != null) {
                _class = classDesc.getJavaClass();

                //-- XXXX This is a hack I know...but we
                //-- XXXX can't use the handler if the field
                //-- XXXX types are different
                if (descriptor.getFieldType() != _class) {
                    state.derived = true;
                }
            }
            else
                _class = descriptor.getFieldType();

            // Retrieving the xsi:type attribute, if present
            String instanceType = getInstanceType(atts);
            if (instanceType != null) {
                Class instanceClass = null;
                try {

                    XMLClassDescriptor instanceDesc
                        = getClassDescriptor(instanceType, _loader);

                    if (instanceDesc != null) {
                        instanceClass = instanceDesc.getJavaClass();
                        classDesc = instanceDesc;
                    }
                    else
                        instanceClass = loadClass(instanceType, null);
                        //the FieldHandler can be either an XMLFieldHandler
                        //or a FieldHandlerImpl
                        FieldHandler tempHandler = descriptor.getHandler();
                        boolean collection = (tempHandler instanceof FieldHandlerImpl)?
                                             ((FieldHandlerImpl)tempHandler).isCollection():false;
                        if ( (! collection ) &&
                         ! _class.isAssignableFrom(instanceClass)) {
                        String err = instanceClass
                            + " is not a subclass of " + _class;
                        throw new SAXException(err);
                    }
                    _class = instanceClass;
                    useHandler = false;
                }
                catch(Exception ex) {
                    String msg = "unable to instantiate " + instanceType;
                    throw new SAXException(msg + "; " + ex);
                }

            }

            //-- Handle support for "Any" type

            if (_class == Object.class) {
                Class pClass = parentState.type;
                ClassLoader loader = pClass.getClassLoader();
                //-- first look for a descriptor based
                //-- on the XML name
                classDesc = _cdResolver.resolveByXMLName(name, namespace, loader);
                //-- if null, create classname, and try resolving
                String cname = null;
                if (classDesc == null) {
                    //-- create class name
                    cname = JavaNaming.toJavaClassName(name);
                    classDesc = getClassDescriptor(cname, loader);
                }
                //-- if still null, try using parents package
                if (classDesc == null) {
                    //-- use parent to get package information
                    String pkg = pClass.getName();
                    idx = pkg.lastIndexOf('.');
                    if (idx > 0) {
                        pkg = pkg.substring(0,idx+1);
                        cname = pkg + cname;
                        classDesc = getClassDescriptor(cname, loader);
                    }
                }
                if (classDesc != null) {
                    _class = classDesc.getJavaClass();
                    useHandler = false;
                }
                else {
                    //we are dealing with an AnyNode
                    //1- creates a new SAX2ANY handler
                    _anyUnmarshaller = new SAX2ANY();
                    //2- delegates the element handling
                    _anyUnmarshaller.startElement(name, atts);
                    //first element so depth can only be one at this point
                    _depth = 1;
                    state.object = _anyUnmarshaller.getStartingNode();
                    state.type = _class;
                    //don't need to continue
                     return;
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
                if ((!state.derived) && useHandler)
                    state.object = handler.newInstance(parentState.object);
                //-- reassign class in case there is a conflict
                //-- between descriptor#getFieldType and
                //-- handler#newInstance...I should hope not, but
                //-- who knows
                if (state.object != null) {
                    _class = state.object.getClass();
                }
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
            throw new SAXException(ise);
        }


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

     //------------------------------------/
    //- org.xml.sax.ErrorHandler methods -/
    //------------------------------------/

    public void error(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        String err = "Parsing Error : "+exception.getMessage()+'\n'+
                     "Line : "+ exception.getLineNumber() + '\n'+
                     "Column : "+exception.getColumnNumber() + '\n';
        throw new SAXException (err);
    } //-- error

    public void fatalError(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        String err = "Parsing Error : "+exception.getMessage()+'\n'+
                     "Line : "+ exception.getLineNumber() + '\n'+
                     "Column : "+exception.getColumnNumber() + '\n';
        throw new SAXException (err);

    } //-- fatalError


    public void warning(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        String err = "Parsing Error : "+exception.getMessage()+'\n'+
                     "Line : "+ exception.getLineNumber() + '\n'+
                     "Column : "+exception.getColumnNumber() + '\n';
        throw new SAXException (err);

    } //-- warning

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
                _cdResolver.resolveByXMLName(xsiTypeAttribute, null, null);

            if (classDesc != null)
                return classDesc.getJavaClass().getName();
        }
        return null;
    } //-- getInstanceType

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

        //-- First loop through Attribute Descriptors.
        //-- Then, if we have any attributes which
        //-- haven't been processed we can ask
        //-- the XMLClassDescriptor for the FieldDescriptor.

        XMLFieldDescriptor[] descriptors = classDesc.getAttributeDescriptors();

        List processedAtts = new List(atts.getLength());
        for (int i = 0; i < descriptors.length; i++) {

            XMLFieldDescriptor descriptor = descriptors[i];

            String attName = descriptor.getXMLName();
            String attValue = atts.getValue(attName);

            if (attValue != null) processedAtts.add(attName);

            try {
                processAttribute(attName, attValue, descriptor, classDesc, object);
            }
            catch(java.lang.IllegalStateException ise) {
                String err = "unable to add attribute \"" + attName + "\" to ";
                err += state.fieldDesc.getXMLName();
                err += "due to the following error: " + ise;
                throw new SAXException(err);
            }
        }
        //-- loop through remaining attributes if necessary
        //-- this might be useful for prefixed attributes
        int len = atts.getLength();
        if (len != processedAtts.size()) {
            for (int i = 0; i < len; i++) {
                String attName = atts.getName(i);

                //-- Begin Namespace Handling :
                int idx = attName.indexOf(':');
                if (idx >= 0)
                    attName = attName.substring(idx+1,attName.length());
               //-- End Namespace Handling

               if (processedAtts.contains(attName)) continue;
               XMLFieldDescriptor descriptor =
                    classDesc.getFieldDescriptor(attName, NodeType.Attribute);

               if (descriptor == null) continue;

               String attValue = atts.getValue(i);
               try {
                   processAttribute(attName, attValue, descriptor, classDesc, object);
               }
               catch(java.lang.IllegalStateException ise) {
                   String err = "unable to add attribute \"" + attName + "\" to ";
                   err += state.fieldDesc.getXMLName();
                   err += "due to the following error: " + ise;
                   throw new SAXException(err);
               }
            }
        }

    } //-- processAttributes

    /**
     * Processes the given Attribute
    **/
    private void processAttribute
        (String attName, String attValue,
         XMLFieldDescriptor descriptor,
         XMLClassDescriptor classDesc,
         Object parent) throws org.xml.sax.SAXException
    {

        Object value = attValue;
        while (descriptor.isContainer()) {
            FieldHandler handler = descriptor.getHandler();
            Object containerObject = handler.getValue(parent);

            if (containerObject == null) {
                containerObject = handler.newInstance(parent);
                handler.setValue(parent, containerObject);
            }

            ClassDescriptor containerClassDesc = ((XMLFieldDescriptorImpl)descriptor).getClassDescriptor();
            descriptor = ((XMLClassDescriptor)containerClassDesc).getFieldDescriptor(attName, NodeType.Attribute);
            parent = containerObject;
        }

        if (attValue == null) {
             //-- Since many attributes represent primitive
             //-- fields, we add an extra validation check here
             //-- in case the class doesn't have a "has-method".
             if (descriptor.isRequired()) {
                String err = classDesc.getXMLName() + " is missing " +
                    "required attribute: " + attName;
                if (_locator != null) {
                    err += "\n  - line: " + _locator.getLineNumber() +
                        " column: " + _locator.getColumnNumber();
                }
                throw new SAXException(err);
            }
            return;
        }

        //-- if this is the identity then save id
        if (classDesc.getIdentity() == descriptor) {
            _idResolver.bind(attValue, parent);
            //-- resolve waiting references
            resolveReferences(attValue, parent);
        }
        //-- if this is an IDREF(S) then resolve reference(s)
        else if (descriptor.isReference()) {
            if (descriptor.isMultivalued()) {
                StringTokenizer st = new StringTokenizer(attValue);
                while (st.hasMoreTokens()) {
                    processIDREF(st.nextToken(), descriptor, parent);
                }
            }
            else processIDREF(attValue, descriptor, parent);
            //-- object values have been set by processIDREF
            //-- simply return
            return;
        }

        //-- check for proper type and do type
        //-- conversion
        Class type = descriptor.getFieldType();
        if (isPrimitive(type))
            value = toPrimitiveObject(type, attValue);
        //check if the value is a QName that needs to
        //be resolved (ns:value -> {URI}value)
        String valueType = descriptor.getSchemaType();
        if ((valueType != null) && (valueType.equals(QNAME_NAME)))
                value = resolveNamespace(value);
        FieldHandler handler = descriptor.getHandler();

        if (handler != null)
            handler.setValue(parent, value);

    } //-- processAttribute

    /**
     * Processes the given IDREF
     *
     * @param idRef the ID of the object in which to reference
     * @param descriptor the current FieldDescriptor
     * @param parent the current parent object
    **/
    private void processIDREF
        (String idRef, XMLFieldDescriptor descriptor, Object parent)
    {
        Object value = _idResolver.resolve(idRef);
        if (value == null) {
            //-- save state to resolve later
            ReferenceInfo refInfo
                = new ReferenceInfo(idRef, parent, descriptor);
            refInfo.next
                = (ReferenceInfo)_resolveTable.remove(idRef);
            _resolveTable.put(idRef, refInfo);
        }
        else {
            FieldHandler handler = descriptor.getHandler();
            if (handler != null)
                handler.setValue(parent, value);
        }
    } //-- processIDREF

    /**
     * Processes the namespace declarations found in the given attribute list
     *
     * @param atts the AttributeList containing the namespace declarations
    **/
    private void processNamespaces(AttributeList atts) {
        if (atts == null) return;

        for (int i = 0; i < atts.getLength(); i++) {
            String attName = atts.getName(i);
            if (attName.equals(XMLNS)) {
                _namespaces.addNamespace("", atts.getValue(i));
            }
            else if (attName.startsWith(XMLNS_PREFIX)) {
                String prefix = attName.substring(XMLNS_PREFIX_LENGTH);
                _namespaces.addNamespace(prefix, atts.getValue(i));
            }
        }

    } //-- method: processNamespaces

    /**
     * Extracts the prefix and resolves it given the the class descriptor.
     * The resolution will change the prefix:value as {NamespaceURI}value
     */
    private Object resolveNamespace(Object value) {

        if ( (value == null) || !(value instanceof String))
            return value;
        String result = (String)value;
        int idx = result.indexOf(':');
        String prefix = null;
        if (idx > 0) {
            prefix = result.substring(0,idx);
            result = result.substring(idx+1);
        }
        result = "{"+_namespaces.getNamespaceURI(prefix)+"}"+result;
        return result;
    }


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
     * Finds and returns an XMLClassDescriptor for the given class name.
     * If a ClassDescriptor could not be found one will attempt to
     * be generated.
     * @param className the name of the class to find the descriptor for
    **/
    private XMLClassDescriptor getClassDescriptor (String className)
        throws SAXException
    {
        Class type = null;
        try {
            //-- use specified ClassLoader if necessary
		    if (_loader != null) {
		        type = _loader.loadClass(className);
		    }
		    //-- no loader available use Class.forName
		    else type = Class.forName(className);
		}
		catch (ClassNotFoundException cnfe) {
		    return null;
		}
        return getClassDescriptor(type);

    } //-- getClassDescriptor

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


        //-- special case for strings
        if (_class == String.class)
            return _stringDescriptor;

        if (_class.isArray()) return null;
        if (isPrimitive(_class)) return null;

        if (_cdResolver == null)
            _cdResolver = new ClassDescriptorResolverImpl();

        XMLClassDescriptor classDesc = null;


        classDesc = _cdResolver.resolve(_class);

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

        XMLClassDescriptor classDesc = _cdResolver.resolve(className, loader);

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
     * Searches for Container descriptors that match the given element name.
     * This is a patch, which will hopefully be changed at a later date.
     *
     * @param name the element to search for
    **/
    private XMLFieldDescriptor searchContainers
        (String name, XMLClassDescriptor classDesc)
        throws SAXException
    {
         XMLFieldDescriptor[] descriptors = classDesc.getElementDescriptors();

        XMLFieldDescriptor descriptor = null;

        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i] == null) continue;

            XMLFieldDescriptor xfd = descriptors[i];
            if (xfd.isContainer()) {
                XMLClassDescriptor xcd = (XMLClassDescriptor)xfd.getClassDescriptor();

                //-- set class descriptor if necessary
                if (xcd  == null) {
                    xcd = getClassDescriptor(xfd.getFieldType());
                    if (xcd != null) {
                         //-- set class descriptor if necessary
                        if (xfd instanceof XMLFieldDescriptorImpl) {
                            ((XMLFieldDescriptorImpl)xfd).setClassDescriptor(xcd);
                        }
                    }
                } // xcd == null

                //have we found the descriptor?
                if (xfd.matches(name)) {
                    descriptor = xfd;
                    break;
                }
                //is it in this class descriptor?
                else if (xcd.getFieldDescriptor(name, NodeType.Element) != null) {
                    descriptor = xfd;
                    break;
                }
                //or is it contained in a fieldDescriptor?
                else if (searchContainers(name, xcd) != null) {
                     descriptor = xfd;
                     break;
                }
            }
        }
        return descriptor;
    } //-- searchContainers.


    /**
     * Search there is a field descriptor which can accept one of the class
     * descriptor which match the given name and namespace. Return a vector of InheritanceMatch
     */
    public static Vector searchInheritance(String name, String namespace, XMLClassDescriptor classDesc, ClassDescriptorResolver cdResolver) {

        Vector inheritanceList = new Vector();
        XMLFieldDescriptor descriptor  = null;
        ClassDescriptorEnumeration cde = cdResolver.resolveAllByXMLName(name, namespace, null);
        XMLFieldDescriptor[] descriptors = classDesc.getElementDescriptors();
        XMLClassDescriptor cdInherited = null;

        if (cde.hasNext()) {
            while (cde.hasNext() && (descriptor == null)) {
                cdInherited = cde.getNext();
                Class subclass = cdInherited.getJavaClass();
                
                for (int i = 0; i < descriptors.length; i++) {

                    if (descriptors[i] == null) continue;
                    //-- check for inheritence
                    Class superclass = descriptors[i].getFieldType();
                    
                    // It is possible that the superclass is of type object if we use any node.
                    if (superclass.isAssignableFrom(subclass) && (superclass != Object.class)) {
                        descriptor = descriptors[i];
                        inheritanceList.add(new InheritanceMatch(descriptor, cdInherited));
                    }
                }
            }
            //-- reset inherited class descriptor, if necessary
            if (descriptor == null) cdInherited = null;
        }

        return inheritanceList;
    }

    /**
     * Used to store the information when we find a possible inheritance. It
     * store the XMLClassDescriptor of the object to instantiate and the
     * XMLFieldDescriptor of the parent, where the instance of the
     * XMLClassDescriptor will be put.
     */
    public static class InheritanceMatch {

        public XMLFieldDescriptor _parentFieldDesc;
        public XMLClassDescriptor _inheritedClassDesc;

        public InheritanceMatch(XMLFieldDescriptor parentFieldDesc, XMLClassDescriptor inheritedClassDesc) {
            _parentFieldDesc    = parentFieldDesc;
            _inheritedClassDesc = inheritedClassDesc;
        }
    }

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
     * Checks the given StringBuffer to determine if it only
     * contains whitespace.
     *
     * @param sb the StringBuffer to check
     * @return true if the only whitespace characters were
     * found in the given StringBuffer
    **/
    private boolean isWhitespace(StringBuffer sb) {
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            switch (ch) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    break;
                default:
                    return false;
            }
        }
        return true;
    } //-- isWhitespace

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
     * Converts a String to the given primitive object type
     * @param type the class type of the primitive in which
     * to convert the String to
     * @param value the String to convert to a primitive
     * @return the new primitive Object
    **/
    public static Object toPrimitiveObject(Class type, String value) {

        Object primitive = null;

        //-- I tried to order these in the order in which
        //-- (I think) types are used more frequently

        boolean isNull = ((value == null) || (value.length() == 0));

        // int
        if ((type == Integer.TYPE) || (type == Integer.class)) {
            if (isNull)
                primitive = new Integer(0);
            else
                primitive = new Integer(value);
        }
        // boolean
        else if ((type == Boolean.TYPE) || (type == Boolean.class)) {
            if (isNull)
                primitive = new Boolean(false);
            else
				primitive = (value.equals("1") ||
							 value.toLowerCase().equals("true"))
								? Boolean.TRUE : Boolean.FALSE;
        }
        // double
        else if ((type == Double.TYPE) || (type == Double.class)) {
            if (isNull)
                primitive = new Double(0.0);
            else
                primitive = new Double(value);
        }
        // long
        else if ((type == Long.TYPE) || (type == Long.class)) {
            if (isNull)
                primitive = new Long(0);
            else
                primitive = new Long(value);
        }
        // char
        else if (type == Character.TYPE) {
            if (!isNull)
                primitive = new Character(value.charAt(0));
            else
                primitive = new Character('\0');
        }
        // short
        else if ((type == Short.TYPE) || (type == Short.class)) {
            if (isNull)
                primitive = new Short((short)0);
            else
                primitive = new Short(value);
        }
        // float
        else if ((type == Float.TYPE) || (type == Float.class)) {
            if (isNull)
                primitive = new Float((float)0);
            else
                primitive = new Float(value);
        }
        // byte
        else if (type == Byte.TYPE) {
            if (isNull)
                primitive = new Byte((byte)0);
            else
                primitive = new Byte(value);
        }
        // otherwise do nothing
        else
            primitive = value;

        return primitive;
    } //-- toPrimitiveObject

    /**
     * Local IDResolver
    **/
    class IDResolverImpl implements IDResolver {

        private Hashtable  _idReferences = null;
        private IDResolver _idResolver   = null;

        IDResolverImpl() {
        } //-- IDResolverImpl

        void bind(String id, Object obj) {

            if (_idReferences == null)
                _idReferences = new Hashtable();


            _idReferences.put(id, obj);

        } //-- bind

        /**
         * Returns the Object whose id matches the given IDREF,
         * or null if no Object was found.
         * @param idref the IDREF to resolve.
         * @return the Object whose id matches the given IDREF.
        **/
        public Object resolve(String idref) {

            if (_idReferences != null) {
                Object obj = _idReferences.get(idref);
                if (obj != null) return obj;
            }

            if (_idResolver != null) {
                return _idResolver.resolve(idref);
            }
            return null;
        } //-- resolve


        void setResolver(IDResolver idResolver) {
            _idResolver = idResolver;
        }

    } //-- IDResolverImpl

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


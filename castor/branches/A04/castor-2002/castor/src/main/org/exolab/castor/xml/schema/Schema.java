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

package org.exolab.castor.xml.schema;

import org.exolab.castor.xml.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * An XML Schema Definition. This class also contains the Factory methods for
 * creating Top-Level structures.
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class Schema extends Annotated {

    /*
      April 200 Working Draft Namespace
        = "http://www.w3.org/1999/XMLSchema";
    */

    /*
      October 2000 Candidate Release Namespace
        = "http://www.w3.org/2000/10/XMLSchema";
     */

    /**
     * The Namespace supported by the W3C XML Schema
     * Recommendation.
     */
    public static final String DEFAULT_SCHEMA_NS
        = "http://www.w3.org/2001/XMLSchema";


    private static final String NULL_ARGUMENT
        = "A null argument was passed to " +
           Schema.class.getName() + "#";

    /**
     * The SimpleTypesFactory used by this Schema
     */
    private static SimpleTypesFactory simpleTypesFactory
        = new SimpleTypesFactory();

    //--------------------/
    //- Member Variables -/
    //--------------------/

    /**
     * The ID for this Schema
    **/
    private String id       = null;

    private String name     = null;

    private String schemaNS = null;

    /**
     * The targetNamespace
    **/
    private String targetNS = null;

    /**
     * The version information as specified by the version
     * attribute
    **/
    private String version  = null;

    /**
     * The global AttribteGroups for this Schema
    **/
    private Hashtable attributeGroups = null;

    /**
     * The global attributes for this Schema
    **/
    private Hashtable attributes = null;

    /**
     * A list of defined architypes
    **/
    private Hashtable complexTypes = null;


    /**
     * A list of defined SimpleTypes
    **/
    private Hashtable simpleTypes = null;

    /**
     * A list of defined elements
    **/
    private Hashtable elements = null;

    /**
     * A list of defined top-levels groups
     */
    private Hashtable _group = null;
    /**
     * A list of imported schemas
     */
    private Hashtable importedSchemas = null;

    /**
     * A list of  XML Schema files included in this schema
     */
    private Vector includedSchemas = null;

    /**
     * A list of namespaces declared in this schema
     */
    private Hashtable namespaces = null;


    /**
     * Creates a new SchemaDef
    **/
    public Schema() {
        this(DEFAULT_SCHEMA_NS);
    } //-- ScehamDef


    /**
     * Creates a new SchemaDef
    **/
    public Schema(String schemaNS) {
        super();

        attributes      = new Hashtable();
        attributeGroups = new Hashtable();
        complexTypes    = new Hashtable();
        simpleTypes     = new Hashtable();
        elements        = new Hashtable();
        _group          = new Hashtable();
        importedSchemas = new Hashtable();
        includedSchemas = new Vector();
        namespaces      = new Hashtable();

        this.schemaNS = schemaNS;

        init();
    } //-- ScehamDef

    private void init() {

    } //-- init


    /**
     * Adds the given attribute definition to this Schema definition
     *
     * @param attribute the AttributeDecl to add
     * @exception SchemaException if an AttributeDecl
     * already exisits with the same name
    **/
    public void addAttribute(AttributeDecl attribute)
        throws SchemaException
    {
        if (attribute == null) return;

        if (attribute.getSchema() != this) {
            String err = "invalid attempt to add an AttributeDecl which ";
            err += "belongs to a different Schema; " + name;
            throw new SchemaException(err);
        }

        Object obj = attributes.get(name);

        if (obj == attribute) return;

        if (obj != null) {
            String err = "Error attempting to add an AttributeDecl to this " +
                "Schema definition, an AttributeDecl already exists with " +
                "the given name: ";
            throw new SchemaException(err + name);
        }

        attributes.put(name, attribute);

    } //-- addAttribute

    /**
     * Adds the given attribute group definition to this Schema
     * definition.
     *
     * @param attrGroup the AttributeGroupDecl to add
     * @exception SchemaException if an AttributeGroupDecl
     * already exisits with the same name
    **/
    public void addAttributeGroup(AttributeGroupDecl attrGroup)
        throws SchemaException
    {
        if (attrGroup == null) return;

        String name = attrGroup.getName();

        //-- handle namespace prefix, if necessary
        int idx = name.indexOf(':');
        if (idx >= 0)
        {
            String nsPrefix = name.substring(0,idx);
            name = name.substring(idx + 1);
            String ns = (String) namespaces.get(nsPrefix);
            if (ns == null)  {
                String err = "addAttributeGroup: ";
                err += "Namespace prefix not recognized '"+nsPrefix+"'";
                throw new IllegalArgumentException(err);
            }
            if (!ns.equals(targetNS)) {
                String err = "AttributeGroup has different namespace " +
                 "than this Schema definition.";
                throw new IllegalArgumentException(err);
            }
        }

        if (attrGroup.getSchema() != this) {
            String err = "invalid attempt to add an AttributeGroup which ";
            err += "belongs to a different Schema; " + name;
            throw new SchemaException(err);
        }

        Object obj = attributeGroups.get(name);

        if (obj == attrGroup) return;

        if (obj != null) {
            String err = "Error attempting to add an AttributeGroup to this " +
                "Schema definition, an AttributeGroup already exists with " +
                "the given name: ";
            throw new SchemaException(err + name);
        }

        attributeGroups.put(name, attrGroup);

    } //-- addAttributeGroup


    /**
     * Adds the given Complextype definition to this Schema defintion
     * @param complextype the Complextype to add to this Schema
     * @exception SchemaException if the Complextype does not have
     * a name or if another Complextype already exists with the same name
    **/
    public synchronized void addComplexType(ComplexType complexType)
        throws SchemaException
    {

        String name = complexType.getName();

        if (name == null) {
            String err = "a global ComplexType must contain a name.";
            throw new SchemaException(err);
        }
        if (complexType.getSchema() != this) {
            String err = "invalid attempt to add an ComplexType which ";
            err += "belongs to a different Schema; type name: " + name;
            throw new SchemaException(err);
        }
        if (complexTypes.get(name) != null) {
            String err = "a ComplexType already exists with the given name: ";
            throw new SchemaException(err + name);
        }
        complexTypes.put(name, complexType);

    } //-- addComplextype

    /**
     * Adds the given Element declaration to this Schema defintion
     * @param elementDecl the ElementDecl to add to this SchemaDef
     * @exception SchemaException when an ElementDecl already
     * exists with the same name as the given ElementDecl
    **/
    public void addElementDecl(ElementDecl elementDecl)
        throws SchemaException
    {

        String name = elementDecl.getName();

        if (name == null) {
            String err = "an element declaration must contain a name.";
            throw new SchemaException(err);
        }
        if (elements.get(name) != null) {
            String err = "an element declaration already exists with the given name: ";
            throw new SchemaException(err + name);
        }

        elements.put(name, elementDecl);


    } //-- addElementDecl

     /**
     * Adds the given Group declaration to this Schema definition
     * @param group the Group to add to this SchemaDef
     * @exception SchemaException when an Group already
     * exists with the same name as the given ElementDecl
    **/
    public void addModelGroup(ModelGroup group)
        throws SchemaException
    {

        String name = group.getName();

        if (name == null) {
            String err = "a group declaration must contain a name.";
            throw new SchemaException(err);
        }
        if (_group.get(name) != null) {
            String err = "an group declaration already exists with the given name: ";
            throw new SchemaException(err + name);
        }

        _group.put(name, group);
    } //-- addModelGroup

    /**
     * Adds the given Schema definition to this Schema definition as an imported schenma
     * @param schema the Schema to add to this Schema as an imported schema
     * @exception SchemaException if the Schema already exists
     */
    public synchronized void addImportedSchema(Schema schema)
        throws SchemaException
    {
        String targetNamespace = schema.getTargetNamespace();
        if (importedSchemas.get(targetNamespace)!=null)
        {
            String err = "a Schema has already been imported with the given namespace: ";
            throw new SchemaException(err + targetNamespace);
        }
        importedSchemas.put(targetNamespace, schema);
    } //-- addImportedSchema

    /**
     * Adds to the namespaces declared in this Schema
     * @param namespaces the list of namespaces
     */
    public void addNamespace(String prefix, String ns) {
        namespaces.put(prefix, ns);
    } //-- setNamespaces

    /**
     * Adds the given SimpletType definition to this Schema defintion
     * @param simpletype the SimpleType to add to this Schema
     * @exception SchemaException if the SimpleType does not have
     * a name or if another SimpleType already exists with the same name
    **/
    public synchronized void addSimpleType(SimpleType simpleType)
        throws SchemaException
    {

        String name = simpleType.getName();

        if ((name == null) || (name.length() == 0)) {
            String err = "No name found for top-level SimpleType. " +
                " A top-level SimpleType must have a name.";
            throw new SchemaException(err);
        }

        if (simpleType.getSchema() != this) {
            String err = "invalid attempt to add a SimpleType which ";
            err += "belongs to a different Schema; type name: " + name;
            throw new SchemaException(err);
        }
        if (simpleTypes.get(name) != null) {
            String err = "a SimpleType already exists with the given name: ";
            throw new SchemaException(err + name);
        }

        simpleType.setParent(this);
        simpleTypes.put(name, simpleType);

    } //-- addSimpleType



    /**
     * Creates a new ComplexType using this Schema as the owning Schema
     * document. A call to #addComplexType must still be made in order
     * to add the complexType to this Schema.
     * @return the new ComplexType
    **/
    public ComplexType createComplexType() {
        return new ComplexType(this);
    } //-- createComplexType

    /**
     * Creates a new ComplexType using this Schema as the owning Schema
     * document. A call to #addComplexType must still be made in order
     * to add the complexType to this Schema.
     * @param name the name of the ComplexType
     * @return the new ComplexType
    **/
    public ComplexType createComplexType(String name) {
        return new ComplexType(this, name);
    } //-- createComplexType

    /**
     * Creates a new SimpleType using this Schema as the owning Schema
     * document. A call to #addSimpleType must till be made in order
     * to add the SimpleType to this Schema.
     * @param name the name of the SimpleType
     * @param baseName the name of the SimpleType's base type
     * @param derivation the name of the derivation method (""/"list"/"restriction")
     * @return the new SimpleType.
    **/
    public SimpleType createSimpleType(String name, String baseName, String derivation)
    {
        return simpleTypesFactory.createUserSimpleType(this, name, baseName, derivation, true);
    } //-- createSimpleType

    /**
     * Creates a new SimpleType using this Schema as the owning Schema
     * document. A call to #addSimpleType must till be made in order
     * to add the SimpleType to this Schema if the type is to be global.
     * @param name the name of the SimpleType
     * @param baseType the base type of the SimpleType to create
     * @return the new SimpleType.
    **/
    public SimpleType createSimpleType(String name, SimpleType baseType)
    {
        return simpleTypesFactory.createUserSimpleType(this, name, baseType, "restriction");
    } //-- createSimpleType


    /**
     * Returns an Enumeration of all top-level Attribute declarations
     * @return an Enumeration of all top-level Attribute declarations
    **/
    public Enumeration getAttributes() {
        return attributes.elements();
    } //-- getAttributes

    /**
     * Returns the top-level Attribute associated with the given name.
     *
     * @return the Attribute associated with the given name,
     * or null if no Attribute association is found.
    **/
    public AttributeDecl getAttribute(String name) {

        //-- Null?
        if (name == null)  {
            String err = NULL_ARGUMENT + "getAttribute: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }

        //-- Namespace prefix?
        String canonicalName = name;
        String nsprefix = "";
        String ns = targetNS;
        int colon = name.indexOf(':');
        if (colon != -1)
        {
            canonicalName = name.substring(colon + 1);
            nsprefix = name.substring(0,colon);
            ns = (String) namespaces.get(nsprefix);
            if (ns == null)  {
                String err = "getAttribute: ";
                err += "Namespace prefix not recognized '"+name+"'";
                throw new IllegalArgumentException(err);
            }
        }

        if ((ns==null) || (ns.equals(targetNS)) )
            return (AttributeDecl)attributes.get(canonicalName);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null)
                return schema.getAttribute(canonicalName);
        }

        return null;

    } //-- getAttribute


    /**
     * Returns an Enumeration of all top-level AttributeGroup declarations
     * @return an Enumeration of all top-level AttributeGroup declarations
    **/
    public Enumeration getAttributeGroups() {
        return attributeGroups.elements();
    } //-- getAttributeGroups

    /**
     * Returns the AttributeGroup associated with the given name.
     *
     * @return the AttributeGroup associated with the given name,
     * or null if no AttributeGroup association is found.
    **/
    public AttributeGroup getAttributeGroup(String name) {

        //-- Null?
        if (name == null)  {
            String err = NULL_ARGUMENT + "getAttributeGroup: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }

        //-- Namespace prefix?
        String canonicalName = name;
        String nsprefix = "";
        String ns = targetNS;
        int colon = name.indexOf(':');
        if (colon != -1)
        {
            canonicalName = name.substring(colon + 1);
            nsprefix = name.substring(0,colon);
            ns = (String) namespaces.get(nsprefix);
            if (ns == null)  {
                String err = "getAttributeGroup: ";
                err += "Namespace prefix not recognized '"+name+"'";
                throw new IllegalArgumentException(err);
            }
        }

        if ((ns==null) || (ns.equals(targetNS)) )
            return (AttributeGroup)attributeGroups.get(canonicalName);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null)
                return schema.getAttributeGroup(canonicalName);
        }

        return null;

    } //-- getAttributeGroup

    /**
     * Gets a built in type's name given its code.
     */
    public String getBuiltInTypeName(int builtInTypeCode) {
        return simpleTypesFactory.getBuiltInTypeName(builtInTypeCode);
    } //-- getBuiltInTypeName


    /**
     * Returns the ComplexType of associated with the given name
     * @return the ComplexType of associated with the given name, or
     *  null if no ComplexType with the given name was found.
    **/
    public ComplexType getComplexType(String name) {

        //-- Null?
        if (name == null)  {
            String err = NULL_ARGUMENT + "getComplexType: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }

        //-- Namespace prefix?
        String canonicalName = name;
        String nsprefix = "";
        String ns = targetNS;
        int colon = name.indexOf(':');
        if (colon != -1)
        {
            canonicalName = name.substring(colon + 1);
            nsprefix = name.substring(0,colon);
            ns = (String) namespaces.get(nsprefix);
            if (ns == null)  {
                String err = "getComplexType: ";
                err += "Namespace prefix not recognized '"+name+"'";
                throw new IllegalArgumentException(err);
            }
        }

        //-- Get GetComplexType object
        if ((ns==null) || (ns.equals(targetNS)) )
            return (ComplexType)complexTypes.get(canonicalName);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null)
                return schema.getComplexType(canonicalName);
        }

        return null;

    } //-- getComplexType

    /**
     * Returns an Enumeration of all top-level ComplexType declarations
     * @return an Enumeration of all top-level ComplexType declarations
    **/
    public Enumeration getComplexTypes() {
        return complexTypes.elements();
    } //-- getComplextypes

    /**
     * Returns the SimpleType associated with the given name,
     * or null if no such SimpleType exists.
     * @return the SimpleType associated with the given name,
     * or null if no such SimpleType exists.
    **/
    public SimpleType getSimpleType(String name) {

        //-- Null?
        if (name == null)  {
            String err = NULL_ARGUMENT + "getSimpleType: ";
            err += "'name' cannot be null.";
            throw new IllegalArgumentException(err);
        }

        //-- Namespace prefix?
        String canonicalName = name;
        String nsPrefix = "";
        String ns = null;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            canonicalName = name.substring(colon + 1);
            nsPrefix = name.substring(0,colon);
            ns = (String) namespaces.get(nsPrefix);
            if (ns == null)  {
                String err = "getSimpleType: ";
                err += "Namespace prefix not recognised '"+name+"'";
                throw new IllegalArgumentException(err);
            }
        }

        //-- Get SimpleType object
        SimpleType result = null;
        if (ns == null) {
            //-- first try built-in types
            result= simpleTypesFactory.getBuiltInType(name);
            //if we have a built-in type not declared in the good namespace -> Exception
             if ( (result != null) && (namespaces.containsValue(DEFAULT_SCHEMA_NS))) {
                String err = "getSimpleType: the simple type '"+name+
                             "' has not been declared in XML Schema namespace.";
                throw new IllegalArgumentException(err);
            }

            //-- otherwise check user-defined types
            if (result == null)
                result = (SimpleType)simpleTypes.get(name);
        }
        else if (ns.equals(schemaNS))
            result= simpleTypesFactory.getBuiltInType(canonicalName);
        else if (ns.equals(targetNS))
            result = (SimpleType)simpleTypes.get(canonicalName);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null)
                result = schema.getSimpleType(canonicalName);
        }

        //-- Result could be a deferredSimpleType => getType will resolve it
        if (result!=null)
            result= (SimpleType)result.getType();

        return result;
    } //-- getSimpleType

    /**
     * Returns an Enumeration of all SimpleType declarations
     * @return an Enumeration of all SimpleType declarations
    **/
    public Enumeration getSimpleTypes() {
        return simpleTypes.elements();
    } //-- getSimpleTypes

    /**
     * Returns the ElementDecl of associated with the given name
     * @return the ElementDecl of associated with the given name, or
     *  null if no ElementDecl with the given name was found.
    **/
    public ElementDecl getElementDecl(String name) {

        String ns = null;
        if (name == null) {
            String err = NULL_ARGUMENT + "getElementDecl: ";
            err += " 'name' can not be null";
            throw new IllegalArgumentException(err);
        }

        int idx = name.indexOf(':');
        if (idx >= 0)
        {
            String nsPrefix = name.substring(0,idx);
            name = name.substring(idx + 1);
            ns = (String) namespaces.get(nsPrefix);
            if (ns == null)  {
                String err = "getElementDecl: ";
                err += "Namespace prefix not recognized '"+nsPrefix+"'";
                throw new IllegalArgumentException(err);
            }
        }

        if ((ns==null) || (ns.equals(targetNS)) )
            return (ElementDecl)elements.get(name);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null) {
                String warning = "Warning : do not forget to generate the source ";
                warning += "for the schema with this targetNamespace: "+schema.getTargetNamespace();
                System.out.println(warning);
                return schema.getElementDecl(name);
            }
        }

        return null;
    } //--getElementDecl

    /**
     * Returns an Enumeration of all top-level element declarations
     * @return an Enumeration of all top-level element declarations
    **/
    public Enumeration getElementDecls() {
        return elements.elements();
    } //-- getElementDecls

     /**
     * Returns the ModeGroup of associated with the given name
     * @return the ModelGroup of associated with the given name, or
     *  null if no ModelGroup with the given name was found.
    **/
    public ModelGroup getModelGroup(String name) {

        String ns = null;
        if (name == null) {
            String err = NULL_ARGUMENT + "getModelGroup: ";
            err += " 'name' can not be null";
            throw new IllegalArgumentException(err);
        }

        int idx = name.indexOf(':');
        if (idx >= 0)
        {
            String nsPrefix = name.substring(0,idx);
            name = name.substring(idx + 1);
            ns = (String) namespaces.get(nsPrefix);
            if (ns == null)  {
                String err = "getModelGroup: ";
                err += "Namespace prefix not recognized '"+nsPrefix+"'";
                throw new IllegalArgumentException(err);
            }
        }

        if ((ns==null) || (ns.equals(targetNS)) )
            return (ModelGroup)_group.get(name);
        else {
            Schema schema = getImportedSchema(ns);
            if (schema!=null) {
                String warning = "Warning : do not forget to generate the source ";
                warning += "for the schema with this targetNamespace: "+schema.getTargetNamespace();
                System.out.println(warning);
                return schema.getModelGroup(name);
            }
        }

        return null;
    } //--getModelGroup

    /**
     * Returns an Enumeration of all top-level ModelGroup declarations
     * @return an Enumeration of all top-level ModelGroup declarations
    **/
    public Enumeration getModelGroups() {
        return _group.elements();
    } //-- getmodelGroup

    /**
     * Returns the Id for this Schema, as specified by the
     * Id attribute, or null if no Id exists.
     *
     * @return the Id for this Scheam, or null if no Id exists
    **/
    public String getId() {
        return id;
    } //-- getId

    /**
     * Returns an imported schema by it's namespace
     * @return The imported schema
     */
    public Schema getImportedSchema(String ns)
    {
        return (Schema) importedSchemas.get(ns);
    } //-- getImportedSchema

    /**
     * Returns the namespaces declared for this Schema
     * @return the namespaces declared for this Schema
    **/
    public Hashtable getNamespaces() {
        return this.namespaces;
    } //-- getNamespaces


    /**
     * Indicates that the given XML Schema file has been processed via an <xs:include>
     */
    public void addInclude(String include)
    {
        includedSchemas.addElement(include);
    } //-- addInclude

    /**
     * Returns True if the given XML Schema has already been included via <xs:include>
     * @return True if the file specified has already been processed
     */
    public boolean includeProcessed(String includeFile)
    {
        return includedSchemas.contains(includeFile);
    } //-- includeProcessed

    /**
     * Returns the namespace of the XML Schema
     * <BR />
     * Note: This is not the same as targetNamespace. This is
     * the namespace of "XML Schema" itself and not the namespace of the
     * schema that is represented by this object model
     * (see #getTargetNamespace).
     * @return the namespace of the XML Schema
     *
    **/
    public String getSchemaNamespace() {
        return this.schemaNS;
    } //-- getSchemaNamespace

    /**
     * Returns the target namespace for this Schema, or null if no
     * namespace has been defined.
     * @return the target namespace for this Schema, or null if no
     * namespace has been defined
    **/
    public String getTargetNamespace() {
        return this.targetNS;
    } //-- getTargetNamespace


    /**
     * Returns the version information of the XML Schema definition
     * represented by this Schema instance.
     *
     * @return the version information of the XML Schema
     * definition, or null if no version information exists.
    **/
    public String getVersion() {
        return version;
    } //-- getVersion

    /**
     * Returns True if the namespace is known to this schema
     * @param namespace the namespace URL
     * @return True if the namespace was declared in the schema
     */
    public boolean isKnownNamespace(String namespaceURL)
    {
        Enumeration urls = namespaces.elements();
        while(urls.hasMoreElements())
            if (urls.nextElement().equals(namespaceURL))
                return true;
        return false;
    }

    /**
     * Removes the given top level ComplexType from this Schema
     * @param complexType the ComplexType to remove
     * @return true if the complexType has been removed, or
     * false if the complexType wasn't top level or
     * didn't exist in this Schema
    **/
    public boolean removeComplexType(ComplexType complexType) {
        if (complexType.isTopLevel()) {
            if (complexTypes.contains(complexType)) {
                complexTypes.remove(complexType.getName());
                return true;
            }
        }
        return false;
    } //-- removeComplexType

    /**
     * Removes the given top level SimpleType from this Schema
     * @param SimpleType the SimpleType to remove
     * @return true if the SimpleType has been removed, or
     * false if the SimpleType wasn't top level or
     * didn't exist in this Schema
    **/
    public boolean removeSimpleType(SimpleType simpleType) {
        if (simpleTypes.contains(simpleType)) {
            simpleTypes.remove(simpleType.getName());
            return true;
        }
        return false;
    } //-- removeSimpleType

    /**
     * Sets the name of this Schema definition
    **/
    public void setName(String name) {
        this.name = name;
    } //-- setName


    /**
     * Returns the first simple or complex type which name equals TypeName
     */
    public XMLType getType(String typeName)
    {
        XMLType result= getSimpleType(typeName);
        if (result == null)
            result = getComplexType(typeName);
        return result;
    }

    /**
     * Sets the Id for this Schema
     *
     * @param id the Id for this Schema
    **/
    public void setId(String id) {
        this.id = id;
    } //-- setId

    /**
     * Sets the target namespace for this Schema
     * @param targetNamespace the target namespace for this Schema
     * @see <B>&sect; 2.7 XML Schema Part 1: Structures</B>
    **/
    public void setTargetNamespace(String targetNamespace) {
        this.targetNS = targetNamespace;
    } //-- setTargetNamespace

    /**
     * Sets the version information for the XML Schema defintion
     * represented by this Schema instance.
     *
     * @param the version for this XML Schema defination.
    **/
    public void setVersion(String version) {
        this.version = version;
    } //-- setVersion


    /** Gets the type factory, package private */
    static SimpleTypesFactory getTypeFactory() { return simpleTypesFactory; }

    //-------------------------------/
    //- Implementation of Structure -/
    //-------------------------------/

    /**
     * Returns the type of this Schema Structure
     * @return the type of this Schema Structure
    **/
    public short getStructureType() {
        return Structure.SCHEMA;
    } //-- getStructureType

    /**
     * Checks the validity of this Schema defintion.
     * @exception ValidationException when this Schema definition
     * is invalid.
    **/
    public void validate()
        throws ValidationException
    {
        //-- do nothing for now...eventually we need
        //-- to cascade calls to all structure types

    } //-- validate

} //-- Schema



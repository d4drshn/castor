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
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.builder;

import org.exolab.castor.xml.schema.reader.*;
import org.exolab.castor.xml.schema.*;

import org.exolab.javasource.*;
import org.exolab.castor.builder.util.ConsoleDialog;
import org.exolab.castor.util.CommandLineOptions;
import org.exolab.castor.util.Configuration;

import org.xml.sax.*;

import java.io.Reader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.net.URL;

/**
 * A Java Source generation tool which uses XML Schema definitions
 * to create an Object model
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class SourceGenerator {

    /**
     * The application name
    **/
    static final String appName = "Castor";

    /**
     * The application description
    **/
    static final String appDesc = "XML data binder for Java";

    /**
     * The application version
    **/
    static final String version = "0.8.9 (CVS)";

    /**
     * The application URI
    **/
    static final String appURI = "http://castor.exolab.org";

    /**
     * The default code header,
     * please leave "$" and "Id" separated with "+" so that the CVS server
     * does not expand it here.
    **/
    private static final String DEFAULT_HEADER =
        "This class was automatically generated with \n"+"<a href=\"" +
        appURI + "\">" + appName + " " + version +
        "</a>, using an XML Schema.\n$" + "Id"+"$";

    private String lineSeparator = null;
    private JComment header = null;

    private boolean warnOnOverwrite = true;

    /**
     * Determines whether or not to print extra messages
    **/
    private boolean verbose         = false;

    private String  destDir = null;

    /**
     * The field info factory.
     */
    private FieldInfoFactory infoFactory = null;

    /** The source factory.
    */
    private SourceFactory sourceFactory = null;

    private ConsoleDialog dialog = null;

    /**
     * Names of properties used in the configuration file.
     *
     */
    public static class Property
    {
        /**
         * Property specifying whether or not to generate source code
         * for bound properties. Currently all properties will be
         * treated as bound properties if this flag is set to true.
         * A value of 'true' enables bound properties.
         * <pre>
         * org.exolab.castor.builder.boundproperties
         * </pre>
         */
         public static final String BOUND_PROPERTIES =
            "org.exolab.castor.builder.boundproperties";

		/**
         * Property specifying how element's and type's are mapped into a Java
         * class hierarchy by the Source Generator.
         * The value must contain one of the following.
         * 'element' outputs a Java class hierarchy based on element
         * names used in the XML Schema. This is the default.
         * 'type' outputs a Java class hierarchy based on the type
         * information defined in the XML Schema.
         * <pre>
         * org.exolab.castor.builder.javaclassmapping
         * </pre>
         */
        public static final String JavaClassMapping = "org.exolab.castor.builder.javaclassmapping";

		/**
		 * Property listing mapping between XML namespaces and Java packages.
		 */
		public static final String NamespacePackages = "org.exolab.castor.builder.nspackages";

        /**
         * The name of the configuration file.
         * <pre>
         * castor.properties
         * </pre>
         */
        public static final String FileName = "castorbuilder.properties";

        static final String ResourceName = "/org/exolab/castor/builder/castorbuilder.properties";

    } //--Property


    // Some static string definitions
	private static final String ELEMENT_VALUE = "element";
	private static final String TYPE_VALUE = "type";

    private static final int    ELEMENT_BINDING = 0;
    private static final int    TYPE_BINDING    = 1;

    /**
     * The flag indicating which type of binding we are
     * configured for
    **/
    private static int  _bindingType = ELEMENT_BINDING;

    /**
     * bound properties flag
    **/
    private static boolean _boundProperties = false;

	/**
     * The default properties loaded from the configuration file.
     */
    private static Properties _default;

	/**
	 * Namespace URL to Java package mapping
	 */
	private static java.util.Hashtable _nspackages;

    /**
     * Creates a SourceGenerator using the default FieldInfo factory
     */
    public SourceGenerator() {
        this(null); //-- use default factory
    } //-- SourceGenerator

    /**
     * Creates a SourceGenerator using the specific field info Factory.
     * @param infoFactory the FieldInfoFactory to use.
    */
    public SourceGenerator(FieldInfoFactory infoFactory) {
        super();

        dialog = new ConsoleDialog();

        if (infoFactory == null)
            this.infoFactory = new FieldInfoFactory();
        else
            this.infoFactory = infoFactory;

        this.sourceFactory = new SourceFactory(infoFactory);

        header = new JComment(JComment.HEADER_STYLE);
        header.appendComment(DEFAULT_HEADER);

    } //-- SourceGenerator



    /**
     * Creates Java Source code (Object model) for the given XML Schema
     * @param schema the XML schema to generate the Java sources for
     * @param packageName the package for the generated source files
    **/
    public void generateSource(Schema schema, String packageName) {
        SGStateInfo sInfo = new SGStateInfo();
        sInfo.packageName = packageName;
		if(sInfo.packageName==null)
			sInfo.packageName=SourceGenerator.getJavaPackage(schema.getTargetNamespace());

        sInfo.setPromptForOverwrite(warnOnOverwrite);
        sInfo.setVerbose(verbose);

        createClasses(schema, sInfo);
    } //-- generateSource

    /**
     * Creates Java Source code (Object model) for the given XML Schema
     * @param reader the Reader with which to read the XML Schema definition.
     * The caller should close the reader, since thie method will not do so.
     * @param packageName the package for the generated source files
    **/
    public void generateSource(Reader reader, String packageName) {

        //-- get default parser from Configuration
        Parser parser = null;
        try {
	    parser = Configuration.getParser();
        }
        catch(RuntimeException rte) {}
        if (parser == null) {
            System.out.println("fatal error: unable to create SAX parser.");
            return;
        }

        SchemaUnmarshaller schemaUnmarshaller = new SchemaUnmarshaller();
        parser.setDocumentHandler(schemaUnmarshaller);
        parser.setErrorHandler(schemaUnmarshaller);

        try {
            parser.parse(new InputSource(reader));
        }
        catch(java.io.IOException ioe) {
            System.out.println("error reading XML Schema file");
            return;
        }
        catch(org.xml.sax.SAXException sx) {

            Exception except = sx.getException();
            if (except == null) except = sx;

            if (except instanceof SAXParseException) {
                SAXParseException spe = (SAXParseException)except;
                System.out.println("SAXParseException: " + spe);
                System.out.print(" - occured at line ");
                System.out.print(spe.getLineNumber());
                System.out.print(", column ");
                System.out.println(spe.getColumnNumber());
            }
            else except.printStackTrace();
            return;
        }

        Schema schema = schemaUnmarshaller.getSchema();
        generateSource(schema, packageName);

    } //-- generateSource

    /**
     * Creates Java Source code (Object model) for the given XML Schema
     * @param filename the full path to the XML Schema definition
     * @param packageName the package for the generated source files
    **/
    public void generateSource(String filename, String packageName)
        throws java.io.FileNotFoundException
    {

        FileReader reader = new FileReader(new File(filename));
        generateSource(reader, packageName);
        try {
            reader.close();
        }
        catch(java.io.IOException iox) {};


    } //-- generateSource


    /**
     * Returns the version number of this SourceGenerator
     * @return the version number of this SourceGenerator
    **/
    public static String getVersion() {
        return version;
    } //-- getVersion

    public void setSuppressNonFatalWarnings(boolean suppress) {
        warnOnOverwrite = (!suppress);
    } //-- setSuppressNonFatalWarnings

    /**
     * Sets whether or not the source code generator prints
     * additional messages during generating source code
     * @param verbose a boolean, when true indicates to
     * print additional messages
    **/
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    } //-- setVerbose

    /**
     * main class used for command line invocation
     * @param args the String[] consisting of the command line arguments
    **/
    public static void main(String[] args) {


        CommandLineOptions allOptions = new CommandLineOptions();

        //-- filename flag
        allOptions.addFlag("i", "filename", "Sets the input filename");

        //-- package name flag
        allOptions.addFlag("package", "package-name", "Sets the package name", true);

        //-- destination directory
        String desc = "Sets the destination output directory";
        allOptions.addFlag("dest", "dest-dir", desc, true);

        //-- line break flag
        desc = "Sets the line separator style for the desired platform";
        allOptions.addFlag("line-separator", "( unix | mac | win)", desc, true);

        //-- Force flag
        desc = "Suppresses non fatal warnings, such as overwriting files.";
        allOptions.addFlag("f", "", desc, true);

        //-- Help flag
        desc = "Displays this help screen.";
        allOptions.addFlag("h", "", desc, true);

        //-- verbose flag
        desc = "Prints out additional messages when creaing source";
        allOptions.addFlag("verbose", "", desc, true);

        //-- source generator types name flag
        desc = "Sets the source generator types name (SGTypeFactory)";
        allOptions.addFlag("types", "types", desc, true);

        //-- XXX maintained temporarily
        allOptions.addFlag("type-factory", "classname", "", true);

        //-- Process the specified command line options
        Properties options = allOptions.getOptions(args);

        //-- check for help option
        if (options.getProperty("h") != null) {
            PrintWriter pw = new PrintWriter(System.out, true);
            allOptions.printHelp(pw);
            pw.flush();
            return;
        }

        String  schemaFilename  = options.getProperty("i");
        String  packageName     = options.getProperty("package");
        String  lineSepStyle    = options.getProperty("line-separator");
        boolean force           = (options.getProperty("f") != null);
        String  typeFactory     = options.getProperty("types");
        boolean verbose         = (options.getProperty("verbose") != null);

        // -- XXX maintained temporarily
        if (typeFactory == null)
            typeFactory = options.getProperty("type-factory");

        String lineSep = System.getProperty("line.separator");
        if (lineSepStyle != null) {
            if ("win".equals(lineSepStyle)) {
                System.out.println(" - using Windows style line separation.");
                lineSep = "\r\n";
            }
            else if ("unix".equals(lineSepStyle)) {
                System.out.println(" - using UNIX style line separation.");
                lineSep = "\n";
            }
            else if ("mac".equals(lineSepStyle)) {
                System.out.println(" - using Macintosh style line separation.");
                lineSep = "\r";
            }
            else {
                System.out.print("- invalid option for line-separator: ");
                System.out.println(lineSepStyle);
                System.out.println("-- using default line separator for this platform");
            }
        }

        SourceGenerator sgen = null;
        if (typeFactory != null) {
            typeFactory = Configuration.getProperty("org.exolab.castor.builder.type." + typeFactory.toLowerCase(),typeFactory);
            try {
                sgen = new SourceGenerator((FieldInfoFactory)Class.forName(typeFactory).newInstance());
            }
            catch(Exception x) {
                System.out.print("- invalid option for types: ");
                System.out.println(typeFactory);
                System.out.println(x);
                System.out.println("-- using default source generator types");
                sgen = new SourceGenerator(); // default
            }
        }
        else {
            sgen = new SourceGenerator(); // default
        }

        sgen.setDestDir(options.getProperty("dest"));
        sgen.setLineSeparator(lineSep);
        sgen.setSuppressNonFatalWarnings(force);
        sgen.setVerbose(verbose);
        if (force) System.out.println("-- Suppressing non fatal warnings.");

        if (schemaFilename == null) {
            System.out.println(appName);
            allOptions.printUsage(new PrintWriter(System.out));
            return;
        }

        try {
            sgen.generateSource(schemaFilename, packageName);
        }
        catch(java.io.FileNotFoundException fne) {
            System.out.println("unable to open XML schema file");
            return;
        }

    } //-- main

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    /**
     * Sets the line separator to use when printing the source code
     * @param lineSeparator the line separator to use when printing
     * the source code. This method is useful if you are generating
     * source on one platform, but will be compiling the source
     * on a different platform.
     * <BR />
     * <B>Note:</B>This can be any string, so be careful. I recommend
     * either using the default or using one of the following:<BR />
     * <PRE>
     *   windows systems use: "\r\n"
     *   unix systems use: "\n"
     *   mac systems use: "\r"
     * </PRE>
    **/
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    } //-- setLineSeparator

    //-------------------/
    //- Private Methods -/
    //-------------------/

    private void createClasses(Schema schema, SGStateInfo sInfo) {

        Enumeration structures = schema.getElementDecls();

        //-- handle all top-level element declarations
        while (structures.hasMoreElements())
            createClasses((ElementDecl)structures.nextElement(), sInfo);

        //-- handle all top-level complextypes
        structures = schema.getComplexTypes();
        while (structures.hasMoreElements())
            processComplexType((ComplexType)structures.nextElement(), sInfo);

        //-- handle all top-level simpletypes
        structures = schema.getSimpleTypes();
        while (structures.hasMoreElements())
            processSimpleType((SimpleType)structures.nextElement(), sInfo);


    } //-- createClasses

    private void createClasses(ElementDecl elementDecl, SGStateInfo sInfo) {

		//-- when mapping schema types, only interested in producing classes
		//-- for elements with anonymous complex types
		if (SourceGenerator.mappingSchemaType2Java())
			if (elementDecl.isReference() ||
				(elementDecl.getType()!=null &&
				 elementDecl.getType().getName()!=null))
				return;

		if (sInfo.verbose()) {
		    System.out.print("Creating classes for element: ");
		    System.out.println(elementDecl.getName());
		}
        //-- create classes for sub-elements if necessary
        XMLType xmlType = elementDecl.getType();

        //-- No type definition
        if (xmlType == null) {
            System.out.print("Type not found for element: ");
            System.out.println(elementDecl.getName());
            return;
        }
        //-- ComplexType
        else if (xmlType.isComplexType()) {

			JClass jClass = sourceFactory.createSourceCode(elementDecl,
                                                        sInfo,
                                                        sInfo.packageName);

            processComplexType((ComplexType)xmlType, sInfo);

            processJClass(jClass, sInfo);
        }
        //-- SimpleType
        else {
            processSimpleType((SimpleType)xmlType, sInfo);
        }

    }  //-- createClasses

    /**
     * Processes the given ComplexType and creates all necessary class
     * to support it
     * @param complexType the ComplexType to process
    **/
    private void processComplexType(ComplexType complexType, SGStateInfo sInfo) {

        if (complexType == null) return;

        ClassInfo classInfo = sInfo.resolve(complexType);

        if (classInfo == null) {

            //-- handle top-leve complextypes
            if (complexType.isTopLevel()) {

                JClass jClass
                    = sourceFactory.createSourceCode(complexType,
                                                     sInfo,
                                                     sInfo.packageName);
                processJClass(jClass, sInfo);

            }

            //-- process base complextype if necessary
            XMLType baseType= complexType.getBaseType();
            if (baseType != null &&
				baseType.getSchema() == complexType.getSchema()) {

				if (baseType.isComplexType())
                    processComplexType((ComplexType)baseType, sInfo);
            }

            //-- process AttributeDecl
            processAttributes(complexType, sInfo);

            //-- process ContentModel
            processContentModel(complexType, sInfo);

        }
        else {
            JClass jClass = classInfo.getJClass();
            if (!sInfo.processed(jClass)) {
                //-- process AttributeDecl
                processAttributes(complexType, sInfo);
                //-- process ContentModel
                processContentModel(complexType, sInfo);
                processJClass(jClass, sInfo);
            }
        }
    } //-- processComplexType


    private void processSimpleType(SimpleType simpleType, SGStateInfo sInfo) {

        if (simpleType == null) return;

        String packageName = sInfo.packageName;

        //-- Right now the only time we actually
        //-- generate source for a simpletype is
        //-- when it's an enumeration
        //if (! (simpleType instanceof BuiltInType) ) {
        if (simpleType.hasFacet(Facet.ENUMERATION)) {

            ClassInfo classInfo = sInfo.resolve(simpleType);

            if (classInfo == null) {

                JClass jClass
                    = sourceFactory.createSourceCode(simpleType,
                                                     sInfo,
                                                     packageName);

                processJClass(jClass, sInfo);
            }
            else {
                JClass jClass = classInfo.getJClass();
                if (!sInfo.processed(jClass)) {
                    processJClass(jClass, sInfo);
                }
            }
        }
    } //-- processSimpleType

    /**
     * Processes the attribute declarations for the given complex type
     * @param complexType the ComplexType containing the attribute
     * declarations to process.
     * @param sInfo the current source generator state information
    **/
    private void processAttributes(ComplexType complexType, SGStateInfo sInfo) {

        if (complexType == null) return;

        Enumeration enum = complexType.getAttributeDecls();
        while (enum.hasMoreElements()) {
            AttributeDecl attribute = (AttributeDecl)enum.nextElement();
            processSimpleType(attribute.getSimpleType(), sInfo);
        }

    } //-- processAttributes

    /**
     * Processes the given ContentModelGroup
     * @param cmGroup the ContentModelGroup to process
     * @param sInfo the current source generator state information
    **/
    private void processContentModel(ContentModelGroup cmGroup, SGStateInfo sInfo) {


        Enumeration enum = cmGroup.enumerate();

        while (enum.hasMoreElements()) {

            Structure struct = (Structure)enum.nextElement();

            switch(struct.getStructureType()) {
                case Structure.ELEMENT:
                    ElementDecl eDecl = (ElementDecl)struct;
                    if (eDecl.isReference()) continue;
                    createClasses(eDecl, sInfo);
                    break;
                case Structure.GROUP:
                    processContentModel((Group)struct, sInfo);
                    break;
                default:
                    break;
            }
        }
    } //-- process

    /**
     * Processes the given JClass by creating the
     * corresponding MarshalInfo and print the Java classes
     * @param classInfo the classInfo to process
    **/
    private void processJClass(JClass jClass, SGStateInfo state) {


        boolean allowPrinting = true;

        if (state.promptForOverwrite()) {
            String filename = jClass.getFilename(destDir);
            File file = new File(filename);
            if (file.exists()) {
                String message = filename + " already exists. overwrite?";
                allowPrinting = dialog.confirm(message);
            }
        }

        //-- print class
        if (allowPrinting) {
            //hack for the moment
            //to avoid the compiler complaining with java.util.Date
            jClass.removeImport("org.exolab.castor.types.Date");
            jClass.setHeader(header);
            jClass.print(destDir,lineSeparator);
        }

        //-- create MarshalInfo and print

        ClassInfo classInfo = state.resolve(jClass);
        if (classInfo != null) {


            JClass desc
                = DescriptorSourceFactory.createSource(classInfo);

            allowPrinting = true;
            if (state.promptForOverwrite()) {
                String filename = desc.getFilename(destDir);
                File file = new File(filename);
                if (file.exists()) {
                    String message = filename + " already exists. overwrite?";
                    allowPrinting = dialog.confirm(message);
                }
            }

            if (allowPrinting) {
                desc.setHeader(header);
                desc.print(destDir,lineSeparator);
            }
        }

        state.markAsProcessed(jClass);
    } //-- processClassInfo


    /**
     * Returns the default configuration file. Changes to the returned
     * properties set will affect all Castor functions relying on the
     * default configuration.
     *
     * @return The default configuration
     */
    public static synchronized Properties getDefault()
    {
        if ( _default == null ) {
            load();
        }
        return _default;
    }

    /**
     * Returns a property from the default configuration file.
     * Equivalent to calling <tt>getProperty</tt> on the result
     * of {@link #getDefault}.
     *
     * @param name The property name
     * @param default The property's default value
     * @return The property's value
     */
    public static String getProperty( String name, String defValue )
    {
        return getDefault().getProperty( name, defValue );
    }

    /**
     * Returns true if bound properties are enabled.
     *
     * Enabling bound properties is controlled via
     * the org.exolab.castor.builder.boundproperties item
     * in the castorbuilder.properties file. The value is
     * either 'true' or 'false'.
     *
     * @return true if bound properties are enabled.
    **/
    public static boolean boundPropertiesEnabled() {
        getDefault();
        return _boundProperties;
    } //-- boundPropertiesEnabled

	/**
	 * Tests the org.exolab.castor.builder.javaclassmapping property for the 'element' value.
	 * @return True if the Source Generator is mapping schema elements to Java classes.
	 */
	public static boolean mappingSchemaElement2Java()
	{
	    //-- call getDefault to ensure we loaded the properties
	    getDefault();

	    return (_bindingType == ELEMENT_BINDING);
	} //-- mappingSchemaElement2Java

	/**
	 * Tests the org.exolab.castor.builder.javaclassmapping property for the 'type' value.
	 * @return True if the Source Generator is mapping schema types to Java classes.
	 */
	public static boolean mappingSchemaType2Java()
	{
	    //-- call getDefault to ensure we loaded the properties
	    getDefault();

	    return (_bindingType == TYPE_BINDING);
	} //-- mappingSchemaType2Java

	/**
	 * Gets a Java package to an XML namespace URL
	 */
	public static String getJavaPackage(String nsURL)
	{
		//-- Ensure properties have been loaded
		getDefault();

		// Lookup Java package via NS
		String javaPackage = (String) _nspackages.get(nsURL);
		if(javaPackage==null)
			return "";
		else
			return javaPackage;
	}

	/**
	 * Gets the qualified class name given an XML namespace URL
	 */
	public static String getQualifiedClassName(String nsURL, String className)
	{
		//-- Ensure properties have been loaded
		getDefault();

		// Lookup Java package via NS and append class name
		String javaPackage = getJavaPackage(nsURL);
		if (javaPackage.length()>0)
			javaPackage+='.';
		return javaPackage+className;
	}

    /**
     * Called by {@link #getDefault} to load the configuration the
     * first time. Will not complain about inability to load
     * configuration file from one of the default directories, but if
     * it cannot find the JAR's configuration file, will throw a
     * run time exception.
     */
    protected static void load()
    {
		_default = Configuration.loadProperties( Property.ResourceName, Property.FileName);

		// Parse XML namespace and package list
		_nspackages = new Hashtable();
		String prop = _default.getProperty( Property.NamespacePackages, "");
		StringTokenizer tokens = new StringTokenizer(prop, ",");
		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			int comma = token.indexOf('=');
			if(comma==-1)
				continue;
			String ns = token.substring(0,comma).trim();
			String javaPackage = token.substring(comma+1).trim();
			_nspackages.put(ns, javaPackage);
		}

		//-- bound properties
		prop = _default.getProperty( Property.BOUND_PROPERTIES, "");
		_boundProperties = prop.equalsIgnoreCase("true");

		initBindingType();

    } //-- load

    /**
     * Called by #load to initialize the binding type
    **/
    protected static void initBindingType() {
		String prop = getDefault().getProperty( Property.JavaClassMapping,  ELEMENT_VALUE);
		if (prop.toLowerCase().equals(TYPE_VALUE))
		    _bindingType = TYPE_BINDING;
    } //-- initBindingType

} //-- SourceGenerator


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


package org.exolab.castor.jdo;


import java.util.Hashtable;
import java.util.Enumeration;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.ObjectDesc;
import org.exolab.castor.jdo.desc.JDOObjectDesc;
import org.exolab.castor.jdo.mapping.Mapping;
import org.exolab.castor.jdo.mapping.ObjectMapping;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class MappingTable
    implements MappingResolver
{


    private Hashtable  _mapping = new Hashtable();


    private static MappingTable _default = new MappingTable();


    public static MappingTable getDefaultMapping()
    {
	return _default;
    }


    public ObjectDesc getDescriptor( Class type )
    {
	JDOObjectDesc desc;

	desc = (JDOObjectDesc) _mapping.get( type );
	return desc;
    }


    public void addDescriptor( ObjectDesc desc )
    {
	_mapping.put( desc.getObjectType(), desc );
    }


    public Enumeration listDescriptors()
    {
	return _mapping.elements();
    }


    public Enumeration listObjectTypes()
    {
	return _mapping.keys();
    }


    public void addMapping( ObjectMapping objMap )
	throws MappingException
    {
	JDOObjectDesc desc;

	desc = MappingHelper.createObjectDesc( objMap, this );
	addDescriptor( desc );
    }


    public void addMapping( Mapping mapping )
	throws MappingException
    {
	JDOObjectDesc  desc;
	Enumeration enum;

	enum = mapping.listObjects();
	while ( enum.hasMoreElements() ) {
	    desc = MappingHelper.createObjectDesc( (ObjectMapping) enum.nextElement(), this );
	    addDescriptor( desc );
	}
    }


    public void addMapping( MappingTable mapping )
	throws MappingException
    {
	Enumeration enum;

	enum = mapping.listDescriptors();
	while ( enum.hasMoreElements() ) {
	    addDescriptor( (JDOObjectDesc) enum.nextElement() );
	}
    }


}

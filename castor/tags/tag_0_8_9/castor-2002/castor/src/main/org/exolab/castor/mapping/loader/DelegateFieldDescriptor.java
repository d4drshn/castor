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


package org.exolab.castor.mapping.loader;


import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.CollectionHandler;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DelegateFieldDescriptor
    implements FieldDescriptor
{


    /**
     * The actual field descriptor.
     */
    private final FieldDescriptor  _desc;


    protected DelegateFieldDescriptor( FieldDescriptor desc )
    {
        if ( desc == null )
            throw new IllegalArgumentException( "Argument 'desc' is null" );
        _desc = desc;
    }

    /**
     * Set the class which contains this field
     */
     public void setContainingClassDescriptor( ClassDescriptor contClsDesc )
     {
      _desc.setContainingClassDescriptor( contClsDesc );
     }

    /**
     * @return the class which contains this field
     */
    public ClassDescriptor getContainingClassDescriptor()
    {
      return _desc.getContainingClassDescriptor();
    }

    public FieldHandler getHandler()
    {
        return _desc.getHandler();
    }


    public String getFieldName()
    {
        return _desc.getFieldName();
    }


    public Class getFieldType()
    {
        return _desc.getFieldType();
    }


    public boolean isRequired()
    {
        return _desc.isRequired();
    }


    public boolean isImmutable()
    {
        return _desc.isImmutable();
    }


    public boolean isTransient()
    {
        return _desc.isTransient();
    }


    public boolean isMultivalued()
    {
        return _desc.isMultivalued();
    }


    public ClassDescriptor getClassDescriptor()
    {
        return _desc.getClassDescriptor();
    }


}


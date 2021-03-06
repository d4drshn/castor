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
 * Copyright 1999-2000 (C) Intalio Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema.simpletypes;

import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.Schema;

/**
 * Represents the decimal type and those derived from it (integer, short...)
 * The value space is made of the values i / 10^n (I guess i x 10^n is a
 * typo error in the xmlscheme-2 document)
 * n is the scale, it is an interger >=0
 * The other facet is precision (maximum number of digits for i)
 * The facets must verify 0<=scale<=precision
 *
 * @author <a href="mailto:berry@intalio.com">Arnaud Berry</a>
 * @version $Revision:
**/
public class DecimalType extends AtomicType
{
    /** @return result can be null **/
    public Long getPrecision()
    {
        Facet precisionFacet= getFacet(Facet.PRECISION);
        if (precisionFacet == null) return null;

        try
        {
            return new Long(precisionFacet.toLong());
        }
        catch (java.lang.Exception e)
        {
            return null;
        }
    }

    /** @return result can be null **/
    public Long getScale()
    {
        Facet scaleFacet= getFacet(Facet.SCALE);
        if (scaleFacet == null) return null;

        try
        {
            return new Long(scaleFacet.toLong());
        }
        catch (java.lang.Exception e)
        {
            return null;
        }
    }




}





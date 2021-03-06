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

import org.exolab.castor.builder.types.*;
import org.exolab.castor.xml.JavaXMLNaming;
import org.exolab.javasource.*;

import java.util.Vector;

/**
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class SGId extends SGAttrMember {
 
    
    public SGId(String name) {
        super(new XSId(), name);
    } //-- SGId
    
    public JMethod[] createAccessMethods() {
        
        JMethod[] methods = new JMethod[3];
        
        String mname = getName().substring(1);
        JType jType  = getXSType().getJType();
        
        //-- create get method
        methods[0] = new JMethod(jType, "get"+mname);
        JSourceCode jsc = methods[0].getSourceCode();
        jsc.add("return this.");
        jsc.append(getName());
        jsc.append(";");
        
        //-- create set method
        methods[1] = new JMethod(null, "set"+mname);
        methods[1].addParameter(new JParameter(jType, getName()));
        jsc = methods[1].getSourceCode();
        jsc.add("this.");
        jsc.append(getName());
        jsc.append(" = ");
        jsc.append(getName());
        jsc.append(";");
        
        //-- add resolver registration
        //jsc.add("if (idResolver != null) ");
        //jsc.indent();
        //jsc.add("idResolver.addResolvable(");
        //jsc.append(getName());
        //jsc.append(", this);");
        //jsc.unindent();
        
        //-- create getReferenceId (from Referable interface)
        methods[2] = new JMethod(SGTypes.String, "getReferenceId");
        jsc = methods[2].getSourceCode();
        jsc.add("return this.");
        jsc.append(getName());
        jsc.append(";");

        
        return methods;
        
    } //-- createAccessMethods
    
} //-- SGId


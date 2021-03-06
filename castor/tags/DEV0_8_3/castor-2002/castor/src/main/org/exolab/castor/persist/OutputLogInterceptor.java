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


package org.exolab.castor.persist;


import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;
import org.exolab.castor.persist.spi.LogInterceptor;


/**
 * A simple log interceptor that reports all messages to a writer or
 * an output stream.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class OutputLogInterceptor
    implements LogInterceptor
{


    private PrintWriter  _writer;


    public OutputLogInterceptor( PrintWriter writer )
    {
        _writer = writer;
    }


    public OutputLogInterceptor( OutputStream output )
    {
        _writer = new PrintWriter( output );
    }


    public OutputLogInterceptor( Writer writer )
    {
        _writer = new PrintWriter( writer );
    }


    public void loading( Class objClass, Object identity )
    {
        _writer.println( "Castor: Loading " + objClass.getName() + " (" + identity + ")" );
    }


    public void creating( Class objClass, Object identity )
    {
        _writer.println( "Castor: Creating " + objClass.getName() + " (" + identity + ")" );
    }


    public void removing( Class objClass, Object identity )
    {
        _writer.println( "Castor: Removing " + objClass.getName() + " (" + identity + ")" );
    }


    public void storing( Class objClass, Object identity )
    {
        _writer.println( "Castor: Storing " + objClass.getName() + " (" + identity + ")" );
    }


    public void storeStatement( String statement )
    {
        _writer.println( statement );
    }


    public void queryStatement( String statement )
    {
        _writer.println( statement );
    }


    public void message( String message )
    {
        _writer.println( message );
    }


    public void exception( Exception except )
    {
        _writer.println( except );
    }


}


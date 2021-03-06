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


package jdo;


import java.io.IOException;
import java.util.Enumeration;
import org.exolab.castor.jdo.DataObjects;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWTestCategory;
import org.exolab.exceptions.CWClassConstructorException;


/**
 */
public class TypeHandling
    extends CWTestCase
{


    private JDOCategory    _category;


    public TypeHandling( CWTestCategory category )
        throws CWClassConstructorException
    {
        super( "TC05", "Type handling" );
        _category = (JDOCategory) category;
    }


    public void preExecute()
    {
        super.preExecute();
    }


    public void postExecute()
    {
        super.postExecute();
    }


    public boolean run( CWVerboseStream stream )
    {
        boolean result = true;
        Database db;

        try {
            OQLQuery      oql;
            TestTypes     types;
            Enumeration   enum;
            
            // Open transaction in order to perform JDO operations
            db = _category.getDatabase( stream.verbose() );
            
            // Determine if test object exists, if not create it.
            // If it exists, set the name to some predefined value
            // that this test will later override.
            db.begin();
            oql = db.getOQLQuery( "SELECT types FROM jdo.TestTypes types WHERE id = $(integer)1" );
            // This one tests that bind performs type conversion
            oql.bind( TestTypes.DefaultId );
            enum = oql.execute();
            if ( enum.hasMoreElements() ) {
                types = (TestTypes) enum.nextElement();
                stream.writeVerbose( "Updating object: " + types );
            } else {
                types = new TestTypes();
                stream.writeVerbose( "Creating new object: " + types );
                db.create( types );
            }
            db.commit();


            stream.writeVerbose( "Testing date/time conversion" );
            db.begin();
            oql = db.getOQLQuery( "SELECT types FROM jdo.TestTypes types WHERE id = $(double)1" );
            // This one tests that bind performs type conversion
            oql.bind( (double) TestTypes.DefaultId );
            enum = oql.execute();
            if ( enum.hasMoreElements() ) {
                types = (TestTypes) enum.nextElement();
                stream.writeVerbose( "Date type: " + types.getDate().getClass() );
                stream.writeVerbose( "Time type: " + types.getTime().getClass() );
                stream.writeVerbose( "Deleting object: " + types );
                db.remove( types );
            } else {
                stream.writeVerbose( "Error: Could not load types object" );
                result = false; 
            }
            db.commit();
            db.begin();
            types = new TestTypes();
            stream.writeVerbose( "Creating new object: " + types );
            db.create( types );
            db.commit();
            stream.writeVerbose( "OK: Handled date/time types" );


            /*

            stream.writeVerbose( "Testing null in required field" );
            db.begin();
            stream.writeVerbose( "Creating new object: " + object );
            oql = db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
            oql.bind( TestObject.DefaultId );
            enum = oql.execute();
            if ( enum.hasMoreElements() ) {
                object = (TestObject) enum.nextElement();
                object.setValue1( null );
            }
            try {
                db.commit();
                stream.writeVerbose( "OK: Failed to detect validity exception" );
                result = false;
            } catch ( TransactionAbortedException except ) {
                stream.writeVerbose( "OK: Detected validity exception: " + except.getMessage() );
            }
            
            stream.writeVerbose( "Testing null in non-required field" );
            db.begin();
            stream.writeVerbose( "Creating new object: " + object );
            oql = db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
            oql.bind( TestObject.DefaultId );
            enum = oql.execute();
            if ( enum.hasMoreElements() ) {
                object = (TestObject) enum.nextElement();
                object.setValue2( null );
            }
            db.commit();
            stream.writeVerbose( "OK: Stored null in non-required field" );
            */

            db.close();
        } catch ( Exception except ) {
            stream.writeVerbose( "Error: " + except );
            except.printStackTrace();
            result = false;
        }
        return result;
    }


}

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


package org.exolab.castor.jdo.drivers;


import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.util.Messages;

/**
 * IDENTITY key generator.
 * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see IdentityKeyGeneratorFactory
 */
public final class IdentityKeyGenerator implements KeyGenerator
{

    private final int _sqlType;


    private final boolean _isHSQL;


    /**
     * Initialize the IDENTITY key generator.
     */
    public IdentityKeyGenerator( PersistenceFactory factory, int sqlType ) throws MappingException
    {
        String fName = factory.getFactoryName();
        if ( !fName.equals("sybase") && !fName.equals("sql-server") && 
                !fName.equals("hsql")) {
            throw new MappingException( Messages.format( "mapping.keyGenNotCompatible",
                                        getClass().getName(), fName ) );
        }
        _isHSQL = fName.equals("hsql");
        _sqlType = sqlType;
        if ( sqlType != Types.INTEGER && sqlType != Types.NUMERIC && sqlType != Types.DECIMAL)
            throw new MappingException( Messages.format( "mapping.keyGenSQLType",
                                        getClass().getName(), new Integer( sqlType ) ) );
        if ( sqlType != Types.INTEGER && _isHSQL )
            throw new MappingException( Messages.format( "mapping.keyGenSQLType",
                                        getClass().getName(), new Integer( sqlType ) ) );
    }

    /**
     * @param conn An open connection within the given transaction
     * @param tableName The table name
     * @param primKeyName The primary key name
     * @param props A temporary replacement for Principal object
     * @return A new key
     * @throws PersistenceException An error occured talking to persistent
     *  storage
     */
    public Object generateKey( Connection conn, String tableName, String primKeyName,
            Properties props )
            throws PersistenceException
    {
        Statement stmt = null;
        ResultSet rs;

        try {
            if ( _isHSQL ) {
                CallableStatement cstmt;

                cstmt = conn.prepareCall( "{call IDENTITY()}" );
                stmt = cstmt; //  for "finally"
                cstmt.execute();
                rs = cstmt.getResultSet();
            } else {
                stmt = conn.createStatement();
                rs = stmt.executeQuery( "SELECT @@identity");
            }
            if ( rs.next() ) {
                int value;

                value = rs.getInt( 1 );
                if ( _sqlType == Types.INTEGER )
                    return new Integer( value );
                else
                    return new BigDecimal( value );
            } else {
                throw new PersistenceException( Messages.message( "persist.keyGenFailed" ) );
            }
        } catch ( SQLException ex ) {
            throw new PersistenceException( Messages.format(
                    "persist.keyGenSQL", ex.toString() ) );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException ex ) {
                }
            }
        }
    }


    /**
     * Style of key generator: BEFORE_INSERT, DURING_INSERT or AFTER_INSERT ? 
     */
    public final byte getStyle() {
        return AFTER_INSERT;
    }


    /**
     * Gives a possibility to patch the Castor-generated SQL statement
     * for INSERT (makes sense for DURING_INSERT key generators)
     */
    public final String patchSQL( String insert, String primKeyName )
            throws MappingException {
        return insert;
    }

    /**
     * Is key generated in the same connection as INSERT?
     */
    public boolean isInSameConnection() {
        return true;
    }

}

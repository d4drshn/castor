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


package org.exolab.castor.persist.sql;

import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.BitSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.engine.DatabaseImpl;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.persist.Entity;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.LogInterceptor;
import org.exolab.castor.persist.Key;
import org.exolab.castor.persist.Relation;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.TransactionContextListener;
import org.exolab.castor.persist.spi.Connector;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.types.Complex;
import org.exolab.castor.persist.types.SQLTypes;
import org.exolab.castor.util.Messages;

/**
 * A factory to build SQL queries of all kinds (LOOKUP, INSERT, UPDATE, DELETE, SELECT)
 * for SQLEngine.
 * It works it terms of EntityInfo, EntityFieldInfo, Entity.
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see SQLQueryExecutor
 */
public class SQLQueryBuilder implements SQLQueryKinds {

    /**
     * The factory method for creating instances of this class.
     */
    public static SQLQueryExecutor getExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log, SQLEntityInfo info, byte kind, BitSet dirtyCheckNulls, boolean withLock)
            throws PersistenceException {
        QueryExpression query;
        String sql;
        KeyGenerator keyGen = null;

        if (kind == LOOKUP || kind == SELECT) {
            try {
                query = factory.getQueryExpression();
                buildLookup(query, info);
                if (kind == SELECT) {
                    buildSelect(query, info);
                }
                sql = query.getStatement(withLock);
                if (kind == SELECT && log != null) {
                    log.storeStatement("SQL for loading " + info + ":  " + sql);
                }
            } catch (QueryException except) {
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            }
        } else {
            switch (kind) {
            case INSERT:
                // Prepare key generator
                // Assume that it generates the very first identity field.
                if (info.info.keyGen != null) {
                    try {
                        keyGen = info.info.keyGen.getKeyGeneratorRegistry().getKeyGenerator(
                                 factory, info.info.keyGen, info.idInfo[0].sqlType[0], log);
                    } catch (Exception except) {
                        throw new PersistenceException(Messages.format("persist.nested", except), except);
                    }
                }
                sql = buildInsert(factory, info, keyGen);
                if (log != null) {
                    log.storeStatement("SQL for creating " + info + ":  " + sql);
                }
                break;
            case UPDATE:
                sql = buildUpdate(factory, info, dirtyCheckNulls);
                if (log != null) {
                    log.storeStatement("SQL for updating " + info + ":  " + sql);
                }
                break;
            case DELETE:
                sql = buildDelete(factory, info, dirtyCheckNulls);
                if (log != null) {
                    log.storeStatement("SQL for deleting " + info + ":  " + sql);
                }
                break;
            default:
                throw new IllegalStateException("Unknown kind of SQL query: " + kind);
            }
        }
        return new SQLQueryExecutor(factory, connector, log, info, sql, kind, dirtyCheckNulls, keyGen);
    }

    /**
     * This is a convenience method for LOOKUP.
     */
    public static SQLQueryExecutor getLookupExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log,  SQLEntityInfo info, boolean withLock)
            throws PersistenceException {
        return getExecutor(factory, connector, log, info, LOOKUP, null, withLock);
    }

    /**
     * This is a convenience method for SELECT.
     */
    public static SQLQueryExecutor getSelectExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log,  SQLEntityInfo info, boolean withLock)
            throws PersistenceException {
        return getExecutor(factory, connector, log, info, SELECT, null, withLock);
    }

    /**
     * This is a convenience method for INSERT.
     */
    public static SQLQueryExecutor getCreateExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log, SQLEntityInfo info)
            throws PersistenceException {
        return getExecutor(factory, connector, log, info, INSERT, null, false);
    }

    /**
     * This is a convenience method for UPDATE.
     */
    public static SQLQueryExecutor getStoreExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log, SQLEntityInfo info, BitSet dirtyCheckNulls)
            throws PersistenceException {
        return getExecutor(factory, connector, log, info, UPDATE, dirtyCheckNulls, false);
    }

    /**
     * This is a convenience method for DELETE.
     */
    public static SQLQueryExecutor getDeleteExecutor(BaseFactory factory, SQLConnector connector,
            LogInterceptor log, SQLEntityInfo info, BitSet dirtyCheckNulls)
            throws PersistenceException {
        return getExecutor(factory, connector, log, info, DELETE, dirtyCheckNulls, false);
    }

    private static void buildLookup(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        EntityFieldInfo idInfo;
        String entityClass;

        entityClass = info.info.entityClass;
        for (int i = 0; i < info.idInfo.length; i++) {
            idInfo = info.idInfo[i].info;
            for (int j = 0; j < idInfo.fieldNames.length; j++) {
                query.addParameter(entityClass, idInfo.fieldNames[j], QueryExpression.OpEquals);
            }
        }
    }

    private static void buildSelect(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        // super-entities must go before sub-entities
        for (int i = 0; i < info.superEntities.length - 1; i++) {
            buildSelectForOneEntity(query, info.superEntities[i]);
            query.addInnerJoin(info.superEntities[i].info.entityClass, info.superEntities[i].idNames,
                               info.superEntities[i + 1].info.entityClass, info.superEntities[i + 1].idNames);
        }
        buildSelectForSubEntities(query, info);
    }

    /**
     * Build SELECT for this entity and all sub-entities (without super-entities)
     */
    private static void buildSelectForSubEntities(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        buildSelectForOneEntity(query, info);
        if (info.subEntities != null) {
            for (int sub = 0; sub < info.subEntities.length; sub++) {
                query.addOuterJoin(info.info.entityClass, info.idNames,
                                   info.subEntities[sub].info.entityClass, info.subEntities[sub].idNames);
                buildSelectForSubEntities(query, info.subEntities[sub]);
            }
        }
    }

    /**
     * Build SELECT for this entity only
     */
    private static void buildSelectForOneEntity(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        String[] fieldNames;
        String entityClass;

        entityClass = info.info.entityClass;
        for (int i = 0; i < info.idNames.length; i++) {
            query.addColumn(entityClass, info.idNames[i]);
        }
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    query.addColumn(entityClass,  fieldNames[j]);
                }
            }
        }
    }

    private static String buildInsert(BaseFactory factory, SQLEntityInfo info, KeyGenerator keyGen)
            throws PersistenceException {
        int count;
        StringBuffer sql;
        String[] fieldNames;
        String result;

        sql = new StringBuffer(JDBCSyntax.Insert);
        sql.append(factory.quoteName(info.info.entityClass));
        sql.append(" (");
        count = 0;
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    if (count > 0) {
                        sql.append(',');
                    }
                    sql.append(factory.quoteName(fieldNames[j]));
                    count++;
                }
            }
        }
        // Put identity after all ather fields as in other kinds of SQL statements
        for (int i = 0; i < info.idNames.length; i++) {
            if (i == 0 && keyGen != null && keyGen.getStyle() != KeyGenerator.BEFORE_INSERT) {
                // The generated column is not known yet
                continue;
            }
            if (count > 0) {
                sql.append(',');
            }
            sql.append(factory.quoteName(info.idNames[i]));
            count++;
        }
        // it is possible to have no fields in INSERT statement:
        // only the primary key field in the table,
        // with KeyGenerator DURING_INSERT or BEFORE_INSERT
        if (count == 0) {
            sql.setLength(sql.length() - 2); // cut " ("
        } else {
            sql.append(')');
        }
        sql.append(" VALUES (");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sql.append( ',' );
            }
            sql.append('?');
        }
        sql.append(')');
        result = sql.toString();

        if (keyGen != null && keyGen.getStyle() != KeyGenerator.BEFORE_INSERT) {
            try {
                result = keyGen.patchSQL(result, info.idNames[0]);
            } catch (Exception except) {
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            }
            if (keyGen.getStyle() == KeyGenerator.DURING_INSERT) {
                result = JDBCSyntax.Call + result + JDBCSyntax.EndCall;
            }
        }
        return result;
    }

    private static String buildUpdate(BaseFactory factory, SQLEntityInfo info, BitSet dirtyCheckNulls) {
        StringBuffer sql;
        String[] fieldNames;
        int count;

        sql = new StringBuffer(JDBCSyntax.Update);
        sql.append(factory.quoteName(info.info.entityClass));
        sql.append(JDBCSyntax.Set);
        count = 0;
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    if (count > 0) {
                        sql.append(',');
                    }
                    sql.append(factory.quoteName(fieldNames[j]));
                    sql.append("=?");
                    count++;
                }
            }
        }
        addWhere(sql, factory, info, dirtyCheckNulls);
        return sql.toString();
    }

    private static String buildDelete(BaseFactory factory, SQLEntityInfo info, BitSet dirtyCheckNulls) {
        StringBuffer sql;

        sql = new StringBuffer(JDBCSyntax.Delete);
        sql.append(factory.quoteName(info.info.entityClass));
        addWhere(sql, factory, info, dirtyCheckNulls);
        return sql.toString();
    }

    private static void addWhere(StringBuffer sql, BaseFactory factory, SQLEntityInfo info, BitSet dirtyCheckNulls) {
        String[] fieldNames;

        sql.append(JDBCSyntax.Where);
        for (int i = 0; i < info.idNames.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(factory.quoteName(info.idNames[i]));
            sql.append("=?");
        }
        if (dirtyCheckNulls != null) {
            for (int i = 0; i < info.fieldInfo.length; i++) {
                if (info.fieldInfo[i] != null && info.fieldInfo[i].info.dirtyCheck) {
                    fieldNames = info.fieldInfo[i].info.fieldNames;
                    for (int j = 0; j < fieldNames.length; j++) {
                        sql.append(" AND ");
                        sql.append(factory.quoteName(fieldNames[j]));
                        if (dirtyCheckNulls.get(i)) {
                            sql.append(" IS NULL");
                        } else {
                            sql.append("=?");
                        }
                    }
                }
            }
        }
    }
}

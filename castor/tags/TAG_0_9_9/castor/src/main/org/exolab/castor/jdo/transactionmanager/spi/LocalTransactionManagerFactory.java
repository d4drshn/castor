/*
 * Copyright 2005 Bruce Snyder, Werner Guttmann, Ralf Joachim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exolab.castor.jdo.transactionmanager.spi;

import javax.transaction.TransactionManager;

import org.exolab.castor.jdo.conf.JdoConf;
import org.exolab.castor.jdo.transactionmanager.TransactionManagerAcquireException;
import org.exolab.castor.jdo.transactionmanager.TransactionManagerFactory;

/**
 * Default transaction manager when Castor is used in standalone mode,
 * in other words not within a J2EE container.
 *  
 * @author <a href="mailto:ferret AT frii DOT com">Bruce Snyder</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href=" mailto:ralf.joachim@syscon-world.de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public final class LocalTransactionManagerFactory implements TransactionManagerFactory {
    //--------------------------------------------------------------------------

    /** The name of the factory. */
    public static final String NAME = "local";
    
    /** The dummy transaction manager that always will be returned. */
    public static final TransactionManager MANAGER = new LocalTransactionManager();
    
    //--------------------------------------------------------------------------

    /**
     * @see org.exolab.castor.jdo.transactionmanager.TransactionManagerFactory#getName()
     */
    public String getName() { return NAME; }

    /**
     * @see org.exolab.castor.jdo.transactionmanager.TransactionManagerFactory
     *      #getTransactionManager(org.exolab.castor.jdo.conf.JdoConf)
     */
    public TransactionManager getTransactionManager(final JdoConf jdoConf)
    throws TransactionManagerAcquireException {
        return MANAGER;
    }
    
    //--------------------------------------------------------------------------
}

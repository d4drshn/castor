package org.castor.jpa.spi;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.castor.jdo.conf.Mapping;
import org.castor.jdo.jpa.info.JPACallbackHandler;
import org.castor.cpa.util.JDOClassDescriptorResolver;
import org.castor.cpa.util.JDOClassDescriptorResolverImpl;
import org.castor.jdo.conf.Database;
import org.castor.jdo.conf.JdoConf;
import org.castor.jdo.util.JDOConfFactory;
import org.castor.jpa.CastorEntityManagerFactory;
import org.castor.jpa.PersistenceLoader;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.mapping.MappingException;
import org.xml.sax.InputSource;

/**
 * Castor JDO-specific JPA PersistenceProvider.
 * 
 * @see javax.persistence.PersistenceProvider
 * @author me
 * @since 1.0
 */
public class CastorPersistenceProvider implements PersistenceProvider {

	public static final String PROVIDER_NAME = "org.castor.jdo.PersistenceProvider";

	private static final String DEFAULT_CONFIGURATION_FILE_NAME = "META-INF/jdo-conf.xml";

	/**
	 * @inheritDoc
	 * @see javax.persistence.spi.PersistenceProvider#createEntityManagerFactory(java.lang.String,
	 *      java.util.Map)
	 */
	public EntityManagerFactory createEntityManagerFactory(final String emName,
			final Map map) {
		// TODO Refine code to deal with 'persistence.xml'
		URL resourceLocation = getClass().getClassLoader().getResource(
				"META-INF/persistence.xml");

		PersistenceLoader persistenceLoader = new PersistenceLoader();
		List<PersistenceUnitInfo> persistenceGroups = persistenceLoader
				.loadPersistence(resourceLocation);

		PersistenceUnitInfo namedPersistenceGroup = null;

		for (int i = 0; i < persistenceGroups.size(); i++) {
			PersistenceUnitInfo persistenceGroup = persistenceGroups.get(i);
			if (persistenceGroup.getPersistenceUnitName().equals(emName)) {
				namedPersistenceGroup = persistenceGroup;
			}
		}

		EntityManagerFactory factory = null;

		if (namedPersistenceGroup != null) {
			factory = createEntityManagerFactory(namedPersistenceGroup, map);
		}

		return factory;

	}

	/**
	 * Creates an {@link javax.persistence.EntityManagerFactory} instance from a
	 * given {@link javax.persistence.PersistenceUnitInfo} instance.
	 * 
	 * @param info
	 *            A given {@link javax.persistence.PersistenceUnitInfo} instance
	 * @param map
	 *            An instance of confiugration parameters stored in a map
	 * @return An {@link javax.persistence.EntityManagerFactory} instance
	 */
	private EntityManagerFactory createEntityManagerFactory(
			PersistenceUnitInfo info, final Map map) {
		EntityManagerFactory factory = null;

		JdoConf jdoConf = loadJDOConfiguration();

		Database database = getDatabase(jdoConf.getDatabase(), info
				.getPersistenceUnitName());
		// TODO: Investigate, why wrong mapping is being loaded, if not cleared.
		database.removeAllMapping();

		JDOClassDescriptorResolver resolver = new JDOClassDescriptorResolverImpl();

		for (String className : info.getManagedClassNames()) {
			try {
				resolver.addClass(Class.forName(className));
			} catch (ClassNotFoundException e) {
				throw new PersistenceException(String.format(
						"Problem adding class `%s` to resolver.", className
				));
			}
		}

		for (String mappingFile : info.getMappingFileNames()) {
                        final Mapping mapping = new Mapping();
			mapping.setHref(mappingFile);
			database.addMapping(mapping);
		}
		
		try {
			final ClassLoader classLoader = getClass().getClassLoader();
			final InputSource source = new InputSource(classLoader.getResource(
					DEFAULT_CONFIGURATION_FILE_NAME).toExternalForm());
			JDOManager.loadConfiguration(source, null, classLoader, resolver);
			JDOManager jdoManager = JDOManager.createInstance(info
					.getPersistenceUnitName());
			jdoManager.setCallbackInterceptor(new JPACallbackHandler());
			factory = new CastorEntityManagerFactory(jdoManager);
		} catch (MappingException e) {
			// TODO !!!!!!!!!!!!!!!! Investigate exception type
			throw new PersistenceException(
					"Problem loading Castor JDO configuration", e);
		}

		return factory;
	}

	/**
	 * Load the Castor JDO configuration from a default location, i.e.
	 * META-INF/jdo-conf.xml.
	 * 
	 * @return The Castor JDO configuration.
	 */
	private JdoConf loadJDOConfiguration() {
		// we always assume that there's a META-INF/jdo-conf.xml file
		String configurationFileName = DEFAULT_CONFIGURATION_FILE_NAME;
		String configurationURL = getClass().getClassLoader().getResource(
				configurationFileName).toExternalForm();
		if (configurationURL == null) {
			throw new IllegalArgumentException("Cannot find resource for "
					+ configurationFileName);
		}

		JdoConf jdoConf;
		try {
			jdoConf = JDOConfFactory.createJdoConf(new InputSource(
					configurationURL), null, getClass().getClassLoader());
		} catch (MappingException e) {
			throw new PersistenceException(
					"Problem obtaining Castor JDO configuration file", e);
		}
		return jdoConf;
	}

	/**
	 * Returns the first {@link Database} instance for the given database name
	 * that was found.
	 * 
	 * @param databases
	 *            {@link org.castor.jdo.conf.Database} instances
	 * @param name
	 *            the name of the requested {@link Database}.
	 * @return A {@link Database} instance or <code>null</code> if not present.
	 */
	private Database getDatabase(Database[] databases, String name) {
		for (Database database : databases) {
			if (name.equals(database.getName())) {
				return database;
			}
		}
		return null;
	}

	/**
	 * @inheritDoc
	 * @see javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory(javax.persistence.spi.PersistenceUnitInfo)
	 */
	public EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo info) {
		return createContainerEntityManagerFactory(info, null);
	}

	public EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo info, Map map) {
		return createEntityManagerFactory(info, map);
	}

}

package org.example.serenitytherapycenterorm.config;

import org.example.serenitytherapycenterorm.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        try {
            Configuration configuration = new Configuration();

            Properties properties = new Properties();
            InputStream inputStream = FactoryConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream("hibernate.properties");

            if (inputStream == null) {
                throw new RuntimeException("hibernate.properties file not found in resources folder!");
            }

            properties.load(inputStream);

            configuration.setProperties(properties);

            configuration.addAnnotatedClass(User.class);

            sessionFactory = configuration.buildSessionFactory();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load hibernate.properties configuration!", e);
        }
    }

    public static FactoryConfiguration getInstance() {
        if (factoryConfiguration == null) {
            factoryConfiguration = new FactoryConfiguration();
        }
        return factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

}

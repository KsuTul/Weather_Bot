package com.example.weatherbot.config;

import com.example.weatherbot.domain.Bot;
import com.example.weatherbot.parser.CommandParser;
import com.example.weatherbot.services.BotService;
import com.example.weatherbot.services.KeyboardService;
import com.example.weatherbot.services.WeatherService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;


@Configuration
@ComponentScan("com.example.weatherbot")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.weatherbot", entityManagerFactoryRef = "entityManagerFactory")
@PropertySource(value = "classpath:application.properties")
public class BotConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("com.example.weatherbot");
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(additionalProperties());

        return entityManagerFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        return properties;
    }
    @Bean
    public WeatherService weatherService() {
        return new WeatherService(Logger.getLogger(WeatherService.class));
    }

    @Bean
    public KeyboardService keyboardService(){
        return new KeyboardService(new InlineKeyboardMarkup());
    }

    @Bean
    public BotService botService() {
        return new BotService(weatherService(), commandParser(), keyboardService());
    }

    @Bean
    public Bot bot() {
        return new Bot(botService(),  "weather_good_weather_bot", "5225586059:AAGyba5EqveyzV6g0LLpyefD6Egkla4JmX4");
    }

    @Bean
    public CommandParser commandParser() {
        return new CommandParser(weatherService());
    }

}

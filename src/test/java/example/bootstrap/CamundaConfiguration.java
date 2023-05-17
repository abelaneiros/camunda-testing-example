package example.bootstrap;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class CamundaConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CamundaConfiguration.class);

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=1000");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        final SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(dataSource);
        config.setDatabaseSchemaUpdate("true");
        config.setExpressionManager(new SpringExpressionManager(applicationContext, null));
        config.setTransactionManager(new DataSourceTransactionManager(dataSource));
        config.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
        config.setJobExecutorActivate(false);
        config.setIdGenerator(new StrongUuidGenerator());
        config.setTelemetryReporterActivate(false);
        config.setDbMetricsReporterActivate(false);

        return config;
    }

    @Bean
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        final ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean
    public JavaDelegate callDelegate1() {
        return (execution) -> {
            logger.info(">>> executing callDelegate1");
        };
    }

    @Bean
    public JavaDelegate callDelegate2() {
        return (execution) -> {
            logger.info(">>> executing callDelegate2");
        };
    }

    @Bean
    public JavaDelegate callDelegate3() {
        return (execution) -> {
            logger.info(">>> executing callDelegate3");
        };
    }

    @Bean
    public JavaDelegate callDelegate4() {
        return (execution) -> {
            logger.info(">>> executing callDelegate4. I'm a delegate that lives inside CamundaSubProcess2!!!");
        };
    }
}

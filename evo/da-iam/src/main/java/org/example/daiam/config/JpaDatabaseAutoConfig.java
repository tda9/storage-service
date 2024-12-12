package org.example.daiam.config;

import org.example.daiam.repo.custom.CustomInspector;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

@Configuration
@EnableJpaRepositories("com.**")
@EnableTransactionManagement
public class JpaDatabaseAutoConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.statement_inspector", safeSqlInterceptor());
    }
    private CustomInspector safeSqlInterceptor() {
        return new CustomInspector();
    }

}

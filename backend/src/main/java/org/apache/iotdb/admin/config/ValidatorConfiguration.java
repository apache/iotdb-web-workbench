package org.apache.iotdb.admin.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/** 校验框架配置类 */
@Configuration
public class ValidatorConfiguration {

  @Bean("validator")
  public Validator getValidatorFactory() {
    ValidatorFactory validatorFactory =
        Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
    return validatorFactory.getValidator();
  }
}

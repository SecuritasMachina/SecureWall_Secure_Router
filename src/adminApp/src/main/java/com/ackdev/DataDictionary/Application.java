package com.ackdev.DataDictionary;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class Application extends SpringBootServletInitializer  {
	private static Logger log = Logger.getLogger(Application.class);
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

//    @Bean(destroyMethod = "destroy")
//    public ThreadLocalTargetSource threadLocalTenantStore() {
//      ThreadLocalTargetSource result = new ThreadLocalTargetSource();
//      result.setTargetBeanName("requestStore");
//      log.info("destroy ThreadLocalTargetSource");
//      return result;
//    }
//    
//    @Primary
//    @Bean(name = "proxiedThreadLocalTargetSource")
//    public ProxyFactoryBean proxiedThreadLocalTargetSource(ThreadLocalTargetSource threadLocalTargetSource) {
//      ProxyFactoryBean result = new ProxyFactoryBean();
//      result.setTargetSource(threadLocalTargetSource);
//      log.info("ProxyFactoryBean");
//      return result;
//    }
//
//    @Bean(name = "requestStore")
//    @Scope(scopeName = "prototype")
//    public RequestStore requestStore() {
//    	log.info("prototype RequestStore requestStore");
//      return new RequestStore();
//    }
}

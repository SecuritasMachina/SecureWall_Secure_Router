package com.ackdev.DataDictionary;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TenantFilter implements Filter {
	@Autowired
	private RequestStore requestStore;
	
	private Logger log = Logger.getLogger(TenantFilter.class);
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		try {
			log.info("* TenantFilter *");
			this.requestStore.setRequest(request);
			chain.doFilter(servletRequest, servletResponse);
		} finally {
			// Otherwise when a previously used container thread is used, it will have the
			// old tenant id set and
			// if for some reason this filter is skipped, tenantStore will hold an
			// unreliable value
			this.requestStore.clear();
		}
	}
}

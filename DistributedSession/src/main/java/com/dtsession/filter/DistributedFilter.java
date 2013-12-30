package com.dtsession.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ds.session.SessionManager;
import com.ds.session.ZooSessionManager;
import com.ds.session.wrapper.DTRequestWrapper;

/**
 * Servlet Filter implementation class distributedFilter.
 */
@WebFilter(filterName = "AuthenticateFilter", urlPatterns = { "/Login", "/getquote" })
public class DistributedFilter implements Filter {

	/**init SessionManager.*/
    private SessionManager manager = new ZooSessionManager();
    /**
     * Default constructor. 
     */
    public DistributedFilter() {
        // TODO Auto-generated constructor stub
        
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	    manager.close();
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 * @param request ServletRequest
	 * @param response ServletResponse
	 * @param chain FilterChain
	 * @throws IOException  IOException
	 * @throws ServletException ServletException
	 */
	public void doFilter(ServletRequest request, 
			ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		// pass the request along the filter chain
	    DTRequestWrapper wrapper = new DTRequestWrapper(
                (HttpServletRequest) request, (HttpServletResponse) response, manager);
		chain.doFilter(wrapper, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 * @param filterConfig FilterConfig
	 * @throws ServletException ServletException
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	    manager.setServletContext(filterConfig.getServletContext());
	}

}

package br.com.maia.filtro;

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
import javax.servlet.http.HttpSession;

import br.com.maia.util.PoolString;

@WebFilter(filterName ="contreleAcesso", urlPatterns={"*.jsf"} )
public class FiltroAcesso implements Filter {

	

	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = ( HttpServletRequest ) request ;
		HttpSession session = req.getSession(false);
		
		if ( session != null && ( session.getAttribute(PoolString.USUARIO_VAR) != null )
		    || req.getRequestURI().endsWith(PoolString.LOGIN_JSF)) {
		   
			chain.doFilter ( request , response );
		
		} else {
		
			HttpServletResponse res = ( HttpServletResponse ) response ;
		    res.sendRedirect (PoolString.PAGES_LOGIN_LOGIN_JSF);
		
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}

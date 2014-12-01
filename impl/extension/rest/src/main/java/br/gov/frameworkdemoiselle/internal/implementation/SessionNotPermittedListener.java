package br.gov.frameworkdemoiselle.internal.implementation;

import static javax.servlet.SessionTrackingMode.URL;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.gov.frameworkdemoiselle.transaction.BeforeTransactionComplete;
import br.gov.frameworkdemoiselle.util.Beans;

@WebListener
public class SessionNotPermittedListener implements ServletContextListener, HttpSessionListener {

	private static final String ATTR_NAME = "br.gov.frameworkdemoiselle.SESSION_NOT_PERMITTED";

	private static final String ATTR_VALUE = "created";

	public void contextInitialized(ServletContextEvent event) {
		Set<SessionTrackingMode> modes = new HashSet<SessionTrackingMode>();
		modes.add(URL);
		event.getServletContext().setSessionTrackingModes(modes);
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		Beans.getReference(HttpServletRequest.class).setAttribute(ATTR_NAME, ATTR_VALUE);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}

	public void beforeTransactionComplete(@Observes BeforeTransactionComplete event, HttpServletRequest request) {
		if (ATTR_VALUE.equals(request.getAttribute(ATTR_NAME))) {
			invalidateSesstion(request);
			throw new IllegalStateException("Session use is not permitted.");
		}
	}

	private void invalidateSesstion(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}
	}
}

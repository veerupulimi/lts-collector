package com.lts.web.api.server;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.lts.core.listener.ListenerManager;
import com.lts.web.api.util.ConfigConstants;

public class EmbeddedJetty {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EmbeddedJetty.class);

	public static void main(String[] args) throws Exception {
		ListenerManager.getListenerManager();
		new EmbeddedJetty().startJetty(ConfigConstants.PORT);
	}

	private void startJetty(int port) throws Exception {
		LOGGER.debug("Starting server at port {}", port);
		Server server = new Server(port);

		server.setHandler(getServletContextHandler());

		addRuntimeShutdownHook(server);

		server.start();
		LOGGER.info("Server started at port {}", port);
		server.join();
	}

	private static ServletContextHandler getServletContextHandler()
			throws IOException {
		ServletContextHandler contextHandler = new ServletContextHandler(
				ServletContextHandler.SESSIONS); // SESSIONS requerido para JSP
		contextHandler.setErrorHandler(null);

		contextHandler.setResourceBase(new ClassPathResource(
				ConfigConstants.WEBAPP_DIRECTORY).getURI().toString());
		contextHandler.setContextPath(ConfigConstants.CONTEXT_PATH);

		// JSP
		// contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
		// // Necesario para cargar JspServlet
		// contextHandler.addServlet(JspServlet.class, "*.jsp");

		// Spring
		WebApplicationContext webAppContext = getWebApplicationContext();
		DispatcherServlet dispatcherServlet = new DispatcherServlet(
				webAppContext);
		ServletHolder springServletHolder = new ServletHolder("mvc-dispatcher",
				dispatcherServlet);
		contextHandler.addServlet(springServletHolder,
				ConfigConstants.MAPPING_URL);
		contextHandler
				.addEventListener(new ContextLoaderListener(webAppContext));

		return contextHandler;
	}

	private static WebApplicationContext getWebApplicationContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation(ConfigConstants.CONFIG_LOCATION_PACKAGE);
		return context;
	}

	private static void addRuntimeShutdownHook(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (server.isStarted()) {
					server.setStopAtShutdown(true);
					try {
						server.stop();
					} catch (Exception e) {
						System.out
								.println("Error while stopping jetty server: "
										+ e.getMessage());
						LOGGER.error(
								"Error while stopping jetty server: "
										+ e.getMessage(), e);
					}
				}
			}
		}));
	}

}

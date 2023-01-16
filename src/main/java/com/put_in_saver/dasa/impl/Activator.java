package com.put_in_saver.dasa.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		bundleContext.registerService(SwingInstallationNodeService.class, new putinsaverInstallationNodeService(), null);
		System.out.println("Start put_in_saver!");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("End put_in_saver!");
	}
}


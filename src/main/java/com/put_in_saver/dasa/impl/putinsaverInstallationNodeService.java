package com.put_in_saver.dasa.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class putinsaverInstallationNodeService implements SwingInstallationNodeService<putinsaverInstallationNodeContribution, putinsaverInstallationNodeView>{

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle(Locale locale) {
		// TODO Auto-generated method stub
		return "Put in saver";
	}

	@Override
	public putinsaverInstallationNodeView createView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated method stub
		return new putinsaverInstallationNodeView(apiProvider);
	}

	@Override
	public putinsaverInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
			putinsaverInstallationNodeView view, DataModel model, CreationContext context) {
		// TODO Auto-generated method stub
		return new putinsaverInstallationNodeContribution(apiProvider, view, model);
	}

}

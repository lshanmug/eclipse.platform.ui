/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import java.util.ArrayList;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.ContributionsAnalyzer;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MTrimContribution;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuFactoryImpl;
import org.eclipse.e4.ui.workbench.renderers.swt.ContributionRecord;

/**
 * @since 3.102.0
 * 
 */
public class MenuFactoryGenerator {
	private MApplication application;
	private IConfigurationElement configElement;
	private MenuLocationURI location;

	public MenuFactoryGenerator(MApplication application, IEclipseContext appContext,
			IConfigurationElement configElement, String attribute) {
		this.application = application;
		// this.appContext = appContext;
		assert appContext.equals(this.application.getContext());
		this.configElement = configElement;
		this.location = new MenuLocationURI(attribute);
	}

	public void mergeIntoModel(ArrayList<MMenuContribution> menuContributions,
			ArrayList<MToolBarContribution> toolBarContributions,
			ArrayList<MTrimContribution> trimContributions) {
		MMenuContribution menuContribution = MenuFactoryImpl.eINSTANCE.createMenuContribution();
		String idContrib = MenuHelper.getId(configElement);
		if (idContrib != null && idContrib.length() > 0) {
			menuContribution.setElementId(idContrib);
		}
		if ("org.eclipse.ui.popup.any".equals(location.getPath())) { //$NON-NLS-1$
			menuContribution.setParentId("popup"); //$NON-NLS-1$
		} else {
			menuContribution.setParentId(location.getPath());
		}
		String query = location.getQuery();
		if (query == null || query.length() == 0) {
			query = "after=additions"; //$NON-NLS-1$
		}
		menuContribution.setPositionInParent(query);
		menuContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$
		String filter = ContributionsAnalyzer.MC_MENU;
		if ("popup".equals(location.getScheme())) { //$NON-NLS-1$
			filter = ContributionsAnalyzer.MC_POPUP;
		}
		menuContribution.getTags().add(filter);
		menuContribution.setVisibleWhen(MenuHelper.getVisibleWhen(configElement));
		ContextFunction generator = new ContributionFactoryGenerator(configElement);
		menuContribution.getTransientData().put(ContributionRecord.FACTORY, generator);
		menuContributions.add(menuContribution);
	}
}

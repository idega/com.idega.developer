package com.idega.development;

import com.idega.development.presentation.ApplicationPropertySetter;
import com.idega.development.presentation.comp.BundleComponentFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
		RefactorClassRegistry registry = RefactorClassRegistry.getInstance();
		registry.registerRefactoredPackage("com.idega.development", this.getClass().getPackage());
		registry.registerRefactoredPackage("com.idega.development.presentation", ApplicationPropertySetter.class.getPackage());
		registry.registerRefactoredPackage("com.idega.development.presentation.comp", BundleComponentFactory.class.getPackage());
	}

	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}
}

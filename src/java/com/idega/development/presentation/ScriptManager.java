package com.idega.development.presentation;
import com.idega.block.beanshell.presentation.BeanShellScript;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
/**
 * This block manages a list of available Beanshell scripts (end with .bsh) within bundles and includes a simple script editor and the possibility to run the scripts.
*@author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
*@version 1.0

*/
public class ScriptManager extends Block {
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.development.presentation";
		
	public ScriptManager() {
	}

	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		if (iwc.isLoggedOn()) {
		
			BeanShellScript scriptEditor = new BeanShellScript();
			scriptEditor.setToShowScriptEditor(true);
			scriptEditor.addParameterToMaintain(IWDeveloper.PARAMETER_CLASS_NAME);
			
			add(scriptEditor);
			
		}
		else {
			add(iwrb.getLocalizedString("not.logged.on","Not logged on"));
		}
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}

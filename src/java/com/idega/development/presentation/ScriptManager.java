package com.idega.development.presentation;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.PresentationUtil;
import com.idega.util.reflect.MethodInvoker;
/**
 * This block manages a list of available Beanshell scripts (end with .bsh) within bundles and includes a simple script editor and the possibility to run the scripts.
*@author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
*@version 1.0

*/
public class ScriptManager extends Block {
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";
		
	public ScriptManager() {
	}

	
	@Override
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));
		
		if (iwc.isLoggedOn()) {
		
		try{
			Block scriptEditor = (Block) RefactorClassRegistry.forName("com.idega.block.beanshell.presentation.BeanShellScript").newInstance();
			MethodInvoker invoker = MethodInvoker.getInstance();
			
			invoker.invokeMethodWithBooleanParameter(scriptEditor,"setToShowScriptEditor",true);
			//scriptEditor.setToShowScriptEditor(true);
			invoker.invokeMethodWithStringParameter(scriptEditor,"addParameterToMaintain",IWDeveloper.PARAMETER_CLASS_NAME);
			//scriptEditor.addParameterToMaintain(IWDeveloper.PARAMETER_CLASS_NAME);
			add(scriptEditor);
		}
		catch(ClassNotFoundException e){
			this.add((iwrb.getLocalizedString("feature.beanshell.not.installed","You can not use this feature because you do not have the BeanShell module installed")));
		}
			
		}
		else {
			add(iwrb.getLocalizedString("not.logged.on","Not logged on"));
		}
	}
	
	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}

package com.idega.development.presentation;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageCacher;
import com.idega.business.IBOLookup;
import com.idega.data.IDOContainer;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.util.IWTimestamp;
import java.io.File;
/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class Caches extends Block
{
	private static final String PARAM_IB_PAGES = "iw_cache_ib_pages";
	private static final String PARAM_LOOKUP = "iw_cache_lookup";
	private static final String PARAM_IDO_BEAN = "iw_cache_ido_bean";
	private static final String PARAM_IDO_QUERY = "iw_cache_ido_query";
	public Caches()
	{}
	public void main(IWContext iwc) throws Exception
	{
		add(IWDeveloper.getTitleTable(this.getClass()));
		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		add(form);
		Table table = new Table();
		table.setAlignment(3, 1, "right");
		form.add(table);
		SubmitButton ib_pages = new SubmitButton(PARAM_IB_PAGES,"Clear all preloaded Builder Pages");
		SubmitButton lookup = new SubmitButton(PARAM_LOOKUP,"Clear all Lookup Cache");
		SubmitButton ido_bean = new SubmitButton(PARAM_IDO_BEAN,"Clear all IDO Bean Cache");
		SubmitButton ido_query = new SubmitButton(PARAM_IDO_QUERY,"Clear all IDO Query Cache");
		table.add(ib_pages, 3, 1);
		table.add(lookup, 3, 2);
		table.add(ido_bean, 3, 3);
		table.add(ido_query, 3, 4);
		
		processBusiness(iwc);
	}
	private void processBusiness(IWContext iwc) throws Exception
	{
		String clearIBPages = iwc.getParameter(PARAM_IB_PAGES);
		if (clearIBPages != null)
		{
			BuilderLogic.getInstance().clearAllCachedPages();
			add(IWDeveloper.getText("Cleared all ib pages cache!"));
		}
		String clearLookup = iwc.getParameter(PARAM_LOOKUP);
		if (clearLookup != null)
		{
			IBOLookup.clearAllCache();
			add(IWDeveloper.getText("Cleared all Lookup cache!"));
		}
		String idobean = iwc.getParameter(PARAM_IDO_BEAN);
		if (idobean != null)
		{
			IDOContainer.getInstance().flushAllBeanCache();
			add(IWDeveloper.getText("Flushed all IDO bean cache!"));
		}
		String idoquery = iwc.getParameter(PARAM_IDO_QUERY);
		if (idoquery != null)
		{
			IDOContainer.getInstance().flushAllQueryCache();
			add(IWDeveloper.getText("Flushed all IDO query cache!"));
		}
		
	}
}

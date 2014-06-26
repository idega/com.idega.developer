package com.idega.development.business;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.business.IBOSessionBean;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class FilesManagerBusinessBean extends IBOSessionBean implements FilesManagerBusiness {

	private static final long serialVersionUID = -4600940859804313580L;

	private List<String> copiedFiles = null;

	@Override
	public boolean copyFilesToRepository() {
		ICFileHome icFileHome = null;
		try {
			icFileHome = (ICFileHome) getIDOHome(ICFile.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (icFileHome == null) {
			return false;
		}

		Collection<ICFile> files = null;
		try {
			files = icFileHome.findAllDescendingOrdered();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		copiedFiles = new ArrayList<String>();

		return copyFiles(files, DeveloperConstants.OLD_FILES_FOLDER);
	}

	private boolean copyFiles(Collection<ICFile> files, String basePath) {
		if (ListUtil.isEmpty(files)) {
			return true;	//	Nothing to copy
		}

		ICFile file = null;
		boolean result = true;
		for (Iterator<ICFile> it = files.iterator(); (it.hasNext() && result);) {
			file = it.next();
			if (file.isLeaf()) {
				result = copyFile(file, DeveloperConstants.OLD_FILES_FOLDER_FOR_OTHER_FILES, true);
			} else if (file.isFolder() || !file.isLeaf()) {
				basePath = StringUtil.isEmpty(basePath) ? DeveloperConstants.OLD_FILES_FOLDER : basePath;
				Collection<ICFile> children = file.getChildren();
				result = copyFiles(children, new StringBuffer(basePath).append(file.getName()).append(CoreConstants.SLASH).toString());
			}
		}

		return result;
	}

	private boolean copyFile(ICFile file, String basePath, boolean checkName) {
		String name = file.getName();
		if (name == null) {
			name = "Untitled";
		}

		if (checkName) {
			int index = 1;
			String tempName = name;
			while (copiedFiles.contains(tempName)) {
				tempName = new StringBuffer().append(index).append("_").append(name).toString();
				index++;
			}
			name = tempName;
			copiedFiles.add(name);
		}

		InputStream stream = file.getFileValue();
		if (stream == null) {
			return true;	//	Skipping
		}

		boolean result = true;
		try {
			result = getRepositoryService().uploadFile(basePath, name, file.getMimeType(), stream);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtil.close(stream);
		}
		if (result) {
			file.setFileUri(new StringBuffer(CoreConstants.WEBDAV_SERVLET_URI).append(basePath).append(name).toString());
			file.store();
		}
		return result;
	}

}
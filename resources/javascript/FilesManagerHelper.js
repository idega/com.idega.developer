var ACTIVE_CONTAINER_ID = null;

function copyFilesToSlide(parameters) {
	if (parameters == null) {
		return false;
	}
	
	showLoadingMessage(parameters[0]);
	
	if (ACTIVE_CONTAINER_ID != null) {
		var containerToHide = document.getElementById(ACTIVE_CONTAINER_ID);
		ACTIVE_CONTAINER_ID = null;
		if (containerToHide != null) {
			containerToHide.style.display = 'none';
		}
	}
	
	FilesManagerBusiness.copyFilesToSlide({
		callback: function(result) {
			copyFilesToSlideCallback(result, parameters);
		}
	});
}

function copyFilesToSlideCallback(result, parameters) {
	closeLoadingMessage();
	
	var containerToShow = null;
	if (result) {
		ACTIVE_CONTAINER_ID = parameters[1];
	}
	else {
		ACTIVE_CONTAINER_ID = parameters[2];
	}
	
	containerToShow = document.getElementById(ACTIVE_CONTAINER_ID);
	if (containerToShow == null) {
		return false;
	}
	
	containerToShow.style.display = 'block';
	
	return true;
}
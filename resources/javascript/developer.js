function addComponentPropertyToList(formId, hiddenName, object) {
	if (object == null || hiddenName == null) {
		return;
	}
	var form = null;
	if (formId == null) {
		var list = document.getElementsByTagName("form");
		if (list == null) {
			return;
		}
		if (list.length == 0) {
			return;
		}
		form = list[0];
	}
	else {
		form = document.getElementById(formId);
	}
	if (form == null) {
		return;
	}
	removeOldValue(form, hiddenName + object.value);
	var hiddenInput = document.createElement("input");
	hiddenInput.setAttribute("type", "hidden");
	hiddenInput.setAttribute("name", hiddenName);
	hiddenInput.setAttribute("id", hiddenName + object.value);
	if (object.checked) {
		hiddenInput.setAttribute("value", object.value + "@enable");
	}
	else {
		hiddenInput.setAttribute("value", object.value + "@disable");
	}
	form.appendChild(hiddenInput);
}

function removeOldValue(form, id) {
	var oldInput = document.getElementById(id);
	if (form != null && oldInput != null) {
		form.removeChild(oldInput);
	}
}
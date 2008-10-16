jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery("#delete").hide();
	
	jQuery("#save").click(function() {
		var key = dwr.util.getValue("key");
		var newKey = dwr.util.getValue("newKey");
		var value = dwr.util.getValue("value");
		
		var locale = dwr.util.getValue("locale");
		var bundleIdentifier = dwr.util.getValue("bundle");
		
		Localizer.storeLocalizedString(key, newKey, value, bundleIdentifier, locale, {
			callback: function(index) {
				if (newKey.length > 0) {
					var newValue = "<tr><td class=\"firstColumn\"><a class=\"keyLink\" href=\"#\">" + newKey + "</a></td><td class=\"lastColumn\"><span>" + value + "</span></td></tr>";
					
					if (index == 0) {
						jQuery("table tbody").prepend(newValue);
					}
					else {
						var beforeIndex = index - 1;
						jQuery("table tbody tr:eq(" + beforeIndex + ")");
					}
					
					dwr.util.removeAllOptions("key");
					Localizer.getLocalizedStrings(bundleIdentifier, {
						callback: function(values) {
							dwr.util.addOptions("key", values);
							dwr.util.setValue("key", newKey);
							dwr.util.setValue("newKey", "");
						}
					});

					initializeLinks();
					initializeZebraColors();
					humanMsg.displayMsg("Localized string added...");
				}
				else {
					humanMsg.displayMsg("Localized string saved...");
				}			

				jQuery("table tbody tr:eq(" + index + ") td.lastColumn").removeClass("isEmpty").text(value);
				jQuery("#delete").fadeIn();
			}
		});
	});
	
	jQuery("#delete").click(function() {
		var key = dwr.util.getValue("key");
		var bundleIdentifier = dwr.util.getValue("bundle");
		
		Localizer.removeLocalizedKey(key, bundleIdentifier, {
			callback: function(index) {
				if (index >= 0) {
					jQuery("table tbody tr:eq(" + index + ")").fadeOut().remove();
					initializeZebraColors();
				}
				dwr.util.removeAllOptions("key");
				Localizer.getLocalizedStrings(bundleIdentifier, {
					callback: function(value) {
						dwr.util.addOptions("key", value);
						dwr.util.setValue("value", "");
						jQuery("#delete").fadeOut();
						humanMsg.displayMsg("Localized string deleted...");
					}
				});
			}
		});
	});
	
	initializeLinks();
	initializeDropdown();
});

function initializeLinks() {
	jQuery("a.keyLink").click(function() {
		var bundleIdentifier = dwr.util.getValue("bundle");
		var key = jQuery(this).text();

		var locale = dwr.util.getValue("locale");
		Localizer.getLocalizedString(key, bundleIdentifier, locale, {
			callback: function(value) {
				dwr.util.setValue("value", value);
				dwr.util.setValue("key", key);
				jQuery("#delete").fadeIn();
			}
		});
		
		jQuery(".wf_blockmainarea").scrollTo(0, 300);
	});
}

function initializeDropdown() {
	jQuery("#key").change(function() {
		var bundleIdentifier = dwr.util.getValue("bundle");
		var key = dwr.util.getValue("key");

		var locale = dwr.util.getValue("locale");
		Localizer.getLocalizedString(key, bundleIdentifier, locale, {
			callback: function(value) {
				dwr.util.setValue("value", value);
				jQuery("#delete").fadeIn();
			}
		});
	});
}

function initializeZebraColors() {
	jQuery("table tbody tr").each(function() {
		var index = jQuery("table tbody tr").index(this);
		jQuery(this).removeClass("oddRow").removeClass("evenRow");
		
		if (index % 2 == 0) {
			jQuery(this).addClass("evenRow");
		}
		else {
			jQuery(this).addClass("oddRow");
		}
	});	
}
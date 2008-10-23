jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery("#localizerDelete").hide();
	
	jQuery("#localizerSave").click(function() {
		var key = dwr.util.getValue("localizerKey");
		var newKey = dwr.util.getValue("localizerNewKey");
		var value = dwr.util.getValue("localizerValue");
		
		var locale = dwr.util.getValue("localizerLocale");
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		
		Localizer.storeLocalizedString(key, newKey, value, bundleIdentifier, locale, {
			callback: function(index) {
				if (newKey.length > 0) {
					var newValue = "<tr><td class=\"firstColumn\"><a class=\"keyLink\" href=\"#\">" + newKey + "</a></td><td class=\"lastColumn\"><span class=\"stringValue\">" + value + "</span></td></tr>";
					
					if (index == 0) {
						jQuery("table tbody").prepend(newValue);
					}
					else {
						var beforeIndex = index - 1;
						jQuery("table tbody tr:eq(" + beforeIndex + ")");
					}
					
					dwr.util.removeAllOptions("localizerKey");
					Localizer.getLocalizedStrings(bundleIdentifier, {
						callback: function(values) {
							dwr.util.addOptions("localizerKey", values);
							dwr.util.setValue("localizerKey", newKey);
							dwr.util.setValue("localizerNewKey", "");
						}
					});

					initializeLinks();
					initializeZebraColors();
					humanMsg.displayMsg("Localized string added...");
				}
				else {
					humanMsg.displayMsg("Localized string saved...");
				}			

				jQuery("table tbody tr:eq(" + index + ") td.lastColumn").removeClass("isEmpty").children().get(0).text(value);
				jQuery("#localizerDelete").fadeIn();
			}
		});
	});
	
	jQuery("#localizerDelete").click(function() {
		var key = dwr.util.getValue("localizerKey");
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		
		Localizer.removeLocalizedKey(key, bundleIdentifier, {
			callback: function(index) {
				if (index >= 0) {
					jQuery("table tbody tr:eq(" + index + ")").fadeOut().remove();
					initializeZebraColors();
				}
				dwr.util.removeAllOptions("localizerKey");
				Localizer.getLocalizedStrings(bundleIdentifier, {
					callback: function(value) {
						dwr.util.addOptions("localizerKey", value);
						dwr.util.setValue("localizerValue", "");
						jQuery("#localizerDelete").fadeOut();
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
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		var key = jQuery(this).text();

		var locale = dwr.util.getValue("localizerLocale");
		Localizer.getLocalizedString(key, bundleIdentifier, locale, {
			callback: function(value) {
				dwr.util.setValue("localizerValue", value);
				dwr.util.setValue("localizerKey", key);
				jQuery("#localizerDelete").fadeIn();
			}
		});
		
		jQuery(".wf_blockmainarea").scrollTo(0, 300);
	});

	jQuery(".stringValue").dblclick(function() {
		jQuery(this).fadeOut('fast', function() {
			var oldValue = jQuery(this).text();
			if (jQuery(this).parent().hasClass('isEmpty')) {
				oldValue = "";
			}
			jQuery(this).parent().prepend("<textarea class=\"newStringValue\">" + oldValue + "</textarea>");
			jQuery(".newStringValue").focus().blur(function() {
				var value = jQuery(this).val();
				if (value.length > 0) {
					var key = jQuery(this).parents('tr').children('td.firstColumn').text();
					var locale = dwr.util.getValue("localizerLocale");
					var bundleIdentifier = dwr.util.getValue("localizerBundle");
					
					Localizer.storeLocalizedString(key, '', value, bundleIdentifier, locale);
					humanMsg.displayMsg("Localized string saved...");
					jQuery(this).parent().removeClass('isEmpty').children('span').text(value);
				}

				jQuery(this).hide();
				jQuery(this).parent().children('span').fadeIn('fast');
				jQuery(this).remove();
				initializeZebraColors();
			})
		});
	});
}

function initializeDropdown() {
	jQuery("#localizerKey").change(function() {
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		var key = dwr.util.getValue("localizerKey");

		var locale = dwr.util.getValue("localizerLocale");
		Localizer.getLocalizedString(key, bundleIdentifier, locale, {
			callback: function(value) {
				dwr.util.setValue("localizerValue", value);
				jQuery("#localizerDelete").fadeIn();
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
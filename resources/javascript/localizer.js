jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery("#localizerDelete").hide();
	
	jQuery("#localizerSave").click(function() {
		var key = dwr.util.getValue("localizerKey");
		var newKey = dwr.util.getValue("localizerNewKey");
		var value = dwr.util.getValue("localizerValue");
		
		var locale = dwr.util.getValue("localizerLocale");
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		var storageIdentifier = dwr.util.getValue("localizerStorage");
		
		Localizer.storeLocalizedStrings(key, newKey, value, bundleIdentifier, locale, {
			callback: function(strings) {
				strings.forEach(
					function(str) {
						if (newKey.length > 0) {
							var newValue = "<tr><td class=\"firstColumn\"><a class=\"keyLink\" href=\"#\">" + newKey + "</a></td><td><span class=\"stringValue\">" + str.value + "</span></td><td class=\"lastColumn\"><span class=\"storageKey\">" + str.storageIdentifier + "</span></td></tr>";
							jQuery("table tbody").prepend(newValue);
							if (str.index != 0) {
								var beforeIndex = str.index;
								jQuery("table tbody tr:first").insertAfter("table tbody tr:eq(" + beforeIndex + ")");
							}
		
							initializeLinks();
							initializeZebraColors();
							humanMsg.displayMsg("New localized string added...");
						}
						else {
							humanMsg.displayMsg("Localized string saved...");
						}			
		
						jQuery("table tbody tr:eq(" + str.index + ") td:eq('1')").removeClass("isEmpty").children("span").text(str.value);
						jQuery("#localizerDelete").fadeIn();
					}
				);
			}
		});
		
		dwr.util.removeAllOptions("localizerKey");
		Localizer.getLocalizedStringProperties(bundleIdentifier, storageIdentifier, locale, {
			callback: function(values) {
				dwr.util.addOptions("localizerKey", values);
				dwr.util.setValue("localizerKey", newKey);
				dwr.util.setValue("localizerNewKey", "");
			}
		});
							
	});
	
	jQuery("#localizerDelete").click(function() {
		var key = dwr.util.getValue("localizerKey");
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		var storageIdentifier = dwr.util.getValue("localizerStorage");
		var locale = dwr.util.getValue("localizerLocale");
		
		Localizer.removeLocalizedKey(key, bundleIdentifier, storageIdentifier, locale, {
			callback: function(indexes) {
				indexes.forEach(
					function(index, count) {
						if (index >= 0) {
							index = index - count; //decreasing index acccording to the number of already removed elements
							jQuery("table tbody tr:eq(" + index + ")").fadeOut().remove();
							initializeZebraColors();
						}
					}
				);
				dwr.util.removeAllOptions("localizerKey");
				Localizer.getLocalizedStringProperties(bundleIdentifier, storageIdentifier, locale, {
					callback: function(values) {
						dwr.util.addOptions("localizerKey", values);
						dwr.util.setValue("localizerValue", "");
						jQuery("#localizerDelete").fadeOut();
						humanMsg.displayMsg("Localized string deleted from all resources marked as autoinsert ...");
					}
				});
			}
		});
	});
	
	initializeLinks();
	initializeDropdown();
});

function initializeLinks() {
	jQuery("a.keyLink").unbind("click").click(function() {
		var bundleIdentifier = dwr.util.getValue("localizerBundle");
		var storageIdentifier = jQuery(this).parents("tr").find("span:last").text();
		//var storageIdentifier = dwr.util.getValue("localizerStorage");
		var key = jQuery(this).text();
		
		//dwr.util.setValue("localizerStorage", storageIdentifier);

		var locale = dwr.util.getValue("localizerLocale");
		Localizer.getLocalizedString(key, bundleIdentifier, locale, storageIdentifier, {
			callback: function(foundString) {
				dwr.util.setValue("localizerValue", foundString.value);
				dwr.util.setValue("localizerKey", foundString.index + " (" + foundString.storageIdentifier + ")");
				jQuery("#localizerDelete").fadeIn();
			}
		});
		
		jQuery(".wf_blockmainarea").scrollTo(0, 300);
		return false;
	});

	jQuery(".stringValue").unbind("dblclick").dblclick(function() {
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
		if(key != "") {
			var storageIdentifier = key.substring(key.indexOf('(') + 1, key.indexOf(')'));
			key = key.substring(0, key.indexOf(' '));
	
			var locale = dwr.util.getValue("localizerLocale");
			Localizer.getLocalizedString(key, bundleIdentifier, locale, storageIdentifier, {
				callback: function(foundString) {
					dwr.util.setValue("localizerValue", foundString.value);
					jQuery("#localizerDelete").fadeIn();
				}
			});
		} else {
			dwr.util.setValue("localizerValue", "");
		}
	});
}

function initializeZebraColors() {
	jQuery("table tbody tr:odd").removeClass("oddRow").removeClass("evenRow").addClass("evenRow");
	jQuery("table tbody tr:even").removeClass("oddRow").removeClass("evenRow").addClass("oddRow");
}
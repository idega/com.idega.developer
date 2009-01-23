jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery("#applicationPropertySetter #save").click(function() {
		var key = dwr.util.getValue("applicationPropertyKey");
		var value = dwr.util.getValue("applicationPropertyValue");
		
		ApplicationProperties.doesPropertyExist(key, {
			callback: function(doesExist) {
				ApplicationProperties.setProperty(key, value, {
					callback: function(index) {
						if (!doesExist) {
							var newValue = "<tr><td class=\"firstColumn\"><a class=\"keyLink\" href=\"#\">" + key + "</a></td><td><span class=\"keyValue\">" + value + "</span></td><td class=\"lastColumn\"><input type=\"checkbox\" name=\"property\" class=\"removeApplicationPropertyCheck\" value=\"" + key + "\" /></td></tr>";
							
							jQuery("table tbody").prepend(newValue);
							if (index != 0) {
								var beforeIndex = index;
								jQuery("table tbody tr:first").insertAfter("table tbody tr:eq(" + beforeIndex + ")");
							}
							
							initializeLinks();
							initializeZebraColors();
							humanMsg.displayMsg("Application property created...");
						}
						else {
							humanMsg.displayMsg("Application property stored...");
						}
						
						jQuery("table tbody tr:eq(" + index + ") td:eq(1) span").text(value);
					}
				});
			}
		});
	});
	
	jQuery(".setApplicationPropertyCheck").click(function() {
		var value = this.checked ? 'true' : null;
		ApplicationProperties.setProperty(jQuery(this).attr('name'), value);
		humanMsg.displayMsg("Application property stored...");
	})
	
	jQuery("#applicationPropertyMarkupKey").change(function() {
		ApplicationProperties.setProperty(jQuery(this).attr('name'), jQuery(this).val());
		humanMsg.displayMsg("Application property stored...");
	})
	
	initializeLinks();
});

function initializeLinks() {
	jQuery("a.keyLink").unbind("click").click(function() {
		var key = jQuery(this).text();

		ApplicationProperties.getProperty(key, {
			callback: function(value) {
				dwr.util.setValue("applicationPropertyValue", value);
				dwr.util.setValue("applicationPropertyKey", key);
			}
		});
		
		jQuery(".wf_blockmainarea").scrollTo(0, 300);
		return false;
	});
	
	jQuery("input.removeApplicationPropertyCheck").unbind("click").click(function() {
		var key = jQuery(this).val();
		ApplicationProperties.removeProperty(key);

		var oldKey = dwr.util.getValue("applicationPropertyKey");
		if (oldKey == key) {
			dwr.util.setValue("applicationPropertyValue", "");
			dwr.util.setValue("applicationPropertyKey", "");
		}

		jQuery(this).parent().parent().fadeOut().remove();
		initializeZebraColors();
		humanMsg.displayMsg("Application property deleted...");
	});

	jQuery(".keyValue").unbind("dblclick").dblclick(function() {
		jQuery(this).fadeOut('fast', function() {
			var oldValue = jQuery(this).text();
			if (oldValue == "&nbsp;") {
				oldValue = "";
			}
			jQuery(this).parent().prepend("<input type=\"text\" name=\"newValue\" class=\"newStringValue\" value=\"" + oldValue + "\" />");
			jQuery(".newStringValue").focus().blur(function() {
				var value = jQuery(this).val();
				if (value.length > 0) {
					var key = jQuery(this).parents('tr').children('td.firstColumn').text();
					
					ApplicationProperties.setProperty(key, value);
					humanMsg.displayMsg("Application property stored...");
					jQuery(this).parent().children('span').text(value);
				}

				jQuery(this).hide();
				jQuery(this).parent().children('span').fadeIn('fast');
				jQuery(".newStringValue").remove();
				initializeZebraColors();
			}).keypress(function(event) {
				if (isEnterEvent(event)) {
					jQuery(this).blur();
				}
			});
		});
	});
;
}

function initializeZebraColors() {
	jQuery("table tbody tr:odd").removeClass("oddRow").removeClass("evenRow").addClass("evenRow");
	jQuery("table tbody tr:even").removeClass("oddRow").removeClass("evenRow").addClass("oddRow");
}
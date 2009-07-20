RestartNotifierHelper = {};

RestartNotifierHelper.startTicking = function(id) {
	if (id == null) {
		return false;
	}
	
	var timeOutId = window.setTimeout(function() {
		SystemRestartNotifier.getTextAndTimeLeftToRestart({
			callback: function(textAndTime) {
				window.clearTimeout(timeOutId);
				
				if (textAndTime == null || textAndTime == '') {
					jQuery.gritter.remove(id);
					return false;
				}
				
				jQuery.each(jQuery('p', jQuery('#gritter-item-' + id)), function() {
					var p = jQuery(this);
					
					if (p.text().indexOf(' min.') != -1) {
						p.text(textAndTime);
					}
				});
				
				RestartNotifierHelper.startTicking(id);
			}
		});
	}, 60000);
}
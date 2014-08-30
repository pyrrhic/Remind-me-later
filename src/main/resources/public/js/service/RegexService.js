
myAppModule.service('RegexService', function() {
	
	this.getReminderDateTimeRegex = function() {
		var soonDate = "(today|tomorrow)";
		var slashDate = "([0-9]{1,2}/{1}[0-9]{1,2}/{1}([0-9]{4}|[0-9]{2}))";
		var weekdayDate = "(monday|tuesday|wednesday|thursday|friday|saturday|sunday)";
		
		var shortTime = "(([1]{1}[0-2]{1}|[1-9]{1})(AM|PM|am|pm){1})";
		var longTime = "(([1]{1}[0-2]{1}|[1-9]{1})(:){1}[0-5][0-9](AM|PM|am|pm){1})"
			
		return new RegExp("(" + soonDate + "|" + slashDate + "|" + weekdayDate + "){1}" +
					      " " +
		                  "(" + shortTime + "|" + longTime + "){1}");

	}								    
});
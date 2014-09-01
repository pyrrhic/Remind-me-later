
myAppModule.controller('HomeController', function($scope, $http, $timeout, RegexService, ReminderService, RegistrationService) {	
	var initPage = function() {		
		$http.get(myAppModule.baseUrl+"getProviders")
		.success(function(data) {
			//populate providers drop down
			$scope.providers = data;
			
			//create regex
			$scope.addReminderForm.dateTimeRegex = RegexService.getReminderDateTimeRegex();
		});
	}();
	
	$scope.addReminder = function() {
		ReminderService.addReminder($scope, $http, $timeout);
	}
	
	$scope.register = function() {
		RegistrationService.register($scope, $http, $timeout);
	}
})









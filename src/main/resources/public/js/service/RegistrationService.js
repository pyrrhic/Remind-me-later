
myAppModule.service('RegistrationService', function() {	
	this.register = function($scope, $http, $timeout) {
		if ($scope.registrationForm.$invalid) {
			$scope.registrationForm.showErrors = true;
			
			$timeout(function() {
				$scope.registrationForm.showErrors = false;
				},
				4000);
			
			return;
		}
		
		$http({
			method: 'POST',
			url: myAppModule.baseUrl + 'register',
			data: $scope.registration,
			headers: {
				'Content-Type': 'application/json'
			}
		})
		.success(function(errors) {		
			if (errors.length === 0) {
				$scope.registered = true;
				
				$timeout(function() {
					$scope.registered = false;
					},
					2000);
			}
			else {
				console.log(errors);
			}
		});
	}
});
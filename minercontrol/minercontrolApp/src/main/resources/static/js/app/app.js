var app = angular.module('crudApp',['ui.router','ngStorage']);

app.constant('urls', {
    BASE: 'http://localhost:8080//minerControl'
    ,USER_SERVICE_API : 'http://localhost:8080/minerControl/api/user/'
    ,CONFIG_SERVICE_API : 'http://localhost:8080/minerControl/api/config/'
});

app.config(['$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {

        $stateProvider
        .state('Dashboard', {
            url: '/',
            templateUrl: 'partials/dashboard',
            controller:'DashboardController',
            controllerAs:'ctrl',
            resolve: {
                users: function ($q, UserService) {
                    console.log('Load all users');
                    var deferred = $q.defer();
                    UserService.loadAllUsers().then(deferred.resolve, deferred.resolve);
                    return deferred.promise;
                }
            }
        })
//        .state('home', {
//            url: 'home',
//            templateUrl: 'partials/list',
//            controller:'UserController',
//            controllerAs:'ctrl',
//            resolve: {
//                users: function ($q, UserService) {
//                    console.log('Load all users');
//                    var deferred = $q.defer();
//                    UserService.loadAllUsers().then(deferred.resolve, deferred.resolve);
//                    return deferred.promise;
//                }
//            }
//        })
	        .state('config', {
	            url: 'config',
	            templateUrl: 'partials/config',
	            controller:'ConfigController',
	            controllerAs:'ctrl',
	            resolve: {
	                configs: function ($q, ConfigService) {
	                    console.log('Load all configs');
	                    var deferred = $q.defer();
	                    ConfigService.loadAllConfigs().then(deferred.resolve, deferred.resolve);
	                    return deferred.promise;
	                }
	            }
	        })
            ;
        $urlRouterProvider.otherwise('/');
    }]);


'use strict';

angular.module('crudApp').factory('ConfigService',
    ['$localStorage', '$http', '$q', 'urls',
        function ($localStorage, $http, $q, urls) {

	        var factory = {
	            loadAllConfigs: loadAllConfigs,
	            getAllConfigs: getAllConfigs,
	            getConfig: getConfig,
	            createConfig: createConfig,
	            updateConfig: updateConfig,
	            removeConfig: removeConfig
	        };
	
	        return factory;
	
	        function loadAllConfigs() {
	            console.log('Fetching all configs');
	            var deferred = $q.defer();
	            $http.get(urls.CONFIG_SERVICE_API)
	                .then(
	                    function (response) {
	                        console.log('Fetched successfully all configs');
	                        $localStorage.configs = response.data;
	                        deferred.resolve(response);
	                    },
	                    function (errResponse) {
	                        console.error('Error while loading configs');
	                        deferred.reject(errResponse);
	                    }
	                );
	            return deferred.promise;
	        }
	
	        function getAllConfigs(){
	            return $localStorage.configs;
	        }
	
	        function getConfig(id) {
	            console.log('Fetching Config with id :'+id);
	            var deferred = $q.defer();
	            $http.get(urls.CONFIG_SERVICE_API + id)
	                .then(
	                    function (response) {
	                        console.log('Fetched successfully Config with id :'+id);
	                        deferred.resolve(response.data);
	                    },
	                    function (errResponse) {
	                        console.error('Error while loading config with id :'+id);
	                        deferred.reject(errResponse);
	                    }
	                );
	            return deferred.promise;
	        }
	
	        function createConfig(config) {
	            console.log('Creating Config');
	            var deferred = $q.defer();
	            $http.post(urls.CONFIG_SERVICE_API, config)
	                .then(
	                    function (response) {
	                        loadAllConfigs();
	                        deferred.resolve(response.data);
	                    },
	                    function (errResponse) {
	                       console.error('Error while creating Config : '+errResponse.data.errorMessage);
	                       deferred.reject(errResponse);
	                    }
	                );
	            return deferred.promise;
	        }
	
	        function updateConfig(config, id) {
	            console.log('Updating Config with id '+id);
	            var deferred = $q.defer();
	            $http.put(urls.CONFIG_SERVICE_API + id, config)
	                .then(
	                    function (response) {
	                        loadAllConfigs();
	                        deferred.resolve(response.data);
	                    },
	                    function (errResponse) {
	                        console.error('Error while updating Config with id :'+id);
	                        deferred.reject(errResponse);
	                    }
	                );
	            return deferred.promise;
	        }
	
	        function removeConfig(id) {
	            console.log('Removing Config with id '+id);
	            var deferred = $q.defer();
	            $http.delete(urls.CONFIG_SERVICE_API + id)
	                .then(
	                    function (response) {
	                        loadAllConfigs();
	                        deferred.resolve(response.data);
	                    },
	                    function (errResponse) {
	                        console.error('Error while removing Config with id :'+id);
	                        deferred.reject(errResponse);
	                    }
	                );
	            return deferred.promise;
	        }
	
	    }
    ]);
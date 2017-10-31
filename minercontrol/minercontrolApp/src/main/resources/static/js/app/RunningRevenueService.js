'use strict';

angular.module('crudApp').factory('RunningRevenueService',
    ['$localStorage', '$http', '$q', 'urls',
        function ($localStorage, $http, $q, urls) {

            var factory = {
            		API:urls.RUNING_REVENUE_SERVICE_API,
                loadAllDatas: loadAllDatas,
                getAllDatas: getAllDatas,
                getData: getData,
                createData: createData,
                updateData: updateData,
                removeData: removeData
            };
            
            

            return factory;

            function loadAllDatas() {
                console.log('Fetching all datas');
                var deferred = $q.defer();
                $http.get(this.API)
                    .then(
                        function (response) {
                            console.log('Fetched successfully all datas');
                            $localStorage.runningRevenue = response.data;
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading datas');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function getAllDatas(){
                return $localStorage.runningRevenue;
            }

            function getData(id) {
                console.log('Fetching Data with id :'+id);
                var deferred = $q.defer();
                $http.get(this.API + id)
                    .then(
                        function (response) {
                            console.log('Fetched successfully Data with id :'+id);
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while loading data with id :'+id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function createData(data) {
                console.log('Creating Data');
                var deferred = $q.defer();
                $http.post(urls.RUNING_REVENUE_SERVICE_API, data)
                    .then(
                        function (response) {
                            loadAllDatas();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                           console.error('Error while creating Data : '+errResponse.data.errorMessage);
                           deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function updateData(data, id) {
                console.log('Updating Data with id '+id);
                var deferred = $q.defer();
                $http.put(this.API + id, data)
                    .then(
                        function (response) {
                            loadAllDatas();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while updating Data with id :'+id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function removeData(id) {
                console.log('Removing Data with id '+id);
                var deferred = $q.defer();
                $http.delete(this.API + id)
                    .then(
                        function (response) {
                            loadAllDatas();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while removing Data with id :'+id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

        }
    ]);
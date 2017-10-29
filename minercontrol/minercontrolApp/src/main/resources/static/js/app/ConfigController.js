'use strict';

angular.module('crudApp').controller('ConfigController',
    ['ConfigService', '$scope',  function( ConfigService, $scope) {

        var self = this;
        self.config = {};
        self.configs=[];

        self.submit = submit;
        self.getAllConfigs = getAllConfigs;
        self.updateConfig = updateConfig;
        self.editConfig = editConfig;
        self.reset = reset;

        self.successMessage = '';
        self.errorMessage = '';
        self.done = false;

        self.onlyIntegers = /^\d+$/;
        self.onlyNumbers = /^\d+([,.]\d+)?$/;

        function submit() {
            console.log('Submitting');
            updateConfig(self.config, 1);
            console.log('config updated with id ', self.config);
        }



        function updateConfig(config, id){
            console.log('About to update config');
            ConfigService.updateConfig(config, id)
                .then(
                    function (response){
                        console.log('config updated successfully');
                        self.successMessage='Config updated successfully';
                        self.errorMessage='';
                        self.done = true;
                        $scope.myForm.$setPristine();
                    },
                    function(errResponse){
                        console.error('Error while updating Config');
                        self.errorMessage='Error while updating Config '+errResponse.data;
                        self.successMessage='';
                    }
                );
        }



        function getAllConfigs(){
            return ConfigService.getAllConfigs();
        }

        function editConfig(id) {
            self.successMessage='';
            self.errorMessage='';
            ConfigService.getConfig(id).then(
                function (config) {
                    self.config = config;
                },
                function (errResponse) {
                    console.error('Error while removing config ' + id + ', Error :' + errResponse.data);
                }
            );
        }
        function reset(){
            self.successMessage='';
            self.errorMessage='';
            self.config={};
            $scope.myForm.$setPristine(); //reset Form
        }
    }


    ]);
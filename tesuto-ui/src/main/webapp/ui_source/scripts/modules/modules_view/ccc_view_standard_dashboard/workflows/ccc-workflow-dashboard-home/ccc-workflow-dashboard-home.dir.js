/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
(function () {

    angular.module('CCC.View.Dashboard').directive('cccWorkflowDashboardHome', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'ViewManagerEntity',

                function ($scope, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addMicroServicePropertiesView = function (targetGroupName, microServiceProperties) {

                        var viewScope = $scope.$new();

                        viewScope.targetGroupName = targetGroupName;
                        viewScope.microServiceProperties = microServiceProperties;

                        $scope.viewManager.pushView({
                            id: 'micro-service-propreties',
                            title: 'Micro Service Properties',
                            breadcrumb: 'Micro Service Properties',
                            scope: viewScope,
                            template: '<ccc-micro-service-properties target-group-name="targetGroupName" micro-service-properties="microServiceProperties"></ccc-micro-service-properties>'
                        });
                    };
                    
                    var addMicroServiceRestClientView = function (targetGroupName, microServiceRestClientResults) {

                        var viewScope = $scope.$new();

                        viewScope.targetGroupName = targetGroupName;
                        viewScope.microServiceRestClientResults = microServiceRestClientResults;

                        $scope.viewManager.pushView({
                            id: 'micro-service-rest-client-results',
                            title: 'Microservice Rest Client Active Endpoint Test Results',
                            breadcrumb: 'Microservice Rest Client Results',
                            scope: viewScope,
                            template: '<ccc-micro-service-rest-client-results target-group-name="targetGroupName" micro-service-rest-client-results="microServiceRestClientResults"></ccc-micro-service-rest-client-results>'
                        });
                    };

                    var addDashboardHomeView = function () {

                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'dashboard-home',
                            title: 'Health Check Metrics',
                            breadcrumb: 'Health Check Metrics',
                            scope: viewScope,
                            template: '<ccc-dashboard-main></ccc-dashboard-main>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-dashboard-main.microServiceClicked', function (e, nodeMetric, microServiceProperties) {
                        addMicroServicePropertiesView(nodeMetric.targetGroup, microServiceProperties);
                    });
                    
                    $scope.$on('ccc-dashboard-main.restClientStatusClicked', function (e, nodeRestClient, microServiceRestClientResults) {
                        addMicroServiceRestClientView(nodeRestClient.targetGroup, microServiceRestClientResults);
                    });


                    /*============ INITIALIZATION ==============*/

                    addDashboardHomeView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();

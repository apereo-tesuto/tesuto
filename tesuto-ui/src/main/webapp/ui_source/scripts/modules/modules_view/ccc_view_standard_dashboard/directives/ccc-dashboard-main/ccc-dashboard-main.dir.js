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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMain', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'MetricsService',

                function ($scope, MetricsService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.rawMetrics = MetricsService.getInitialMetrics();


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-dashboard-metrics-health-check.microServiceClicked', function (e, nodeMetric, microServiceProperties, microServicePropertyKeyToNodeMap) {
                        $scope.$emit('ccc-dashboard-main.microServiceClicked', nodeMetric, microServiceProperties, microServicePropertyKeyToNodeMap);
                    });
                    
                    $scope.$on('ccc-dashboard-metrics-rest-client-status.restClientStatusClicked', function (e, nodeRestClientStatus, microServiceRestClientResults, groupKey) {
                        $scope.$emit('ccc-dashboard-main.restClientStatusClicked', nodeRestClientStatus, microServiceRestClientResults, groupKey);
                    });

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-5">',
                        '<h2>Health Check</h2>',
                        '<ccc-dashboard-metrics-health-check health-check-metrics="rawMetrics.healthcheck" micro-service-properties="rawMetrics.microServiceProperties" micro-service-property-key-to-node-map="rawMetrics.microServicePropertyKeyToNodeMap" ></ccc-dashboard-metrics-health-check>',
                    '</div>',
                    '<div class="col-md-4">',
                        '<h2>Version Numbers</h2>',
                        '<ccc-dashboard-metrics-versions raw-metrics="rawMetrics.buildNumbers"></ccc-dashboard-metrics-versions>',
                    '</div>',
                    '<div class="col-md-3">',
                        '<h2>NEW IDP Logins</h2>',
                        '<ccc-dashboard-metrics-students raw-metrics="rawMetrics.recentStudentLogins"></ccc-dashboard-metrics-students>',
                    '</div>',
                '</div>',

                '<h2 class="margin-top-lg">RDS Metrics</h2>',
                '<p class="help-block">Relational database performance</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-rds raw-metrics="rawMetrics.rdsMetrics"></ccc-dashboard-metrics-rds>',
                    '</div>',
                '</div>',

                '<h2 class="margin-top-lg">Dynamo DB</h2>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-ddb raw-metrics="rawMetrics.ddbMetrics"></ccc-dashboard-metrics-ddb>',
                    '</div>',
                '</div>',

                '<h2 class="margin-top-lg">SQS Metrics</h2>',
                '<p class="help-block">Message queue information</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-sqs raw-metrics="rawMetrics.sqsMetrics"></ccc-dashboard-metrics-sqs>',
                    '</div>',
                '</div>',

                '<h2 class="margin-top-lg">AWS Lambdas</h2>',
                '<p class="help-block">Metrics associated with different AWS Lambdas</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-lambdas raw-metrics="rawMetrics.lambdaMetrics"></ccc-dashboard-metrics-lambdas>',
                    '</div>',
                '</div>',

                '<h2 class="margin-top-lg">College Rules Updates</h2>',
                '<p class="help-block">Listing the latest colleges and that have rules updates</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-rules raw-metrics="rawMetrics.collegeRules"></ccc-dashboard-metrics-rules>',
                    '</div>',
                '</div>',
                
                '<h2 class="margin-top-lg">Rest Clients Status</h2>',
                '<p class="help-block">Listing of rest clients used by each microservice and status.</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-metrics-rest-client-status micro-service-rest-client-status="rawMetrics.microServiceRestClientStatus" micro-service-rest-client-results="rawMetrics.microServiceRestClientResults" micro-service-property-key-to-node-map="rawMetrics.microServicePropertyKeyToNodeMap"></ccc-dashboard-metrics-rest-client-status>',
                    '</div>',
                '</div>',
                
                '<h2 class="margin-top-lg">Onboard College Request</h2>',
                '<p class="help-block">Enter miscode for college Hit button to generate an initial set of approved subject areas for the college.</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-on-board-college \>',
                    '</div>',
                '</div>',
                '<h2 class="margin-top-lg">Seed Data Request</h2>',
                '<p class="help-block">Hit button to seed all allowable data for this installation.</p>',
                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<ccc-dashboard-seed-data \>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();







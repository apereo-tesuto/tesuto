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

    angular.module('CCC.View.Home').directive('cccBulkActivationAddAssessment', function () {

        return {

            restrict: 'E',

            scope: {
                initialDeliveryType: '@?'
            },

            controller: [

                '$scope',
                '$element',
                '$compile',
                '$translate',
                'NotificationService',
                'LocationService',

                function ($scope, $element, $compile, $translate, NotificationService, LocationService) {


                    /*============ MODEL ============*/

                    $scope.assessments = [];
                    $scope.isDisabled = true;
                    $scope.locationError = false;
                    $scope.location = LocationService.getCurrentTestCenter() || null;


                    /*============ BEHAVIOR ============*/

                    $scope.submit = function () {

                        if (_.isNull($scope.location)) {

                            $scope.locationError = true;

                            $scope.notifyMinError = NotificationService.open({
                                    icon: 'fa fa-exclamation-triangle',
                                    title: $translate.instant('CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.ERROR.NO_LOCATION'),
                                    message: '',
                                    uid: 'ccc-activate-student.error'
                                },
                                {
                                    delay: 0,
                                    type: "warning",
                                    allow_dismiss: true
                                });

                            return;
                        }

                        $scope.$emit('ccc-bulk-activation-add-assessment.submit', $scope.assessments);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-assessments-list.selectedAssessmentsChanged', function (event, assessments) {
                        $scope.assessments = assessments;
                        $scope.isDisabled = _.isEmpty(assessments);
                    });

                    $scope.$on('ccc-bulk-activation-add-assessment.requestReset', function () {
                        $scope.isDisabled = true;
                        $scope.assessments = [];
                        $scope.$broadcast('ccc-assessments-list.requestRefresh', $scope.location);
                    });


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [
                '<div class="row margin-bottom-md">',
                    '<div class="col-md-8 col-md-offset-2">',
                        '<div>',
                            '<h2 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.LOCATIION"></h2>',
                            '<ccc-locations-list class="btn-full-width" ng-class="{ error: locationError }"></ccc-locations-list>',
                        '</div>',
                    '</div>',
                '</div>',
                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',
                        '<div>',
                            '<h2 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.ASSESSMENTS"></h2>',
                            '<ccc-assessments-list initial-delivery-type="{{initialDeliveryType}}"></ccc-assessments-list>',
                        '</div>',
                        '<div>',
                            '<p class="help-block" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.WARNING"></p>',
                        '</div>',
                    '</div>',
                '</div>',
                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',
                        '<div class="text-right">',
                            '<button type="button" class="btn btn-primary btn-full-width-when-small select-student-btn" ng-disabled="isDisabled" ng-click="submit()">Select Students <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span></button>',
                            '<button ui-sref="home" type="button" class="btn btn-default btn-full-width-when-small cancel-btn">Cancel</button>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join('')
        };
    });

})();

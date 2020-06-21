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

    angular.module('CCC.View.Home').directive('cccTestLocationSummary', function () {

        return {

            restrict: 'E',

            scope: {
                testLocation: '=',
                isExpanded: '='
            },

            controller: [

                '$scope',
                'TestLocationClass',
                'AssessmentsAPIService',

                function ($scope, TestLocationClass, AssessmentsAPIService) {

                    /*============ MODEL ============*/

                    $scope.enabling = false;

                    $scope.loading = false;

                    $scope.assessments = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadAssessmentsCalled = false;
                    var loadAssessments = function () {

                        if (!loadAssessmentsCalled) {

                            loadAssessmentsCalled = true;

                            $scope.loading = true;

                            AssessmentsAPIService.getAssessmentsByTestLocationIdWithCollegeAffiliation($scope.testLocation.id).then(function (assessments) {
                                $scope.assessments = assessments;
                            }).finally(function () {
                                $scope.loading = false;
                            });
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.enable = function () {

                        var testLocationInstance = new TestLocationClass($scope.testLocation);

                        $scope.enabling = true;

                        testLocationInstance.setEnabled(true).then(function () {

                            $scope.testLocation.enabled = true;
                            $scope.$emit('ccc-test-location-summary.enabled', $scope.testLocation);

                        }).finally(function () {
                            $scope.enabling = false;
                        });
                    };

                    $scope.editLocation = function () {
                        $scope.$emit('ccc-test-location-summary.edit', $scope.testLocation);
                    };

                    $scope.addUser = function () {
                        $scope.$emit('ccc-test-location-summary.addUser');
                    };

                    $scope.toggleExpand = function () {

                        $scope.isExpanded = !$scope.isExpanded;

                        if ($scope.isExpanded) {
                            loadAssessments();
                        }
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-college-test-location-users.addUser', $scope.addUser);

                    $scope.$on('ccc-college-test-location-users.userClicked', function (e, user) {
                        $scope.$emit('ccc-test-location-summary.userClicked', user);
                    });


                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<div class="expand-collapse-item">',
                    '<button ng-click="toggleExpand()" class="btn btn-sm btn-default btn-icon-only expand-collapse-toggle" tabindex="0" role="button">',
                        '<span ng-if="!isExpanded" class="toggle-open">',
                            '<i class="fa fa-caret-right" aria-hidden="true"></i>',
                            '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.SR_TOGGLE_OPEN"></span>',
                        '</span>',
                        '<span ng-if="isExpanded" class="toggle-closed">',
                            '<i class="fa fa-caret-down" aria-hidden="true"></i>',
                            '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.SR_TOGGLE_CLOSED"></span>',
                        '</span>',
                    '</button>',
                    '<h5 class="title">',
                        '{{::testLocation.name}}',
                        '<span ng-if="!testLocation.enabled">',
                            '<span class="sr-only">, disabled</span>',
                            ' <i class="fa fa-pause-circle" aria-hidden="true"></i>',
                        '</span>',
                    '</h5>',
                '</div>',
                '<div ng-if="isExpanded" class="expand-collapse-body">',

                    '<div class="actions">',
                        '<button class="btn btn-sm btn-link" aria-describedby="district1College1LocationTitle1" ng-click="editLocation()" ng-disabled="enabling"><span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-SUMMARY.EDIT_LOCATION"></span></button>',
                        '<button class="btn btn-sm btn-link" aria-describedby="district1College1LocationTitle1" ng-click="addUser()" ng-disabled="enabling"><span class="icon fa fa-plus" role="presentation" aria-hidden="true"></span> Add User</button>',
                    '</div>',

                    '<div class="alert alert-info" ng-if="!testLocation.enabled">',
                        '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-SUMMARY.IS_DISABLED"></span>',
                        '<button class="btn btn-link" ng-click="enable()" ng-disabled="enabling">',
                            '<i class="fa fa-play-circle" aria-hidden="true" ng-if="!enabling"></i>',
                            '<i class="noanim fa fa-spin fa-spinner" aria-hidden="true" ng-if="enabling"></i>',
                            'Enable',
                        '</button>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col col-md-6">',
                            '<div class="list-container user-list-container">',

                                '<h6 class="title"><img class="icon" src="ui/resources/images/icons/group.png" alt="Icon of a group of three people" role="presentation" aria-hidden="true"> Users</h6>',

                                '<ccc-college-test-location-users test-location="testLocation" is-disabled="enabling"></ccc-college-test-location-users>',

                            '</div>',
                        '</div>',
                        '<div class="col col-md-6">',
                            '<div class="list-container assessment-list-container">',

                                '<h6 class="title"><img class="icon" src="ui/resources/images/icons/exam.png" alt="Icon of a paper with writing with a pencil placed on top" role="presentation" aria-hidden="true">Assessments</h6>',

                                '<ccc-content-loading-placeholder ng-if="assessments.length === 0" no-results-info="assessments.length === 0 && !loading" hide-default-no-results-text="true" class="ccc-content-loading-placeholder-small">',
                                    '<div><span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-SUMMARY.NO_ASSESSMENTS"></span> <button ng-click="editLocation()" class="btn btn-sm btn-default" ng-disabled="isDisabled" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-SUMMARY.EDIT_LOCATION" aria-describedby="college-{{::college.cccId}}"></button></div>',
                                '</ccc-content-loading-placeholder>',

                                '<ul class="list-unstyled assessment-list assessment-list-not-selectable" ng-if="!loading && assessments.length">',
                                    '<li class="assessment" ng-repeat="assessment in assessments">',
                                        '<div class="name">',
                                            '{{::assessment.title}}',
                                            '<span class="text-fade"></span>',
                                        '</div>',
                                        '<div class="assessment-data">',
                                            '<span class="data-group version">',
                                                '<span class="data-label">Version</span> ',
                                                '<span class="data">{{::assessment.version}}</span>',
                                            '</span>',
                                        '</div>',
                                    '</li>',
                                '</ul>',
                            '</div>',
                        '</div>',
                    '</div>',

                '</div>'

            ].join('')
        };
    });

})();

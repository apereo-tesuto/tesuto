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

    angular.module('CCC.View.Home').directive('cccTestLocationEdit', function () {

        return {

            restrict: 'E',

            scope: {
                testLocation: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'CurrentUserService',
                'AssessmentsAPIService',

                function ($scope, $element, $timeout, CurrentUserService, AssessmentsAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    // Selected from previous view
                    $scope.selectedCollege = {
                        name: $scope.testLocation.collegeName,
                        cccId: $scope.testLocation.collegeId
                    };

                    // Colleges available to current user
                    $scope.collegeList = _.map(CurrentUserService.getUser().colleges, function (college) {
                        return {
                            name: college.name,
                            cccId: college.cccId
                        };
                    });

                    $scope.assessments = [];
                    $scope.selectedAssessments = [];

                    $scope.submitted = false;
                    $scope.loading = false;

                    $scope.assessmentsLoaded = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refreshAssessmentList = function () {

                        AssessmentsAPIService.getAssessments().then(function (assessments) {

                        	AssessmentsAPIService.getAssessmentsByTestLocationIdWithCollegeAffiliation($scope.testLocation.id).then(function (testLocationAssessments) {

                                $scope.testLocation.assessments = testLocationAssessments;
                                $scope.assessments = assessments;

                                var selectedTestLocationIdMap = _.reduce($scope.testLocation.assessments, function (memo, assessment) {
                                    memo[assessment.id] = true;
                                    return memo;
                                }, {});

                                _.each($scope.assessments, function (assessment) {
                                    assessment.checked = selectedTestLocationIdMap[assessment.id] ? true: false;
                                });
                            });

                        }).finally(function () {
                            $scope.assessmentsLoaded = true;
                        });
                    };

                    var getSelectedAssessments = function () {

                        $timeout(function () {

                            $scope.selectedAssessments = _.filter($scope.assessments, function (assessment) {
                                return assessment.checked;
                            });

                            $scope.testLocation.assessments = $scope.selectedAssessments;
                        });
                    };

                    var doSubmit = function () {
                        $scope.loading = true;

                        return $scope.testLocation.update().then(function () {

                            $scope.$emit('ccc-test-location-edit.complete');

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    $scope.setTestLocationCollege = function (college) {
                        $scope.selectedCollege = college;
                        $scope.testLocation.collegeId = college.cccId;
                        $scope.testLocation.collegeName = college.name;
                        $scope.cccTestLocationCreateForm.$setDirty();
                    };

                    $scope.attemptDoSubmit = function () {
                        $scope.submitted = true;

                        if ($scope['cccTestLocationCreateForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        doSubmit();
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-test-location-edit.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-radio-box.change', getSelectedAssessments);


                    /*============ INITIALIZATION ==============*/

                    refreshAssessmentList();

                }
            ],

            template: [

                '<form name="cccTestLocationCreateForm" novalidate>',

                    '<ccc-test-location-form></ccc-test-location-form>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<div class="row">',
                                '<div class="col-xs-12 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading  || !cccTestLocationCreateForm.$dirty" type="submit" ng-click="attemptDoSubmit()">',
                                        '<i class="fa fa-save noanim" aria-hidden="true"></i>',
                                        '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="loading"></i>',
                                        '<span translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ACTIONS.SAVE"></span>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="loading" ng-click="cancel()" translate="CCC_VIEW_HOME.CCC-TEST-LOCATION-FORM.ACTIONS.CANCEL"></button>',

                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')
        };

    });

})();

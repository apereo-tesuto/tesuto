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
angular.module('CCC.View.Common').directive('cccGroupedActivationsSelector', function () {

        return {

            restrict: 'E',

            scope: {
                activations: '=',
                selectedActivationId: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.activationsGroupedByAssessment = [];

                    $scope.selectedActivation = false;


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var populateactivationsGroupedByAssessment = function () {

                        // first, get a unique list of assessment titles
                        var uniqueAssessmentTitles = _.uniq(_.map($scope.activations, function (activation) {
                            return activation.assessmentTitle;
                        }));

                        // then, add those to our assessment list
                        _.each(uniqueAssessmentTitles, function (title) {

                            $scope.activationsGroupedByAssessment.push({
                                title: title,
                                activations: []
                            });
                        });

                        // then, run thru the history and add matches to their respective group.
                        _.each($scope.activations, function (activation) {

                            _.each($scope.activationsGroupedByAssessment, function (group) {

                                if (activation.assessmentTitle === group.title) {
                                    group.activations.push(activation);
                                }
                            });
                        });
                    };


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setInitiallySelectedActivation = function () {

                        if (!$scope.selectedActivationId) {
                            $scope.selectedActivationId = $scope.activations[0].activationId;
                        }

                        var selectedActivation = _.find($scope.activations, function (activation) {
                            return activation.activationId === $scope.selectedActivationId;
                        });

                        $scope.setSelectedActivation(selectedActivation);
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.setSelectedAssessmentGroup = function (group) {
                        if (group.title !== $scope.selectedActivation.assessmentTitle) {
                            $scope.setSelectedActivation(group.activations[0]);
                        }
                    };

                    $scope.setSelectedActivation = function (activation) {
                        $scope.selectedActivation = activation;
                        $scope.$emit('ccc-grouped-activation-selector.activationSelected', $scope.selectedActivation);
                    };




                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    setInitiallySelectedActivation();

                    populateactivationsGroupedByAssessment();
                }
            ],

            template: [

                '<ul class="nav nav-pills nav-stacked primary-list">',
                    '<li ng-repeat="group in activationsGroupedByAssessment track by $index" ng-class="{\'active\': group.title === selectedActivation.assessmentTitle}">',
                        '<a ccc-autofocus href="#" ng-click="setSelectedAssessmentGroup(group)">{{group.title}}</a>',
                        '<ul ng-show="group.title === selectedActivation.assessmentTitle" class="nav nav-pills nav-stacked sub-list completed-list">',
                            '<li ng-repeat="activation in group.activations track by activation.activationId" ng-class="{\'active\': activation.activationId === selectedActivation.activationId}">',
                                '<a href="#" ng-click="setSelectedActivation(activation)">',
                                    '<span ng-if="activation.activationId === selectedActivation.activationId" class="icon fa fa-chevron-right"></span>',
                                    '<span class="date">{{activation.completedDate}}</span>',
                                    '<span class="college">{{activation.collegeName}}</span>',
                                    '<span class="location">{{activation.locationLabel}}</span>',
                                '</a>',
                            '</li>',
                        '</ul>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

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

    angular.module('CCC.View.Student').directive('cccStudentStartActivationInstructions', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '='
            },

            controller: [

                '$scope',
                '$timeout',
                'AssessmentMetadataAPIService',

                function ($scope, $timeout, AssessmentMetadataAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.starting = false;
                    $scope.started = false;

                    $scope.openingAssessment = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var attemptOpenAssessment = function () {

                        $scope.openingAssessment = true;

                        $scope.activation.open();

                        $timeout(function () {
                            $scope.openingAssessment = false;
                        }, 1000);
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.cancel = function () {
                        $scope.$emit('ccc-student-start-activation-instructions.cancel');
                    };

                    $scope.startAssessment = function () {

                        $scope.$emit('ccc-student-start-activation-instructions.beforeStart');

                        $scope.starting = true;
                        $scope.activation.start().then(function () {

                            $scope.started = true;
                            attemptOpenAssessment();

                            $scope.$emit('ccc-student-start-activation-instructions.startComplete');

                        }, function () {

                            $scope.$emit('ccc-student-start-activation-instructions.startFailed');

                        }).finally(function () {
                            $scope.starting = false;
                        });
                    };

                    $scope.attemptReopenAssessment = attemptOpenAssessment;


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<div class="form-styled ccc-student-start-activation-instructions">',

                    '<div class="panel-body" ng-hide="started">',

                        '<div id="ccc-student-start-activation-instructions-content">',
                            '<h2><span translate="CCC_STUDENT.ACTIVATIONS.START_INSTRUCTIONS.TITLE"></span></h2>',
                            '<div translate="CCC_STUDENT.ACTIVATIONS.START_INSTRUCTIONS.BODY"></div>',
                        '</div>',

                        '<div class="form-submit-controls">',
                            '<button ccc-autofocus  class="btn btn-success btn-submit-button" type="button" ng-click="startAssessment()" ng-disabled="starting || started || loading" aria-describedby="ccc-student-start-activation-instructions-content">',
                                '<span translate="CCC_STUDENT.ACTIVATIONS.START_INSTRUCTIONS.{{activation.status === \'IN_PROGRESS\' ? \'RESUME_ASSESSMENT\' : \'BEGIN_ASSESSMENT\'}}"></span> ',
                                '<span class="fa fa-chevron-right" role="presentation"></span>',
                                '<i class="fa fa-spin fa-spinner noanim" ng-show="starting"></i>',
                            '</button> ',
                            '<button type="button" class="btn btn-default" ng-click="cancel()" ng-disabled="starting || started" translate="CCC_STUDENT.ACTIVATIONS.START_INSTRUCTIONS.CANCEL">Cancel</button>',
                        '</div>',
                    '</div>',

                    '<div class="panel-body" ng-show="started">',
                        '<div id="ccc-student-start-activation-instructions-content-started">',
                            '<h2 translate="CCC_STUDENT.ACTIVATIONS.IN_PROGRESS_INSTRUCTIONS.TITLE">Assessment Started</h2>',
                            '<span translate="CCC_STUDENT.ACTIVATIONS.IN_PROGRESS_INSTRUCTIONS.BODY"></span>',
                        '</div>',
                        '<div><button class="btn btn-default btn-sm" ng-click="attemptReopenAssessment()" ng-disabled="openingAssessment" translate="CCC_STUDENT.ACTIVATIONS.IN_PROGRESS_INSTRUCTIONS.RE_OPEN" aria-describedby="ccc-student-start-activation-instructions-content-started">Tesutoment</button></div>',
                    '</div>',

                '</div>'
            ].join('')

        };

    });

})();

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

    angular.module('CCC.View.Student').directive('cccStudentAssessmentInstructions', function () {

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

                    var wrapAssessmentInstructions = function (instructionsContent) {

                        var wrapperElement = $([
                            '<div class="ccc-student-assessment-instructions-body"></div>',
                        ].join(''));

                        wrapperElement.html(instructionsContent);

                        return wrapperElement.html();
                    };


                    /*============ MODEL ============*/

                    $scope.starting = false;
                    $scope.started = false;

                    $scope.showControls = false;

                    $scope.openingAssessment = false;
                    $scope.loading = true;

                    $scope.instructionsTitle = '<span translate="CCC_STUDENT.ACTIVATIONS.ASSESSMENT_INSTRUCTIONS.TITLE"></span>';
                    $scope.instructionsSubTitle = '<div><span translate="CCC_STUDENT.ACTIVATIONS.ASSESSMENT_INSTRUCTIONS.SUB_TITLE"></span> <span class="emphasize">' + $scope.activation.assessmentTitle + '</span>.</div>';

                    $scope.instructions = [];


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.accept = function () {
                        $scope.$emit('ccc-student-assessment-instructions.accepted');
                    };

                    $scope.decline = function () {
                        $scope.$emit('ccc-student-assessment-instructions.declined');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-instructions-player.ready', function () {
                        $timeout(function () {
                            $scope.showControls = true;
                        }, 500);
                    });


                    /*============ INITIALIZATION ============*/

                    AssessmentMetadataAPIService.getAssessmentMetadata($scope.activation.activationId).then(function (metadata) {

                        if (metadata.instructions) {

                            $scope.instructions = [{
                                'id': 1,
                                'body': wrapAssessmentInstructions(metadata.instructions),
                                'confirm': false
                            }];

                        } else {

                            $scope.$emit('ccc-student-assessment-instructions.noAssessmentInstructions');
                        }

                    }).finally(function () {
                        $scope.loading = false;
                    });
                }
            ],

            template: [
                '<ccc-content-loading-placeholder ng-if="loading"></ccc-content-loading-placeholder>',
                '<div ng-if="!loading && instructions.length">',
                    '<ccc-instructions-player instructions="instructions" instructions-title="instructionsTitle" instructions-sub-title="instructionsSubTitle"></ccc-instructions-player>',
                    '<div ng-if="showControls" class="ccc-instructions-player-outer-actions">',
                        '<button class="btn btn-default" ng-click="decline()"><span translate="CCC_STUDENT.ACTIVATIONS.DECLINE"></span></button>',
                        '<button class="btn btn-success" ng-click="accept()"><span class="fa fa-check-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_STUDENT.ACTIVATIONS.READY"></span></button>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();

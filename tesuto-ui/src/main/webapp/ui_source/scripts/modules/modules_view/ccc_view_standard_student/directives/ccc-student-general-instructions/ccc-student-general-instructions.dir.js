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

    angular.module('CCC.View.Student').directive('cccStudentGeneralInstructions', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '='
            },

            controller: [

                '$scope',
                '$timeout',

                function ($scope, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.showControls = false;

                    $scope.instructionsDisclaimer = '<i class="icon fa fa-info-circle" aria-hidden="true"></i><p><strong>You viewing a demonstration of the Tesutoment system.</strong>.</p>';

                    $scope.instructionsTitle = 'Welcome to Tesuto!';
                    $scope.instructionsSubTitle = '<p>You are about to take the an assessment test created as a demonstration for Tesuto. The purpose of the test is to help underline overall functionality of the systme..</p>';

                    $scope.instructions = [
                        {
                            'id': 1,
                            'body': [
                                '<div class=reading-font>',
                                    '<h2>Answer questions to the best of your ability</h2>',
                                    '<ul class="ccc-instruction-list">',
                                        '<li>',
                                            '<i class="fa fa-circle ccc-instruction-bullet" aria-hidden="true"></i>',
                                            'This test is an <strong>adaptive</strong> test, which means <strong>you must answer each question</strong> before going on to the next question.  You cannot skip a question or return to an earlier question to review or change your answer.',
                                        '</li>',
                                    '</ul>',
                                '</div>'
                            ].join(''),
                            'confirm': false
                        },
                        {
                            'id': 2,
                            'body': [
                                '<div class=reading-font>',
                                    '<h2>Academic Honor Code Acknowledgement</h2>',
                                    '<ul class="ccc-instruction-list">',
                                        '<li>',
                                            '<i class="fa fa-circle ccc-instruction-bullet" aria-hidden="true"></i>',
                                            'Apereo and Tesuto are committed to the highest standards of academic integrity and assume full responsibility for maintaining those standards.',
                                        '</li>',
                                    '</ul>',
                                '</div>'
                            ].join(''),
                            'confirm': true,
                            'confirmText': 'By checking this box, I acknowledge my interest in and ability to assess the Tesuto platform.',
                            'checked': false
                        },
                    ];


                    /*============ BEHAVIOR ============*/

                    $scope.accept = function () {
                        $scope.$emit('ccc-student-general-instructions.accepted');
                    };

                    $scope.decline = function () {
                        $scope.$emit('ccc-student-general-instructions.declined');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-instructions-player.ready', function () {
                        $timeout(function () {
                            $scope.showControls = true;
                        }, 500);
                    });

                    $scope.$on('ccc-instructions-player.notReady', function () {
                        $scope.showControls = false;
                    });


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<ccc-instructions-player instructions-disclaimer="instructionsDisclaimer" instructions="instructions" instructions-title="instructionsTitle" instructions-sub-title="instructionsSubTitle"></ccc-instructions-player>',
                '<div ng-if="showControls" class="ccc-instructions-player-outer-actions">',
                    '<button class="btn btn-default" ng-click="decline()"><span translate="CCC_STUDENT.ACTIVATIONS.DECLINE"></span></button>',
                    '<button class="btn btn-success" ng-click="accept()"><span class="fa fa-check-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_STUDENT.ACTIVATIONS.READY"></span></button>',
                '</div>'

            ].join('')

        };

    });

})();

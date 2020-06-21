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

    angular.module('CCC.View.Student').directive('cccStudentAuthorizeActivation', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'CurrentStudentService',

                function ($scope, $element, $timeout, CurrentStudentService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.student = CurrentStudentService.getStudent();
                    $scope.proctorCode = '';

                    $scope.submitted = false;
                    $scope.loading = false;

                    $scope.attemptingAuthorizationWithoutPasscode = true;

                    $scope.done = false;


                    /*============ BEHAVIOR ============*/

                    $scope.submit = function () {

                        $scope.submitted = true;

                        // if the form is invalid nogo
                        if ($scope['cccStudentStartActivationProctorCode'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        $scope.loading = true;

                        $scope.activation.authorizeProctorCode($scope.proctorCode).then(function (results) {
                            if (results.data === true) {

                                $scope.$emit('ccc-student-start-activation.authorized');
                                $scope.done = true;

                            } else {

                                $scope.$emit('ccc-student-start-activation.denied');
                            }

                        }, function (err) {

                            if (err.status >= 400 && err.status <= 499 && err.status !== 429) { // covering all our bases for invalid passwords to avoid triggering unnecessary modals.
                                $scope.cccStudentStartActivationProctorCode.proctorCode.$setValidity('isPasswordValid', false);
                            }

                            if (err.status === 429) { // activation locked after too many bad attempts
                                $scope.cccStudentStartActivationProctorCode.proctorCode.$setValidity('activationLocked', false);
                            }

                        }).finally(function () {

                            $scope.loading = false;
                        });
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-student-start-activation.cancel');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$watch('proctorCode', function () {
                        $scope.cccStudentStartActivationProctorCode.proctorCode.$setValidity('isPasswordValid', true);
                        $scope.cccStudentStartActivationProctorCode.proctorCode.$setValidity('activationLocked', true);
                    });


                    /*============ INITIALIZATION ============*/

                    $scope.activation.authorizeWithoutPasscode().then(function (isAuthorized) {

                        // if we can authorize without a passcode then we are good to go. Let our parents know!
                        if (isAuthorized) {

                            $scope.$emit('ccc-student-start-activation.authorized');

                        // if we can't authorize without a passcode then we need to stay here and re-focus on the password area
                        } else {

                            $scope.attemptingAuthorizationWithoutPasscode = false;

                            // wrap in a timeout to give angular a digest loop to show the actual passcode form
                            $timeout(function () {
                                $element.find('form input[ccc-autofocus]').focus();
                            }, 1);
                        }
                    });
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-if="attemptingAuthorizationWithoutPasscode" tabindex="-1" ccc-autofocus></ccc-content-loading-placeholder>',

                '<form ng-submit="submit()" class="form-styled" name="cccStudentStartActivationProctorCode" ng-show="!attemptingAuthorizationWithoutPasscode">',

                    '<div class="form-group" ccc-show-errors="cccStudentStartActivationProctorCode.proctorCode.$dirty || submitted">',

                        '<label for="proctorCode"><h2><i class="fa fa-lock"></i> <span translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.VALIDATION_CODE"></span></h2></label>',
                        '<input ccc-autofocus type="password" aria-required="true" id="proctorCode" class="form-control input-lg" name="proctorCode" placeholder="{{ \'CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ENTER_CODE\' | translate }}" ',
                            'required ',
                            'ng-maxlength="100" ',
                            'ng-model-options="{ debounce: 100 }" ',
                            'ng-disabled="loading || done" ',
                            'ng-model="proctorCode" ',
                            'ccc-validation-badge="cccStudentStartActivationProctorCode" ',
                            'ccc-validation-badge-style="fullWidth" ',
                            'aria-describedby="cccProctorCodeErrors" ',
                        '> ',

                        '<div id="cccProctorCodeErrors" class="ccc-validation-messages noanim ccc-validation-messages-proctor-code" ng-messages="cccStudentStartActivationProctorCode.proctorCode.$error">',
                            '<p ng-message="required" class="noanim"><span translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ERROR_REQUIRED"></span></p>',
                            '<p ng-message="isPasswordValid" class="noanim"><span translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ERROR_INVALID"></span></p>',
                            '<p ng-message="activationLocked" class="noanim"><span translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ERROR_ACTIVATIONLOCKED"></span></p>',
                            '<p ng-message="maxlength" class="noanim"><span translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ERROR_MAXLENGTH"></span></p>',
                        '</div>',

                        '<p class="help-block" translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.ALERT"></p>',

                    '</div>',

                    '<div class="form-submit-controls">',
                        '<button class="btn btn-primary btn-submit-button change-view" type="button" ng-click="submit()" ng-disabled="loading" translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.CONTINUE">',
                            'Continue ',
                            '<span class="fa fa-chevron-right" role="presentation"></span>',
                            '<i class="fa fa-spin fa-spinner noanim" ng-show="loading"></i>',
                        '</button> ',
                        '<button type="button" class="btn btn-default" ng-click="cancel()" ng-disabled="loading" translate="CCC_STUDENT.ACTIVATIONS.PROCTOR_CODE.CANCEL">Cancel</button>',
                    '</div>',
                '</form>'
            ].join('')

        };

    });

})();

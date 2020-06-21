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

    angular.module('CCC.View.Home').directive('cccUserCreate', function () {

        return {

            restrict: 'E',

            scope: {
                initialUserFields: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$q',
                '$timeout',
                'Moment',
                'UserClass',
                'ModalService',

                function ($scope, $element, $q, $timeout, Moment, UserClass, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var giveUserAccessToSelectedPrimaryCollege = function () {

                        $scope.user.collegeIds.push($scope.user.primaryCollegeId);

                        return $scope.user.populateCollegeDataFromCollegeIds().then(function () {

                            var collegeResetDeferred = $q.defer();

                            $scope.$broadcast('ccc-user-college-selector.requestReset', function () {
                                collegeResetDeferred.resolve();
                            }, function (err) {
                                collegeResetDeferred.reject(err);
                            });

                            return collegeResetDeferred.promise;
                        });
                    };

                    var finishFormSubmission = function () {

                        $scope.loading = true;

                        $scope.user.create().then(function () {

                            $scope.$emit('ccc-user-create.userCreated', $scope.user);

                        }, function (err) {

                            // the only error code that should come back is 400
                            if (err.status === 400) {
                                ModalService.openAlertModal('CCC_VIEW_HOME.WORKFLOW.CREATE_USER.DUPLICATE_USER_MODAL.TITLE', 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.DUPLICATE_USER_MODAL.MESSAGE', 'warning');
                            }

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var showPrimaryCollegeIdModal = function () {

                        var buttonConfigs = {
                            cancel: {
                                title: 'Cancel',
                                btnClass: 'btn-default'
                            },
                            okay: {
                                title: 'Continue',
                                btnClass: 'btn-primary',
                                btnIcon: 'fa-check'
                            }
                        };

                        var primaryCollegeIdModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.PRIMARY_COLLEGE_ID_MODAL.TITLE',
                            message: 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.PRIMARY_COLLEGE_ID_MODAL.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'warning'
                        });

                        primaryCollegeIdModal.result.then(function () {
                            giveUserAccessToSelectedPrimaryCollege().then(finishFormSubmission);
                        });
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccUserCreateForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        // check to see if the user has access to their primaryCollege
                        if (_.contains($scope.user.collegeIds, $scope.user.primaryCollegeId) === false) {
                            showPrimaryCollegeIdModal();
                        } else {
                            finishFormSubmission();
                        }
                    };

                    var resetForm = function () {

                        $scope.submitted = false;
                        $scope.loading = false;
                        $scope.user = new UserClass($.extend({}, $scope.initialUserFields));

                        // after used once... just clear these out so if externally they reset we get a full reset
                        $scope.initialUserFields = {};
                    };


                    /*============ BEHAVIOR ==============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-user-create.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-user-create.requestReset', resetForm);


                    /*============ INITIALIZATION ==============*/

                    resetForm();
                }
            ],

            template: [


                '<form name="cccUserCreateForm" novalidate>',

                    '<ccc-user-form></ccc-user-form>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<div class="row">',
                                '<div class="col-xs-12 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading" type="submit" ng-click="attemptDoSubmit()">',
                                        '<i class="fa fa-plus noanim" aria-hidden="true"></i>',
                                        '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="loading"></i>',
                                        '<span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.BUTTONS.ADD_USER"></span>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="loading" ng-click="cancel()" translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.BUTTONS.CANCEL"></button>',

                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')
        };

    });

})();

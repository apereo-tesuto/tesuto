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

    angular.module('CCC.View.Home').directive('cccUserEdit', function () {

        return {

            restrict: 'E',

            scope: {
                user: '='
            },

            controller: [

                '$scope',
                '$element',
                '$q',
                '$timeout',
                '$translate',
                'ModalService',
                'LocationService',

                function ($scope, $element, $q, $timeout, $translate, ModalService, LocationService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var initialAccountStatus = $scope.user.enabled;


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.loading = false;
                    $scope.saving = false;
                    $scope.deleting = false;


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
                        $scope.saving = true;

                        $scope.user.update().then(function () {

                            $scope.$emit('ccc-user-edit.updated', $scope.user);
                            LocationService.breakTestLocationsCache();

                        }, function (err) {

                            // the only error code that should come back is 400
                            if (err.status === 400) {
                                ModalService.openAlertModal('CCC_VIEW_HOME.WORKFLOW.CREATE_USER.DUPLICATE_USER_MODAL.TITLE', 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.DUPLICATE_USER_MODAL.MESSAGE', 'warning');
                            }
                        }).finally(function () {
                            $scope.loading = false;
                            $scope.saving = false;
                        });
                    };

                    var showPrimaryCollegeIdModal = function () {

                        var deferred = $q.defer();

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
                            giveUserAccessToSelectedPrimaryCollege().then(function () {
                                return deferred.resolve();

                            }, function () {
                                return deferred.reject();
                            });

                        }, function () {
                            return deferred.reject();
                        });

                        return deferred.promise;
                    };

                    var showAccountStatusModal = function (status) {

                        var deferred = $q.defer();

                        var action = status ? 'enable' : 'disable';

                        var buttonConfigs = {
                            cancel: {
                                title: 'CCC_VIEW_HOME.USER_EDIT.ACCOUNT_MODAL.CANCEL',
                                btnClass: 'btn-default'
                            },
                            okay: {
                                title: 'CCC_VIEW_HOME.USER_EDIT.ACCOUNT_MODAL.YES',
                                btnClass: 'btn-primary',
                                btnIcon: 'fa-check'
                            }
                        };

                        var accountStatusModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.USER_EDIT.ACCOUNT_MODAL.TITLE',
                            message: $translate.instant('CCC_VIEW_HOME.USER_EDIT.ACCOUNT_MODAL.MSG_1') + action + $translate.instant('CCC_VIEW_HOME.USER_EDIT.ACCOUNT_MODAL.MSG_2'),
                            buttonConfigs: buttonConfigs,
                            type: 'warning'
                        });

                        accountStatusModal.result.then(function () {
                            $scope.user.enabled = status;
                            deferred.resolve();

                        }, function () {
                            $scope.user.enabled = initialAccountStatus;
                            deferred.reject();
                        });

                        return deferred.promise;
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccUserEditForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        var needsCollegeModal = _.contains($scope.user.collegeIds, $scope.user.primaryCollegeId) === false;
                        var needsAccountModal = $scope.user.enabled !== initialAccountStatus;

                        if (needsCollegeModal && needsAccountModal) {
                            showPrimaryCollegeIdModal().then(function () {
                                showAccountStatusModal($scope.user.enabled).then(finishFormSubmission);
                            });

                        } else if (needsCollegeModal) {
                            showPrimaryCollegeIdModal().then(finishFormSubmission);

                        } else if (needsAccountModal) {
                            showAccountStatusModal($scope.user.enabled).then(finishFormSubmission);

                        } else {
                            finishFormSubmission();
                        }
                    };


                    /*============ BEHAVIOR ==============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.attemptDelete = function () {

                            var buttonConfigs = {
                                cancel: {
                                    title: 'Cancel',
                                    btnClass: 'btn-primary'
                                },
                                okay: {
                                    title: 'Delete User',
                                    btnClass: 'btn-default',
                                    btnIcon: 'fa-times-circle'
                                }
                            };

                            var deleteUserModal = ModalService.openConfirmModal({
                                title: 'CCC_VIEW_HOME.USER_EDIT.DELETE_USER.TITLE',
                                message: 'CCC_VIEW_HOME.USER_EDIT.DELETE_USER.MESSAGE',
                                buttonConfigs: buttonConfigs,
                                type: 'warning'
                            });

                            deleteUserModal.result.then(function () {

                                $scope.loading = true;
                                $scope.deleting = true;

                                $scope.user.delete().then(function () {

                                    $scope.$emit('ccc-user-edit.userDeleted', $scope.user);

                                }).finally(function () {
                                    $scope.loading = false;
                                    $scope.deleting = false;
                                });
                            });
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-user-edit.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<form name="cccUserEditForm" novalidate>',

                    '<ccc-user-form></ccc-user-form>',

                    // Enable or disable user account section
                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<h2 class="section-title section-title-large" translate="CCC_VIEW_HOME.USER_EDIT.ACCOUNT_STATUS.TITLE"></h2>',
                            '<div class="well">',

                                '<label class="radio-inline" for="ccc-user-edit-enabled">',
                                    '<input name="account-status" ng-model="user.enabled" type="radio" id="ccc-user-edit-enabled" ng-value="true" ng-disabled="loading"/> ',
                                    '<span translate="CCC_VIEW_HOME.USER_EDIT.ACCOUNT_STATUS.ACTIVE"></span>',
                                '</label>',
                                '<label class="radio-inline" for="ccc-user-edit-disabled">',
                                    '<input name="account-status" ng-model="user.enabled" type="radio" id="ccc-user-edit-disabled" ng-value="false" ng-disabled="loading" /> ',
                                    '<span translate="CCC_VIEW_HOME.USER_EDIT.ACCOUNT_STATUS.INACTIVE"></span>',
                                '</label>',

                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<div class="row">',

                                '<div class="col-sm-8 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading || !cccUserEditForm.$dirty" type="submit" ng-click="attemptDoSubmit()">',
                                        '<i class="fa fa-save noanim" aria-hidden="true" ng-if="!saving"></i>',
                                        '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="saving"></i>',
                                        '<span translate="CCC_VIEW_HOME.USER_EDIT.BUTTONS.OKAY"></span>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="loading" ng-click="cancel()" translate="CCC_VIEW_HOME.USER_EDIT.BUTTONS.CANCEL"></button>',

                                '</div>',

                                '<div class="col-sm-4 ccc-form-submit-controls text-right">',

                                    '<button class="btn btn-danger btn-full-width-when-small btn-submit-button btn-icon-left" ng-disabled="loading" ng-click="attemptDelete()">',
                                        '<i class="fa fa-times-circle noanim" aria-hidden="true"></i>',
                                        '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="deleting"></i>',
                                        '<span translate="CCC_VIEW_HOME.USER_EDIT.BUTTONS.DELETE"></span>',
                                    '</button>',

                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')
        };

    });

})();

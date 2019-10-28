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

    angular.module('CCC.View.Home').directive('cccProctorPasscodes', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'Moment',
                'PasscodesAPIService',

                function ($scope, $element, $timeout, Moment, PasscodesAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    $scope.isConfirmVisible = false;
                    $scope.hasErrors = false;

                    $scope.visibleTab = 'PUBLIC';

                    $scope.publicIsLoading = false;
                    $scope.privateIsLoading = false;

                    $scope.publicPasscode = {};
                    $scope.privatePasscode = {};

                    $scope.expirationTime = new Moment().ceil(30, 'minutes').format('h:mm A');


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.showConfirmScreen = function (element) {
                        $scope.isConfirmVisible = true;
                        $scope.hasErrors = false;

                        if (element.currentTarget.id === 'public') {
                            $scope.visibleTab = 'PUBLIC';
                        }

                        if (element.currentTarget.id === 'private') {
                            $scope.visibleTab = 'PRIVATE';
                        }
                    };

                    $scope.hideConfirmScreen = function () {
                        $scope.isConfirmVisible = false;
                        $scope.$emit('ccc-proctor-passcodes.confirmHidden');
                    };

                    $scope.generatePublicPasscode = function () {
                        $scope.hideConfirmScreen();
                        $scope.publicIsLoading = true;

                        $timeout(function () {
                            PasscodesAPIService.generatePublicPasscode().then(function (passcode) {

                                $scope.publicPasscode = passcode;
                                $scope.publicIsLoading = false;

                            }, function (error) {

                                $scope.publicIsLoading = false;
                                $scope.hasErrors = true;
                            });
                        }, 1000);
                    };

                    $scope.generatePrivatePasscode = function () {
                        $scope.hideConfirmScreen();
                        $scope.privateIsLoading = true;

                        $timeout(function () {
                            PasscodesAPIService.generatePrivatePasscode().then(function (passcode) {

                                $scope.privatePasscode = passcode;
                                $scope.privateIsLoading = false;

                            }, function (error) {

                                $scope.privateIsLoading = false;
                                $scope.hasErrors = true;
                            });
                        }, 1000);
                    };


                    /*============ LISTENERS ===========*/

                    $scope.$watch('isConfirmVisible', function () {

                        if ($scope.isConfirmVisible === true) {
                            $timeout(function () {
                                $element.find('.btn-generate-new-passcode').focus();
                            });
                        }
                    });


                    /*============ INITIALIZATION ===========*/

                    PasscodesAPIService.getPasscodes().then(function (passcodes) {

                        _.each(passcodes, function (passcode) {

                            if (passcode.type === 'PUBLIC') {
                                $scope.publicPasscode = passcode;
                            }

                            if (passcode.type === 'PRIVATE') {
                                $scope.privatePasscode = passcode;
                            }
                        });

                        // if a passcode has expired, we auto generate a new one
                        if (_.isEmpty($scope.publicPasscode)) {
                            $scope.generatePublicPasscode();
                        }

                        if (_.isEmpty($scope.privatePasscode)) {
                            $scope.generatePrivatePasscode();
                        }

                    }, function (error) {

                        $scope.hasErrors = true;
                    });

                }
            ],

            template: [

                '<div ng-if="hasErrors" class="ccc-proctor-passcodes errors bg-danger">',
                    '<span class="text-danger"><span class="fa fa-warning" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.ERROR"></span></span>',
                '</div>',

                '<p ng-if="!isConfirmVisible" class="text-warning"><span class="fa fa-clock-o icon" role="presentation" aria-hidden="true"></span> To help keep your passcodes safe, this window will close after 2 minutes.</p>',

                '<div ng-if="!isConfirmVisible" class="well-with-tabs">',
                    '<ul class="nav nav-tabs" role="tablist">',
                        '<li role="presentation" ng-class="visibleTab === \'PUBLIC\' ? \'active\' : \'\'"><a ng-click="visibleTab = \'PUBLIC\'" href="" aria-controls="public-passcode" aria-selected="{{visibleTab === \'PUBLIC\'}}" role="tab" id="public-passcode"><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.TAB_PUBLIC"></span></a></li>',
                        '<li role="presentation" ng-class="visibleTab === \'PRIVATE\' ? \'active\' : \'\'"><a ng-click="visibleTab = \'PRIVATE\'" href="" aria-controls="private-passcode" aria-selected="{{visibleTab === \'PRIVATE\'}}" role="tab" id="private-passcode"><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.TAB_PRIVATE"></span></a></li>',
                    '</ul>',

                    '<div class="tab-content">',
                        '<div ng-if="visibleTab === \'PUBLIC\'" role="tabpanel" class="tab-pane active" aria-labelledby="public-passcode">',
                            '<div class="well ccc-proctor-passcodes public">',
                                '<h3 tabindex="0"><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PUBLIC_PASSCODE"></span> <strong ng-class="{hide: publicIsLoading}" class="text-success">{{publicPasscode.value}}</strong><span ng-class="{hide: !publicIsLoading}" class="fa fa-refresh fa-spin" role="presentation" aria-hidden="true"></span></h3>',
                                '<p class="text-success"><span class="fa fa-check-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PUBLIC_PASSCODE_BODY"></span></p>',
                                '<p><strong><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PUBLIC_PASSCODE_INFO" translate-values="{EXPIRATION_TIME: expirationTime}"></span></strong></p>',
                                '<div class="actions">',
                                    '<button id="public" ng-click="showConfirmScreen($event)" class="btn btn-sm btn-default"><span class="fa fa-key" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PUBLIC_PASSCODE_GENERATE"></span></button>',
                                '</div>',
                            '</div>',
                        '</div>',

                        '<div ng-if="visibleTab === \'PRIVATE\'" role="tabpanel" class="tab-pane active" aria-labelledby="private-passcode">',
                            '<div class="well ccc-proctor-passcodes private">',
                                '<h3 tabindex="0"><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PRIVATE_PASSCODE"></span> <strong ng-class="{hide: privateIsLoading}" class="text-warning">{{privatePasscode.value}}</strong><span ng-class="{hide: !privateIsLoading}" class="fa fa-refresh fa-spin" role="presentation" aria-hidden="true"></span></h3>',
                                '<p class="text-warning"><span class="fa fa-warning" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PRIVATE_PASSCODE_BODY"></span></p>',
                                '<p><strong><span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PRIVATE_PASSCODE_INFO"></span></strong></p>',
                                '<div class="actions">',
                                    '<button id="private" ng-click="showConfirmScreen($event)" class="btn btn-sm btn-default"><span class="fa fa-key" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.PRIVATE_PASSCODE_GENERATE"></span></button>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div ng-if="isConfirmVisible" class="ccc-proctor-passcodes confirmation">',
                    '<h3 ng-if="visibleTab === \'PUBLIC\'" translate="CCC_VIEW_HOME.MODAL.PASSCODES.TITLE_GENERATE_PUBLIC"></h3>',
                    '<h3 ng-if="visibleTab === \'PRIVATE\'" translate="CCC_VIEW_HOME.MODAL.PASSCODES.TITLE_GENERATE_PRIVATE"></h3>',
                    '<p class="text-warning"><span class="fa fa-warning" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.MODAL.PASSCODES.GENERATE_WARNING"></span></p>',
                    '<div class="actions">',
                        '<button ng-if="visibleTab === \'PUBLIC\'" ng-click="generatePublicPasscode()" class="btn btn-primary btn-generate-new-passcode" translate="CCC_VIEW_HOME.MODAL.PASSCODES.PASSCODE_GENERATE"></button>',
                        '<button ng-if="visibleTab === \'PRIVATE\'" ng-click="generatePrivatePasscode()" class="btn btn-primary btn-generate-new-passcode" translate="CCC_VIEW_HOME.MODAL.PASSCODES.PASSCODE_GENERATE"></button>',
                        '<button ng-click="hideConfirmScreen()" class="btn btn-default" translate="CCC_VIEW_HOME.MODAL.PASSCODES.CANCEL"></button>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();

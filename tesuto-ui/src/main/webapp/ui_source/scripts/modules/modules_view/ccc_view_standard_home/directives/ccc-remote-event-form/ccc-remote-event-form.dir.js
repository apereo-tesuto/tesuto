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

    angular.module('CCC.View.Home').directive('cccRemoteEventForm', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent:'=',
                submitted: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'Moment',
                'LocationService',

                function ($scope, $element, $timeout, Moment, LocationService) {

                    /*============ PRIVATE VARIABLES ============*/


                    /*============ MODEL ============*/

                    $scope.locationsLoading = true;

                    $scope.getLocationName = function (location) {
                        return location.name;
                    };

                    $scope.getLocationId = function (location) {
                        return location.id;
                    };

                    // functionality changes when we are editing vs creating
                    $scope.mode = false;

                    $scope.filteredTestCenters = [];

                    $scope.subForms = {};


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadLocationsForCollege = function (collegeId, collegeHasChanged) {

                        $scope.locationsLoading = true;

                        if (collegeHasChanged) {
                            $scope.remoteEvent.testLocationId = false;
                            $scope.remoteEvent.assessmentIdentifiers = [];
                        }

                        LocationService.getTestCenterList().then(function (testCenters) {

                            // we only want the test centers for this college and those that are used for "REMOTE" proctoring
                            var filteredTestCenters = _.filter(testCenters, function (testCenter) {
                                return testCenter.college.cccId === collegeId && testCenter.locationType === 'REMOTE';
                            });

                            $scope.filteredTestCenters = filteredTestCenters;

                            $scope.$broadcast('ccc-item-dropdown.setItems', filteredTestCenters, 'cccLocationDropdown');
                            $scope.$broadcast('ccc-item-dropdown.setSelectedItemId', $scope.remoteEvent.testLocationId, 'cccLocationDropdown');

                        }, function () {

                            $scope.$broadcast('ccc-item-dropdown.setItems', [], 'cccLocationDropdown');

                        }).finally(function () {
                            $scope.locationsLoading = false;
                        });
                    };

                    var setSelectedCollege = function (college) {

                        var collegeIdHasChanged = $scope.remoteEvent.collegeId !== college.cccId;

                        loadLocationsForCollege(college.cccId, collegeIdHasChanged);
                        $scope.remoteEvent.collegeId = college.cccId;
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.cancel = function () {
                        $scope.$emit('ccc-remote-event-form.cancel');
                    };

                    $scope.getEmailsDoNotMatch = function () {
                        return $.trim($scope.remoteEvent.proctorEmailVerify) !== $.trim($scope.remoteEvent.proctorEmail);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-colleges-list.collegeSelected', function (e, college) {
                        setSelectedCollege(college);
                    });

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {
                        $scope.remoteEvent.testLocationId = itemId;
                    });

                    $scope.$on('ccc-assessments-list.selectedAssessmentsChanged', function (e, selectedAssessments, deliveryType) {

                        $scope.remoteEvent.deliveryType = deliveryType;

                        $scope.remoteEvent.assessmentIdentifiers = _.map(selectedAssessments, function (assessment) {
                            return {
                                identifier: assessment.identifier,
                                namespace: assessment.namespace
                            };
                        });
                    });

                    $scope.$watch('remoteEvent.testEventId', function () {
                        $scope.mode = $scope.remoteEvent.testEventId === false ? 'CREATE' : 'EDIT';
                    });


                    /*============ INITIALIZATOIN ============*/

                    if ($scope.remoteEvent.collegeId === false) {
                        loadLocationsForCollege(false, false);
                    }
                }
            ],

            template: [

                '<div class="ng-form" name="remoteEventForm">',

                    '<div class="row margin-bottom-md">',
                        '<div class="col-xs-12">',

                            '<h2 class="section-title section-title-large" id="nameLabel" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_TEST_EVENT_NAME"></h2>',

                            '<div class="form-group">',
                                '<p class="help-block help-block-sub-title" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_TEST_EVENT_NAME_HELP"></p>',
                                '<div ccc-show-errors="remoteEventForm.name.$dirty || submitted">',
                                    '<input type="text" ccc-autofocus id="name" aria-labelledby="nameLabel" class="form-control" name="name" placeholder="Test Event Name" ',
                                        'required ',
                                        'ng-maxlength="120" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.name" ',
                                        'ccc-validation-badge="remoteEventForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="nameErrors" ',
                                    '> ',
                                    '<div id="nameErrors" class="ccc-validation-messages noanim ccc-validation-messages-test-event-name" ng-messages="remoteEventForm.name.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.TEST_EVENT_NAME_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.TEST_EVENT_NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.TEST_EVENT_NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="row margin-bottom-sm">',
                        '<div class="col-xs-12">',

                            '<h2 class="section-title section-title-large" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_DATES"></h2>',
                            '<p class="help-block help-block-sub-title" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_DATES_HELP"></p>',

                            '<div class="row">',
                                '<div class="col-sm-4" ccc-show-errors="remoteEventForm.startDate.$dirty || submitted">',
                                    '<label translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_FROM"></label>',
                                    '<ccc-date-picker ccc-autofocus name="startDate" is-disabled="isDisabled" date="remoteEvent.startDate" id="ccc-user-temporary-account-selector-start-date-input" label="start date"></ccc-date-picker>',
                                '</div>',
                                '<div class="col-sm-4" ccc-show-errors="remoteEventForm.endDate.$dirty || submitted || remoteEventForm.endDate.$error.cccMinDate">',
                                    '<label translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_TO"></label>',
                                    '<ccc-date-picker is-disabled="isDisabled" name="endDate" min-date="remoteEvent.startDate" date="remoteEvent.endDate" id="ccc-user-temporary-account-selector-end-date-input" label="end date"></ccc-date-picker>',
                                '</div>',
                            '</div>',

                            '<div class="row">',
                                '<div class="col-sm-8" ccc-show-errors="remoteEventForm.endDate.$error.cccMinDate">',
                                    '<div class="ccc-validation-messages ccc-validation-messages-standalone">',
                                        '<p ng-if="remoteEventForm.endDate.$error.cccMinDate">',
                                            '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                                            '<span translate="CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.VALIDATION_OVERLAP"></span>',
                                        '</p>',
                                    '</div>',
                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="row margin-bottom-md">',
                        '<div class="col-xs-12">',

                            '<h2 class="section-title section-title-large" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_LOCATION"></h2>',
                            '<p class="help-block help-block-sub-title" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_LOCATION_HELP"></p>',

                            '<div class="form-group">',

                                '<div class="form-group">',

                                    '<label translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_COLLEGE"></label>',
                                    '<div ccc-show-errors="submitted">',
                                        '<ccc-colleges-dropdown class="btn-full-width" show-all-colleges="true" selected-ccc-id="remoteEvent.collegeId" auto-select-first-college="false" is-required="true" is-disabled="mode === \'EDIT\'"></ccc-colleges-dropdown>',
                                    '</div>',

                                '</div>',

                                '<div class="form-group">',

                                    '<label translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_TEST_LOCATION"></label>',
                                    '<div ccc-show-errors="submitted">',

                                        // we use ng-show instead of ng-if here so that the invalid required dropdown will force the form to be invalid during submission
                                        '<ccc-item-dropdown id="cccLocationDropdown" ng-show="locationsLoading || !locationsLoading && filteredTestCenters.length" class="btn-full-width" is-disabled="remoteEvent.collegeId === false || mode === \'EDIT\'" is-required="true" required-error-msg="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.TEST_LOCATION_REQUIRED" icon="fa-university" initial-item-id="remoteEvent.testLocationId" loading="locationsLoading" get-item-id="getLocationId" get-item-name="getLocationName"></ccc-item-dropdown>',

                                        '<ccc-content-loading-placeholder ng-if="!(locationsLoading || !locationsLoading && filteredTestCenters.length)" no-results="!locationsLoading && !filteredTestCenters.length" hide-default-no-results-text="true">',
                                            '<div translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NO_TEST_LOCATION_WARNING"></div>',
                                        '</ccc-content-loading-placeholder>',

                                    '</div>',

                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="row margin-bottom-md">',
                        '<div class="col-xs-12">',

                            '<h2 class="section-title section-title-large" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_PROCTOR"></h2>',
                            '<p class="help-block help-block-sub-title" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_PROCTOR_HELP"></p>',

                            '<div class="form-group">',

                                '<div ccc-show-errors="remoteEventForm.proctorFirstName.$dirty || submitted">',
                                    '<label id="proctorFirstNameLabel" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_FIRST_NAME"></label>',
                                    '<input type="text" id="proctorFirstName" aria-labelledby="proctorFirstNameLabel" class="form-control" name="proctorFirstName" placeholder="First Name" ',
                                        'required ',
                                        'ng-maxlength="64" ',
                                        'ng-pattern="/^[A-z|0-9|\\.|\\-|\\`|\\,|\\\'|\\s]+$/i" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.proctorFirstName" ',
                                        'ccc-validation-badge="remoteEventForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="proctorFirstNameErrors" ',
                                    '/> ',
                                    '<div id="proctorFirstNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-first-name" ng-messages="remoteEventForm.proctorFirstName.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.FIRST_NAME_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div class="form-group">',

                                '<div ccc-show-errors="remoteEventForm.proctorLastName.$dirty || submitted">',
                                    '<label id="proctorLastNameLabel" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_LAST_NAME"></label>',
                                    '<input type="text" id="proctorLastName" aria-labelledby="proctorLastNameLabel" class="form-control" name="proctorLastName" placeholder="Last Name" ',
                                        'required ',
                                        'ng-maxlength="64" ',
                                        'ng-pattern="/^[A-z|0-9|\\.|\\-|\\`|\\,|\\\'|\\s]+$/i" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.proctorLastName" ',
                                        'ccc-validation-badge="remoteEventForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="proctorLastNameErrors" ',
                                    '/> ',
                                    '<div id="proctorLastNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-last-name" ng-messages="remoteEventForm.proctorLastName.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.LAST_NAME_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div class="form-group">',

                                '<div ccc-show-errors="remoteEventForm.proctorEmail.$dirty || submitted">',
                                    '<label id="emailLabel" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_EMAIL"></label>',
                                    '<input type="text" id="email" aria-labelledby="emailLabel" class="form-control" name="proctorEmail" placeholder="Email" ',
                                        'ng-maxlength="120" ',
                                        'autocomplete="off" ',
                                        'ccc-required-email="true" ',
                                        'autocomplete="false" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.proctorEmail" ',
                                        'ccc-validation-badge="remoteEventForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="emailErrors" ',
                                    '/> ',
                                    '<div id="emailErrors" class="ccc-validation-messages noanim ccc-validation-messages-email" ng-messages="remoteEventForm.proctorEmail.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EMAIL_MAX"></span></p>',
                                        '<p ng-message="requiredEmail" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.EMAIL"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div class="form-group" ng-if="!remoteEvent.proctorEmail || remoteEventForm.proctorEmail.$dirty">',

                                '<div ccc-show-errors="remoteEventForm.proctorEmailVerify.$dirty || submitted">',
                                    '<label id="emailVerifyLabel" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_EMAIL_VERIFY"></label>',
                                    '<input type="text" id="emailVerify" aria-labelledby="emailVerifyLabel" class="form-control" name="proctorEmailVerify" placeholder="Verify Email" ',
                                        'required ',
                                        'autocomplete="off" ',
                                        'autocomplete="false" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.proctorEmailVerify" ',
                                        'ccc-validation-expression="!getEmailsDoNotMatch()"',
                                        'ccc-validation-badge="remoteEventForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="emailVerifyErrors" ',
                                    '/> ',
                                    '<div id="emailVerifyErrors" class="ccc-validation-messages noanim ccc-validation-messages-email-verify" ng-messages="remoteEventForm.proctorEmailVerify.$error">',
                                        '<p class="noanim" ng-message="cccValidationExpression, required"><span translate="This field should match the email address above"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div class="form-group">',

                                '<label for="proctorPhone" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.INPUT_LABEL_PHONE"></label>',
                                '<div ccc-show-errors="remoteEventForm.proctorPhone.$dirty || submitted">',
                                    '<input type="text" id="proctorPhone" class="form-control" name="proctorPhone" placeholder="Ten Digit Phone" ',
                                        'autocomplete="off" ',
                                        'ui-us-phone-number ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="remoteEvent.proctorPhone" ',
                                        'ccc-validation-badge="userDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="proctorPhoneErrors" ',
                                        'ng-maxlength="14" ',
                                    '/> ',
                                    '<div id="proctorPhoneErrors" class="ccc-validation-messages noanim ccc-validation-messages-phone" ng-messages="remoteEventForm.proctorPhone.$error">',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PHONE_LENGTH"></span></p>',
                                        '<p ng-message="usPhoneNumber" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PHONE"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="row margin-bottom-sm">',
                        '<div class="col-xs-12" ccc-show-errors="submitted">',

                            '<h2 class="section-title section-title-large" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_ASSESSMENTS"></h2>',
                            '<p class="help-block help-block-sub-title" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.SECTION_ASSESSMENTS_HELP"></p>',

                            '<ccc-assessments-list ng-if="mode === \'CREATE\'" initial-assessment-identifiers="remoteEvent.assessmentIdentifiers" is-required="true" test-location-id="remoteEvent.testLocationId" no-results-msg="CCC_VIEW_HOME.CCC-REMOTE-EVENT-FORM.NO_ASSESSMENTS_MSG"></ccc-assessments-list>',
                            '<ccc-assessments-list-read-only ng-if="mode === \'EDIT\'" assessment-identifiers="remoteEvent.assessmentIdentifiers" test-location-id="remoteEvent.testLocationId"></ccc-assessments-list-read-only>',

                        '</div>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();

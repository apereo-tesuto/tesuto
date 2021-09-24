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

    angular.module('CCC.View.Home').directive('cccAssessmentScoringSetup', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '=',
                student: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ActivationsAPIService',
                'AssessmentsAPIService',
                'Moment',

                function ($scope, $element, $timeout, ActivationsAPIService, AssessmentsAPIService, Moment) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============= MODEL =============*/

                    $scope.assessmentSessionId = false;

                    $scope.submitted = false;

                    $scope.versionsLoading = true;

                    $scope.assessmentSessionCreating = false;

                    $scope.version = false;

                    $scope.assessedDate = false;

                    $scope.isHelpTextVisible = false;

                    $scope.forms = {};

                    $scope.maxDate = Date.now();

                    $scope.meridiems = [
                        {
                            id: 'AM',
                            name: 'A.M'
                        },
                        {
                            id: 'PM',
                            name: 'P.M'
                        }
                    ];


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var generateAssessmentSession = function (activationId, version, assessedDate) {

                        $scope.assessmentSessionCreating = true;

                        ActivationsAPIService.startActivationWithVersion(activationId, version).then(function (assessmentSessionId) {

                            return $scope.$emit('ccc-assessment-scoring-setup.assessmentSessionLoaded', assessmentSessionId, assessedDate);

                        }).finally(function () {
                            $scope.assessmentSessionCreating = false;
                        });
                    };

                    var loadVersions = function () {

                        AssessmentsAPIService.getAssessmentVersions($scope.activation.assessmentScopedIdentifier).then(function (assessmentVersions) {

                            $scope.$broadcast('ccc-item-dropdown.setItems', assessmentVersions, 'cccAssessmentScoringVersionDropdown');

                            if (assessmentVersions.length) {
                                var selectedVersionId = _.max(assessmentVersions, function(o){return o;});
                                $scope.$broadcast('ccc-item-dropdown.setSelectedItemId', selectedVersionId, 'cccAssessmentScoringVersionDropdown');
                            }

                        }).finally(function () {
                            $scope.versionsLoading = false;
                        });
                    };

                    var processAssessedDate = function () {

                        var minute = $scope.startMinute;
                        var hour = $scope.startHour;
                        var day = $scope.assessedDay ? $scope.assessedDay._d : null;

                        if ($scope.startMeridiem === 'PM') {
                            hour = hour + 12;
                        }

                        day = Moment(day).add(hour, 'h').add(minute, 'm');
                        $scope.assessedDate = Moment(day).toISOString();
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        processAssessedDate();

                        if ($scope.forms['versionForm'].$invalid) {
                            return;
                        }

                        generateAssessmentSession($scope.activation.activationId, $scope.version, $scope.assessedDate);
                    };

                    var initialize = function () {
                        loadVersions();
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.getVersionId = function (versionItem) {
                        return versionItem;
                    };

                    $scope.getVersionName = function (versionItem) {
                        return 'Version ' + versionItem;
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-assessment-scoring-setup.cancel');
                    };

                    $scope.toggleHelpText = function () {
                        $scope.isHelpTextVisible = !$scope.isHelpTextVisible;
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {
                        if (dropdownId === 'cccAssessmentScoringVersionDropdown') {
                            $scope.version = itemId;
                        } else if (dropdownId === 'cccAssessmentScoringMeridiemDropdown') {
                            $scope.startMeridiem = itemId;
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    $timeout(initialize, 1600);
                }
            ],

            template: [

                '<div class="row">',

                    '<div class="col-md-8 col-md-offset-2">',

                        '<ccc-label-required></ccc-label-required>',

                        '<h2 class="no-outline" ccc-focusable translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.SECTION_TITLE.STUDENT"></h2>',

                        '<ccc-student-user class="ccc-user" user="student"></ccc-student-user>',

                        '<h2 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.SECTION_TITLE.ATTEMPT"></h2>',

                        '<ccc-activation-card class="ccc-activation-card-full-width" activation="activation" activation-details="true"></ccc-activation-card>',

                        '<h2 class="margin-top-md">',
                            '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.SECTION_TITLE.VERSION"></span> ',
                            '<a ng-click="toggleHelpText()" href="#">',
                                '<i class="fa fa-question-circle" aria-hidden="true"></i>',
                                '<span class="sr-only">show help text</span>',
                            '</a>',
                        '</h2>',

                        '<form name="forms.versionForm" novalidate>',

                            '<div class="form-group" ccc-show-errors="submitted">',

                                '<label for="version">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span>{{::activation.assessmentTitle}} </span>',
                                '</label>',

                                '<div class="row">',
                                    '<div class="col-md-6">',

                                        '<ccc-item-dropdown ',
                                            'id="cccAssessmentScoringVersionDropdown" ',
                                            'class="btn-full-width" ',
                                            'is-required="true" ',
                                            'is-disabled="assessmentSessionCreating || versionsLoading" ',
                                            'required-error-msg="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.VERSION_REQUIRED" ',
                                            'initial-item-id="initialItemId" ',
                                            'loading="versionsLoading" ',
                                            'get-item-id="getVersionId" ',
                                            'get-item-name="getVersionName" ',
                                        '></ccc-item-dropdown>',

                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div ng-show="isHelpTextVisible" class="version-help help-tip-box margin-bottom-sm">',
                                '<div class="title-bar">',
                                    '<h4 class="title" translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.VERSION_HELP.TITLE"></h4>',
                                    '<a ng-click="toggleHelpText()" href="#">',
                                        '<i class="fa fa-times close-help-text" aria-hidden="true"></i>',
                                        '<span class="sr-only">close help text</span>',
                                    '</a>',
                                '</div>',
                                '<div class="row">',
                                    '<div class="col-sm-6">',
                                        '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.VERSION_HELP.BODY"></span>',
                                    '</div>',
                                    '<div class="col-sm-6">',
                                        '<img src="ui/resources/images/assessment-version-location.jpg" alt="{{\'CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.VERSION_HELP.ALT\' | translate}}" />',
                                    '</div>',
                                '</div>',
                            '</div>',

                            '<div class="form-group">',
                                '<h2 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.SECTION_TITLE.DATE_TIME"></h2>',
                                '<div class="row">',
                                    '<div class="col-sm-6" ccc-show-errors="submitted || forms.versionForm.assessedDayPicker.$dirty">',
                                        '<label for="startDate">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                            '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_LABEL.START_DATE"></span>',
                                        '</label>',
                                        '<ccc-date-picker is-disabled="assessmentSessionCreating" name="assessedDayPicker"  date="assessedDay" id="ccc-assessment-scoring-assessed-date" label="assessed date" max-date="maxDate" max-error-text="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MAXDATE"></ccc-date-picker>',
                                    '</div>',
                                    '<div class="col-sm-6">',
                                        '<label id="startTime" for="startTime">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                            '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_LABEL.START_TIME"></span>',
                                        '</label>',
                                        '<div class="row">',
                                            '<div class="col-sm-12">',

                                                '<span class="hours" ccc-show-errors="submitted || forms.versionForm.startHour.$dirty">',
                                                    '<input type="number" id="startHour" aria-labelledby="startTime" class="form-control" name="startHour" placeholder="hh" ',
                                                        'required ',
                                                        'ng-maxlength="2" ',
                                                        'min="1" ',
                                                        'max="12" ',
                                                        'ng-pattern="/^[0-9]*$/" ',
                                                        'autocomplete="off" ',
                                                        'ng-model-options="{ debounce: 100 }" ',
                                                        'ng-disabled="assessmentSessionCreating" ',
                                                        'ng-model="startHour" ',
                                                        'ccc-validation-badge="forms.versionForm" ',
                                                        'aria-describedby="startHourErrors" ',
                                                    '/>',
                                                '</span>',

                                                '<span class="separator">:</span>',

                                                '<span class="minutes" ccc-show-errors="submitted || forms.versionForm.startMinute.$dirty">',
                                                    '<input type="number" id="startMinute" aria-labelledby="startTime" class="form-control" name="startMinute" placeholder="mm" ',
                                                        'required ',
                                                        'ng-maxlength="2" ',
                                                        'min="0" ',
                                                        'max="59" ',
                                                        'ng-pattern="/^[0-9]*$/" ',
                                                        'autocomplete="off" ',
                                                        'ng-model-options="{ debounce: 100 }" ',
                                                        'ng-disabled="assessmentSessionCreating" ',
                                                        'ng-model="startMinute" ',
                                                        'ccc-validation-badge="forms.versionForm" ',
                                                        'aria-describedby="startMinuteErrors" ',
                                                    '/> ',
                                                '</span>',

                                                '<span class="meridiem" ccc-show-errors="submitted">',
                                                    '<ccc-item-dropdown ',
                                                        'id="cccAssessmentScoringMeridiemDropdown" ',
                                                        'class="btn-full-width" ',
                                                        'loading="assessmentSessionCreating" ',
                                                        'is-required="true" ',
                                                        'is-disabled="assessmentSessionCreating" ',
                                                        'required-error-msg="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MERIDIEM.REQUIRED" ',
                                                        'initial-items="meridiems" ',
                                                        'placeholder="AM/PM" ',
                                                    '></ccc-item-dropdown>',
                                                '</span>',

                                                '<div class="grouped-errors">',

                                                    '<div ccc-show-errors="submitted || forms.versionForm.startHour.$dirty">',
                                                        '<div id="startHourErrors" class="ccc-validation-messages noanim ccc-validation-messages-start-hour" ng-messages="forms.versionForm.startHour.$error">',
                                                            '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.HOURS.REQUIRED"></span></p>',
                                                            '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.HOURS.LENGTH"></span></p>',
                                                            '<p ng-message="min" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.HOURS.MIN"></span></p>',
                                                            '<p ng-message="max" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.HOURS.MIN"></span></p>',
                                                            '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.HOURS.PATTERN"></span></p>',
                                                        '</div>',
                                                    '</div>',

                                                    '<div ccc-show-errors="submitted || forms.versionForm.startMinute.$dirty">',
                                                        '<div id="startMinuteErrors" class="ccc-validation-messages noanim ccc-validation-messages-start-hour" ng-messages="forms.versionForm.startMinute.$error">',
                                                            '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MINUTES.REQUIRED"></span></p>',
                                                            '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MINUTES.LENGTH"></span></p>',
                                                            '<p ng-message="min" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MINUTES.MIN"></span></p>',
                                                            '<p ng-message="max" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MINUTES.MIN"></span></p>',
                                                            '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MINUTES.PATTERN"></span></p>',
                                                        '</div>',
                                                    '</div>',

                                                    '<div ccc-show-errors="submitted">',
                                                        '<div id="startMeridiemErrors" class="ccc-validation-messages noanim ccc-validation-messages-start-meridiem" ng-messages="forms.versionForm.startMeridiem.$error">',
                                                            '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.FORM_ERROR.MERIDIEM.REQUIRED"></span></p>',
                                                        '</div>',
                                                    '</div>',

                                                '</div>',

                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            '<div class="ccc-form-submit-controls">',

                                '<button class="btn btn-primary btn-icon-right btn-full-width-when-small btn-submit-button" ng-disabled="assessmentSessionCreating || versionsLoading" type="submit" ng-click="attemptDoSubmit()">',
                                    '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.BUTTON_CONTINUE">Continue</span>',
                                    '<i class="fa fa-chevron-right noanim" aria-hidden="true" ng-if="!assessmentSessionCreating"></i>',
                                    '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="assessmentSessionCreating"></i>',
                                '</button>',

                                '<button class="btn btn-default btn-full-width-when-small" ng-disabled="assessmentSessionCreating || versionsLoading" ng-click="cancel()">',
                                    '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING.BUTTON_CANCEL"></span>',
                                '</button>',

                            '</div>',

                        '</form>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();

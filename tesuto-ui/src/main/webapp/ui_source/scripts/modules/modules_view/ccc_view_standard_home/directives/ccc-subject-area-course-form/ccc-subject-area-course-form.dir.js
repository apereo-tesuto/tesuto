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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseForm', function () {

        return {

            restrict: 'E',

            scope: {
                course: '=',
                competency: '=',        // required so the proper list of mmap equivalents can be loaded

                loading: '=',
                submitted: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$translate',
                'CompetencyDisciplinesAPIService',
                'SUBJECT_AREA_CB_21_CODES',

                function ($scope, $element, $timeout, $translate, CompetencyDisciplinesAPIService, SUBJECT_AREA_CB_21_CODES) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var MAX_COURSE_GROUPS = 15;

                    var getGroupTitle = function (groupIndex) {

                        if (groupIndex === 1) {
                            return $translate.instant('CCC_VIEW_HOME.CCC-SUBJECT-AREA-COURSE-FORM.LEAST_DIFFICULT');
                        } else {
                            return groupIndex;
                        }
                    };


                    /*============ MODEL ==============*/

                    $scope.mmapEquivalents = [];
                    $scope.loadingMMAPEquivalents = true;

                    $scope.courseGroups = [];
                    for (var i=1; i <= MAX_COURSE_GROUPS; i++) {
                        $scope.courseGroups.push({
                            id: i,
                            title: getGroupTitle(i)
                        });
                    }

                    $scope.showCourseGroupHelp = false;

                    $scope.transferLevels = _.map(SUBJECT_AREA_CB_21_CODES, function (cb21Code) {
                        return {
                            id: cb21Code,
                            title: cb21Code
                        };
                    });


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadMMAPEquivalents = function () {

                        $scope.loadingMMAPEquivalents = true;

                        CompetencyDisciplinesAPIService.get($scope.competency).then(function (mmapEquivalents) {

                            $scope.mmapEquivalents = _.map(mmapEquivalents, function (equiv) {
                                return {
                                    id: equiv.code,
                                    title: equiv.mmapEquivalent
                                };
                            });

                            $scope.$broadcast('ccc-item-dropdown.setItems', $scope.mmapEquivalents, 'mmapEquivalentsDropdown');

                        }).finally(function () {
                            $scope.loadingMMAPEquivalents = false;
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    $scope.toggleHelpText = function () {
                        $scope.showCourseGroupHelp = !$scope.showCourseGroupHelp;
                    };

                    $scope.getOptionId = function (item) {
                        return item.id;
                    };

                    $scope.getOptionName = function (item) {
                        return item.title;
                    };

                    $scope.getTransferLevelId = function (item) {
                        return item.id;
                    };

                    $scope.getTransferLevelName = function (item) {
                        return item.title;
                    };

                    $scope.getCourseGroupOptionId = $scope.getOptionId;
                    $scope.getCourseGroupOptionName = $scope.getOptionName;


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {

                        if (dropdownId === 'mmapEquivalentsDropdown') {
                            $scope.course.mmapEquivalentCode = itemId;
                        } else if (dropdownId === 'courseGroupDropdown') {
                            $scope.course.courseGroup = itemId;
                        } else if (dropdownId === 'transferLevelDropdown') {
                            $scope.course.cb21Code = itemId;
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    loadMMAPEquivalents();
                }
            ],

            template: [

                '<ng-form name="cccSubjectAreaCourseForm">',

                    '<ccc-label-required></ccc-label-required>',

                    '<div class="row">',

                        '<div class="col-md-6">',
                            '<div class="form-group">',

                                '<label id="subject-area-form-label-subject">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.SUBJECT"></span>',
                                '</label>',
                                '<div ccc-show-errors="cccSubjectAreaCourseForm.subject.$dirty || submitted">',
                                    '<input ccc-autofocus type="text" id="subject" class="form-control" name="subject" placeholder="Subject" ',
                                        'required ',
                                        'ng-maxlength="64" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="course.subject" ',
                                        'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-labelledby="subject-area-form-label-subject" ',
                                        'aria-describedby="subjectErrors" ',
                                    '> ',
                                    '<div id="subjectErrors" class="ccc-validation-messages noanim ccc-validation-messages-subject" ng-messages="cccSubjectAreaCourseForm.subject.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.SUBJECT_MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-md-6">',
                            '<div class="form-group">',

                                '<label id="subject-area-form-label-number">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.NUMBER"></span>',
                                '</label>',
                                '<div ccc-show-errors="cccSubjectAreaCourseForm.NUMBER.$dirty || submitted">',
                                    '<input ccc-autofocus type="text" id="number" class="form-control" name="number" placeholder="Number" ',
                                        'required ',
                                        'ng-maxlength="34" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="course.number" ',
                                        'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-labelledby="subject-area-form-label-number" ',
                                        'aria-describedby="numberErrors" ',
                                    '> ',
                                    '<div id="numberErrors" class="ccc-validation-messages noanim ccc-validation-messages-number" ng-messages="cccSubjectAreaCourseForm.number.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.NUMBER_MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                    '</div>',

                    '<div class="row">',
                        '<div class="col-md-12">',

                            '<div class="form-group">',

                                '<label id="subject-area-form-label-name">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.NAME"></span>',
                                '</label>',
                                '<div ccc-show-errors="cccSubjectAreaCourseForm.name.$dirty || submitted">',
                                    '<input ccc-autofocus type="text" id="name" class="form-control" name="name" placeholder="Name" ',
                                        'required ',
                                        'ng-maxlength="120" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="course.name" ',
                                        'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-labelledby="subject-area-form-label-name" ',
                                        'aria-describedby="nameErrors" ',
                                    '> ',
                                    '<div id="nameErrors" class="ccc-validation-messages noanim ccc-validation-messages-name" ng-messages="cccSubjectAreaCourseForm.name.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.NAME_MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="form-group">',

                        '<div class="row">',
                            '<div class="col-md-12">',

                                '<label id="subject-area-form-label-cid">',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.CID"></span>',
                                '</label>',
                                '<div ccc-show-errors="cccSubjectAreaCourseForm.cid.$dirty || submitted">',
                                    '<input type="text" id="cid" class="form-control" name="cid" placeholder="C-ID" ',
                                        'ng-maxlength="10" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="course.cid" ',
                                        'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-labelledby="subject-area-form-label-cid" ',
                                        'aria-describedby="cidErrors" ',
                                    '> ',
                                    '<div id="cidErrors" class="ccc-validation-messages noanim ccc-validation-messages-cid" ng-messages="cccSubjectAreaCourseForm.cid.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.CID_MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="form-group">',

                        '<label id="subject-area-form-label-description">',
                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                            '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.DESCRIPTION"></span>',
                        '</label>',
                        '<div ccc-show-errors="cccSubjectAreaCourseForm.description.$dirty || submitted">',
                            '<textarea id="description" class="form-control resize-vertical" name="description" rows="3" ',
                                'required ',
                                'ng-maxlength="3000" ',
                                'ng-model-options="{ debounce: 100 }" ',
                                'ng-disabled="loading" ',
                                'ng-model="course.description" ',
                                'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                'ccc-validation-badge-style="fullWidth" ',
                                'aria-labelledby="subject-area-form-label-description" ',
                                'aria-describedby="descriptionErrors" ',
                            '></textarea>',
                            '<div id="descriptionErrors" class="ccc-validation-messages noanim ccc-validation-messages-description" ng-messages="cccSubjectAreaCourseForm.description.$error">',
                                '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.DESCRIPTION_MAX_LENGTH"></span></p>',
                            '</div>',
                        '</div>',

                    '</div>',

                    '<div class="form-group">',

                        '<div class="row">',

                            '<div class="col-md-6">',

                                '<label id="subject-area-form-label-transfer">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.TRANSFER_LEVEL"></span>',
                                '</label>',
                                '<div ccc-show-errors="cccSubjectAreaCourseForm.transferLevelDropdown.$dirty || submitted">',

                                    '<ccc-item-dropdown ',
                                        'id="transferLevelDropdown" ',
                                        'class="btn-full-width" ',
                                        'is-required="true" ',
                                        'is-disabled="loading" ',
                                        'required-error-msg="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED" ',
                                        'placeholder="Transfer Level" ',
                                        'initial-items="transferLevels" ',
                                        'initial-item-id="course.cb21Code" ',
                                        'get-item-id="getTransferLevelId" ',
                                        'get-item-name="getTransferLevelName" ',
                                    '></ccc-item-dropdown>',

                                '</div>',

                            '</div>',

                            '<div class="col-md-6 sm-margin-top-sm">',

                                '<label id="subject-area-form-label-course-group">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.COURSE_GROUP"></span>',
                                    ' <a ng-click="toggleHelpText()" href="#"><i class="fa fa-question-circle" aria-hidden="true"></i><span class="sr-only">show course group help text</span></a>',
                                '</label>',

                                '<div ccc-show-errors="cccSubjectAreaCourseForm.courseGroupDropdown.$dirty || submitted">',

                                    '<ccc-item-dropdown ',
                                        'id="courseGroupDropdown" ',
                                        'class="btn-full-width" ',
                                        'is-required="true" ',
                                        'is-disabled="loading" ',
                                        'required-error-msg="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED" ',
                                        'initial-items="courseGroups" ',
                                        'initial-item-id="course.courseGroup" ',
                                        'loading="false" ',
                                        'get-item-id="getCourseGroupOptionId" ',
                                        'get-item-name="getCourseGroupOptionName" ',
                                        'labelled-by="subject-area-form-label-course-group" ',
                                        'described-by="courseGroupErrors" ',
                                    '></ccc-item-dropdown>',

                                '</div>',

                            '</div>',

                        '</div>',

                        '<div class="row margin-top-xs" ng-if="showCourseGroupHelp">',
                            '<div class="col-xs-12">',
                                '<div class="help-tip-box">',
                                    '<a ng-click="toggleHelpText()" href="#">',
                                        '<i class="fa fa-times close-help-text" aria-hidden="true"></i>',
                                        '<span class="sr-only">close help text</span>',
                                    '</a>',
                                    '<p translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.COURSE_GROUP_HELP"></p>',
                                '</div>',
                            '</div>',
                        '</div>',

                    '</div>',

                    '<div class="form-group">',

                        '<div class="row">',

                            '<div class="col-md-12">',

                                '<label id="subject-area-form-label-mmap-equivalents">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.MMAP_EQUIVALENT"></span>',
                                '</label>',

                                '<div ccc-show-errors="cccSubjectAreaCourseForm.mmapEquivalentsDropdown.$dirty || submitted">',

                                    '<ccc-item-dropdown ',
                                        'id="mmapEquivalentsDropdown" ',
                                        'class="btn-full-width" ',
                                        'is-required="true" ',
                                        'required-error-msg="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED" ',
                                        'initial-items="mmapEquivalents" ',
                                        'initial-item-id="course.mmapEquivalentCode" ',
                                        'loading="loadingMMAPEquivalents" ',
                                        'get-item-id="getOptionId" ',
                                        'get-item-name="getOptionName" ',
                                    '></ccc-item-dropdown>',

                                '</div>',

                            '</div>',

                        '</div>',

                    '</div>',

                    '<div class="form-group margin-bottom-lg">',

                        '<div class="row">',
                            '<div class="col-md-12">',

                                '<label id="subject-area-form-label-sisTestCode">',
                                    '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.SIS_TEST_CODE"></span>',
                                '</label>',

                                '<p class="help-block" translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.SIS_TEST_CODE_DESCRIPTION"></p>',

                                '<div ccc-show-errors="cccSubjectAreaCourseForm.sisTestCode.$dirty || submitted">',
                                    '<input type="text" id="sisTestCode" class="form-control" name="sisTestCode" placeholder="SIS Code" ',
                                        'ng-maxlength="50" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="course.sisTestCode" ',
                                        'ccc-validation-badge="cccSubjectAreaCourseForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-labelledby="subject-area-form-label-sisTestCode" ',
                                        'aria-describedby="sisTestCodeErrors" ',
                                    '> ',
                                    '<div id="sisTestCodeErrors" class="ccc-validation-messages noanim ccc-validation-messages-sisTestCode" ng-messages="cccSubjectAreaCourseForm.sisTestCode.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.ERROR.SIS_TEST_CODE_MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                        '</div>',
                    '</div>',

                '</ng-form>'

            ].join('')
        };

    });

})();

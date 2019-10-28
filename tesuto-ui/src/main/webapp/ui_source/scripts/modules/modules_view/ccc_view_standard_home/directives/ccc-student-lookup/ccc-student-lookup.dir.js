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

    angular.module('CCC.View.Home').directive('cccStudentLookup', function () {

        return {

            restrict: 'E',

            scope: {
                activationControls: '=',
                initialCccid: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'StudentsAPIService',
                'CCCUtils',
                'localStorageService',

                function ($scope, $element, $timeout, StudentsAPIService, CCCUtils, localStorageService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var defaultSearchFields = {
                        firstName: '',
                        middleInitial: '',
                        lastName: '',
                        age: '',
                        phone: '',
                        email: '',
                        cccId: ''
                    };

                    var updateFirstFocus = function (targetElement) {
                        $element.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        targetElement.attr('ccc-autofocus', 'ccc-autofocus');
                    };

                    var setFirstFocusOnActiveTab = function () {
                        var activeTab = $scope.searchMode === 'cccId' ? $element.find('.ccc-student-lookup-form-tab-cccid') : $element.find('.ccc-student-lookup-form-tab-advanced');
                        updateFirstFocus(activeTab);
                        return activeTab;
                    };


                    /*============= MODEL =============*/

                    $scope.students = [];
                    $scope.searchMode = $scope.initialCccid ? 'cccId' : (localStorageService.get('ccc-student-lookup-mode') || 'cccId');

                    $scope.searching = false;
                    $scope.noResultsFound = false;
                    $scope.searchComplete = false;

                    $scope.searchFields = $.extend({}, defaultSearchFields);
                    $scope.searchFields.cccId = $scope.initialCccid ? $scope.initialCccid : '';

                    $scope.submitted = false;


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var getFormattedSearchFields = function () {
                        var searchFieldsCopy = $.extend({}, $scope.searchFields);
                        searchFieldsCopy.cccId = searchFieldsCopy.cccId.toUpperCase();
                        return searchFieldsCopy;
                    };

                    var doSearch = function () {

                        $scope.submitted = true;

                        if ($scope['cccStudentLookupForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        $scope.$emit('ccc-student-lookup.searching');

                        $scope.searching = true;
                        $scope.searchComplete = false;

                        $scope.students = [];

                        // Some model values need to be modified before sent to the server
                        var formattedSearchFields = getFormattedSearchFields();

                        StudentsAPIService.studentSearch(formattedSearchFields).then(function (students) {

                            $scope.students = students;

                            if ($scope.students.length > 0) {

                                $timeout(function () {

                                    var firstUserElement = $($element.find('ccc-user')[0]);

                                    firstUserElement.focus();
                                    updateFirstFocus(firstUserElement);

                                    $scope.$emit('ccc-assess.element-focused');

                                    if ($scope.searchMode === "cccId") {
                                        $scope.$emit('ccc-student-lookup.selected', students[0]);
                                    }

                                }, 1);

                            } else {
                                $timeout(function () {

                                    $element.find('#ccc-student-lookup-submit').focus();
                                    updateFirstFocus($element.find('#ccc-student-lookup-submit'));

                                    $scope.$emit('ccc-assess.element-focused');
                                }, 1);
                            }

                        }, function (err) {

                            $scope.students = [];

                            // todo: determine if 404 expected if no results are there
                            if (err.type === '404') {
                                $scope.$emit('ccc-student-lookup.error', err);
                            } else {
                                $scope.$emit('ccc-student-lookup.error', err);
                            }

                        }).finally(function () {

                            $scope.searching = false;
                            $scope.searchComplete = true;
                            $scope.$emit('ccc-student-lookup.results', $scope.students);
                        });
                    };

                    var initialize = function () {

                        setFirstFocusOnActiveTab();

                        if ($scope.initialCccid) {
                            $scope.attemptDoSearch();
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.clearFields = function () {

                        $scope.searchFields = $.extend({}, defaultSearchFields);
                        $scope.students = [];
                        $scope.searchComplete = false;
                        $scope.submitted = false;
                        $scope['cccStudentLookupForm'].$setPristine();
                    };

                    $scope.setSearchMode = function (searchMode) {
                        $scope.searchMode = searchMode;
                        localStorageService.set('ccc-student-lookup-mode', $scope.searchMode);
                        $scope.clearFields();

                        $timeout(function () {
                            var activeTab = setFirstFocusOnActiveTab();
                            activeTab.focus();
                        }, 1);
                    };

                    $scope.searchModeClass = function (searchMode) {
                        return {
                            active: searchMode === $scope.searchMode
                        };
                    };

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSearch = function () {
                        $timeout(doSearch,1);
                    };


                    /*============= LISTENERS =============*/

                    // bubble up the selected student
                    $scope.$on('ccc-student-results.selected', function (event, student, studentElement) {

                        // update first focus
                        updateFirstFocus(studentElement);

                        $scope.$emit('ccc-student-lookup.selected', student);
                    });

                    // others can ask us to clear ourselves
                    $scope.$on('ccc-student-lookup.request-clear', $scope.clearFields);


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [
                '<form name="cccStudentLookupForm" novalidate>',

                    '<ccc-label-required></ccc-label-required>',

                    '<div class="well-with-tabs">',

                        '<div class="row">',
                            '<div class="col-xs-12">',

                                '<ul class="nav nav-tabs">',
                                    '<li role="presentation" ng-class="searchModeClass(\'cccId\')">',
                                        '<a href="#" id="tab-search-by-cccid" aria-controls="panel-search-by-cccid" role="tab" aria-selected="{{searchMode === \'cccId\'}}" class="ccc-student-lookup-form-tab-cccid" tabindex="0" ng-click="setSearchMode(\'cccId\')">',
                                            '<i class="fa fa-tag" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.SEARCH_BY_CCID"></span>',
                                        '</a>',
                                    '</li>',
                                    '<li role="presentation" ng-class="searchModeClass(\'ADVANCED\')">',
                                        '<a href="#" id="tab-advanced-search" aria-controls="panel-advanced-search" role="tab" aria-selected="{{searchMode === \'ADVANCED\'}}" class="ccc-student-lookup-form-tab-advanced" tabindex="0" ng-click="setSearchMode(\'ADVANCED\')">',
                                            '<i class="fa fa-list" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ADVANCED_SEARCH"></span>',
                                        '</a>',
                                    '</li>',
                                '</ul>',

                            '</div>',
                        '</div>',

                        '<div class="well">',

                            '<div id="panel-search-by-cccid" aria-labelledby="tab-search-by-cccid" ng-if="searchMode === \'cccId\'">',
                                '<div class="form-horizontal">',

                                    '<div class="row">',
                                        '<div class="col-md-5 col-sm-12">',

                                            '<div class="form-group row">',
                                                '<label for="cccId" class="col-md-4 col-sm-3 control-label form-inline-first-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Student ID</label>',
                                                '<div class="col-md-8 col-sm-5" ccc-show-errors="cccStudentLookupForm.cccId.$dirty || submitted">',
                                                    '<input type="text" id="cccId" class="form-control" name="cccId" placeholder="STUDENT ID" ',
                                                        'required ',
                                                        'ng-model-options="{ debounce: 100 }" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.cccId" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="cccIdErrors" ',
                                                    '> ',
                                                    '<div id="cccIdErrors" class="ccc-validation-messages noanim ccc-validation-messages-first-name" ng-messages="cccStudentLookupForm.cccId.$error">',
                                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.REQUIRED_CCCID"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                        '</div>',

                                        '<label class="col-md-5 col-md-offset-0 col-sm-offset-3 col-sm-5 control-label">',
                                            '<div class="text-left">',
                                                '<a href="#" class="btn-link ccc-student-lookup-button-go-advanced" ng-click="setSearchMode(\'ADVANCED\')" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.NO_ID"></a>',
                                            '</div>',
                                        '</label>',

                                    '</div>',

                                '</div>',
                            '</div>',

                            '<div id="panel-advanced-search" aria-labelledby="tab-advanced-search" ng-if="searchMode !== \'cccId\'">',

                                '<p id="ccc-student-lookup-form-help" class="help-block"><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FORM_HELP"></span></p>',

                                '<div class="form-horizontal">',

                                    '<div class="row">',

                                        '<div class="col-md-5 col-sm-12 ccc-student-lookup-primary-fields">',

                                            '<div class="form-group row">',
                                                '<label for="firstName" class="col-md-4 col-sm-3 control-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FIRST_NAME"></span></label>',

                                                '<div class="col-sm-8" ccc-show-errors="cccStudentLookupForm.firstName.$dirty || submitted">',

                                                    '<input type="text" id="firstName" aria-required="true" class="form-control" name="firstName" autocomplete="false" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FIRST_NAME\' | translate }}" ',
                                                        'required ',
                                                        'ng-model-options="{ debounce: 100 }" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.firstName" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="firstNameErrors" ',
                                                    '> ',
                                                    '<div id="firstNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-first-name" ng-messages="cccStudentLookupForm.firstName.$error">',
                                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.REQUIRED_FIRST_NAME"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                            '<div class="form-group row">',
                                                '<label for="middleInitial" class="col-md-4 col-sm-3 control-label" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.MIDDLE_INITIAL"></label>',
                                                '<div class="col-sm-8" ccc-show-errors="cccStudentLookupForm.middleInitial.$dirty || submitted">',
                                                    '<input type="text" id="middleInitial" autocomplete="false" name="middleInitial" class="form-control input-length-1" ',
                                                        'ng-maxlength="1" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.middleInitial" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'aria-describedby="middleInitialErrors" ',
                                                    '> ',
                                                    '<div id="middleInitialErrors" class="ccc-validation-messages noanim ccc-validation-messages-last-name" ng-messages="cccStudentLookupForm.middleInitial.$error">',
                                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.MIDDLE_INITIAL"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                            '<div class="form-group row">',
                                                '<label for="lastName" class="col-md-4 col-sm-3 control-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LAST_NAME"></span></label>',
                                                '<div class="col-sm-8" ccc-show-errors="cccStudentLookupForm.lastName.$dirty || submitted">',
                                                    '<input type="text" id="lastName" aria-required="true" autocomplete="false" class="form-control" name="lastName" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LAST_NAME\' | translate }}" ',
                                                        'required ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.lastName" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="lastNameErrors" ',
                                                    '> ',
                                                    '<div id="lastNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-last-name" ng-messages="cccStudentLookupForm.lastName.$error">',
                                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.REQUIRED_LAST_NAME"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                        '</div>',

                                        '<div class="col-md-6 col-sm-12">',

                                            '<div class="form-group row">',
                                                '<label for="age" class="col-sm-3 control-label"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.AGE"></span></label>',
                                                '<div class="col-md-9 col-sm-8" ccc-show-errors="cccStudentLookupForm.age.$dirty || submitted">',
                                                    '<input type="number" id="age" class="form-control input-length-1" pattern="^[1-9]{1}[0-9]*$" name="age" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.AGE\' | translate }}" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.age" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'aria-describedby="ageNameErrors" ',
                                                    '> ',
                                                    '<div id="ageNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-age" ng-messages="cccStudentLookupForm.age.$error">',
                                                        '<p ng-message="number" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.AGE_NUMBER"></span></p>',
                                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.AGE_PATTERN"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                            '<div class="form-group row">',
                                                '<label id="phoneLabel" class="col-sm-3 control-label" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.PHONE"></label>',
                                                '<div class="col-md-9 col-sm-8 form-inline" ccc-show-errors="cccStudentLookupForm.phone.$dirty || submitted">',
                                                    '<input type="text" id="phone" autocomplete="false" name="phone" class="form-control input-length-6" aria-labelledby="phoneLabel" placeholder="Ten Digit Phone" ',
                                                        'ui-us-phone-number ',
                                                        'ng-disabled="searching" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'ng-model="searchFields.phone" ',
                                                        'aria-describedby="phoneErrors" ',
                                                        'maxlength="14" ',
                                                    '>',
                                                    '<div id="phoneErrors" class="ccc-validation-messages noanim ccc-validation-messages-phone" ng-messages="cccStudentLookupForm.phone.$error">',
                                                        '<p ng-message="usPhoneNumber" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.PHONE"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                            '<div class="form-group row">',
                                                '<label for="email" class="col-sm-3 control-label"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.EMAIL"></span></label>',
                                                '<div class="col-md-9 col-sm-8" ccc-show-errors="cccStudentLookupForm.email.$dirty || submitted">',
                                                    '<input type="text" id="email" class="form-control" autocomplete="false" name="email" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.EMAIL\' | translate }}" ',
                                                        'ng-disabled="searching" ',
                                                        'ccc-required-email="allowEmpty" ',
                                                        'ng-model="searchFields.email" ',
                                                        'ccc-validation-badge="cccStudentLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="emailNameErrors" ',
                                                    '> ',
                                                    '<div id="emailNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-email" ng-messages="cccStudentLookupForm.email.$error">',
                                                        '<p ng-message="requiredEmail" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.EMAIL"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                        '</div>',

                                    '</div>',

                                '</div>',
                            '</div>',

                            '<div class="form-horizontal form-submit-controls row">',

                                '<div class="col-md-5 col-sm-12">',

                                    '<div class="form-group row">',

                                        '<div class="col-md-offset-4 col-sm-offset-3 col-xs-12 button-bar">',

                                            '<button id="ccc-student-lookup-submit" ng-disabled="searching" ng-click="attemptDoSearch()" class="btn btn-primary btn-submit-button btn-full-width-when-small">',
                                                '<i class="fa fa-search"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.SEARCH"></span>',
                                                '<i class="fa fa-spin fa-spinner noanim" ng-show="searching"></i>',
                                            '</button>',

                                            '<a href="#" class="ccc-student-lookup-clear btn-full-width-when-small btn-link" ng-click="clearFields()"><i class="fa fa-magic" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.CLEAR_FIELDS"></span></a>',

                                        '</div>',

                                    '</div>',

                                '</div>',

                            '</div>',

                        '</div>',

                    '</div>',

                '</form>',

                '<div class="row ccc-student-lookup-results">',
                    '<div class="col-xs-12">',
                        '<ccc-content-loading-placeholder class="ccc-student-lookup-no-results" ng-hide="!searching && !(students.length === 0 && searchComplete)" no-results="students.length === 0 && searchComplete && !searching"></ccc-content-loading-placeholder>',
                        '<ccc-student-results class="ccc-user-list" students="students" activation-controls="activationControls" ng-show="students.length > 0"></ccc-student-results>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();

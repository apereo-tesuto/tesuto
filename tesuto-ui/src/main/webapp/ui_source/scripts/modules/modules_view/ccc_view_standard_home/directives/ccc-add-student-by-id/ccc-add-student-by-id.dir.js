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

    angular.module('CCC.View.Home').directive('cccAddStudentById', function () {

        return {

            restrict: 'E',

            scope: {
                addedStudentIds: '=',   // you can bind an array of already added student ids, if you do this the widget will change to "remove" for these student ids
                isDisabled: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'StudentsAPIService',

                function ($scope, $element, $timeout, StudentsAPIService) {

                    /*============ MODEL ============*/

                    $scope.students = [];

                    $scope.searchFields = {
                        cccId: ''
                    };

                    // unfortuntately nested forms in angular make this necessary
                    $scope.forms = {};


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var focusOnInput = function () {
                        $element.find('#ccc-bulk-activation-student-search-cccid').focus();
                    };

                    var resetSearch = function () {

                        $scope.students = [];

                        $scope.searchFields.cccId = '';

                        $scope.forms['cccStudentSearch'].cccId.$setPristine();

                        $scope.submitted = false;

                        $timeout(focusOnInput);
                    };

                    var getFormattedSearchFields = function () {
                        var searchFieldsCopy = $.extend({}, $scope.searchFields);
                        searchFieldsCopy.cccId = searchFieldsCopy.cccId.toUpperCase();
                        return searchFieldsCopy;
                    };

                    var listContainsCCCID = function (cccId) {

                        if (!$scope.addedStudentIds) {
                            return false;
                        } else {

                            var locatedId = _.find($scope.addedStudentIds, function (alreadyAddedId) {
                                return alreadyAddedId.toLowerCase() === cccId.toLowerCase();
                            });

                            return locatedId !== undefined ? true : false;
                        }
                    };

                    var doSearch = function () {

                        $scope.submitted = true;

                        if($scope.forms['cccStudentSearch'].$invalid) {
                            return;
                        }

                        $scope.searching = true;
                        $scope.students = [];

                        // Some model values need to be modified before sent to the server
                        var formattedSearchFields = getFormattedSearchFields();

                        StudentsAPIService.studentSearch(formattedSearchFields).then(function (students) {

                            $scope.students = students;
                            $scope.studentAlreadyExists = listContainsCCCID($.trim($scope.searchFields.cccId.toUpperCase()));

                        }, function (err) {

                            $scope.students = [];

                            if (err.type === '404') {
                                $scope.$emit('ccc-add-student-by-id.error', err);
                            } else {
                                $scope.$emit('ccc-add-student-by-id.error', err);
                            }

                        }).finally(function () {
                            $scope.searching = false;
                        });
                    };

                    /*============ BEHAVIOR ============*/

                    $scope.addStudent = function (student) {
                        $scope.$emit('ccc-add-student-by-id.addStudent', student);
                        resetSearch();
                    };

                    $scope.removeStudent = function (student) {
                        $scope.$emit('ccc-add-student-by-id.removeStudent', student);
                        resetSearch();
                    };

                    $scope.isStudentsFound = function() {
                        return !_.isEmpty($scope.students);
                    };

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSearch = function () {

                        if (!$scope.isDisabled) {
                            $timeout(doSearch, 1);
                        }
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-add-student-by-id.clear', resetSearch);

                    $scope.$on('ccc-add-student-by-id.focus', focusOnInput);


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="student-search-cccid well well-sm">',

                    '<form name="forms.cccStudentSearch" novalidate>',
                        '<div ccc-show-errors="forms.cccStudentSearch.cccId.$dirty && submitted">',

                            '<div class="input-group">',
                                '<label id="ccc-student-search-cccid-label" class="sr-only" translate="CCC_VIEW_HOME.CCC-ADD-STUDENT-BY-ID.CCCID"></label>',
                                '<input ccc-autofocus ',
                                    'id="ccc-bulk-activation-student-search-cccid" ',
                                    'autocomplete="off" ',
                                    'name="cccId" ',
                                    'required ',
                                    'ng-model-options="{ debounce: 100 }" ',
                                    'class="form-control" ',
                                    'type="text" ',
                                    'maxlength="7" ',
                                    'ng-model="searchFields.cccId" ',
                                    'ng-minlength="7" ',
                                    'ccc-validation-badge="forms.cccStudentSearch" ',
                                    'ccc-validation-badge-style="fullWidth" ',
                                    'placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.SEARCH_BY_CCCID\' | translate }}" ',
                                    'aria-describedby="cccIdErrors" ',
                                    'aria-labelledby="ccc-student-search-cccid-label" ',
                                '/>',
                                '<span class="input-group-btn">',
                                    '<button id="ccc-student-search-submit" class="btn btn-default btn-icon-only" ng-click="attemptDoSearch()" ng-disabled="isDisabled">',
                                        '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-ADD-STUDENT-BY-ID.SR_SEARCH"></span>',
                                        '<i class="fa fa-search" aria-hidden="true"></i>',
                                    '</button>',
                                '</span>',
                            '</div>',

                            '<div id="cccIdErrors" class="ccc-validation-messages ccc-validation-messages-first-name noanim" ng-messages="forms.cccStudentSearch.cccId.$error">',
                                '<p ng-message="required minlength" class="noanim" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.INVALID_CCCID"></p>',
                            '</div>',

                        '</div>',
                    '</form>',

                    '<div class="well" aria-live="polite" aria-relevant="all">',

                        '<div ng-if="!isStudentsFound()" class="ccc-bulk-activation-student-search-instructions noanim">',
                            '<div ng-if="searching" class="noanim"><i class="fa fa-spinner fa-spin"></i> Searching</div>',
                            '<div class="noanim" ng-if="!searching && !submitted" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.SEARCH_INSTRUCTIONS"></div>',
                            '<div class="noanim" ng-if="!searching && submitted">',
                                '<i class="fa fa-exclamation-triangle icon-warning" aria-hidden="true"></i>',
                                '<span class="text-warning" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.NO_RESULTS"></span>',
                            '</div>',
                        '</div>',

                        '<div class="row ccc-bulk-activation-student-search-results noanim" ng-if="isStudentsFound()">',
                            '<div class="col-sm-2 col-md-1">',
                                '<button class="btn btn-primary btn-full-width-when-small add" ng-click="addStudent(students[0])" ng-disabled="isDisabled" ng-if="!studentAlreadyExists">',
                                    '<span class="icon fa fa-plus" role="presentation" aria-hidden="true"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_CARD.ADD"></span>',
                                '</button>',
                                '<button class="btn btn-default btn-full-width-when-small remove" ng-click="removeStudent(students[0])" ng-if="studentAlreadyExists">',
                                    '<span class="icon fa fa-times" role="presentation" aria-hidden="true"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_CARD.REMOVE"></span>',
                                '</button>',
                            '</div>',
                            '<div class="col-sm-10 col-md-11">',
                                '<ccc-student-card student="students[0]" ng-if="isStudentsFound()"></ccc-student-card>',
                            '</div>',
                        '</div>',

                    '</div>',

                '</div>'

            ].join('')
        };
    });

})();

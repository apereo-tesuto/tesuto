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

    angular.module('CCC.View.Home').directive('cccUserLookup', function () {

        return {

            restrict: 'E',

            scope: {
                activationControls: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'CCCUtils',
                'CurrentUserService',
                'UsersAPIService',

                function ($scope, $element, $timeout, CCCUtils, CurrentUserService, UsersAPIService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var updateFirstFocus = function (targetElement) {
                        $element.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        targetElement.attr('ccc-autofocus', 'ccc-autofocus');
                    };


                    /*============= MODEL =============*/

                    $scope.users = [];

                    $scope.searching = false;
                    $scope.noResultsFound = false;
                    $scope.searchComplete = false;

                    $scope.searchFields = {};

                    $scope.submitted = false;

                    $scope.submittedFields = {};


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var getFormattedSearchFields = function () {
                        var searchFieldsCopy = $.extend({}, $scope.searchFields);
                        return searchFieldsCopy;
                    };

                    var updateUsers = function (users_in) {

                        $scope.users = users_in;

                        if ($scope.users.length > 0) {

                            $timeout(function () {

                                var firstUserElement = $($element.find('ccc-user')[0]);

                                firstUserElement.focus();
                                updateFirstFocus(firstUserElement);

                                $scope.$emit('ccc-assess.element-focused');
                            }, 1);

                        } else {
                            $timeout(function () {

                                $element.find('#ccc-user-lookup-submit').focus();
                                updateFirstFocus($element.find('#ccc-user-lookup-submit'));

                                $scope.$emit('ccc-assess.element-focused');
                            }, 1);
                        }
                    };

                    var doSearch = function () {

                        $scope.submitted = true;

                        if ($scope['cccUserLookupForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        $scope.submittedFields = angular.copy($scope.searchFields);

                        CurrentUserService.getCollegeList().then(function (collegeList) {

                            $scope.$emit('ccc-user-lookup.searching');

                            $scope.searching = true;
                            $scope.searchComplete = false;

                            $scope.users = [];

                            // Some model values need to be modified before sent to the server
                            var formattedSearchFields = getFormattedSearchFields();

                            return UsersAPIService.userListSearch(formattedSearchFields).then(function (userList) {

                                updateUsers(userList);

                            }, function (err) {

                                $scope.users = [];

                                // todo: determine if 404 expected if no results are there
                                if (err.type === '404') {
                                    $scope.$emit('ccc-user-lookup.error', err);
                                } else {
                                    $scope.$emit('ccc-user-lookup.error', err);
                                }

                            });
                        })
                        .finally(function () {
                            $scope.searching = false;
                            $scope.searchComplete = true;
                            $scope.$emit('ccc-user-lookup.results', $scope.users);
                        });
                    };

                    var resetSearchModel = function () {

                        $scope.searchFields = {
                            firstName: '',
                            lastName: '',
                            collegeIds: []
                        };
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.clearFields = function () {

                        resetSearchModel();

                        $scope.users = [];
                        $scope.searchComplete = false;
                        $scope.submitted = false;
                        $scope['cccUserLookupForm'].$setPristine();
                    };

                    $scope.getIsInvalid = function () {
                        return (!$.trim($scope.searchFields.firstName) && !$.trim($scope.searchFields.lastName));
                    };

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSearch = function () {
                        $timeout(doSearch,1);
                    };

                    $scope.createUser = function (e) {
                        e.preventDefault();
                        $scope.$emit('ccc-user-lookup.createUser', $scope.submittedFields.firstName, $scope.submittedFields.lastName);
                    };


                    /*============= LISTENERS =============*/

                    // bubble up the selected user
                    $scope.$on('ccc-user-list.selected', function (event, user, userElement) {

                        // update first focus
                        updateFirstFocus(userElement);

                        // bubble up the event as a ccc-user-lookup event api
                        $scope.$emit('ccc-user-lookup.selected', user);
                    });

                    // others can ask us to clear ourselves
                    $scope.$on('ccc-user-lookup.requestClear', $scope.clearFields);


                    /*============ INITIALIZATION ==============*/

                    resetSearchModel();
                }
            ],

            template: [
                '<form name="cccUserLookupForm" novalidate>',

                    '<div class="well-with-tabs">',

                        '<div class="row">',
                            '<div class="col-xs-12">',

                                '<ul class="nav nav-tabs">',
                                    '<li role="presentation" class="active"><a href="#" class="ccc-user-lookup-form-tab-advanced" tabindex="0"><i class="fa fa-list" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.USER_LOOKUP.SEARCH_TAB"></span></a></li>',
                                '</ul>',

                            '</div>',
                        '</div>',

                        '<div class="well">',

                            '<div>',

                                '<p id="ccc-user-lookup-form-help" class="help-block"><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FORM_HELP"></span></p>',

                                '<div class="form-horizontal">',

                                    '<div class="row">',

                                        '<div class="col-md-6 col-sm-12">',

                                            '<div class="form-group row">',

                                                '<label for="firstName" class="col-md-4 col-sm-3 control-label"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FIRST_NAME"></span></label>',

                                                '<div class="col-sm-8" ccc-show-errors="submitted">',

                                                    '<input type="text" ccc-autofocus id="firstName" class="form-control" name="firstName" autocomplete="false" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.FIRST_NAME\' | translate }}" ',
                                                        'ng-model-options="{ debounce: 100 }" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.firstName" ',
                                                        'ccc-input-invalid="getIsInvalid()" ',
                                                        'ccc-validation-badge="cccUserLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="lastNameErrors" ',
                                                    '> ',
                                                '</div>',
                                            '</div>',

                                            '<div class="form-group row">',

                                                '<label for="lastName" class="col-md-4 col-sm-3 control-label"><span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LAST_NAME"></span></label>',

                                                '<div class="col-sm-8" ccc-show-errors="submitted">',
                                                    '<input type="text" id="lastName" aria-required="true" autocomplete="false" class="form-control" name="lastName" placeholder="{{ \'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LAST_NAME\' | translate }}" ',
                                                        'ng-disabled="searching" ',
                                                        'ng-model="searchFields.lastName" ',
                                                        'ccc-input-invalid="getIsInvalid()" ',
                                                        'ccc-validation-badge="cccUserLookupForm" ',
                                                        'ccc-validation-badge-style="fullWidth" ',
                                                        'aria-describedby="lastNameErrors" ',
                                                    '> ',
                                                    '<div id="lastNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-last-name" ng-messages="cccUserLookupForm.lastName.$error">',
                                                        '<p ng-message="cccInputInvalid" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.ERROR.REQUIRED_NAME"></span></p>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                        '</div>',

                                    '</div>',

                                '</div>',
                            '</div>',

                            '<div class="form-horizontal form-submit-controls row">',

                                '<div class="col-md-6 col-sm-12">',

                                    '<div class="form-group row">',

                                        '<div class="col-md-offset-4 col-sm-offset-3 col-xs-12 button-bar">',

                                            '<button id="ccc-user-lookup-submit" ng-disabled="searching" ng-click="attemptDoSearch()" class="btn btn-primary btn-submit-button btn-full-width-when-small">',
                                                '<i class="fa fa-search"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.SEARCH"></span>',
                                                '<i class="fa fa-spin fa-spinner noanim" ng-show="searching"></i>',
                                            '</button>',

                                            '<a href="#" class="ccc-user-lookup-clear btn-full-width-when-small btn-link" ng-click="clearFields()"><i class="fa fa-magic" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.CLEAR_FIELDS"></span></a>',

                                        '</div>',

                                    '</div>',

                                '</div>',

                            '</div>',

                        '</div>',

                    '</div>',

                '</form>',

                '<div class="row ccc-user-lookup-results">',
                    '<div class="col-xs-12">',

                        '<ccc-content-loading-placeholder class="ccc-user-lookup-no-results" ng-hide="!searching && !(users.length === 0 && searchComplete)" no-results-info="users.length === 0 && searchComplete && !searching" hide-default-no-results-text="true">',
                            '<span translate="CCC_VIEW_HOME.USER_LOOKUP.NO_RESULTS_TEXT"> </span> <a href="#" ng-click="createUser($event)" class="ccc-link-em"><span translate="CCC_VIEW_HOME.USER_LOOKUP.NO_RESULTS_LINK"></span> "{{submittedFields.firstName}}<span ng-if="searchFields.firstName"> </span>{{submittedFields.lastName}}"</a>.',
                        '</ccc-content-loading-placeholder>',

                        '<ccc-user-list class="ccc-user-list" users="users" activation-controls="activationControls" ng-show="users.length > 0"></ccc-user-list>',

                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();

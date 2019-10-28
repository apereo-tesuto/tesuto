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

    angular.module('CCC.View.Home').directive('cccActivationFacetedSearch', function () {

        return {

            restrict: 'E',

            scope: {
                activations: "=?",      // pass them in optionally or fire and event to update the list
                selectedFacets: "=?",   // optional, would be a copy of the selectedFacetMap that comes form this component
                filterData: '=',        // {status: 'started' or 'completed', dateWindowType: 'today' or 'lastSeven' or 'custom', minDate: <minDate>, maxDate: <maxDate>}
                loading: "=?",          // optional flag to put loaders everywhere
                hardDisplayLimit: "=?", // optional override for the hard display limit
                softDisplayLimit: "=?"  // optional override for the soft display limit
            },

            controller: [

                '$scope',
                '$timeout',
                '$translate',
                'Moment',
                'FacetedSearchManagerClass',

                'ACTIVATION_STATUS_TITLE_MAP',
                'ACTIVATION_STATUS_ORDER_MAP',

                function ($scope, $timeout, $translate, Moment, FacetedSearchManagerClass, ACTIVATION_STATUS_TITLE_MAP, ACTIVATION_STATUS_ORDER_MAP) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var HARD_DISPLAY_LIMIT = $scope.hardDisplayLimit || 1500;
                    var SOFT_DISPLAY_LIMIT = $scope.softDisplayLimit || 1000;

                    var filteredStudents = [];

                    var notifyContentChange = function () {
                        $scope.ariaLiveMessage = '';
                        $timeout(function () {
                            $translate('CCC_VIEW_HOME.ACTIVATION_FSEARCH.ARIA_CONTENT_LOADED').then(function (ariaString) {
                                $scope.ariaLiveMessage = ariaString;
                            });
                        }, 500);
                    };


                    /*============ MODEL ============*/

                    $scope.isOverHardDisplayLimit = false;  // we won't let the user display this much... their browser will crash
                    $scope.isOverSoftDisplayLimit = false;  // warn the user that this ammount of data can not perform well

                    $scope.softLimitAccepted = false;   // the user can say "yes" display results up to the hard limit

                    $scope.activations = $scope.activations || [];
                    $scope.students = [];
                    $scope.selectedFacets = $scope.selectedFacets || {};
                    $scope.loading = $scope.loading || false;

                    $scope.filteredStudentWithActivationsListFilteredByFacets = [];
                    $scope.facetsSelectedCount = 0;

                    $scope.studentFilter = '';
                    $scope.studentFilterActive = false;

                    $scope.ariaLiveMessage = '';

                    $scope.facetedSearchManager = new FacetedSearchManagerClass({
                        title: 'REFINE BY:',
                        data: filteredStudents,
                        selectedFacets: $scope.selectedFacets,
                        facets: [
                            {
                                id: 'assessmentType',
                                title: 'CCC_VIEW_HOME.ACTIVATION_FSEARCH.FTITLE.ASSESSMENT',
                                fieldValueAndTitle: function (dataItem) {
                                    return _.map(dataItem.activations, function (activation) {
                                        return {
                                            value: activation.assessmentScopedIdentifier,
                                            title: activation.name
                                        };
                                    });
                                },
                                sortBy: function (facetValue) {
                                    return facetValue.title.toLowerCase();
                                }
                            },
                            {
                                id: 'deliveryType',
                                title: 'CCC_VIEW_HOME.ACTIVATION_FSEARCH.FTITLE.DELIVERY',
                                fieldValueAndTitle: function (dataItem) {
                                    return _.map(dataItem.activations, function (activation) {
                                        return {
                                            value: activation.deliveryType,
                                            title: activation.deliveryType.toLowerCase()
                                        };
                                    });
                                },
                                sortBy: function (facetValue) {
                                    return facetValue.title.toLowerCase();
                                }
                            },
                            {
                                id: 'status',
                                title: 'CCC_VIEW_HOME.ACTIVATION_FSEARCH.FTITLE.STATUS',
                                fieldValueAndTitle: function (dataItem) {
                                    return _.map(dataItem.activations, function (activation) {
                                        return {
                                            value: activation.status,
                                            title: ACTIVATION_STATUS_TITLE_MAP[activation.status].toLowerCase()
                                        };
                                    });
                                },
                                sortBy: function (facetValue) {
                                    return ACTIVATION_STATUS_ORDER_MAP[facetValue.id];
                                }
                            }
                        ]
                    });


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var filterStudentWithActivations = function (students_in) {

                        $scope.studentFilterActive = $.trim($scope.studentFilter) !== '';

                        var searchTokens = $scope.studentFilter.split(/[\s,]+/g);

                        for (var i=0; i < searchTokens.length; i++) {
                            searchTokens[i] = $.trim(searchTokens[i].toLowerCase());
                        }

                        // if there is only an empty token then don't filter
                        if (searchTokens.length === 1 && searchTokens[0] === '') {
                            return students_in;
                        }

                        return _.filter(students_in, function (student) {

                            var matches = true;
                            _.each(searchTokens, function (token) {
                                matches = matches && (student._nameSearch.indexOf(token) !== -1);
                            });
                            return matches;
                        });
                    };

                    var groupActivationsByStudents = function (activations) {

                        // First, extract student deets from each activation
                        var studentWithActivationsList = _.map(activations, function (activation) {
                            return {
                                firstName: activation.student.firstName,
                                lastName: activation.student.lastName,
                                middleInitial: activation.student.middleInitial,
                                cccId: activation.student.cccId,
                                _nameSearch: activation._nameSearch,
                                age: activation.student.age,
                                phoneFormatted: activation.student.phoneFormatted,
                                email: activation.student.email,
                                collegeStatuses: activation.student.collegeStatuses,
                                activations: []
                            };
                        });

                        // Then, filter down the list to unique student ids
                        studentWithActivationsList = _.unique(studentWithActivationsList, function (studentWithActivations, key, cccId) {
                            return studentWithActivations.cccId;
                        });

                        // Finally, populate each students list of activations
                        _.each(studentWithActivationsList, function (studentWithActivations) {
                            _.each(activations, function (activation) {

                                if (studentWithActivations.cccId === activation.student.cccId) {

                                    studentWithActivations.activations.push({
                                        id: activation.activationId,
                                        name: activation.assessmentTitle,
                                        status: activation.status,
                                        assessmentScopedIdentifier: activation.assessmentScopedIdentifier.identifier,
                                        deliveryType: activation.deliveryType
                                    });
                                }
                            });

                            // sort by alpha activation name
                            studentWithActivations.activations = _.sortBy(studentWithActivations.activations, function (activation) {
                                return activation.name.toLowerCase();
                            });
                        });

                        return studentWithActivationsList;
                    };

                    var updateFilters = function () {
                        var activationsGroupedByStudents = groupActivationsByStudents($scope.activations);
                        var filteredStudentWithActivationsList = filterStudentWithActivations(activationsGroupedByStudents);

                        $scope.facetedSearchManager.setData(filteredStudentWithActivationsList);
                    };

                    var updateLimitFlags = function () {
                        $scope.isOverHardDisplayLimit = $scope.filteredStudentWithActivationsListFilteredByFacets.length > HARD_DISPLAY_LIMIT;
                        $scope.isOverSoftDisplayLimit = $scope.filteredStudentWithActivationsListFilteredByFacets.length > SOFT_DISPLAY_LIMIT;
                    };

                    var dataWasFiltered = function () {
                        updateLimitFlags();
                        notifyContentChange();
                    };

                    var debounced_updateFilters = _.debounce(function () {
                        updateFilters();
                        $scope.$apply();
                    }, 300);


                    /*============ BEHAVIOR ============*/

                    $scope.clearStudentFilter = function () {
                        $scope.studentFilter = '';
                    };

                    $scope.clearAllFacets = function () {
                        $scope.facetedSearchManager.clearAllFacets();
                    };

                    $scope.acceptSoftLimit = function () {
                        $scope.softLimitAccepted = true;
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-activation-faceted-search.updateData', function (e, activations_in) {
                        $scope.activations = activations_in;
                        updateFilters();
                    });

                    // when the user updates the student filter search we need to filter the activations
                    $scope.$watch('studentFilter', debounced_updateFilters);

                    $scope.facetedSearchManager.addListener('filteredDataUpdated', function (dataFilteredByFacets) {
                        $scope.filteredStudentWithActivationsListFilteredByFacets = dataFilteredByFacets;
                        dataWasFiltered();
                        $scope.$broadcast('ccc-activations-by-student.dataUpdated', $scope.filteredStudentWithActivationsListFilteredByFacets);
                    });

                    $scope.facetedSearchManager.addListener('facetDataUpdated', function (facetData, selectedFacetMap, facetsSelectedCount) {
                        $scope.$emit('ccc-activation-faceted-search.selectedFacetsUpdated', selectedFacetMap);
                        $scope.facetsSelectedCount = facetsSelectedCount;
                        dataWasFiltered();
                    });

                    $scope.$on('ccc-activation-status-and-date-filter.changed', function () {
                        $scope.$emit('ccc-activation-faceted-search.filterDataUpdated', $scope.filterData);
                    });
                }
            ],

            template: [

                '<div class="row">',

                    '<div class="col-md-3 col-sm-4">',
                        '<ccc-activation-status-and-date-filter filter-data="filterData"></ccc-activation-status-and-date-filter>',
                        '<ccc-facet-list faceted-search-manager="facetedSearchManager"></ccc-facet-list>',
                    '</div>',

                    '<div class="col-md-9 col-sm-8">',

                        '<div class="well well-thin ccc-activation-faceted-search-controls">',

                            '<div class="row">',
                                '<div class="col-xs-12">',

                                    '<div class="input-group" ccc-clearable-input="studentFilter">',
                                        '<span class="input-group-addon" id="ccc-activation-faceted-search-addon" ng-class="{\'input-group-addon-active\': studentFilterActive}">',
                                            '<i class="fa fa-search" aria-hidden="true"></i> ',
                                            '<span class="sr-only" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.SEARCH_STUDENT_NAME_SR"></span>',
                                        '</span>',
                                        '<input ',
                                            'id="ccc-activation-faceted-search" ',
                                            'class="form-control" ',
                                            'type="text" ',
                                            'ng-model="studentFilter" ',
                                            'placeholder="{{ \'CCC_VIEW_HOME.ACTIVATION_FSEARCH.STUDENT_NAME_SEARCH\' | translate }}" ',
                                            'aria-describedby="ccc-activation-faceted-search-addon"',
                                        '/>',
                                        '<label class="sr-only" for="ccc-activation-faceted-search" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.STUDENT_NAME_SEARCH"></label>',
                                    '</div>',

                                '</div>',
                            '</div>',

                        '</div>',

                        '<ccc-content-loading-placeholder ng-if="filteredStudentWithActivationsListFilteredByFacets.length === 0" no-results-info="filteredStudentWithActivationsListFilteredByFacets.length === 0 && !loading">',
                            '<div class="ccc-content-loading-placeholder-details" ng-if="facetsSelectedCount !== 0 && !loading" ng-bind-html="\'CCC_VIEW_HOME.ACTIVATION_FSEARCH.CLEAR_FACETS\' | translate" ng-click="clearAllFacets()"></div>',
                            '<div class="ccc-content-loading-placeholder-details" ng-if="studentFilterActive && !loading" ng-bind-html="\'CCC_VIEW_HOME.ACTIVATION_FSEARCH.CLEAR_NAME\' | translate" ng-click="clearStudentFilter()"></div>',
                            '<div class="ccc-content-loading-placeholder-details" ng-if="facetsSelectedCount === 0 && !studentFilterActive && !loading" ng-bind-html="\'CCC_VIEW_HOME.ACTIVATION_FSEARCH.MODIFY_DATE\' | translate"></div>',
                        '</ccc-content-loading-placeholder>',

                        '<div aria-live="polite" aria-relevant="all" class="sr-only">{{ariaLiveMessage}}</div>',

                        '<ccc-activations-by-student ng-if="!loading && (!isOverSoftDisplayLimit || (isOverSoftDisplayLimit && softLimitAccepted)) && !isOverHardDisplayLimit" students="filteredStudentWithActivationsListFilteredByFacets" loading="loading"></ccc-activations-by-student>',

                        '<ccc-content-loading-placeholder ng-if="!loading && isOverSoftDisplayLimit && !softLimitAccepted && !isOverHardDisplayLimit" no-results="true" hide-default-no-results-text="true">',
                            '<div class="text-left">',
                                '<h4 translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_RESULTS_TITLE"></h4>',
                                '<p translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_RESULTS_SOFT_DESCRIPTION"></p>',
                                '<ul>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_USE_SMALLER_DATE"></li>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_FILTER_BY_FACETS"></li>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_FILTER_BY_NAME"></li>',
                                    '<li>',
                                        '<span translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_DISPLAY_ANYWAY_PRE"></span> ',
                                        '<button class="btn btn-default btn-sm" ng-click="acceptSoftLimit()" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_DISPLAY_ANYWAY_BUTTON"></button> ',
                                        '<span translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_DISPLAY_ANYWAY_POST"></span>',
                                    '</li>',
                                '</ul>',
                            '</div>',
                        '</ccc-content-loading-placeholder>',

                        '<ccc-content-loading-placeholder ng-if="!loading && isOverHardDisplayLimit" no-results="true" hide-default-no-results-text="true">',
                            '<div class="text-left">',
                                '<h4 translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_RESULTS_TITLE"></h4>',
                                '<p translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_RESULTS_HARD_DESCRIPTION"></p>',
                                '<ul>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_USE_SMALLER_DATE"></li>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_FILTER_BY_FACETS"></li>',
                                    '<li translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.TOO_MANY_FILTER_BY_NAME"></li>',
                                '</ul>',
                            '</div>',
                        '</ccc-content-loading-placeholder>',

                    '</div>',

                '</div>'
            ].join('')

        };

    });

})();





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

    angular.module('CCC.View.Home').directive('cccUserFacetedSearch', function () {

        return {

            restrict: 'E',

            scope: {
                users: "=?",            // pass them in optionally or fire and event to update the list
            },

            controller: [

                '$scope',
                '$timeout',
                '$translate',
                'FacetedSearchManagerClass',

                'UsersAPIService',
                'RolesService',

                function ($scope, $timeout, $translate, FacetedSearchManagerClass, UsersAPIService, RolesService) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var SOFT_DISPLAY_LIMIT = $scope.softDisplayLimit || 200;
                    var HARD_DISPLAY_LIMIT = $scope.hardDisplayLimit || 500;

                    var filteredUsers = [];

                    var notifyContentChange = function () {
                        $scope.ariaLiveMessage = '';
                        $timeout(function () {
                            $translate('CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.ARIA_CONTENT_LOADED').then(function (ariaString) {
                                $scope.ariaLiveMessage = ariaString;
                            });
                        }, 500);
                    };

                    var getEnabledTitle = function (isEnabled) {
                        return isEnabled ? 'Yes' : 'No';
                    };


                    /*============ MODEL ============*/

                    $scope.searchCount = 0;

                    $scope.users = $scope.users || [];
                    $scope.loading = false;

                    $scope.isOverHardDisplayLimit = false;  // we won't let the user display this much... their browser will crash
                    $scope.isOverSoftDisplayLimit = false;  // warn the user that this ammount of data can not perform well

                    $scope.softLimitAccepted = false;   // the user can say "yes" display results up to the hard limit

                    $scope.filteredUsersFilteredByFacets = [];
                    $scope.facetsSelectedCount = 0;

                    $scope.userNameFilter = '';
                    $scope.userNameFilterActive = false;

                    $scope.ariaLiveMessage = '';

                    $scope.facetedSearchManager = new FacetedSearchManagerClass({
                        title: 'REFINE BY:',
                        data: filteredUsers,
                        selectedFacets: {
                            enabled: {
                                enabled: true
                            }
                        },
                        facets: [
                            {
                                id: 'role',
                                title: 'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.FTITLE.ROLE',
                                multiSelect: true,
                                fieldValueAndTitle: function (dataItem) {

                                    return _.map(dataItem.roles, function (role) {

                                        return {
                                            value: role,
                                            title: RolesService.getRoleTitle(role)
                                        };
                                    });
                                },
                                sortBy: function (facetValue) {
                                    return facetValue.title.toLowerCase();
                                }
                            },
                            {
                                id: 'college',
                                multiSelect: true,
                                title: 'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.FTITLE.COLLEGE',
                                fieldValueAndTitle: function (dataItem) {

                                    return _.map(dataItem.colleges, function (college) {

                                        return {
                                            value: college.cccId,
                                            title: college.name
                                        };
                                    });
                                },
                                sortBy: function (facetValue) {
                                    return facetValue.title.toLowerCase();
                                }
                            },
                            {
                                id: 'enabled',
                                multiSelect: false,
                                title: 'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.FTITLE.ENABLED',
                                fieldValueAndTitle: function (dataItem) {

                                    return {
                                        value: dataItem.enabled ? 'enabled' : 'disabled',
                                        title: getEnabledTitle(dataItem.enabled)
                                    };
                                }
                            }
                        ]
                    });


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var filterUsers = function (users_in) {

                        $scope.userNameFilterActive = $.trim($scope.userNameFilter) !== '';

                        var searchTokens = $scope.userNameFilter.split(/[\s,]+/g);

                        for (var i=0; i < searchTokens.length; i++) {
                            searchTokens[i] = $.trim(searchTokens[i].toLowerCase());
                        }

                        // if there is only an empty token then don't filter
                        if (searchTokens.length === 1 && searchTokens[0] === '') {
                            return users_in;
                        }

                        return _.filter(users_in, function (user) {

                            var matches = true;
                            _.each(searchTokens, function (token) {
                                matches = matches && (user._nameSearch.indexOf(token) !== -1);
                            });
                            return matches;
                        });
                    };

                    var updateFilters = function () {
                        filteredUsers = filterUsers($scope.users);
                        $scope.facetedSearchManager.setData(filteredUsers);
                    };

                    var debounced_updateFilters = _.debounce(function () {
                        updateFilters();
                        $scope.$apply();
                    }, 300);

                    var updateLimitFlags = function () {
                        $scope.isOverHardDisplayLimit = $scope.filteredUsersFilteredByFacets.length > HARD_DISPLAY_LIMIT;
                        $scope.isOverSoftDisplayLimit = $scope.filteredUsersFilteredByFacets.length > SOFT_DISPLAY_LIMIT;
                    };

                    var dataWasFiltered = function () {
                        updateLimitFlags();
                        notifyContentChange();
                    };

                    // ensure that we don't show develepors and students
                    // so ultimately if we clean up roles and there are no roles left that user should not be shown
                    var preProcessUsers = function (users) {

                        RolesService.markLeanUsersWithBlackListedRoles(users);

                        return _.filter(users, function (user) {
                            return user.roles.length > 0 && !user._hasBlackListedRoles;
                        });
                    };

                    var refresh = function () {

                        $scope.loading = true;

                        $scope.searchCount++;

                        UsersAPIService.userListSearch({}, true).then(function (users) {

                            $scope.users = preProcessUsers(users);

                            _.each($scope.users, function (user) {

                                user._nameSearch =  (user.lastName ? user.lastName.toLowerCase() : "") + " " + (user.firstName ? user.firstName.toLowerCase() : "");

                                user._colleges = _.sortBy(_.map(user.colleges, function (college) {
                                    return college.name;
                                })).join(', ');

                                user._rolesString = _.sortBy(_.map(user.roles, function (role) {
                                    return RolesService.getRoleTitle(role);
                                })).join(', ');
                            });

                            updateFilters();

                        }).finally(function () {
                            $scope.loading = false;
                        });

                        updateFilters();
                    };

                    var initialize = function () {
                        refresh();
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.clearUserNameFilter = function () {
                        $scope.userNameFilter = '';
                    };

                    $scope.clearAllFacets = function () {
                        $scope.facetedSearchManager.clearAllFacets();
                    };

                    $scope.acceptSoftLimit = function () {
                        $scope.softLimitAccepted = true;
                    };

                    $scope.addUser = function () {
                        $scope.$emit('ccc-user-faceted-search.addUser');
                    };

                    $scope.userSelected = function (user) {
                        $scope.$emit('ccc-user-faceted-search.userSelected', user);
                    };


                    /*============ LISTENERS ============*/

                    // when the user updates the username filter search we need to filter the users
                    $scope.$watch('userNameFilter', debounced_updateFilters);

                    $scope.facetedSearchManager.addListener('filteredDataUpdated', function (dataFilteredByFacets) {

                        $scope.filteredUsersFilteredByFacets = _.sortBy(dataFilteredByFacets, function (user) {
                            return user._nameSearch;
                        });

                        dataWasFiltered();
                    });

                    $scope.facetedSearchManager.addListener('facetDataUpdated', function (facetData, selectedFacetMap, facetsSelectedCount) {

                        $scope.$emit('ccc-user-faceted-search.selectedFacetsUpdated', selectedFacetMap);
                        $scope.facetsSelectedCount = facetsSelectedCount;

                        dataWasFiltered();
                    });

                    $scope.$on('ccc-user-faceted-search.refresh', refresh);


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="row">',

                    '<div class="col-md-4 col-sm-5">',

                        '<ccc-facet-list faceted-search-manager="facetedSearchManager"></ccc-facet-list>',

                    '</div>',

                    '<div class="col-md-8 col-sm-7">',

                        '<div class="well well-thin ccc-activation-faceted-search-controls">',

                            '<div class="row">',
                                '<div class="col-xs-12">',

                                    '<div class="input-group" ccc-clearable-input="userNameFilter">',
                                        '<span class="input-group-addon" id="ccc-activation-faceted-search-addon" ng-class="{\'input-group-addon-active\': userNameFilterActive}">',
                                            '<i class="fa fa-search" aria-hidden="true"></i> ',
                                            '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.SEARCH_USER_NAME_SR"></span>',
                                        '</span>',
                                        '<input ',
                                            'id="ccc-activation-faceted-search" ',
                                            'class="form-control" ',
                                            'type="text" ',
                                            'ng-model="userNameFilter" ',
                                            'placeholder="{{ \'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.USER_NAME_SEARCH\' | translate }}" ',
                                            'aria-describedby="ccc-activation-faceted-search-addon"',
                                        '/>',
                                    '</div>',

                                '</div>',
                            '</div>',

                        '</div>',


                        '<ccc-content-loading-placeholder ng-if="filteredUsersFilteredByFacets.length === 0" no-results="filteredUsersFilteredByFacets.length === 0 && !loading">',
                            '<div class="ccc-content-loading-placeholder-details" ng-if="facetsSelectedCount !== 0 && !loading" ng-bind-html="\'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.CLEAR_FACETS\' | translate" ng-click="clearAllFacets()"></div>',
                            '<div class="ccc-content-loading-placeholder-details" ng-if="userNameFilterActive && !loading" ng-bind-html="\'CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.CLEAR_NAME\' | translate" ng-click="clearUserNameFilter()"></div>',
                        '</ccc-content-loading-placeholder>',

                        '<ul class="list-unstyled search-result user-cards" ng-if="!loading && (!isOverSoftDisplayLimit || (isOverSoftDisplayLimit && softLimitAccepted)) && !isOverHardDisplayLimit">',

                            '<li class="user user-card" ng-repeat="user in filteredUsersFilteredByFacets track by (user.userAccountId + \'-\' + searchCount)" ng-click="userSelected(user)" ccc-focusable>',
                                '<div class="row card-data">',
                                    '<div class="number-wrapper">',
                                        '<div class="list-number">',
                                            '<span class="value number">{{$index + 1}}</span>',
                                        '</div>',
                                    '</div>',
                                    '<div class="card-wrapper">',
                                        '<div class="col-sm-10">',
                                            '<div class="user-data">',
                                                '<h3 class="name">',
                                                    '<span class="value lastname emphasize">{{::user.lastName}}</span><span ng-if="user.lastName">, </span>',
                                                    '<span class="value firstname">{{::user.firstName}}</span> ',
                                                    '<span class="value middleInitial" ng-if="user.middleInitial">{{::user.middleInitial}}.</span>',
                                                '</h3>',
                                                '<div class="profile-data">',
                                                    '<span class="data college">',
                                                        '<span class="data-label sr-only">College:</span> ',
                                                        '<span class="value">{{::user._colleges}}</span>',
                                                    '</span>',
                                                    '<span class="data roles">',
                                                        '<span class="data-label sr-only">Roles:</span> ',
                                                        '<span class="value">{{::user._rolesString}}</span>',
                                                    '</span>',
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</li>',
                        '</ul>',

                        '<ccc-content-loading-placeholder ng-if="!loading && isOverSoftDisplayLimit && !softLimitAccepted && !isOverHardDisplayLimit" no-results="true" hide-default-no-results-text="true">',
                            '<div class="text-left">',
                                '<h4 translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_RESULTS_TITLE"></h4>',
                                '<p translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_RESULTS_SOFT_DESCRIPTION"></p>',
                                '<ul>',
                                    '<li translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_FILTER_BY_FACETS"></li>',
                                    '<li translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_FILTER_BY_NAME"></li>',
                                    '<li>',
                                        '<span translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_DISPLAY_ANYWAY_PRE"></span> ',
                                        '<button class="btn btn-default btn-sm" ng-click="acceptSoftLimit()" translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_DISPLAY_ANYWAY_BUTTON"></button> ',
                                        '<span translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_DISPLAY_ANYWAY_POST"></span>',
                                    '</li>',
                                '</ul>',
                            '</div>',
                        '</ccc-content-loading-placeholder>',

                        '<ccc-content-loading-placeholder ng-if="!loading && isOverHardDisplayLimit" no-results="true" hide-default-no-results-text="true">',
                            '<div class="text-left">',
                                '<h4 translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_RESULTS_TITLE"></h4>',
                                '<p translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_RESULTS_HARD_DESCRIPTION"></p>',
                                '<ul>',
                                    '<li translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_FILTER_BY_FACETS"></li>',
                                    '<li translate="CCC_VIEW_HOME.CCC-USER-FACETED-SEARCH.TOO_MANY_FILTER_BY_NAME"></li>',
                                '</ul>',
                            '</div>',
                        '</ccc-content-loading-placeholder>',

                        '<div aria-live="polite" aria-relevant="all" class="sr-only">{{ariaLiveMessage}}</div>',

                    '</div>',

                '</div>'
            ].join('')

        };

    });

})();





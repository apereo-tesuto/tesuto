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

    angular.module('CCC.View.Home').directive('cccProctorLocation', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$location',
                'CCCUtils',
                'Moment',
                'ActivationClass',
                'ActivationSearchAPIService',
                'StudentsAPIService',
                'CurrentUserService',
                'ACTIVATION_STATUS_COMPARATOR',
                'FakeData',

                function ($scope, $element, $timeout, $location, CCCUtils, Moment, ActivationClass, ActivationSearchAPIService, StudentsAPIService, CurrentUserService, ACTIVATION_STATUS_COMPARATOR, FakeData) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var updateTimeout;
                    var UPDATE_TIMEOUT_INTERVAL = 2 * (1000 * 60);

                    var scopeDestroyed = false;

                    var STARTED_STATUS_LIST = ['IN_PROGRESS', 'COMPLETE', 'PAUSED'];

                    // TODO: Once the default values for soft and hard display limits are fine tuned we can remove these flags and related testing code
                    var queryParamSoftLimit = $location.search()['proctorSoftLimit'] || false;
                    var queryParamHardLimit = $location.search()['proctorHardLimit'] || false;
                    var queryParamNumberStudents = $location.search()['proctorStudents'] || false;

                    // you can turn this to true if you want to override params
                    var allowParamOverrides = false;


                    /*============ MODEL ==============*/

                    $scope.activations = [];
                    $scope.selectedFacets = {};

                    $scope.loading = true;
                    $scope.errorLoadingActivations = false;

                    $scope.allowActivationCreation = CurrentUserService.hasPermission('CREATE_ACTIVATION');

                    $scope.filterData = {
                        status: 'started',
                        dateWindowType: 'today'
                    };

                    // DEFAULT for the hard and soft display limits of proctor location results
                    $scope.facetedSearchDisplaySoftLimit = 500;
                    $scope.facetedSearchDisplayHardLimit = 1500;

                    // for testing you can pass in query params to use fake data and modify the limits
                    // this allows for convenient testing of the UI performacne of rendering a ton of elements
                    if (queryParamSoftLimit && queryParamHardLimit && queryParamNumberStudents && allowParamOverrides) {
                        $scope.facetedSearchDisplaySoftLimit = parseInt(queryParamSoftLimit);
                        $scope.facetedSearchDisplayHardLimit = parseInt(queryParamHardLimit);
                    }


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var updateActivations = function (activations_in) {

                        $scope.activations = activations_in;

                        // for testing you can pass in query params to use fake data and modify the limits
                        // this allows for convenient testing of the UI performacne of rendering a ton of elements
                        if (queryParamSoftLimit && queryParamHardLimit && queryParamNumberStudents && allowParamOverrides) {
                            $scope.activations = FakeData.getFakeActivations(parseInt(queryParamNumberStudents));
                        }

                        $scope.$broadcast('ccc-activation-faceted-search.updateData', $scope.activations);
                        $scope.$broadcast('ccc-activation-statistics.updateData', $scope.activations);
                    };

                    var removeDuplicateStudentIds = function (activations_in) {

                        var studentList = [];

                        _.each(activations_in, function (activation) {
                            studentList.push(activation.userId);
                        });

                        var filteredStudentList = _.uniq(studentList);
                        return filteredStudentList;
                    };

                    // right now we have some client side filtering based on the filterData.status
                    var filterActivationList = function (activationList) {

                        // no additional filtering needed on created
                        if ($scope.filterData.status === 'created') {

                            return activationList;

                        // for 'started' filter we could end up with activations that were created but not started, so filter those out
                        // this is because we are searching on minStatusUpdateData and activations when created get their status data updated even though they haven't started
                        } else {
                            return _.filter(activationList, function (activation) {
                                return STARTED_STATUS_LIST.indexOf(activation.status) !== -1;
                            });
                        }
                    };

                    var getActivations = function () {

                        $scope.loading = true;
                        updateActivations([]);

                        var searchConfigs = {
                            locationIds: [$scope.location.id]
                        };

                        var minSearchDate = new Moment($scope.filterData.minDate).startOf('day').valueOf();
                        var maxSearchDate = new Moment($scope.filterData.maxDate).endOf('day').valueOf();

                        // for created it's easy, just search min and max create date
                        if ($scope.filterData.status === 'created') {

                            searchConfigs.minCreateDate = minSearchDate;
                            searchConfigs.maxCreateDate = maxSearchDate;

                        // (https://cccnext.jira.com/browse/CCCAS-4435) for 'started' we search for minStatus, but NOTE, created date is included in minStatusUpdate so we will need to filter out those that only have a 'ready' state
                        } else {
                            searchConfigs.minStatusUpdateDate = minSearchDate;
                            searchConfigs.maxStatusUpdateDate = maxSearchDate;
                        }

                        ActivationSearchAPIService.getActivationsForLocation(searchConfigs).then(function (activationList) {

                                activationList = filterActivationList(activationList);

                                // Because ActivationSearchAPIService only returns student id, we need an extra call to get studentFirstName, studentLastName
                                var studentList = removeDuplicateStudentIds(activationList);

                                StudentsAPIService.studentListSearch(studentList).then(function (students) {

                                    // This may need refactoring once StudentsAPIService returns real data
                                    _.each(students, function (student) {

                                        if (studentList.indexOf(student.cccId) !== -1) {

                                            var matchingStudent = student;

                                            _.each(activationList, function (activation) {

                                                if (activation.userId === matchingStudent.cccId) {
                                                    activation.student = matchingStudent;
                                                }
                                            });
                                        }
                                    });

                                    var activations = CCCUtils.coerce(ActivationClass, activationList);

                                    // first sort before we send it in
                                    activations.sort(ACTIVATION_STATUS_COMPARATOR);
                                    updateActivations(activations);

                                    $scope.loading = false;
                                });

                            }, function () {

                                $scope.errorLoadingActivations = true;

                            })
                            .finally(function () {

                                // here we set up an interval for refreshing the data
                                $timeout.cancel(updateTimeout);

                                // it's possible an ajax call was finishing but the directive was removed
                                // so check a flag that would be set if this was destroyed
                                if (!scopeDestroyed) {
                                    updateTimeout = $timeout(getActivations, UPDATE_TIMEOUT_INTERVAL);
                                }
                            });
                    };

                    var getActivationsDebounced = _.debounce(function () {

                        getActivations();
                        $scope.$apply();
                    }, 100);


                    /*============ BEHAVIOR ==============*/

                    $scope.refreshActivations = function () {
                        getActivationsDebounced();
                    };

                    $scope.checkInStudent = function () {
                        $scope.$emit('ccc-proctor-location.requestCheckinStudent');
                    };

                    $scope.setActiveDateFilter = function (filter) {
                        $scope.activeDateFilter = filter;
                        getActivationsDebounced();
                        $element.find('.ccc-proctor-location-date-filter button').focus();
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('$destroy', function () {
                        $timeout.cancel(updateTimeout);
                        scopeDestroyed = true;
                    });

                    // we cache any changes in facets so we can pass them back in when we redraw the faceted search
                    $scope.$on('ccc-activation-faceted-search.selectedFacetsUpdated', function (e, selectedFacetMap) {
                        $scope.selectedFacets = selectedFacetMap;
                    });

                    // we can be asked to update our results
                    $scope.$on('ccc-proctor-location.requestRefresh', $scope.refreshActivations);

                    $scope.$on('LocationService.currentTestCenterUpdated', function (e, location) {
                        $scope.location = location;
                        $element.find('ccc-locations-list button').focus();
                    });

                    $scope.$on('ccc-activation-faceted-search.selectedFacetsUpdated', function (e, selectedFacetMap) {
                        $scope.selectedFacets = selectedFacetMap;
                    });

                    $scope.$on('ccc-activation-faceted-search.filterDataUpdated', getActivationsDebounced);


                    /*============ INITIALIZATION ==============*/

                    getActivationsDebounced();
                }
            ],

            template: [
                '<div class="row margin-bottom-double">',

                    '<div class="col-sm-6 col-sm-push-6 text-right">',

                        '<button class="btn btn-default btn-full-width-when-small btn-icon-left " ng-click="refreshActivations()" ng-disabled="loading">',
                            '<i class="fa fa-refresh" ng-class="{\'fa-spin\':loading}" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.REFRESH"></span>',
                        '</button>',

                        '<button ccc-autofocus class="btn btn-primary btn-full-width-when-small ccc-proctor-location-create-activation" ng-click="checkInStudent()" ng-if="allowActivationCreation"><span translate="CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.ACTIVATE_STUDENT"></span></button>',

                    '</div>',

                    '<div class="col-sm-6 col-sm-pull-6">',
                        '<ccc-locations-list list-style="primary"></ccc-locations-list>',
                        '<ccc-activation-date-filter></ccc-activation-date-filter>',
                    '</div>',

                '</div>',

                '<ccc-activation-faceted-search loading="loading" activations="activations" selected-facets="selectedFacets" filter-data="filterData" soft-display-limit="facetedSearchDisplaySoftLimit" hard-display-limit="facetedSearchDisplayHardLimit"></ccc-activation-faceted-search>'
            ].join('')

        };

    });

})();

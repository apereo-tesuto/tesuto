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

    angular.module('CCC.View.Home').directive('cccWorkflowManagePlacements', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$timeout',
                '$state',
                'ViewManagerEntity',
                'NotificationService',
                'CurrentUserService',
                'SubjectAreaClass',
                'MMSubjectAreaClass',
                'ASSESSMENTS_DISABLED',

                function ($scope, $timeout, $state, ViewManagerEntity, NotificationService, CurrentUserService, SubjectAreaClass, MMSubjectAreaClass, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES =============*/

                    var overviewSubjectArea;


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});
                    $scope.college = null;

                    $scope.loading = true;


                    /*============ VIEW LOADING METHODS ============*/

                    var addSequenceWorkflow = function (subjectArea) {
                        var viewScope = $scope.$new();

                        viewScope.subjectArea = subjectArea;

                        viewScope.$on('ccc-workflow-subject-area-sequence.done', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-subject-area-sequence.sequenceUpdated', function (e) {

                            // the edit date has now changed and the dirty flag so need to refresh the list
                            $scope.$broadcast('ccc-subject-areas-list.requestRefresh');

                            // the overviewSubjectArea has not changed the sequence view modifies only courses and associated course competencies so those references are all updated
                            overviewSubjectArea.dirty = true;
                            overviewSubjectArea.lastEditedDate = new Date().getTime();
                            $scope.$broadcast('ccc-subject-area-overview.update', overviewSubjectArea);
                        });

                        $scope.viewManager.pushView({
                            id: 'subject-area-sequence-workflow',
                            title: viewScope.subjectArea.title + ' Subject Area',
                            breadcrumb: viewScope.subjectArea.title + ' Subject Area',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-subject-area-sequence subject-area="subjectArea"></ccc-workflow-subject-area-sequence>'
                        });
                    };

                    var addSubjectAreaCreateView = function (college) {
                        var viewScope = $scope.$new();

                        if (ASSESSMENTS_DISABLED) {
                            viewScope.subjectArea = new MMSubjectAreaClass({
                                collegeId: college.cccId
                            });
                        } else {
                            viewScope.subjectArea = new SubjectAreaClass({
                                collegeId: college.cccId
                            });
                        }

                        viewScope.$on('ccc-workflow-subject-area-create.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-subject-area-create.complete', function (e, subjectArea) {

                            $scope.$broadcast('ccc-subject-areas-list.requestRefresh');
                            $scope.viewManager.popView();

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Subject Area "' + subjectArea.title + ' " created.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-area-create-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.ADD_SUBJECT_AREA',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.ADD_SUBJECT_AREA',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-subject-area-create subject-area="subjectArea"></ccc-workflow-subject-area-create>'
                        });
                    };

                    var addSubjectAreaEditView = function (subjectArea) {

                        var viewScope = $scope.$new();

                        viewScope.subjectArea = subjectArea.clone();

                        viewScope.$on('ccc-workflow-subject-area-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-subject-area-edit.complete', function (e, subjectArea) {

                            overviewSubjectArea = subjectArea;
                            overviewSubjectArea.dirty = true;
                            overviewSubjectArea.lastEditedDate = new Date().getTime();

                            $scope.$broadcast('ccc-subject-areas-list.requestRefresh');
                            $scope.$broadcast('ccc-subject-area-overview.update', overviewSubjectArea);

                            $scope.viewManager.popView();

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Subject Area "' + subjectArea.title + ' " updated.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-area-edit-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.EDIT_SUBJECT_AREA',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.EDIT_SUBJECT_AREA',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-subject-area-edit subject-area="subjectArea"></ccc-workflow-subject-area-edit>'
                        });
                    };

                    var addPlacementOptionsView = function (options) {

                        var viewScope = $scope.$new();

                        viewScope.options = options;

                        viewScope.$on('ccc-placement-display-options-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-display-options',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.ELA_PLACEMENT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.ELA_PLACEMENT',
                            scope: viewScope,
                            template: '<ccc-placement-display-options-edit options="options"></ccc-placement-display-options-edit>'
                        });
                    };

                    var addSubjectAreaPublishView = function (subjectArea) {
                        var viewScope = $scope.$new();

                        viewScope.subjectArea = subjectArea;

                        viewScope.$on('ccc-subject-area-publish.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-subject-area-publish.done', function () {
                            $scope.viewManager.goToView('manage-placements-subject-areas-view', true);
                        });

                        viewScope.$on('ccc-subject-area-publish.subjectAreaPublished', function (e, newSubjectArea) {
                            overviewSubjectArea = newSubjectArea;
                            $scope.$broadcast('ccc-subject-areas-list.requestRefresh');
                            $scope.$broadcast('ccc-subject-area-overview.update', overviewSubjectArea);
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-area-publish',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.PUBLISH',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.PUBLISH',
                            scope: viewScope,
                            template: '<ccc-subject-area-publish subject-area="subjectArea"></ccc-subject-area-publish>'
                        });
                    };

                    var addSubjectAreaOverviewView = function (subjectArea, view) {

                        var viewScope = $scope.$new();

                        overviewSubjectArea = subjectArea;

                        viewScope.subjectArea = overviewSubjectArea;
                        viewScope.initialMode = view;
                        viewScope.college = $scope.college;

                        viewScope.$on('ccc-subject-area-overview.editConfigs', function (e) {
                            addSubjectAreaEditView(overviewSubjectArea);
                        });

                        viewScope.$on('ccc-subject-area-overview.editSequences', function (e) {
                            addSequenceWorkflow(overviewSubjectArea);
                        });

                        viewScope.$on('ccc-subject-area-overview.publish', function () {
                            addSubjectAreaPublishView(overviewSubjectArea);
                        });

                        viewScope.$on('ccc-subject-area-overview.done', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-area-overview',
                            title: 'Subject Area Details',
                            breadcrumb: 'Back',
                            scope: viewScope,
                            template: '<ccc-subject-area-overview subject-area="subjectArea" initial-mode="initialMode" college="college"></ccc-subject-area-overview>'
                        });
                    };

                    var addSubjectAreasView = function () {

                        var viewScope = $scope.$new();

                        viewScope.college = $scope.college;

                        viewScope.$on('ccc-placement-display-options.update', function (e, options) {
                            addPlacementOptionsView(options);
                        });

                        viewScope.$on('ccc-manage-subject-areas.addSubjectArea', function () {
                            addSubjectAreaCreateView($scope.college);
                        });

                        viewScope.$on('ccc-manage-subject-areas.viewPublished', function (e, subjectArea) {
                            addSubjectAreaOverviewView(subjectArea, 'published');
                        });

                        viewScope.$on('ccc-manage-subject-areas.editClicked', function (e, subjectArea) {
                            addSubjectAreaOverviewView(subjectArea, 'edits');
                        });

                        viewScope.$on('ccc-manage-subject-areas.subjectAreaArchived', function (e, subjectArea) {

                            $scope.$broadcast('ccc-subject-areas-list.requestRefresh');

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Subject Area "' + subjectArea.title + ' " deleted.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-areas-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.SUBJECT_AREAS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.SUBJECT_AREAS',
                            scope: viewScope,
                            template: '<ccc-manage-subject-areas college="college"></ccc-manage-subject-areas>'
                        });
                    };

                    var addCollegeSelectionView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-college-selection-view.collegeSelected', function (e, college) {
                            $scope.college = college;
                            addSubjectAreasView();
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-select-college',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.MANAGE_PLACEMENTS',
                            breadcrumb: 'Select a College',
                            scope: viewScope,
                            template: '<ccc-college-selection-view></ccc-college-selection-view>'
                        });
                    };


                    /*============ VIEW LOADER DEPENDENT METHODS ============*/

                    var loadColleges = function () {

                        CurrentUserService.getCollegeList().then(function (collegeList) {

                            // uncomment this line to test if the user only has one college... the view should skip straight to subject areas list
                            // collegeList = [collegeList[1]];

                            $scope.loading = false;

                            if (collegeList.length === 1) {

                                $scope.college = collegeList[0];
                                addSubjectAreasView();

                            } else {

                                addCollegeSelectionView();
                            }
                        });
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        $scope.location = location;
                    });

                    $scope.$on('ccc-placement-display-options-edit.updated', function () {
                        $scope.$broadcast('ccc-placement-display-options.requestRefresh');
                        $scope.viewManager.popView();
                    });


                    /*============ INITIALIZATION ==============*/

                    // before we can load the proper first view we need to get colleges
                    loadColleges();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager" loading="loading"></ccc-view-manager>'
            ].join('')

        };

    });

})();

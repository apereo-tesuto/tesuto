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

    angular.module('CCC.View.Home').directive('cccClassReportSetup', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$timeout',
                'LocationService',
                'CollegeSubjectAreasAPIService',
                'ClassReportAPIService',
                'CCCUtils',
                'ASSESSMENTS_DISABLED',

                function ($scope, $timeout, LocationService, CollegeSubjectAreasAPIService, ClassReportAPIService, CCCUtils, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.selectedCollegeId = LocationService.getCurrentCollegeId();

                    $scope.courseSubjectArea = [];
                    $scope.courseSubjectAreaLoading = false;
                    $scope.selectedCourseSubjectArea = null;

                    $scope.courses = [];
                    $scope.coursesLoading = false;
                    $scope.selectedCourse = null;

                    $scope.sectionId = null;
                    $scope.file = null;

                    $scope.loading = false;
                    $scope.submitted = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getCourseSubjectAreas = function () {
                        $scope.courseSubjectArea = [];
                        $scope.selectedCourseSubjectArea = null;

                        if ($scope.selectedCollegeId) {
                            $scope.courseSubjectAreaLoading = true;

                            CollegeSubjectAreasAPIService.getSubjectAreasByCollegeId($scope.selectedCollegeId).then(function (subjectAreasData) {

                                var subjectAreas =  _.map(subjectAreasData, function (subjectArea) {
                                    if (ASSESSMENTS_DISABLED) {
                                        return CCCUtils.coerce('MMSubjectAreaClass', subjectArea);
                                    } else {
                                        return CCCUtils.coerce('SubjectAreaClass', subjectArea);
                                    }
                                });

                                $scope.courseSubjectArea = _.sortBy(subjectAreas, function (subjectArea) {
                                    return subjectArea.title.toLowerCase();
                                });

                                $scope.$broadcast('ccc-item-dropdown.setItems', $scope.courseSubjectArea, 'cccCourseSubjectAreaDropdown');

                            }).finally(function () {
                                $scope.courseSubjectAreaLoading = false;
                            });
                        }
                    };

                    var getCourses = function (selectedCourseSubjectArea) {
                        $scope.courses = [];
                        $scope.selectedCourse = null;

                        if (selectedCourseSubjectArea) {
                            $scope.coursesLoading = true;

                            selectedCourseSubjectArea.fetchSequences().then(function (sequences) {

                                _.each(sequences, function (sequence) {
                                    if (sequence.courses.length) {
                                        _.each(sequence.courses, function (course) {
                                            $scope.courses.push(course);
                                        });
                                    }
                                });

                            }).finally(function () {
                                $scope.coursesLoading = false;
                            });
                        }

                        $scope.$broadcast('ccc-item-dropdown.setItems', $scope.courses, 'cccCoursesDropdown');
                    };

                    var refresh = function () {
                        getCourseSubjectAreas();
                        getCourses();
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccClassReportSetupForm'].$invalid) {
                            return;
                        }

                        ClassReportAPIService.upload($scope.file).then(function (fileName) {

                            var classReportForm = {
                                collegeId: $scope.selectedCollegeId,
                                competencyName: $scope.selectedCourseSubjectArea.competencyMapDiscipline,
                                courseId: $scope.selectedCourse.cid,
                                importedFilename: fileName,
                                sectionId: $scope.sectionId
                            };

                            ClassReportAPIService.validateAndGenerate(classReportForm).then(function (response) {

                                $scope.submitted = false;
                                checkResponseForErrors(response);

                            }, function (err) {

                                if (err.status === 400) {
                                    $scope.submitted = false;
                                    checkResponseForErrors(err);
                                }

                            });

                        }, function (err) {

                            if (err.status === 400) {
                                $scope.submitted = false;
                                checkResponseForErrors(err);
                            }

                        });
                    };

                    var checkResponseForErrors = function (response) {
                        var hasErrors = false;
                        var errors = [];

                        if (_.isArray(response.data)) {

                            _.each(response.data, function (result) {

                                if (_.has(result, 'errors') || _.has(result, 'errorCode')) {

                                    hasErrors = true;
                                    errors.push(result);
                                }
                            });

                        } else {

                            if (response.status === 400) {
                                hasErrors = true;
                                errors.push(response.data);
                            }
                        }

                        if (hasErrors) {
                            $scope.$emit('ccc-class-report-setup.generateErrors', errors);
                        } else {

                            // add on a few fields we'll need for the report view
                            response.college = $scope.selectedCollegeName;
                            response.courseSubjectArea = $scope.selectedCourseSubjectArea.competencyMapDiscipline;
                            response.course = $scope.selectedCourse.subject;
                            response.sectionId = $scope.sectionId;

                            $scope.$emit('ccc-class-report-setup.generateSuccess', response);
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.fileSelected = function (ele) {
                        $scope.file = null;
                        $scope.file = ele.currentTarget.files[0];

                        if ($scope.file) {
                            var fileExtension = $scope.file.name.substr($scope.file.name.lastIndexOf('.')+1).toLowerCase();

                            if (fileExtension !== 'csv') {
                                $scope.cccClassReportSetupForm.file.$setValidity('fileType', false);
                                $scope.cccClassReportSetupForm.file.$setDirty(true);
                            } else {
                                $scope.cccClassReportSetupForm.file.$setValidity('fileType', true);
                                $scope.cccClassReportSetupForm.file.$setDirty(true);
                            }
                        }

                        $scope.$apply();
                    };

                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.getCourseSubjectAreaId = function (itemId) {
                        return itemId.disciplineId;
                    };

                    $scope.getCourseSubjectAreaName = function (itemName) {
                        return itemName.title;
                    };

                    $scope.getCourseId = function (itemId) {
                        return itemId.courseId;
                    };

                    $scope.getCourseName = function (itemName) {
                        return itemName.name;
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$watch('selectedCourseSubjectArea', function () {
                        if ($scope.selectedCourseSubjectArea) {
                            getCourses($scope.selectedCourseSubjectArea);
                        }
                    });

                    $scope.$on('ccc-colleges-list.collegeSelected', function (e, selectedCollege) {
                        if (selectedCollege.cccId !== $scope.selectedCollegeId) {
                            $scope.selectedCollegeId = selectedCollege.cccId;
                            $scope.selectedCollegeName = selectedCollege.name;
                            refresh();
                        }
                    });

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {
                        if (dropdownId === 'cccCourseSubjectAreaDropdown') {
                            $scope.selectedCourseSubjectArea = item;
                        } else if (dropdownId === 'cccCoursesDropdown') {
                            $scope.selectedCourse = item;
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    refresh();
                }
            ],

            template: [

                '<div class="form-container ccc-form-container">',
                    '<form id="cccClassReportSetupForm" name="cccClassReportSetupForm">',
                        '<div class="form-body">',
                            '<div class="row">',
                                '<div class="col-md-6 col-md-offset-3">',
                                    '<h2 class="form-title" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.TITLE"></h2>',
                                    '<p class="help-block" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.TITLE_HELP"></p>',

                                    '<ccc-label-required></ccc-label-required>',

                                    '<div class="form-group">',
                                        '<label for="college">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.COLLEGE"></span>',
                                            ' <i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="collegeLoading"></i>',
                                        '</label>',
                                        '<div ccc-show-errors="cccClassReportSetupForm.college.$dirty || submitted">',
                                            '<ccc-colleges-dropdown class="btn-full-width-when-small" list-style="default" auto-select-first-college="true" is-required="true" selected-ccc-id="selectedCollegeId" is-disabled="loading || submitted"></ccc-colleges-dropdown>',
                                            '<div id="collegeErrors" class="ccc-validation-messages no-anim" ng-messages="cccClassReportSetupForm.college.$error">',
                                                '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.REQUIRED"></span></p>',
                                            '</div>',
                                        '</div>',
                                    '</div>',

                                    '<div class="form-group">',
                                        '<label for="cccCourseSubjectAreaDropdown">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.COURSE_SUB"></span>',
                                            ' <i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="cccCourseSubjectAreaLoadingDropdown"></i>',
                                        '</label>',
                                        '<div ccc-show-errors="cccClassReportSetupForm.cccCourseSubjectAreaDropdown.$dirty || submitted">',

                                            '<ccc-item-dropdown ',
                                                'id="cccCourseSubjectAreaDropdown" ',
                                                'class="btn-full-width" ',
                                                'loading="loading || courseSubjectAreaLoading || submitted" ',
                                                'is-required="true" ',
                                                'is-disabled="loading || courseSubjectAreaLoading || submitted" ',
                                                'required-error-msg="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.REQUIRED" ',
                                                'get-item-id="getCourseSubjectAreaId" ',
                                                'get-item-name="getCourseSubjectAreaName" ',
                                            '></ccc-item-dropdown>',

                                        '</div>',
                                    '</div>',

                                    '<div class="form-group">',
                                        '<label for="cccCoursesDropdown">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.COURSE"></span>',
                                            ' <i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="coursesLoading"></i>',
                                        '</label>',
                                        '<div ccc-show-errors="cccClassReportSetupForm.cccCoursesDropdown.$dirty || submitted">',

                                            '<ccc-item-dropdown ',
                                                'id="cccCoursesDropdown" ',
                                                'class="btn-full-width" ',
                                                'loading="loading || courseSubjectAreaLoading || submitted" ',
                                                'is-required="true" ',
                                                'is-disabled="loading || coursesLoading || submitted" ',
                                                'required-error-msg="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.REQUIRED" ',
                                                'get-item-id="getCourseId" ',
                                                'get-item-name="getCourseName" ',
                                            '></ccc-item-dropdown>',

                                        '</div>',
                                    '</div>',

                                    '<div class="form-group">',
                                        '<label for="sectionId">',
                                            '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                            '<span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.SECTIONID.LABEL"></span>',
                                        '</label>',
                                        '<p class="help-block" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.SECTIONID.HELP"></p>',
                                        '<div ccc-show-errors="cccClassReportSetupForm.sectionId.$dirty || submitted">',
                                            '<input type="text" class="form-control" id="sectionId" name="sectionId" placeholder="{{ \'CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.SECTIONID.PLACEHOLDER\' | translate }}"',
                                                'required ',
                                                'ng-model-options="{ debounce: 100 }" ',
                                                'ng-disabled="loading || submitted" ',
                                                'ng-model="sectionId" ',
                                                'ng-maxlength="140" ',
                                                'ccc-validation-badge="cccClassReportSetupForm" ',
                                                'ccc-validation-badge-style="fullWidthSelect" ',
                                                'aria-describedby="sectionIdErrors" ',
                                            '/>',
                                            '<div id="sectionIdErrors" class="ccc-validation-messages no-anim" ng-messages="cccClassReportSetupForm.sectionId.$error">',
                                                '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.PATTERN"></span></p>',
                                                '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.REQUIRED"></span></p>',
                                                '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.MAXLENGTH"></span></p>',
                                            '</div>',
                                        '</div>',
                                    '</div>',

                                    '<div class="file-upload">',
                                        '<div class="form-group">',
                                            '<div ccc-show-errors="cccClassReportSetupForm.file.$dirty || submitted">',
                                                '<label><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.UPLOAD"></span></label>',
                                                '<div class="drop-target" ng-class="{\'invalid\': cccClassReportSetupForm.file.$invalid && cccClassReportSetupForm.file.$dirty}">',
                                                    '<span class="icon fa fa-cloud-upload"></span>',
                                                    '<label class="btn btn-success" for="file">',
                                                        '<input id="file" name="file" ',
                                                            'type="file" ',
                                                            'ccc-input-file="fileSelected" ',
                                                            'required ',
                                                            'ng-disabled="loading || submitted" ',
                                                            'ng-model="file" ',
                                                            'accept=".csv" ',
                                                        '>',
                                                        '<span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.LABEL.CHOOSE_FILE"></span>',
                                                    '</label>',
                                                    '<p ng-if="!file" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.UPLOAD_HELP"></p>',
                                                    '<p ng-if="file"><i class="fa fa-file-text-o" aria-hidden="true"></i> {{file.name}}</p>',
                                                '</div>',
                                                '<div id="fileErrors" class="ccc-validation-messages no-anim" ng-messages="cccClassReportSetupForm.file.$error">',
                                                    '<p ng-message="fileType" class="noanim"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.ERROR.FILETYPE"></span></p>',
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                        '<div class="form-actions">',
                            '<div class="row">',
                                '<div class="col-md-6 col-md-offset-3">',
                                    '<div class="actions">',
                                        '<button ng-disabled="cccClassReportSetupForm.$invalid || submitted" class="btn btn-primary" ng-click="attemptDoSubmit()"><span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.GENERATE"></span> <span ng-if="!submitted" class="icon fa fa-chevron-right"></span><span class="icon fa fa-spin fa-spinner noanim" role="presentation" aria-hidden="true" ng-if="submitted"></span></button>',
                                        '<button class="btn btn-default" ui-sref="home" ng-disabled="submitted"><span class="icon fa fa-times" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT-SETUP.CANCEL"></span></button>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</form>',
                '</div>'

            ].join('')

        };

    });

})();

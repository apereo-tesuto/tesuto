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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSequence', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                readOnly: '@?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$translate',
                'ModalService',
                'CSSService',
                'SUBJECT_AREA_CB_21_CODES',

                function ($scope, $element, $timeout, $translate, ModalService, CSSService, SUBJECT_AREA_CB_21_CODES) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var CB21_CODES = SUBJECT_AREA_CB_21_CODES;


                    /*============ MODEL ==============*/

                    $scope.loading = false;

                    $scope.readOnly = $scope.readOnly === 'true' ? true : false;

                    $scope.sequenceLoading = false;
                    $scope.sequences = [];

                    $scope.highlightedSequencecb21Codes = {};


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getMissingCB21CodesFromSequences = function (existingSequences) {

                        var foundSequenceMap = _.reduce(existingSequences, function (memo, sequence) {
                            memo[sequence.cb21Code] = true;
                            return memo;
                        }, {});

                        return _.filter(CB21_CODES, function (code) {
                            return foundSequenceMap[code] !== true;
                        });
                    };

                    // build a custom object for display that will use existing subjectAreaSequences and fill in gaps for placeholders in certain cb21
                    var getPopulatedSequences = function (subjectAreaSequences_in) {

                        // create a new array reference
                        var subjectAreaSequences = subjectAreaSequences_in.slice(0);

                        // Now find all cb21 levels that have no entries and inject a few fake object to be used as placeholders in the UI
                        _.each(getMissingCB21CodesFromSequences(subjectAreaSequences), function (missingCB21Code) {

                            subjectAreaSequences.push({
                                "disciplineId": null,
                                "cb21Code": missingCB21Code,
                                "courseGroup": 0, // this is the flag that the template will use
                                "level": 0,
                                "courses": [],
                                "explanation": '',
                                "mappingLevel": null,
                                "showStudent": null
                            });
                        });

                        // Now re-sort the array first by cb21Code and then by courseGroup
                        subjectAreaSequences.sort(function (a, b) {
                            if (a.cb21Code !== b.cb21Code) {
                                return CB21_CODES.indexOf(a.cb21Code) - CB21_CODES.indexOf(b.cb21Code);
                            } else if (a.courseGroup !== b.courseGroup) {
                                return b.courseGroup - a.courseGroup;
                            } else {
                                throw new Error('ccc-subject-area-sequence.equivelentSequencesDetected');
                            }
                        });

                        // finally add some meta data to help with determine start/end of course groups for more fine tuned control over styling
                        var isEven = true;
                        var previousCB21Code = false;
                        _.each(subjectAreaSequences, function (sequence, index) {

                            // keep track of switches between sequences
                            if (sequence.cb21Code !== previousCB21Code) {
                                isEven = !isEven;
                            }

                            previousCB21Code = sequence.cb21Code;

                            sequence._isEven = isEven;
                            sequence._isFirst = (index === 0) || (sequence.cb21Code !== subjectAreaSequences[index - 1].cb21Code);
                            sequence._isLast = (index === subjectAreaSequences.length - 1) || (sequence.cb21Code !== subjectAreaSequences[index + 1].cb21Code);
                        });

                        return subjectAreaSequences;
                    };

                    var refresh = function () {

                        $scope.loading = true;

                        return $scope.subjectArea.fetchSequences().then(function () {

                            $scope.sequences = getPopulatedSequences($scope.subjectArea.sequences);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var highlightSequencecb21Codes = function (sequences, immediate) {

                        var animDelayTime = immediate ? 10 : 600;

                        $scope.highlightedSequencecb21Codes = _.reduce(sequences, function (memo, sequence) {
                            memo[sequence.cb21Code + '-' + sequence.courseGroup] = true;
                            return memo;
                        }, {});

                        $timeout(function () {
                            $scope.highlightedSequencecb21Codes = {};
                        }, animDelayTime);
                    };

                    var doSequenceShowStudentUpdate = function (sequence) {

                        sequence.update().then(function (){

                            highlightSequencecb21Codes([{cb21Code: sequence.cb21Code, courseGroup: sequence.courseGroup}], true);

                            $scope.$emit('ccc-subject-area-sequence.sequenceUpdated');

                        }, function () {

                            // since it failed, we should invert their choice so the view matches what is on the server
                            sequence.showStudent = !sequence.showStudent;

                        }).finally(function () {
                            $scope.sequenceLoading = false;
                        });
                    };

                    // we have a responsive table that requires the use of :before psuedo classes
                    // we therfore use the CSSService to dynamically inject the proper i18n friendly styles
                    var addInternationalizedStyles = function () {

                        var getCSSBeforeContent = function (beforeText) {
                            return "'" + beforeText + ":'";
                        };

                        CSSService.addDynamicStyles([
                            {
                                selector: '.subject-area-sequence tbody>tr>th:nth-of-type(1):before',
                                styles: {
                                    content: getCSSBeforeContent($translate.instant('CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.CB21'))
                                }
                            },
                            {
                                selector: '.subject-area-sequence td:nth-of-type(1):before',
                                styles: {
                                    content: getCSSBeforeContent($translate.instant('CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.COURSES'))
                                }
                            },
                            {
                                selector: '.subject-area-sequence td:nth-of-type(3):before',
                                styles: {
                                    content: getCSSBeforeContent($translate.instant('CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.EXPLANATION'))
                                }
                            }
                        ], 'ccc-subject-area-sequence');
                    };

                    var checkReadOnlyStyles = function () {

                        if ($scope.readOnly) {
                            $element.addClass('ccc-subject-area-sequence-read-only');
                        } else {
                            $element.addClass('ccc-subject-area-sequence-responsive');
                        }
                    };

                    var initialize = function () {

                        checkReadOnlyStyles();
                        addInternationalizedStyles();

                        // the subject area might have already loaded their sequences so we can skip this
                        if (!$scope.subjectArea.sequences || !$scope.subjectArea.sequences.length) {
                            refresh();
                        } else {
                            $scope.sequences = getPopulatedSequences($scope.subjectArea.sequences);
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.addCourse = function (e, sequence) {

                        // if the sequence we are adding to doesn't have a courseGroup number yet, then by default send 1
                        $scope.$emit('ccc-subject-area-sequence.addCourse', sequence, sequence.cb21Code, (sequence.courseGroup ? sequence.courseGroup : 1));
                    };

                    $scope.editExplanation = function (e, sequence) {
                        $scope.$emit('ccc-subject-area-sequence.editExplanation', sequence);
                    };

                    $scope.getExplanation = function (sequence) {

                        if (sequence.courseGroup) {
                            var max = 300;
                            return sequence.explanation.substr(0, max-1) + (sequence.explanation.length>max ? '...' : '');
                        } else {
                            return 'This sequence has no courses.';
                        }
                    };

                    $scope.updateSequenceShowStudent = function (sequence) {

                        $scope.sequenceLoading = sequence;

                        // because checkbox values and model values take a digest loop to update after clicked, we wait a sec before updating the sequence
                        $timeout(function () {

                            doSequenceShowStudentUpdate(sequence);

                        }, 1);
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-sequence-course-list.courseClicked', function (e, sequence, course) {
                        $scope.$emit('ccc-subject-area-sequence.courseClicked', sequence, course);
                    });

                    $scope.$on('ccc-sequence-course-list.deleteCourse', function (e, sequence, course) {

                        var buttonConfigs = {
                            cancel: {
                                title: 'Cancel',
                                btnClass: 'btn-primary'
                            },
                            okay: {
                                title: 'Delete',
                                btnClass: 'btn-default',
                                btnIcon: 'fa-times-circle'
                            }
                        };

                        var deleteCourseModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COURSE.TITLE',
                            message: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COURSE.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'warning'
                        });

                        deleteCourseModal.result.then(function () {
                            $scope.$emit('ccc-subject-area-sequence.deleteCourse', sequence, course);
                        });
                    });

                    $scope.$on('ccc-subject-area-sequence.requestHighlightSequences', function (e, sequencesChanged, immediate) {
                        highlightSequencecb21Codes(sequencesChanged, immediate);
                    });

                    $scope.$on('ccc-subject-area-sequence.refresh', function (e, sequencesChanged, immediate) {

                        // on success we have the option of highlighting a list of sequenced that changes
                        refresh().then(function () {
                            if (sequencesChanged) {
                                highlightSequencecb21Codes(sequencesChanged, immediate);
                            }
                        });
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder class="ccc-subject-area-sequence-loader" ng-hide="sequences.length > 0 && !loading" no-results="sequences.length === 0 && !loading"></ccc-content-loading-placeholder>',

                '<table ng-if="!loading && sequences.length" class="table table-responsive subject-area-sequence">',
                    '<thead>',
                        '<tr>',
                            '<th scope="col" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.CB21" class="text-center"></th>',
                            '<th scope="col" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.COURSES" width="35%" class="text-center">Courses</th>',
                            '<th scope="col" class="hidden-sm hidden-xs text-center" ng-if="::!readOnly"><span class="sr-only">add course controls</span></th>',
                            '<th scope="col" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.TABLE_HEADERS.EXPLANATION" ng-class="{\'text-left\': readOnly, \'text-center\': !readOnly}"></th>',
                            '<th scope="col" class="hidden-sm hidden-xs text-right" ng-if="::!readOnly"><span class="sr-only">explanation edit controls</span></th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="sequence in sequences track by sequence.cb21Code + \'-\' + sequence.courseGroup" ng-class="{\'ccc-subject-area-sequence-has-courses\': sequence.courses.length, \'sequence-highlighted\': highlightedSequencecb21Codes[sequence.cb21Code + \'-\' + sequence.courseGroup], \'row-odd\': !sequence._isEven, \'row-first\': sequence._isFirst, \'row-last\': sequence._isLast}">',

                            '<th scope="row" class="text-center">',
                                '<div class="responsive-table-cell-content">',

                                    '<span class="cb-21">{{::sequence.cb21Code}}<span class="sr-only">, course group {{sequence.courseGroup}}</span></span>',

                                '</div>',
                            '</th>',

                            '<td class="text-center">',
                                '<div class="responsive-table-cell-content">',

                                    '<button ng-click="addCourse($event, sequence)" ng-disabled="sequenceLoading || readOnly" class="btn btn-default add-course responsive-table-cell-content-button hidden-md hidden-lg">',
                                        '<span class="icon fa fa-plus" role="presentation" aria-hidden="true"></span>',
                                        '<span class="sr-only" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.BUTTON_LABELS.ADD_COURSE"></span>',
                                    '</button>',

                                    '<div class="courses course-group-box" ng-if="sequence.courses.length">',

                                        '<span class="course-group-box-number">{{::sequence.courseGroup}}</span>',

                                        '<ccc-sequence-course-list sequence="sequence" is-disabled="sequenceLoading || readOnly" read-only="{{::readOnly}}"></ccc-sequence-course-list>',

                                        '<div class="checkbox">',
                                            '<label>',
                                                '<input type="checkbox" ng-disabled="sequenceLoading || readOnly" ng-model="sequence.showStudent" ng-true-value="true" ng-false-value="false" ng-click="updateSequenceShowStudent(sequence)">',
                                                ' <span translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.SHOW_STUDENT_CHECKBOX_LABEL"></span> <span class="sr-only">for {{::sequence.cb21Code}} {{::sequence.courseGroup}}</span>',
                                                ' <i ng-if="sequence === sequenceLoading" class="fa fa-spin fa-spinner noanim" aria-hidden="true"></i>',
                                            '</label>',
                                        '</div>',

                                    '</div>',
                                    '<span ng-if="!sequence.courses.length" class="no-match" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.NO_COURSE"></span>',

                                '</div>',
                            '</td>',

                            '<td class="hidden-sm hidden-xs" ng-if="::!readOnly">',
                                '<button ng-click="addCourse($event, sequence)" ng-disabled="sequenceLoading || readOnly" class="btn btn-default add-course" ccc-focusable>',
                                    '<span class="icon fa fa-plus" role="presentation" aria-hidden="true"></span>',
                                    ' <span class="sr-only"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.BUTTON_LABELS.ADD_COURSE"></span> to {{::sequence.cb21Code}} {{::sequence.courseGroup}}</span>',
                                '</button>',
                            '</td>',

                            '<td colspan="{{sequence.courseGroup ? 1 : 2}}">',
                                '<div class="responsive-table-cell-content text-left">',

                                    '<button ng-if="sequence.courseGroup" ng-click="editExplanation($event, sequence)" ng-disabled="sequenceLoading || readOnly" class="btn btn-default edit-explanation responsive-table-cell-content-button hidden-md hidden-lg">',
                                        '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span>',
                                        '<span class="sr-only" translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.BUTTON_LABELS.EDIT_EXPLANATION"></span>',
                                    '</button>',

                                    '<div class="explanation dont-break-out">{{getExplanation(sequence)}}</div>',

                                '</div>',
                            '</td>',

                            '<td class="hidden-sm hidden-xs text-right" ng-if="sequence.courseGroup && !readOnly">',
                                '<button ng-click="editExplanation($event, sequence)" ng-disabled="sequenceLoading || readOnly" class="btn btn-default edit-explanation">',
                                    '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span>',
                                    ' <span class="sr-only"><span translate="CCC_VIEW_HOME.SUBJECT_AREA_SEQUENCE.BUTTON_LABELS.EDIT_EXPLANATION"></span> for {{::sequence.cb21Code}} {{::sequence.courseGroup}}</span>',
                                '</button>',
                            '</td>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();

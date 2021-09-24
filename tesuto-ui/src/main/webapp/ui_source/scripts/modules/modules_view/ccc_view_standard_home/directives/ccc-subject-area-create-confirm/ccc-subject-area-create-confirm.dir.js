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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCreateConfirm', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'SubjectAreaClass',
                'CurrentUserService',

                function ($scope, $element, $timeout, SubjectAreaClass, CurrentUserService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.submitted = false;
                    $scope.loading = false;

                    $scope.subjectAreaCollege = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSubmit = function () {

                        $scope.submitted = true;
                        $scope.loading = true;

                        if ($scope['cccCreateSubjectAreaForm'].$invalid) {

                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            $scope.loading = false;
                            return;

                        } else {

                            $scope.subjectArea.create().then(function () {
                                $scope.$emit('ccc-subject-areas-create.subjectAreaCreated', $scope.subjectArea);
                            }).finally(function () {
                                $scope.loading = false;
                            });
                        }
                    };

                    var loadCollege = function () {

                        CurrentUserService.getCollegeBycccId($scope.subjectArea.collegeId).then(function (college) {
                            $scope.subjectAreaCollege = college;
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-subject-areas-create.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    loadCollege();
                }
            ],

            template: [

                '<form name="cccCreateSubjectAreaForm" novalidate>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<h2 class="margin-bottom-double">',
                                '<span translate="CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.ADD_SUBJECT_AREA_PRE"></span> ',
                                '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="!subjectAreaCollege"></i>',
                                '<strong ng-if="subjectAreaCollege">{{subjectAreaCollege.name}}</strong>',
                            '</h2>',

                            '<ccc-subject-area-form></ccc-subject-area-form>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<div class="actions ccc-form-submit-controls">',
                                '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading" type="submit" ng-click="attemptDoSubmit()">',
                                    '<i class="fa fa-plus noanim" aria-hidden="true"></i>',
                                    '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="loading"></i>',
                                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.BUTTON_ADD_SUBJECT_AREA"></span>',
                                '</button>',
                                '<button ng-click="cancel()" class="btn btn-default btn-full-width-when-small">Cancel</button>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')

        };

    });

})();

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

    angular.module('CCC.View.Home').directive('cccCourseCompetencyGroupForm', function () {

        return {

            restrict: 'E',

            scope: {
                competencyGroup: '=',
                isDisabled: '=',
                submitted: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="ng-form" name="competencyGroupForm">',

                    '<ccc-label-required></ccc-label-required>',

                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<div class="form-group">',

                                '<label for="competencyGroupName"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.GROUP_NAME"></span>:</label>',
                                '<div ccc-show-errors="competencyGroupForm.name.$dirty || submitted">',
                                    '<input type="text" ccc-autofocus id="competencyGroupName" class="form-control" name="name" placeholder="Group Name" ',
                                        'required ',
                                        'ng-maxlength="120" ',
                                        'ng-pattern="/^[A-z|0-9|\\.|\\-|\\`|\\,|\\\'|\\s]+$/i" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="isDisabled" ',
                                        'ng-model="competencyGroup.name" ',
                                        'ccc-validation-badge="competencyGroupForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="competencyGroupNameErrors" ',
                                    '> ',
                                    '<div id="competencyGroupNameErrors" class="ccc-validation-messages noanim ccc-validation-messages-group-name" ng-messages="competencyGroupForm.name.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.GROUP_NAME_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.GROUP_NAME_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',

                            // '<div class="form-group">',

                            //     '<label for="percent"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MINIMUM_STUDENT_ABILITY"></span>:</label>',
                            //     '<p class="help-block" translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MINIMUM_STUDENT_ABILITY_HELP"></p>',
                            //     '<div ccc-show-errors="competencyGroupForm.percent.$dirty || submitted">',
                            //         '<div class="input-group input-length-4">',
                            //             '<input type="number" id="percent" class="form-control" name="percent" placeholder="" min="0" max="100" ',
                            //                 'required ',
                            //                 'autocomplete="off" ',
                            //                 'ng-pattern="/^[0-9]+$/i" ',
                            //                 'ng-model-options="{ debounce: 100 }" ',
                            //                 'ng-disabled="isDisabled" ',
                            //                 'ng-model="competencyGroup.percent" ',
                            //                 'ccc-validation-badge="competencyGroupForm" ',
                            //                 'ccc-validation-badge-style="fullWidth" ',
                            //                 'aria-describedby="percentErrors" ',
                            //             '> ',
                            //             '<span class="input-group-addon"><i class="fa fa-percent"></i></span>',
                            //         '</div>',
                            //         '<div id="percentErrors" class="ccc-validation-messages noanim ccc-validation-messages-percent" ng-messages="competencyGroupForm.percent.$error">',
                            //             '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.STUDENT_ABILITY_RANGE"></span></p>',
                            //             '<p ng-message="max" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.STUDENT_ABILITY_RANGE"></span></p>',
                            //             '<p ng-message="min" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.STUDENT_ABILITY_RANGE"></span></p>',
                            //             '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.SUBJECT_AREA_COURSE.ERROR.STUDENT_ABILITY_RANGE"></span></p>',
                            //         '</div>',
                            //     '</div>',

                            // '</div>',

                        '</div>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();

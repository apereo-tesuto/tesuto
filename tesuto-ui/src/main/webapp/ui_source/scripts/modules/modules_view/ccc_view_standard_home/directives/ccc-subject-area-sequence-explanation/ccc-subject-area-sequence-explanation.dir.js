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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSequenceExplanation', function () {

        return {

            restrict: 'E',

            scope: {
                sequence: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.submitted = false;
                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSubmit = function () {

                        $scope.submitted = true;
                        $scope.loading = true;

                        if ($scope['cccSubjectAreaSequenceExplanationForm'].$invalid) {

                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            $scope.loading = false;
                            return;

                        } else {

                            $scope.loading = true;

                            $scope.sequence.update().then(function () {

                                $scope.$emit('ccc-subject-area-sequence-explanation.updated', $scope.sequence);

                            }).finally(function () {
                                $scope.loading = false;
                            });
                        }
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
                        $scope.$emit('ccc-subject-area-sequence-explanation.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<ccc-label-required></ccc-label-required>',

                        '<form name="cccSubjectAreaSequenceExplanationForm" novalidate class="reading-width">',

                            '<div class="form-group">',
                                '<label for="explanation"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Explanation</label>',
                                '<div ccc-show-errors="cccSubjectAreaSequenceExplanationForm.explanation.$dirty || submitted">',
                                    '<textarea ccc-autofocus id="explanation" class="form-control resize-vertical" name="explanation" placeholder="Explanation" rows="6"',
                                        'required ',
                                        'ng-maxlength="3000" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="sequence.explanation" ',
                                        'ccc-validation-badge="cccSubjectAreaSequenceExplanationForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="explanationErrors" ',
                                    '></textarea>',
                                    '<div id="explanationErrors" class="ccc-validation-messages" ng-messages="cccSubjectAreaSequenceExplanationForm.explanation.$error">',
                                        '<p ng-message="required"><span translate="This is a required field."></span></p>',
                                        '<p ng-message="maxlength"><span translate="You have exceeded the 3000 character limit."></span></p>',
                                    '</div>',
                                '</div>',
                            '</div>',

                            '<div class="actions ccc-form-submit-controls">',
                                '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading || !cccSubjectAreaSequenceExplanationForm.$dirty" type="submit" ng-click="attemptDoSubmit()">',
                                    '<i class="fa fa-save noanim" aria-hidden="true"></i>',
                                    '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="loading"></i>',
                                    '<span>Save</span>',
                                '</button>',
                                '<button ng-click="cancel()" ng-disabled="loading" class="btn btn-default btn-full-width-when-small">Cancel</button>',
                            '</div>',
                        '</form>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();

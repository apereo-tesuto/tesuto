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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSequenceSisCode', function () {

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

                        if ($scope['cccSubjectAreaSequenceSisCodeForm'].$invalid) {

                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            $scope.loading = false;
                            return;

                        } else {

                            $scope.loading = true;

                            $scope.sequence.update().then(function () {

                                $scope.$emit('ccc-subject-area-sequence-sis-code.updated', $scope.sequence);

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
                        $scope.$emit('ccc-subject-area-sequence-sis-code.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<form name="cccSubjectAreaSequenceSisCodeForm" novalidate class="reading-width">',

                            '<div class="form-group">',
                                '<label for="mappingLevel" translate="CCC_VIEW_HOME.SEQUENCE_SIS_CODE_EDIT.LABEL"></label>',
                                '<div class="help-block" translate="CCC_VIEW_HOME.SEQUENCE_SIS_CODE_EDIT.HELP_TEXT"></div>',
                                '<div ccc-show-errors="cccSubjectAreaSequenceSisCodeForm.mappingLevel.$dirty || submitted">',
                                    '<input type="text" ccc-autofocus id="mappingLevel" class="form-control" name="mappingLevel" placeholder="SIS Code" ',
                                        'ng-maxlength="50" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="sequence.mappingLevel" ',
                                        'ccc-validation-badge="cccSubjectAreaSequenceSisCodeForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="mappingLevelErrors" ',
                                    '/>',
                                    '<div id="mappingLevelErrors" class="ccc-validation-messages" ng-messages="cccSubjectAreaSequenceSisCodeForm.mappingLevel.$error">',
                                        '<p ng-message="maxlength"><span translate="CCC_VIEW_HOME.SEQUENCE_SIS_CODE_EDIT.ERROR.MAX_LENGTH"></span></p>',
                                    '</div>',
                                '</div>',
                            '</div>',

                            '<div class="actions ccc-form-submit-controls">',
                                '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading || !cccSubjectAreaSequenceSisCodeForm.$dirty" type="submit" ng-click="attemptDoSubmit()">',
                                    '<i class="fa fa-save noanim" aria-hidden="true"></i>',
                                    '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="loading"></i>',
                                    '<span translate="CCC_VIEW_HOME.SEQUENCE_SIS_CODE_EDIT.BUTTON_SAVE"></span>',
                                '</button>',
                                '<button ng-click="cancel()" ng-disabled="loading" class="btn btn-default btn-full-width-when-small">',
                                    '<span translate="CCC_VIEW_HOME.SEQUENCE_SIS_CODE_EDIT.BUTTON_CANCEL"></span>',
                                '</button>',
                            '</div>',
                        '</form>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();

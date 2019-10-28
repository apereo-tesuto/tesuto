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

    // Ultimately the wizard uses steps to systematically add fields and values to a model that is passed in
    // The wizard accepts steps that are optional based an a useStep value provided by each step that can be conditional based on external immutableData and the current model passed into the wizard
    // Each step is responsible for calling an onReady callback passed into it with it's own isValid function
    // Each step has a fields array. If useStep is ever false for each step.. the list of fields can be used to clear those key value pairs from the model
    // When there are no more steps and next is clicked... the wizard will fire. $scope.$emit('ccc-wizard.complete')
    // The wizard will always present a cancel button... if that is clicked the wizard will fire $scope.$emit('ccc-wizard.cancel')

    angular.module('CCC.View.Home').directive('cccWizard', function () {

        return {

            restrict: 'E',

            scope: {
                wizard: '='    // pass in an instance of WizardClass
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$compile',
                '$animate',

                function ($scope, $element, $timeout, $compile, $animate) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var formBodyContentsElement = $element.find('.form-body-contents');
                    var currentFormBodyScope;

                    var defaultNextButtonTitle = 'CCC_VIEW_HOME.CCC-WIZARD.NEXT';
                    var defaultNextButtonIcon = 'fa fa-chevron-right';

                    var defaultBackButtonTitle = 'CCC_VIEW_HOME.CCC-WIZARD.BACK';
                    var defaultBackButtonIcon = 'fa fa-chevron-left';

                    var currentCompiledStepElement;
                    var leavingCompiledStepElement;


                    /*============= MODEL =============*/

                    $scope.nextTitle = defaultNextButtonTitle;
                    $scope.nextIcon = defaultNextButtonIcon;

                    $scope.backTitle = defaultBackButtonTitle;
                    $scope.backIcon = defaultBackButtonIcon;

                    $scope.stepWidth = false;


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var updateButtonConfigs = function (stepWrapper) {

                        if (stepWrapper.step.nextButton) {
                            $scope.nextTitle = stepWrapper.step.nextButton.title;
                            $scope.nextIcon = stepWrapper.step.nextButton.iconClass;
                        } else {
                            $scope.nextTitle = defaultNextButtonTitle;
                            $scope.nextIcon = defaultNextButtonIcon;
                        }

                        if (stepWrapper.step.backButton) {
                            $scope.backTitle = stepWrapper.step.backButton.title;
                            $scope.backIcon = stepWrapper.step.backButton.iconClass;
                        } else {
                            $scope.backTitle = defaultBackButtonTitle;
                            $scope.backIcon = defaultBackButtonIcon;
                        }
                    };

                    var loadStep = function (stepWrapper, direction) {

                        if (direction === -1) {
                            $element.addClass('ccc-wizard-direction-left').removeClass('ccc-wizard-direction-right');
                        } else {
                            $element.addClass('ccc-wizard-direction-right').removeClass('ccc-wizard-direction-left');
                        }

                        $scope.stepWidth = stepWrapper.step.width ? stepWrapper.step.width : false;

                        var contentTargetElement = formBodyContentsElement.find('.template-wrapper');

                        if (currentFormBodyScope) {
                            currentFormBodyScope.$destroy();
                        }

                        currentFormBodyScope = $scope.$new();
                        currentFormBodyScope.onReady = stepWrapper.onReady;
                        currentFormBodyScope.immutableData = $scope.wizard.immutableData;
                        currentFormBodyScope.model = $scope.wizard.model;
                        currentFormBodyScope.wizard = $scope.wizard;

                        // it's possible for the step to provide dynamic value mappers, and it may be better to encapsulate custom value mappers in the step
                        currentFormBodyScope.setValueMapper = function (fieldKey, newValueMapper) {
                            stepWrapper.step.fields[fieldKey].valueMapper = newValueMapper;
                        };

                        updateButtonConfigs(stepWrapper);

                        leavingCompiledStepElement = currentCompiledStepElement;

                        if (leavingCompiledStepElement) {
                            $animate.leave(leavingCompiledStepElement);
                        }

                        currentCompiledStepElement =  $compile(stepWrapper.step.template)(currentFormBodyScope);

                        $animate.enter(currentCompiledStepElement, contentTargetElement);

                        $scope.$emit('ccc-wizard.stepLoaded', stepWrapper);

                        $timeout(function () {
                            $element.find('.focus-bar').focus(); // in addition to making it more accessible, this will also make sure the view comes into view
                        }, 600); // need to be 600 to allow the next view to slide in before focusing because the browser will shift the whole screen if the focused element is not in view
                    };

                    var wizardComplete = function () {
                        $scope.$emit('ccc-wizard.complete');
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.next = function () {
                        $scope.$broadcast('ccc-wizard.stepSubmitted');
                        $scope.wizard.next();
                    };

                    $scope.back = function () {
                        $scope.wizard.back();
                    };

                    $scope.cancel = function () {
                        $scope.wizard.onBeforeCancel(function () {
                            $scope.$emit('ccc-wizard.cancel');
                        });
                    };

                    $scope.wizardContentClass = function () {

                        return {
                            'col-md-6 col-md-offset-3': $scope.stepWidth !== 'large' && $scope.stepWidth !== 'medium',
                            'col-md-8 col-md-offset-2': $scope.stepWidth === 'medium',
                            'col-md-10 col-md-offset-1': $scope.stepWidth === 'large'
                        };
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('$destroy', function () {
                        $scope.wizard.removeListener('stepLoaded', loadStep);
                        $scope.wizard.removeListener('noNextStep', wizardComplete);
                    });

                    $scope.wizard.addListener('stepLoaded', loadStep);
                    $scope.wizard.addListener('noNextStep', wizardComplete);


                    /*============= INITIALIZATION ===========*/

                    $scope.wizard.initialize();
                }
            ],

            template: [

                '<div class="form-container ccc-form-container">',

                    '<div class="form-body">',
                        '<div class="row">',
                            '<div class="form-body-contents" ng-class="wizardContentClass()">',
                                '<div class="focus-bar" tabindex="-1" aria-labelledby="template-wrapper"></div>', // we are scrolling to the top of the view but focusing on the template-wrapper if the content is large will then scroll down to center the element rather than be at the top
                                '<div id="template-wrapper" class="template-wrapper"></div>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="form-actions">',

                        '<div class="row">',
                            '<div class="col-md-6 col-md-offset-3">',
                                '<div class="actions">',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-if="wizard.currentStepIndex > 0" ng-disabled="wizard.isDisabled || wizard.isLoading" ng-click="back()">',
                                        '<i class="fa fa-chevron-left" aria-hidden="true"></i>',
                                        '<span translate="{{backTitle}}"></span>',
                                    '</button>',

                                    '<button class="btn btn-primary btn-icon-right btn-full-width-when-small" ng-disabled="wizard.isDisabled || wizard.isLoading" ng-click="next()">',
                                        '<span translate="{{nextTitle}}"></span>',
                                        '<i class="{{nextIcon}} noanim" aria-hidden="true" ng-if="!wizard.isLoading"></i>',
                                        '<i class="fa fa-spinner fa-spin noanim" aria-hidden="true" ng-if="wizard.isLoading"></i>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="wizard.isDisabled || wizard.isLoading" ng-click="cancel()">',
                                        '<i class="fa fa-times-circle" aria-hidden="true"></i>',
                                        '<span translate="CCC_VIEW_HOME.CCC-WIZARD.CANCEL"></span>',
                                    '</button>',

                                '</div>',
                            '</div>',
                        '</div>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();

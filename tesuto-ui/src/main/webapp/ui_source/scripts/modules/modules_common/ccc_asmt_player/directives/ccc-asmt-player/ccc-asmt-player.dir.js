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

    // This is the root level directive for asmt player
    angular.module('CCC.AsmtPlayer').directive('cccAsmtPlayer', function () {

        return {

            restrict: 'E',

            scope: {
                taskSetService: '=',    // must be provided and have methods for getting tasks and submitting tasks
                assessment: '=',        // will contain the title and other assessment configurations
                initialTaskSet: '=?',   // you can provide an initial task set to start things off if you like
                user: '=',              // the user using this assessment player
                allowPause: '='         // should we support pausing, the taskSetService's api will be called during a pause
            },

            controller: [

                '$scope',
                'AsmtService',
                'ForegroundTrayService',
                'ModalService',
                'WindowFocusService',

                function ($scope, AsmtService, ForegroundTrayService, ModalService, WindowFocusService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var toggleShowScores = function () {
                        AsmtService.toggleShowScores();
                    };

                    var showProgressErrorModal = function (errorData, direction) {

                        var modalScope = $scope.$new();

                        AsmtService.disableNavigation(function (enableNavigationCallback) {

                            var recordProgressModal = ModalService.open({
                                scope: modalScope,
                                template: '<ccc-asmt-progress-warning-modal modal="modal"></ccc-asmt-progress-warning-modal>'
                            });

                            recordProgressModal.result.finally(function () {

                                enableNavigationCallback().then(function () {

                                    // when the modal closes, where do we go for focus?
                                    if (direction === 'next') {
                                        $scope.$broadcast('ccc-asmt-player-toolbar-bottom.requestFocusNext');
                                    } else {
                                        $scope.$broadcast('ccc-asmt-player-toolbar-bottom.requestFocusPrevious');
                                    }
                                });
                            });

                        });
                    };


                    /*=========== MODEL ===========*/

                    $scope.assessmentTask = null;

                    $scope.allowPause = $scope.allowPause || false;

                    $scope.showValidationErrors = false;


                    /*=========== MODEL DEPENDENT METHODS ===========*/

                    // there are some layout related items that may need changing if content changes
                    var updateLayout = function () {
                        $scope.$broadcast('ccc-scroll-indicator.update');
                    };

                    var setNextAssessmentTask = function (newAssessmentTask) {

                        if (newAssessmentTask) {
                            $scope.assessmentTask = AsmtService.currentAssessmentTask;
                        } else {
                            $scope.assessmentTask = false;
                        }

                        // hide validation errors
                        $scope.showValidationErrors = false;

                        // tell the content to update their assessment task
                        $scope.$broadcast('ccc-asmt-player-content.requestSetTask', $scope.assessmentTask);

                        // new assessment item means we need to ask the ccc-scroll-indicator to move us back up
                        $scope.$broadcast('ccc-scroll-indicator.scrollToTop');
                    };

                    var interactionUpdated = function () {
                        AsmtService.updateValidationState();
                        updateLayout();
                    };

                    var doGotoNextTask = function () {
                        AsmtService.nextAssessmentTask();
                    };

                    var doGotoPreviousTask = function () {
                        AsmtService.previousAssessmentTask();
                    };

                    var notifyItemsInvalid = function () {
                        $scope.$broadcast('ccc-asmt-player.itemsInvalid');
                    };

                    var validateAndContinue = function (direction, onValidatedCallBack) {

                        // if the current assessmentTask is good then we can proceed to the next item
                        if (AsmtService.state.assessmentTaskComplete || $scope.assessment.validateInteractions === false) {

                            onValidatedCallBack();

                        // otherwise show validation errors
                        } else {

                            AsmtService.disableNavigation(function (enableNavigationCallback) {

                                $scope.showValidationErrors = true;

                                updateLayout();

                                var modalScope = $scope.$new();

                                var hasUnskippableValidateResponsesErrors = AsmtService.getHasUnskippableValidateResponsesErrors();
                                var hasUnskippableAllowSkippingErrors = AsmtService.getHasUnskippableAllowSkippingErrors();

                                // set a flag that determines if we should allow the user to move foward based on if useSoftValidation is on and there are no unskippable errors
                                var hasSoftValidationErrors = AsmtService.getHasSoftValidationErrors() && !hasUnskippableValidateResponsesErrors;

                                modalScope.allowSkipInvalidTask = !hasUnskippableAllowSkippingErrors && !hasUnskippableValidateResponsesErrors || !hasUnskippableAllowSkippingErrors && hasSoftValidationErrors;

                                // here is where we would add logic based on a flag if we should show validation errors
                                var validationModal = ModalService.open({
                                    scope: modalScope,
                                    template: '<ccc-asmt-task-warning-modal modal="modal" allow-skip-invalid-task="allowSkipInvalidTask"></ccc-asmt-task-warning-modal>'
                                });

                                $scope.$emit('ccc-asmt-player.validationFailures');

                                validationModal.result.then(function() {

                                    onValidatedCallBack();

                                }).finally(function () {
                                    enableNavigationCallback();
                                    notifyItemsInvalid();
                                });
                            });
                        }
                    };

                    var tryGotoNextTask = function () {
                        validateAndContinue('next', doGotoNextTask);
                    };

                    var tryGotoPrevTask = function () {
                        // for now, we don't validate when we go previous
                        // validateAndContinue('previous', doGotoPreviousTask); we may want to conditionally turn this on if they made a change in the future
                        doGotoPreviousTask();
                    };

                    var tryPause = function () {
                        AsmtService.pause();
                    };


                    /*=========== BEHAVIOR ============*/

                    // TODO: remove this and retest drag and drop of calculator
                    $scope.onDrop = function (e) {};


                    /*=========== LISTENERS ===========*/

                    // listen for the newTask event to trigger us to set the task for the player
                    $scope.$on('AsmtService.newTask', function (event, newTask) {
                        setNextAssessmentTask(newTask);
                    });

                    // when an interaction is updated we need to revalidate among other things
                    $scope.$on('ccc-interaction.updated', interactionUpdated);

                    // listen for previous and next
                    $scope.$on('ccc-asmt-player-toolbar-bottom.nextClicked', tryGotoNextTask);
                    $scope.$on('ccc-asmt-player-toolbar-bottom.prevClicked', tryGotoPrevTask);
                    $scope.$on('ccc-asmt-player-toolbar-bottom.toggleShowScores', toggleShowScores);

                    // listen for pause
                    $scope.$on('ccc-asmt-player-toolbar-bottom.pauseClicked', tryPause);

                    // bubble up events from AsmtService for when we are all out of tasks
                    $scope.$on('AsmtService.noNextTaskSet', function () {
                        $scope.$emit('ccc-asmt-player.noNextTaskSet');
                    });
                    $scope.$on('AsmtService.noPreviousTaskSet', function () {
                        $scope.$emit('ccc-asmt-player.noPreviousTaskSet');
                    });

                    $scope.$on('AsmtService.recordProgressError', function (e, errorData, direction) {
                        showProgressErrorModal(errorData, direction);
                    });

                    $scope.$on('ccc-asmt-player.requestDisable', function () {
                        AsmtService.setDisabled(true);
                    });
                    $scope.$on('ccc-asmt-player.requestEnable', function () {
                        AsmtService.setDisabled(false);
                    });

                    $scope.$on('AsmtService.requestPause', function () {
                        $scope.$emit('ccc-asmt-player.requestPause');
                    });


                    /*=========== INITIALIZATION ===========*/

                    // start up the foreground content service
                    // this let's us slide content on top of the the existing content
                    ForegroundTrayService.initialize({
                        leftEdgeElementSelector: '.ccc-asmt-player-content-area',
                        rightEdgeElementSelector: '.ccc-asmt-player-content-area'
                    });

                    // pass along the configured assessment and taskSetService
                    AsmtService.setAssessment($scope.assessment || {});

                    // this should be set so if it is not then the AsmtService will start throwing errors
                    if ($scope.taskSetService) {
                        AsmtService.setTaskSetService($scope.taskSetService);
                    }

                    // and let's start by setting up the first task
                    if ($scope.initialTaskSet && $scope.initialTaskSet.tasks && $scope.initialTaskSet.tasks.length) {

                        AsmtService.setAssessmentTaskSet({
                            taskSet: $scope.initialTaskSet
                        });

                    } else {
                        AsmtService.nextAssessmentTask();
                    }

                    // initialize the WindowFocusService service to allow events broadcast from rootscope
                    WindowFocusService.initialize();

                    // we have initialized
                    $scope.$emit('ccc-asmt-player.initialized');
                }
            ],

            template: [
                '<div class="ccc-asmt-player-components" ng-class="{\'ccc-show-validation-errors\': showValidationErrors}" jqyoui-droppable="{onDrop: \'onDrop\'}">',
                    '<ccc-asmt-player-toolbar-top assessment="assessment" user="user"></ccc-asmt-player-toolbar-top>',
                    '<ccc-asmt-player-content ccc-scroll-indicator assessment="assessment"></ccc-asmt-player-content>',
                    '<ccc-foreground-tray></ccc-foreground-tray>',
                    '<ccc-asmt-player-toolbar-bottom class="ccc-asmt-player-toolbar" allow-pause="allowPause" assessment="assessment"></ccc-asmt-player-toolbar-bottom>',
                '</div>'
            ].join('')

        };

    });

})();

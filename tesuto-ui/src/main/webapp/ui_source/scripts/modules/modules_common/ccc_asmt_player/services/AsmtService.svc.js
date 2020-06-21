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

    angular.module('CCC.AsmtPlayer').provider('AsmtService', function () {

        /*=========== SERVICE PROVIDER API ===========*/


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$rootScope',
            '$timeout',
            '$q',
            'ModalService',
            'NotificationService',
            'AsmtTaskUtilsService',
            'TimerClass',
            'InteractionFactoryService',
            'TaskSetServiceClass',
            'ToolsPermissionService',
            'CSSService',
            'WindowFocusService',

            function ($rootScope, $timeout, $q, ModalService, NotificationService, AsmtTaskUtilsService, TimerClass, InteractionFactoryService, TaskSetServiceClass, ToolsPermissionService, CSSService, WindowFocusService) {

                var AsmtService;


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                // this should be overriden using the setTaskSetService public method
                var taskSetService;

                // a time we can use internally to calculate duration
                var timer = new TimerClass();

                var existingTaskSetDuration = 0;

                // a counter for number of navigation locks and lock and unlock helper methods
                var navigationLockCount = 0;
                var addNavigationLock = function () {
                    navigationLockCount++;
                    AsmtService.state.isNavigationDisabled = true;
                };
                var removeNavigationLock = function () {
                    navigationLockCount--;
                    AsmtService.state.isNavigationDisabled = (navigationLockCount !== 0);
                };


                // this method helps us throw errors around the format at the assessmentTask level
                var normalizeAssessmentTask = function (assessmentTask) {

                    if (!assessmentTask.itemSessions || !assessmentTask.itemSessions.length) {
                        throw new Error('AsmtService.normalizeAssessmentTask.noItemSessions');
                    }

                    // force a two column layout by making assessmentTask stimulus equal to the single itemSession stimulus
                    if (!assessmentTask.stimulus && assessmentTask.itemSessions.length === 1 && $.trim(assessmentTask.itemSessions[0].assessmentItem.stimulus) !== "") {

                        var assessmentItemStimulus = assessmentTask.itemSessions[0].assessmentItem.stimulus;
                        delete assessmentTask.itemSessions[0].assessmentItem.stimulus;

                        return {
                            taskId: assessmentTask.taskId,
                            stimulus: assessmentItemStimulus,
                            itemSessions: assessmentTask.itemSessions
                        };

                    // otherwise no major transforms
                    } else {

                        return {
                            taskId: assessmentTask.taskId,
                            stimulus: assessmentTask.stimulus || false,
                            itemSessions: assessmentTask.itemSessions
                        };
                    }
                };

                // tasks can come in with a testlet that contains multiple "items", or just on item, we normalize both uses cases into one deterministic structure of {stimulus: <>, items: [], testletId: <optional>}
                var normalizeAssessmentTaskSet = function (assessmentTaskSet) {

                    if (!assessmentTaskSet.tasks || !assessmentTaskSet.tasks.length) {
                        throw new Error('AsmtService.normalizeAssessmentTaskSet.noTasksWithinAssessmentTaskSet');
                    }

                    for (var i=0; i < assessmentTaskSet.tasks.length; i++) {
                        assessmentTaskSet.tasks[i] = normalizeAssessmentTask(assessmentTaskSet.tasks[i]);
                    }

                    return assessmentTaskSet;
                };

                // this will loop through all interaction plain javascript objects and turn them into Interaction class instances
                var coerceInteractions = function (assessmentTask) {

                    var itemSessions = assessmentTask.itemSessions;

                    var j;
                    var interactionResponse;
                    for (var i=0; i < itemSessions.length; i++) {

                        var interactionSettings = InteractionFactoryService.getInteractionSettings(itemSessions[i]);

                        if (!(itemSessions[i].assessmentItem.interactions && itemSessions[i].assessmentItem.interactions.length > 0)) {
                            itemSessions[i].assessmentItem.interactions = [];
                        }
                        for (j=0; j < itemSessions[i].assessmentItem.interactions.length; j++) {

                            // first add on any existing values coming in from the assessmentItem
                            interactionResponse = _.find(itemSessions[i].responses || [], function (response) {
                                return response.responseIdentifier === itemSessions[i].assessmentItem.interactions[j].responseIdentifier;
                            });
                            itemSessions[i].assessmentItem.interactions[j].values = interactionResponse ? interactionResponse.values : [];

                            // then generate the interaction class
                            itemSessions[i].assessmentItem.interactions[j] = InteractionFactoryService.coerceInteraction(itemSessions[i].assessmentItem.interactions[j], interactionSettings);
                        }
                    }
                };

                // since the interactions are nested across multiple items, we provide this method to consolidate them in a list rather than spam it out onto the assessment
                var getInteractionsList = function () {
                    return AsmtTaskUtilsService.getInteractionsList(AsmtService.currentAssessmentTask);
                };

                var resetAssessmentItemStyles = function (assessmentTask) {

                    // first clear all styles from the assessment-item-level group
                    CSSService.unloadStylesGroup('assessment-item-level');

                    // next add styles for each item that has one
                    _.each(assessmentTask.itemSessions, function (itemSession) {

                        if (itemSession.assessmentItem && itemSession.assessmentItem.stylesheets && $.trim(itemSession.assessmentItem.stylesheets)) {
                            CSSService.loadStyles(itemSession.itemSessionId, itemSession.assessmentItem.stylesheets, 'assessment-item-level');
                        }
                    });
                };

                var setCurrentAssessmentTask = function (assessmentTask) {

                    AsmtService.isValid = false;
                    AsmtService.isDirty = false;
                    AsmtService.currentAssessmentTask = assessmentTask;

                    if (!AsmtService.currentAssessmentTask.itemSessions || AsmtService.currentAssessmentTask.itemSessions.length === 0) {
                        throw new Error('no items defined for this assessmentTask');
                    }

                    // before we can move on, we need to turn plain javascript interaction data into
                    // actual instances of an interaction class which will have standard methods to be re-used everywhere

                    // TODO: if we cache items in the UI, then we wouldn't want to reprocess interactions
                    coerceInteractions(AsmtService.currentAssessmentTask);

                    AsmtService.state.interactionsList = getInteractionsList();
                    AsmtService.state.interactionsCount = AsmtService.state.interactionsList.length;

                    // we either have a group of items in a teslet or a single item
                    AsmtService.state.currentTaskItemCount = AsmtService.currentAssessmentTask.itemSessions ? AsmtService.currentAssessmentTask.itemSessions.length : 1;

                    // reset the validation state
                    AsmtService.updateValidationState();

                    // reset assessment item styles
                    resetAssessmentItemStyles(AsmtService.currentAssessmentTask);

                    // determine tool permissions for this taskSet
                    ToolsPermissionService.setPermissionsForTaskSet(AsmtService.currentAssessmentTask);

                    // and tell everyone we updated the currentTask
                    $rootScope.$broadcast('AsmtService.newTask', AsmtService.currentAssessmentTask);
                };

                var mergeScoreListIntoTask = function (scoreList) {

                    var itemSessions = AsmtService.currentAssessmentTask.itemSessions;
                    _.each(itemSessions, function (itemSession) {

                        itemSession.feedback = [];

                        var itemSessionOutcomes = _.filter(scoreList.outcomes, function (outcome) {
                            return outcome.itemSessionId === itemSession.itemSessionId;
                        });

                        _.each(itemSessionOutcomes, function (itemSessionOutcome) {

                            itemSession.feedback.push({
                                template: '<ccc-assessment-item-session-feedback-outcome feedback="feedback"></ccc-assessment-item-session-feedback-outcome>',
                                data: itemSessionOutcome
                            });
                        });

                        var itemSessionScores = _.filter(scoreList.scores, function (score) {
                            return score.itemSessionId === itemSession.itemSessionId;
                        });

                        _.each(itemSessionScores, function (itemSessionScore) {

                            itemSession.feedback.push({
                                template: '<ccc-assessment-item-session-feedback-score feedback="feedback"></ccc-assessment-item-session-feedback-score>',
                                data: itemSessionScore
                            });
                        });

                    });

                    return true;
                };

                var getTaskSetScore = function () {

                    if (AsmtService.currentAssessmentTask === false) {
                        return false;
                    }

                    var taskResponses = AsmtService.getCurrentAssessmentTaskResponses();

                    return taskSetService.scoreTaskSet(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId, taskResponses)
                        .then(mergeScoreListIntoTask)
                        .finally(function () {
                            AsmtService.state.isLoadingScores = false;
                        });
                };

                // we have a debounced version because the user could be typing text, we don't want to fire until their changes settle
                var debouncedDoScoreTaskSet = _.debounce(getTaskSetScore, 300);

                var getIsScoreDisabled = function () {
                    return !(taskSetService.isScoreEnabled && taskSetService.isScoreEnabled() === true);
                };

                var clearTaskSetScores = function () {
                    _.each(AsmtService.currentAssessmentTask.itemSessions, function (itemSession) {
                        itemSession.feedback = [];
                    });
                };

                var tryUpdateScores = function () {

                    // if scoring is disabled then just leave
                    if (AsmtService.isScoreDisabled()) {
                        return;
                    }

                    // if the flag to show scores is on
                    if (AsmtService.state.showScores) {
                        debouncedDoScoreTaskSet();
                    } else {
                        clearTaskSetScores();
                    }
                };

                // handle special case where UI get's out of sync with the server (CCCAS-2336)
                var startUIOutOfSyncWorkflow = function () {

                    // this is to help testers test the workflow by setting FORCE_SERVER_ERROR=404 in the console
                    var isTesting = window.FORCE_SERVER_ERROR === 404 || window.FORCE_SERVER_ERROR === '404';
                    window.FORCE_SERVER_ERROR = false;

                    var buttonConfigs = {
                        cancel: {
                            title: 'CCC_PLAYER.SERVER_SYNC_ERROR.MODAL.BUTTON_CANCEL',
                            btnClass: 'btn-default'
                        },
                        okay: {
                            title: 'CCC_PLAYER.SERVER_SYNC_ERROR.MODAL.BUTTON_OKAY',
                            btnClass: 'btn-primary',
                            btnIcon: 'fa-refresh'
                        }
                    };

                    var serverSyncModal = ModalService.openConfirmModal({
                        title: 'CCC_PLAYER.SERVER_SYNC_ERROR.MODAL.TITLE',
                        message: 'CCC_PLAYER.SERVER_SYNC_ERROR.MODAL.BODY',
                        buttonConfigs: buttonConfigs
                    });

                    serverSyncModal.result.then(function () {

                        taskSetService.getCurrentTaskSet(AsmtService.assessment.assessmentSessionId).then(function (taskSetData) {

                            AsmtService.setAssessmentTaskSet({
                                totalTaskSetIndex: taskSetData.taskSet.taskSetIndex,
                                totalTaskSetCount: taskSetData.totalTaskSetCount,
                                totalTaskCount: taskSetData.totalTaskCount,
                                totalTaskIndex: taskSetData.totalTaskIndex,
                                taskSet: taskSetData.currentTaskSet
                            });

                            $rootScope.$broadcast('AsmtService.taskReloaded');

                            // maybe display a notification reminder that they need to run through their task set
                            NotificationService.open({
                                icon: 'fa fa-exclamation-triangle',
                                title: ' Item reloaded.',
                                message: 'Remember, check your answers again before moving on.'
                            },
                            {
                                delay: 0,
                                type: "info",
                                allow_dismiss: true,
                            });
                        });

                    }, function () {

                        // if we were testing using the FORCE_SERVER_ERROR flag, then stick the 404 error back on there
                        if (isTesting) {
                            window.FORCE_SERVER_ERROR = 404;
                        }
                    });
                };

                var handleUpdateError = function (errorData) {

                    // We need to handle 404 in a special case. If we get a 404 assume we are in a strange scenario (CCCAS-2336)
                    // In this scenaior we assume the UI taskSetId is not in sync with the currentTaskSet on the server
                    // So we need to tell the user and allow them to trigger a refresh
                    if (errorData.status === '404') {

                        startUIOutOfSyncWorkflow();
                        $rootScope.$broadcast('ccc.serverError', '404', {status: 404});

                    // if it isn't a 404 then just do the defaul progress error event
                    } else {
                        $rootScope.$broadcast('AsmtService.recordProgressError', errorData);
                    }

                    return $q.reject();
                };

                var submitCurrentAssessmentTaskAndRetrieve = function (direction) {

                    AsmtService.state.isAssessmentTaskLoading = true;

                    var recordUpdatesForCurrentAssessmentTask = function () {

                        // if we have a currentAssessmentTask, then we should send the current data
                        if (AsmtService.currentAssessmentTask) {

                            // first we decide if this is not the last task in the taskset or not
                            var isFirstTaskAndWeAreMovingPrevious = direction === "previous" && AsmtService.state.currentTaskSetTaskIndex === 0;
                            var isLastTaskAndWeAreMovingNext = direction === "next" && AsmtService.state.currentTaskSetTaskIndex === AsmtService.currentAssessmentTaskSet.tasks.length - 1;

                            var taskResponses = AsmtService.getCurrentAssessmentTaskResponses();
                            // {
                            //     "taskId1": {
                            //         "duration": 451,
                            //         "responses": [{
                            //             "itemSessionId": "JDKFD-SDFDD-BFFDE-EWVBD",
                            //             "responseIdentifier": "RESPONSE",
                            //             "values": ["ChoiceA", "ChoiceB"]
                            //         }]
                            //     }
                            // }

                            // if we are going to move to a new task set, mark this task as complete
                            if (isFirstTaskAndWeAreMovingPrevious || isLastTaskAndWeAreMovingNext) {

                                return taskSetService.completeTaskSet(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId, taskResponses, direction);

                            } else {

                                return taskSetService.updateTaskSetResponses(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId, taskResponses);
                            }

                        // else just move along
                        } else {
                            return $q.when(true);
                        }
                    };

                    var getPreviousTask = function () {

                        if (AsmtService.currentAssessmentTaskSet && AsmtService.currentAssessmentTaskSet.tasks) {

                            // if we haven't picked an index within the taskSet, let's start now
                            if (AsmtService.state.currentTaskSetTaskIndex === false) {

                                AsmtService.state.currentTaskSetTaskIndex = 0;

                            // otherwise just increment the index counter as normal
                            } else {
                                AsmtService.state.currentTaskSetTaskIndex--;
                            }

                            // then test if we are still within the bounds of our current assessmentTaskSet task list
                            if (AsmtService.state.currentTaskSetTaskIndex > 0) {

                                return $q.when(AsmtService.currentAssessmentTaskSet.tasks[AsmtService.state.currentTaskSetTaskIndex]);

                            // if we are beyond the task set then reject
                            } else {

                                // reset the currentTaskSetTaskIndex to it's max value
                                AsmtService.state.currentTaskSetTaskIndex = 0;
                                return $q.reject();
                            }

                        } else {
                            return $q.reject();
                        }
                    };

                    var getNextTask = function (dir) {

                        timer.stop();

                        if (AsmtService.currentAssessmentTaskSet && AsmtService.currentAssessmentTaskSet.tasks) {

                            // if we haven't picked an index within the taskSet, let's start now
                            if (AsmtService.state.currentTaskSetTaskIndex === false) {

                                AsmtService.state.currentTaskSetTaskIndex = 0;

                            // otherwise just increment the index counter as normal
                            } else {

                                AsmtService.state.currentTaskSetTaskIndex++;
                            }

                            // then test if we are still within the bounds of our current assessmentTaskSet task list
                            if (AsmtService.state.currentTaskSetTaskIndex < AsmtService.currentAssessmentTaskSet.tasks.length) {

                                return $q.when(AsmtService.currentAssessmentTaskSet.tasks[AsmtService.state.currentTaskSetTaskIndex]);

                            // if we are beyond the task set then reject
                            } else {

                                // reset the currentTaskSetTaskIndex to it's max value
                                AsmtService.state.currentTaskSetTaskIndex = AsmtService.currentAssessmentTaskSet.tasks.length - 1;
                                return $q.reject();
                            }

                        } else {
                            return $q.reject();
                        }
                    };

                    var getPreviousTaskSet = function () {
                        return taskSetService.getPreviousTaskSet(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId);
                    };

                    var getNextTaskSet = function () {
                        return taskSetService.getNextTaskSet(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId);
                    };

                    var getNewTaskSet = function (dir) {
                        if (dir === 'next') {
                            return getNextTaskSet();
                        } else if (dir === 'previous') {
                            return getPreviousTaskSet();
                        }
                    };

                    var doGetNextTask = function () {
                        if (direction === 'next') {
                            return getNextTask();
                        } else if (direction === 'previous') {
                            return getPreviousTask();
                        }
                    };

                    var doGetNextTaskSet = function () {

                        return getNewTaskSet(direction).then(function (taskSet) {

                            // if the service returns false, we have no more taskSets
                            if (taskSet === false) {

                                if (direction === 'next') {
                                    $rootScope.$broadcast('AsmtService.noNextTaskSet');
                                } else {
                                    $rootScope.$broadcast('AsmtService.noPreviousTaskSet');
                                }

                            } else {

                                // if we have a next taskSet
                                AsmtService.setAssessmentTaskSet(taskSet);

                                // attempt to auto score it if scoring is turned on
                                tryUpdateScores();
                            }

                        // or if the service rejects the call
                        }, function () {

                            // if we don't have a next taskSet
                            if (direction === 'next') {
                                $rootScope.$broadcast('AsmtService.noNextTaskSet');
                            } else {
                                $rootScope.$broadcast('AsmtService.noPreviousTaskSet');
                            }
                        });
                    };

                    AsmtService.disableNavigation(function (enableNavigationCallBack) {

                        // First, we allow the taskService to save progress on the current task
                        recordUpdatesForCurrentAssessmentTask().then(function () {

                            // then if that succeeds we try first to get the next task in the current taskSet
                            // if that succeeds, we move on, otherwise we try to get the next TaskSet
                            return doGetNextTask().then(setCurrentAssessmentTask, doGetNextTaskSet);

                        }, handleUpdateError).finally(function () {

                            enableNavigationCallBack().then(function () {
                                AsmtService.state.isAssessmentTaskLoading = false;
                                timer.clear();
                                timer.start();
                            });
                        });
                    });


                };

                var pauseAssessment = function () {

                    // if we have a currentAssessmentTask, then we should send the current data
                    if (AsmtService.currentAssessmentTask) {

                        AsmtService.disableNavigation(function (enableNavigationCallback) {

                            var pauseModal = ModalService.open({
                                template: '<ccc-asmt-task-pause-modal modal="modal"></ccc-asmt-task-pause-modal>'
                            });

                            pauseModal.result.then(function () {

                                $rootScope.$emit('AsmtService.assessmentPaused');

                                // first save any progress on the current task
                                var taskResponses = AsmtService.getCurrentAssessmentTaskResponses();
                                taskSetService.updateTaskSetResponses(AsmtService.assessment.assessmentSessionId, AsmtService.currentAssessmentTaskSet.taskSetId, taskResponses)
                                    .then(function () {

                                        // now issue the pause call
                                        return taskSetService.pause(AsmtService.assessment.assessmentSessionId).then(function () {

                                            // we are about to log them out and possibly close the window so stop blocking
                                            WindowFocusService.stopBlockingCloseWindow();

                                            // fire the request logout event and allow the parent of this assessment player view to handle the logic
                                            $rootScope.$broadcast('AsmtService.requestPause');
                                        });

                                    }, handleUpdateError);

                            }).finally(function () {
                                enableNavigationCallback();
                            });

                        });
                    }
                };


                /*============ SERVICE API ============*/

                AsmtService = {

                    /*============ PUBLIC PROPERTIES ============*/

                    // the main values that determine the current state of this service
                    assessment: {},
                    currentAssessmentTaskSet: false,
                    currentAssessmentTask: false,

                    // public read only state related values (never change these externally, for watching only)
                    state: {

                        isNavigationDisabled: false,              // can be used to halt navigation

                        totalTaskSetIndex: 1,                   // (optional based on assessment type) what is the index of our current taskSet within the entire assessment
                        totalTaskSetCount: false,               // (optional based on assessment type) should remain false if it is not availble

                        currentTaskSetTaskCount: 0,             // how many tasks in this taskSet?
                        currentTaskSetTaskIndex: false,         // ( false when we get a fresh task set ) which task within the taskSet are we in?

                        currentTaskItemCount: 0,                // how many items in the current task?

                        totalTaskCount: false,                  // (optional false if not provided) how many total tasks in the whole assessment
                        totalTaskIndex: false,                  // (optional false if not provided) which task are we on within the whole assessment

                        interactionsList: [],                   // generated when a new taskSet is loaded
                        interactionsCount: 0,                   // determined when a new taskSet is loaded
                        interactionsNotEmpty: 0,                // incrementally changes as user interacts (must pass non-empty rule set for each interaction type)
                        interactionsComplete: 0,                // incrementally changes as user interacts (must pass validation rules of the interaction to be complete)

                        assessmentTaskNotEmpty: false,          // general flag for when this assessmentTask has all non empty interactions
                        assessmentTaskComplete: false,          // general flag for when this assessmentTask has all complete interactions that pass validation rules

                        isAssessmentTaskPristine: false,        // is this a fresh task that has not been submitted?
                        isAssessmentTaskLoading: true,          // are we in the a new task?

                        isLoadingScores: false                  // are we loading scores from the backend?
                    },


                    /*============ PUBLIC METHODS ============*/

                    disableNavigation: function (callBackWhenDisableIsComplete) {
                        var deferred = $q.defer();

                        // add a lock (we use locks to ensure that multiple people can lock and unlock without overridding each other)
                        addNavigationLock();

                        // we wrap in a timeout because the button will not be rendered as enabled/disabled until after a digest
                        // therefore you can trust things are disabled in the UI once the promise resolves or the callback is run
                        // YOU MUST ENVOQUE THE UNLOCK CALLBACK METHOD OR IT WILL REMAINED LOCKED
                        $timeout(function () {

                            var unlockComplete = false;

                            // same deal here, use a promise so the caller can be confident the UI is updated after unlocking
                            var unlockCallBack = function () {

                                var unlockDeferred = $q.defer();

                                $timeout(function () {
                                    if (!unlockComplete) {
                                        unlockComplete = true;
                                        removeNavigationLock();
                                    }
                                    unlockDeferred.resolve();
                                }, 1);

                                return unlockDeferred.promise;
                            };

                            // pass a callback that must be invoked by the caller to unlock
                            callBackWhenDisableIsComplete(unlockCallBack);

                            // also resolve with the callback in case someone wants to chain promises
                            deferred.resolve(unlockCallBack);
                        }, 1);

                        return deferred.promise;
                    },

                    getAssessment: function () {
                        return AsmtService.assessment;
                    },

                    // A task will have unskippable errors if there is an itemSession with flag allowSkipping:false
                    // and has one or more item sessions that have no answers
                    getHasUnskippableAllowSkippingErrors: function () {

                        // we start out good and will force true if we run into a scenario that is unskippable
                        var hasUnskippableAllowSkippingErrors = false;

                        _.each(AsmtService.currentAssessmentTask.itemSessions, function (itemSession) {

                            // if the itemSession allowSkipping is false,
                            // we need to run through each interaction and make sure it is non-empty
                            if (!itemSession.allowSkipping) {

                                var interactionsWithSkippingErrors = 0;
                                _.each(itemSession.assessmentItem.interactions, function (interaction) {
                                    if (interaction.allowSkippingErrors.length !== 0) {
                                        interactionsWithSkippingErrors++;
                                    }
                                });

                                // from QTI, we allow it to be skippable if at least one of the interactions does not have skip errors
                                var interactionsSkippable = interactionsWithSkippingErrors < itemSession.assessmentItem.interactions.length;

                                hasUnskippableAllowSkippingErrors = hasUnskippableAllowSkippingErrors || !interactionsSkippable;
                            }
                        });

                        return hasUnskippableAllowSkippingErrors;
                    },

                    getHasUnskippableValidateResponsesErrors: function () {

                        // we start out good and will force true if we run into a scenario that is unskippable
                        var hasUnskippableValidateResponsesErrors = false;

                        _.each(AsmtService.currentAssessmentTask.itemSessions, function (itemSession) {

                            // if the itemSession validateResponses is true,
                            // run through each interaction and make sure it is valid
                            if (itemSession.validateResponses) {

                                var areItemSessionInteractionsValid = true;
                                _.each(itemSession.assessmentItem.interactions, function (interaction) {
                                    areItemSessionInteractionsValid = areItemSessionInteractionsValid && interaction.validateResponsesErrors.length === 0;
                                });

                                hasUnskippableValidateResponsesErrors = hasUnskippableValidateResponsesErrors || !areItemSessionInteractionsValid;
                            }
                        });

                        return hasUnskippableValidateResponsesErrors;
                    },

                    // return a flag if there are soft validation errors (not currently used, but could be later if we support different QTI configurations later KEEP)
                    getHasSoftValidationErrors: function () {

                        var hasSoftValidationErrors = false;

                        _.each(AsmtService.currentAssessmentTask.itemSessions, function (itemSession) {

                            // soft validation means there are validateResponseErrors, but the validateResponse flag is false and the softValidationFlag is true
                            _.each(itemSession.assessmentItem.interactions, function (interaction) {

                                // we only support soft validation if allow skipping is true and validateResponses is false
                                hasSoftValidationErrors = hasSoftValidationErrors || (interaction.useSoftValidation && interaction.allowSkipping && !interaction.validateResponses && interaction.validateResponsesErrors.length > 0);
                            });
                        });

                        return hasSoftValidationErrors;
                    },

                    // this must be called if you want the assessment title/configs to be used
                    setAssessment: function (assessment_in) {
                        AsmtService.assessment = assessment_in;
                        CSSService.loadStyles('assessment-level-css', AsmtService.assessment.stylesheets || '');
                    },

                    // a task set is a set of tasks :-) The UI will cache the task set more or less while moving through them
                    // {
                    //     taskSet: <taskSetObject which will contain a taskId and tasks>,  // must contain taskSetIndex within this object
                    //
                    //     totalTaskSetCount: <optional assessment session total task sets count>,
                    //
                    //     totalTaskCount: <optional count for total tasks within the assessment>,
                    //     totalTaskIndex: <optional current index for this task within the entire assessment>
                    // }
                    setAssessmentTaskSet: function (taskSetData) {

                        if (!taskSetData || !taskSetData.taskSet) {
                            throw new Error('AsmtService.taskSetData.noTaskSetObject');
                        }

                        // set some optional taskSet indexing and count properties
                        AsmtService.state.totalTaskSetIndex = taskSetData.taskSet.taskSetIndex; // NOTE: taskSetIndex should be within the task object
                        AsmtService.state.totalTaskSetCount = taskSetData.totalTaskSetCount;

                        // set some optional task indexing and count properties
                        AsmtService.state.totalTaskCount = taskSetData.totalTaskCount;
                        AsmtService.state.totalTaskIndex = taskSetData.totalTaskIndex;

                        // get the taskSet object
                        var normalizedAssessmentTaskSet = normalizeAssessmentTaskSet(taskSetData.taskSet);

                        // reset the currentTaskSetTaskIndex
                        AsmtService.state.currentTaskSetTaskIndex = false; // this will tell the navigation to start from the beginning when we call next
                        AsmtService.state.currentTaskSetTaskCount = normalizedAssessmentTaskSet.tasks.length;
                        AsmtService.currentAssessmentTaskSet = normalizedAssessmentTaskSet;

                        // clear out the current assessmentTask to signal we are starting fresh with a new taskSet
                        AsmtService.currentAssessmentTask = false;

                        // set the current taskSet duration, coerce into integer : this will work if duration is undefined, null, NaN, ""
                        existingTaskSetDuration = parseInt(taskSetData.taskSet.duration) || 0;

                        // finally now that the taskSet is loaded load the first task in this taskSet
                        AsmtService.nextAssessmentTask();
                    },

                    // you will need to run this method and override the default taskSetService which just throws errors
                    setTaskSetService: function (taskSetService_in) {
                        if (!(taskSetService_in instanceof TaskSetServiceClass)) {
                            throw new Error('AsmtService:setTaskSetService:requiresInstanceOfTaskSetServiceClass');
                        }
                        taskSetService = taskSetService_in;
                    },
                    // this can be used to identify when the assessment item has changed
                    getAssessmentTaskId: function () {
                        return AsmtService.currentAssessmentTask.taskId;
                    },

                    setAssessmentTaskPristine: function (newPristine) {
                        AsmtService.state.isAssessmentTaskPristine = newPristine;
                    },

                    getCurrentAssessmentTaskResponses: function () {

                        if (AsmtService.currentAssessmentTask === false) {
                            throw new Error('Cannot collect resonses from empty assessmentTask');
                        }

                        var currentTaskId = AsmtService.currentAssessmentTask.taskId;

                        var itemSessions = AsmtService.currentAssessmentTask.itemSessions;
                        var responseData = {};

                        // store the updated duration back on the task
                        existingTaskSetDuration = existingTaskSetDuration + timer.getTimeElapsed();
                        // setup the base structure
                        responseData[currentTaskId] = {
                            "duration": existingTaskSetDuration,
                            "responses": []
                        };

                        var j;
                        for (var i=0; i < itemSessions.length; i++) {
                            for (j=0; j < itemSessions[i].assessmentItem.interactions.length; j++) {

                                // generate response objects for the server
                                responseData[currentTaskId].responses.push({
                                    "itemSessionId": itemSessions[i].itemSessionId,
                                    "responseIdentifier": itemSessions[i].assessmentItem.interactions[j].responseIdentifier,
                                    "values": itemSessions[i].assessmentItem.interactions[j].values
                                });
                            }
                        }

                        return responseData;
                    },

                    updateValidationState: function () {

                        // update the count of interactions complete
                        AsmtService.state.interactionsComplete = _.reduce(AsmtService.state.interactionsList, function (memo, interaction) {
                            return memo + (interaction.validate().length === 0 ? 1 : 0);
                        }, 0);

                        // update the flag for if the entire assessmentTask is complete
                        AsmtService.state.assessmentTaskComplete = AsmtService.state.interactionsComplete === AsmtService.state.interactionsCount;

                        // lastly try to update scores
                        tryUpdateScores();
                    },

                    nextAssessmentTask: function () {
                        submitCurrentAssessmentTaskAndRetrieve('next');
                    },

                    previousAssessmentTask: function () {
                        submitCurrentAssessmentTaskAndRetrieve('previous');
                    },

                    pause: function () {
                        pauseAssessment();
                    },

                    setDisabled: function (isDisabled) {
                        AsmtService.state.isDisabled = isDisabled;
                    },

                    toggleShowScores: function () {
                        AsmtService.state.showScores = !AsmtService.state.showScores;
                        tryUpdateScores();
                    },

                    clearTaskSetScores: function () {
                        return clearTaskSetScores();
                    },

                    isScoreDisabled: function () {
                        return getIsScoreDisabled();
                    }

                };


                /*=========== LISTENERS ===========*/


                /*=========== INITIALIZTION ===========*/

                // At least start with the default blank TaskSetServiceClass instance
                // This will need to be run externally to set a real instance of the TaskSetServiceClass
                AsmtService.setTaskSetService(new TaskSetServiceClass({}));


                /*=========== SERVICE PASSBACK ===========*/

                return AsmtService;

            }
        ];
    });

})();

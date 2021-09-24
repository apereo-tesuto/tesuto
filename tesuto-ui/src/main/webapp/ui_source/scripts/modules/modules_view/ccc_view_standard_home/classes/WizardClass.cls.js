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

    // The Wizard Class is used to share state across several wizard components
    // It keeps track of some internal state that can be used to render steps, render a progress bar, or a summary of all the field values

    angular.module('CCC.View.Home').factory('WizardClass', [

        'ObservableEntity',

        function (ObservableEntity) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var defaultOnBeforeCancel = function (continueCancel) {
                continueCancel();
            };


            /*============ CLASS DECLARATION ============*/

            var WizardClass = function (configs_in) {

                var defaults = {
                    immutableData: {},                          // this data will be accessible to each step of the wizard but SHOULD NOT BE CHANGED within the wizard
                    model: {},                                  // this is the model that all the steps will look for data on and will add data to
                    steps: [],                                  // an array of steps (that can contain nested steps)
                    onBeforeCancel: defaultOnBeforeCancel,      // you can hook into the cancel workflow if you want to warn them.. the callback should eventually call the callback passed in to continue
                    autoCleanModel: true                        // you can pass false to hold on to values on the model that were generated in steps that are no longer required due to going back and changing values in previous steps
                                                                // this can improve the UX if users change their minds and don't want to loose information, when the wizard is done, you can call wizardClassInstance.cleanModel() to discard fields from non-required steps
                };

                /**
                 * When you pass steps into the directive, they should look like this:
                 * {
                 *
                 *     isRequired: function (immutableData, model) {},                  // optional, can be a boolean or a function that returns a boolean, will determine if this step/steps should be included in the flow
                 *
                 *     ---- SINGLE STEP ----
                 *
                 *     // the template will have access to immutableData, model, onReady callback, wizardInstance, isDisabled
                 *     // when your template is ready you must call $scope.viewReady(isValid) and pass in a callback that will return wether or not the current view is valid or not this will ensure any form validation you are using can show and prevent the next view
                 *
                 *     template: '<some-directive immutable-data="immutableData" model="model" on-ready="onReady" wizard-instance="wizardInstance" is-disabled="isDisabled"></some-directive>',
                 *
                 *     stepLabel: 'some text or translate phrase'                       // considering using this in case we want to render a progress tree? If so, will need to roll all these configs into a instance of some class to pass in to both components
                 *
                 *     nextButton: {title: '', iconClass: ''},                          // optional to override the next button
                 *     backButton: {title: '', iconClass: ''}                           // optional to override the back button
                 *
                 *     cleanable: undefined                                             // set to false if when this step if you want to avoid the attribute from being removed when the step is not required
                 *
                 *     fields: {                                                        // fields to remove from the model when this step's isRequired is false (fields this step will effect) will also recursively clear fields from any child steps
                 *         fieldA: {
                 *             fieldLabel: '',                                          // optional, will use this in the UI for when fieldA is displayed, else fieldA will be used
                 *             emptyValue: function (immutableData, model) {},          // optional, can be function or any other value and will be used if this step is not required to set the field, default is to delete the property
                 *             valueMapper: function (value) {return map[value];}       // optional, will use this in the UI to display a user friendly version of the value of the field
                 *         }
                 *     }
                 *
                 *     ---- MULTIPLE NESTED STEPS ----
                 *
                 *     steps: []                                                        // MANDATORY if template not provided, you can pass in an array of step objects of the same form. The advantage is grouping steps using higher level isRequired logic
                 * }
                 */

                // extend the ObservableEntity class to give it listeners
                var that = this;
                ObservableEntity.call(that);
                // merge in the defaults onto the instance
                $.extend(true, that, defaults, configs_in || {});


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                var stepId = 0;
                var getStepId = function () {
                    return stepId++;
                };

                var firstStepHasLoaded = false;

                var currentStepLoaded = false;
                var currentStepIsValid = function () { return false; };

                // a linear flat array of the steps
                var stepsFlattened = [];

                // a look up by id
                var stepMap = {};

                // recursively add steps and populate cached arrays and maps
                var addSteps = function (steps, stepArray, map, isParentRequired) {

                    _.each(steps, function (step) {

                        // if this is a single step with a template
                        if (step.template) {

                            // they can just put the name of the directive and we can auto populate all the directive data as a convenience
                            if (step.template.indexOf('<') === -1) {
                                step.template = '<' + step.template + ' class="ccc-wizard-step no-outline" tabindex="-1" immutable-data="immutableData" model="model" on-ready="onReady" wizard-instance="wizard" set-value-mapper="setValueMapper" is-disabled="wizard.isDisabled || wizard.isLoading"></' + step.template + '>';
                            }

                            var newStepId = step.id ? step.id : getStepId();

                            if (step.isRequired === true) {
                                step.isRequired = function () { return true; };
                            } else if (step.isRequired === false) {
                                throw new Error('WizardClass.stepWillNeverBeLoaded');
                            }

                            // we wrap the step with some internal information like the id and a reference to any parent required flags to supported nested steps
                            var stepWrapper = {

                                id: newStepId,
                                step: step,
                                isParentRequired: isParentRequired || function () { return true; },

                                // we construct an onReady callback to pass to each step. That step MUST call this when it is ready with an "isValid" callback that tells if this step is complete and ready to move on
                                onReady: function (isValidCallback) {
                                    that.stepIsReady(newStepId, isValidCallback);
                                },

                                /**
                                 * Users can always go to previous steps, but can't skip forward unless all previous steps are complete and the current step isValid is true.
                                 * On navigation to the next step, the previous step that was valid will be marked complete
                                 * Going to a previous step will mark that previous step and all following steps as incomplete
                                 * The reason is consider reloading the wizard with an immutableState and model that was valid to the last step (say 10 steps)
                                 * Step 4 may depend on combination of step 2 and 3 outcomes on the model plus some asynchronous data that may be loaded based on the current model state after changes made in step 3.
                                 * Validity of step 4 is determined internally, so we can't know in advance of the validity of step 4, so nobody should be able to skip beyond step 4 if any previous step changes
                                 * This use of this complete flag with the rules stated above allows us to ensure that the user moves through a linearly deterministic flow in the wizard
                                 */
                                complete: false
                            };

                            // now add the step to a map for quick lookup and a flat array of steps for fast traversal
                            map[stepWrapper.id] = stepWrapper;
                            stepArray.push(stepWrapper);

                        // otherwise if this is step with nested steps and we need to add them as a group but pass along the groups "isRequired" logic that will apply to each child step
                        } else {
                            addSteps(step.steps, stepArray, map, step.isRequired);
                        }
                    });
                };

                // based on the current state of things, some steps may not be required
                var getRequiredSteps = function () {

                    var isParentRequired;
                    return _.filter(stepsFlattened, function (stepWrapper) {

                        // first this step may depend on a parent being required as well, so check that
                        isParentRequired = stepWrapper.isParentRequired(that.immutableData, that.model);
                        return isParentRequired && stepWrapper.step.isRequired(that.immutableData, that.model);
                    });
                };
                var getNonRequiredSteps = function () {

                    var isParentRequired;
                    return _.filter(stepsFlattened, function (stepWrapper) {

                        // first this step may depend on a parent being required as well, so check that
                        isParentRequired = stepWrapper.isParentRequired(that.immutableData, that.model);
                        return !isParentRequired || !stepWrapper.step.isRequired(that.immutableData, that.model);
                    });
                };

                var clearFieldsForStep = function (stepWrapper) {

                    // some steps may be disabled but the value should not ever be cleared
                    if (stepWrapper.step.cleanable !== false) {

                        _.each(stepWrapper.step.fields, function (fieldObj, fieldKey) {

                            var keyList = fieldKey.split('.');
                            var currentModelRef = that.model;

                            // drill down the dot notated fieldKey
                            for (var i=0; i < keyList.length; i++) {

                                // if we are at the end of the dot noted key then clear the field
                                if (i === keyList.length - 1) {

                                    // if the step specifies an "emptyValue" for the field, use that
                                    if (fieldObj.hasOwnProperty('emptyValue')) {

                                        currentModelRef[keyList[i]] = fieldObj.emptyValue;

                                    // else totally delete the property from the model
                                    } else {

                                        delete currentModelRef[keyList[i]];
                                    }

                                } else {
                                    currentModelRef = currentModelRef[keyList[i]];
                                }
                            }
                        });
                    }
                };

                // set's the current step and returns the direction it took to get there as an integer -1, 0, 1
                var setCurrentStep = function (stepWrapper) {

                    var newStepIndex = stepsFlattened.indexOf(stepWrapper);
                    var oldStepIndex = stepsFlattened.indexOf(that.currentStep);

                    that.currentStep = stepWrapper;
                    that.currentStepIndex = newStepIndex;

                    currentStepLoaded = false;
                    currentStepIsValid = function () { return false; };
                    that.isDisabled = true;

                    if (!firstStepHasLoaded) {

                        firstStepHasLoaded = true;
                        return 0;

                    } else {

                        // mark all possible future steps as incomplete
                        var stepsFlattenedIndex = stepsFlattened.indexOf(that.currentStep);
                        for (var i=stepsFlattenedIndex; i < stepsFlattened.length; i++) {
                            stepsFlattened[i].complete = false;
                        }

                        // next clear all fields of all steps that are not required
                        if (that.autoCleanModel) {
                            that.cleanModel();
                        }

                        // last return the direction we went to get to this step
                        if (newStepIndex > oldStepIndex) {
                            return 1;
                        } else if (newStepIndex < oldStepIndex) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                };

                var getStepById = function (stepId) {
                    return _.find(stepsFlattened, function (step) {
                        return step.id === stepId;
                    });
                };


                /*=============== PUBLIC PROPERTIES =============*/

                that.currentStep = false;

                that.isDisabled = false;

                // this can be set externally
                // the wizard directive can use it to put a spinner on the next button and disable everything
                that.isLoading = false;


                /*=============== PUBLIC METHODS =============*/

                that.stepIsReady = function (stepId, isValidCallback) {

                    // checking the current step id avoids some async callback from a previously loaded step from saying we are good to go
                    if (that.currentStep.id === stepId) {
                        currentStepLoaded = true;
                        that.isDisabled = false;
                    }

                    stepMap[stepId].step.isValid = isValidCallback;
                };

                that.next = function () {

                    var requiredSteps = getRequiredSteps();
                    var currentStepIndex = that.currentStep ? requiredSteps.indexOf(that.currentStep) : -1;
                    var nextStepIndex = currentStepIndex + 1;

                    // when we move forward, we MUST make sure the current step is valid... this ensures that each step is valid based of previous steps and possible asynchronous dynamic data loaded into each step
                    if (firstStepHasLoaded && !currentStepLoaded) {
                        that.fireEvent('currentStepNotLoaded', [that.currentStep]);
                        return false;
                    }

                    // next check to make sure the current step is valid ( we don't have to check this on back, just next )
                    if (firstStepHasLoaded && !that.currentStep.step.isValid(that.immutableData, that.model)) {
                        that.fireEvent('currentStepInvalid', [that.currentStep]);
                        return false;
                    }

                    // next make sure we have a next step
                    if (nextStepIndex >= requiredSteps.length) {
                        that.fireEvent('noNextStep', [that.currentStep]);
                        return false;
                    }

                    var nextStep = requiredSteps[nextStepIndex];
                    var direction = setCurrentStep(nextStep);

                    that.fireEvent('stepLoaded', [nextStep, direction]);
                    return true;
                };

                that.back = function () {

                    var requiredSteps = getRequiredSteps();
                    var currentStepIndex = that.currentStep ? requiredSteps.indexOf(that.currentStep) : 1;
                    var previousStepIndex = currentStepIndex - 1;

                    if (previousStepIndex < 0) {
                        that.fireEvent('noPreviousSteps', [that.currentStep]);
                        return false;
                    }

                    var previousStep = requiredSteps[previousStepIndex];
                    var direction = setCurrentStep(previousStep);

                    that.fireEvent('stepLoaded', [previousStep, direction]);
                    return true;
                };

                that.initialize = function () {
                    that.next();
                };

                // could be used for summary or a diagram of navigation within the steps
                that.getRequiredSteps = function () {
                    return getRequiredSteps();
                };

                // convencience method to search through model given a dot notated key eg "key1.key2"
                that.getModelValue = function (modelKey) {

                    var keyList = modelKey.split('.');
                    var currentModelRef = that.model;

                    _.each(keyList, function (key) {
                        currentModelRef = currentModelRef[key];
                    });

                    return currentModelRef;
                };

                that.setValueMapper = function (stepId, fieldKey, valueMapper) {

                    var stepWrapper = getStepById(stepId);

                    if (stepWrapper.step) {
                        stepWrapper.step.fields[fieldKey].valueMapper = valueMapper;
                    }
                };

                // This keeps values previously entered within each step that are still required but clears values that are no longer valid in un-required steps
                // WARNING: it's possible that selections in STEP 1 can affect possible options selectable in STEP 3, so STEP 3 needs to be able to deal with changes in STEP 1 because STEP 3 values will not be cleared
                // if the user navigates back to STEP 1 and makes a change. It's best if the actual options within the step do not depend on previous steps values... only the inclusion of the step itself
                that.cleanModel = function () {
                    _.each(getNonRequiredSteps(), clearFieldsForStep);
                };

                that.setIsLoading = function (isLoading) {
                    that.isLoading = isLoading;
                };


                /*=============== INITIALIZTION =============*/

                addSteps(that.steps, stepsFlattened, stepMap);
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return WizardClass;
        }
    ]);

})();

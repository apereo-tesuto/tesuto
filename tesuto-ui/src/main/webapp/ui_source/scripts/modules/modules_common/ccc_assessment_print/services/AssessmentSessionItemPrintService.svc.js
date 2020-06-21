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

    angular.module('CCC.AssessmentPrint').service('AssessmentSessionItemPrintService', [

        '$q',
        '$timeout',
        'AssessmentItemTemplateService',
        'MathService',

        function ($q, $timeout, AssessmentItemTemplateService, MathService) {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentSessionItemPrintService;


            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var itemId = 0;
            var getItemId = function () {
                itemId++;
                return itemId;
            };

            var stimulusIndex = 0;
            var getStimulusIndex = function () {
                stimulusIndex++;
                return stimulusIndex;
            };

            var getItemSessionTemplates = function (itemSession) {

                var itemSessionTemplates = AssessmentItemTemplateService.getTemplatesForItemSession(itemSession);

                return {
                    assessmentItemTemplate: itemSessionTemplates.itemSessionTemplate,
                    bubbleSheetTemplate: itemSessionTemplates.itemSessionBubbleSheetTemplate
                };
            };

            var getStimulusTemplate = function (stimulusString, stimulusLabel) {

                var stimulusContainerElement = $('<div class="ccc-print-stimulus-container"></div>');
                var stimulusLabelElement = $('<div class="ccc-print-stimulus-label"><i class="fa fa-paperclip"></i>' + stimulusLabel + '</div>');
                var stimulusElement = $('<div class="ccc-print-stimulus">' + stimulusString + '</div>');

                stimulusLabelElement.appendTo(stimulusContainerElement);
                stimulusElement.appendTo(stimulusContainerElement);

                return stimulusContainerElement;
            };

            // this function allows you to scan the column items to determine if we need to force a certain column layout
            var validateColumnLayout = function (columns) {

                var forceSingleColumnLayout = false;

                // right now the only rule we have is to force single columns when we have a multiple choice with orientation of horizontal
                if (columns.length > 1) {

                    _.each(columns[1], function (item) {
                        if (item.itemSession) {
                            _.each(item.itemSession.assessmentItem.interactions, function (interaction) {
                                if (interaction.type === 'choiceInteraction' && interaction.orientation === 'horizontal') {
                                    forceSingleColumnLayout = true;
                                }
                            });
                        }
                    });
                }

                if (forceSingleColumnLayout) {
                    columns[0] = columns[0].concat(columns[1]);
                    columns.pop();
                }
            };

            var convertTaskToRenderItems = function (task) {

                var renderItemRows = {
                    assessmentItemRow: {
                        columns: [
                            // [
                            //     {
                            //         id: 1,
                            //         template: $('<div></div>')
                            //     }
                            // ]
                        ]
                    },
                    bubbleSheetItemRow: {
                        columns: [
                            [
                            //     {
                            //         id: 1,
                            //         template: $('<div></div>')
                            //     }
                            ]
                        ]
                    }
                };

                var hasStimulus = task.stimulus && $.trim(task.stimulus !== '');

                var assessmentItemColumn = [];
                var stimulusItemId = false;

                // if we have a stimulus, let's pop in a first column that contains the stimulus with a new itemId
                if (hasStimulus) {

                    stimulusItemId = getItemId();
                    getStimulusIndex();
                    var stimulusLabel = ''; //Reference for questions ' + stimlusIndex;

                    renderItemRows.assessmentItemRow.columns.push([
                        {
                            id: stimulusItemId,
                            label: stimulusLabel,
                            template: getStimulusTemplate(task.stimulus, stimulusLabel)
                        }
                    ]);
                }

                // next add assessmentItems to the right columns
                _.each(task.itemSessions, function (itemSession) {

                    var itemSessionRenderTemplates = getItemSessionTemplates(itemSession);

                    assessmentItemColumn.push({
                        id: getItemId(),
                        itemSession: itemSession,
                        assessmentItemIndex: itemSession.itemSessionIndex,
                        associatedId: stimulusItemId,
                        template: itemSessionRenderTemplates.assessmentItemTemplate
                    });

                    renderItemRows.bubbleSheetItemRow.columns[0].push({
                        id: getItemId(),
                        itemSession: itemSession,
                        assessmentItemIndex: itemSession.itemSessionIndex,
                        template: itemSessionRenderTemplates.bubbleSheetTemplate
                    });
                });

                // lastly push in the column that contains all the rendered itemSessions
                renderItemRows.assessmentItemRow.columns.push(assessmentItemColumn);

                // now run a processor to validate the column layouts
                validateColumnLayout(renderItemRows.assessmentItemRow.columns);

                return renderItemRows;
            };

            var getAssessmentSessionTaskSetPrintItems = function (taskSet) {

                var renderItems = {
                    assessmentItem: {
                        rows: []
                    },
                    bubbleSheetItem: {
                        rows: []
                    }
                };

                _.each(taskSet.tasks, function (task) {

                    var taskRowItems = convertTaskToRenderItems(task);

                    renderItems.assessmentItem.rows.push(taskRowItems.assessmentItemRow);
                    renderItems.bubbleSheetItem.rows.push(taskRowItems.bubbleSheetItemRow);
                });

                return renderItems;
            };


            /*============ PUBLIC METHODS AND PROPERTIES ============*/

            AssessmentSessionItemPrintService = {

                // convertAssessmentTaskSetsToPrintItems must return lists for the assessment and bubble sheet in the following format
                // var itemList = [
                //     {
                //         rows: [
                //             {
                //                 columns: [
                //                     [   // column 1
                //                         {
                //                             id: 1,              // an assessment wide unique ID to be used by other items as a reference
                //                             template: $('<div></div>')   // template should be a dom string or possibly jQuery wrapped element
                //                         }
                //                     ],
                //                     [   // column 2
                //                         {
                //                              associatedId: 1,
                //                              template: $('<div></div>')
                //                         },
                //                         {item template},
                //                         {item template},
                //                         {item template}
                //                     ]
                //                 ]
                //             }
                //         ]
                //     }
                // ];
                convertAssessmentTaskSetsToPrintItems: function (assessmentSessionTaskSets) {

                    var assessmentItemList = [];
                    var bubbleSheetItemList = [];

                    _.each(assessmentSessionTaskSets, function (assessmentSessionTasksSet) {
                        var assessmentSessionTaskSetPrintItems = getAssessmentSessionTaskSetPrintItems(assessmentSessionTasksSet);
                        assessmentItemList.push(assessmentSessionTaskSetPrintItems.assessmentItem);
                        bubbleSheetItemList.push(assessmentSessionTaskSetPrintItems.bubbleSheetItem);
                    });

                    return {
                        assessmentItemList: assessmentItemList,
                        bubbleSheetItemList: bubbleSheetItemList
                    };
                },

                mathPostProcess: function (pageRowInstance) {
                    var deferred = $q.defer();

                    MathService.processTarget(pageRowInstance.element[0], function () {
                        deferred.resolve();
                    });

                    return deferred.promise;
                },

                imagePostProcess: function (pageRowInstance) {
                    var deferred = $q.defer();

                    var longImageLoadWarning;

                    var incompleteImages = 0;
                    var imagesCompleted = 0;
                    var imageCompleted = function () {
                        imagesCompleted++;
                        if (imagesCompleted === incompleteImages) {
                            $timeout.cancel(longImageLoadWarning);
                            deferred.resolve();
                        }
                    };

                    // for each image, make sure it is complete or add an event to track it's completion
                    pageRowInstance.element.find('img').each(function () {
                        if (!this.complete) {
                            incompleteImages++;
                            $(this).load(imageCompleted);
                        }
                    });
                    // for each object which could be an image in disguise, do the same thing
                    pageRowInstance.element.find('object').each(function () {
                        if(!this.objectLoaded) {
                            incompleteImages++;
                            $(this).load(imageCompleted);
                        }
                    });

                    if (incompleteImages === 0) {
                        deferred.resolve();
                    } else {
                        longImageLoadWarning = $timeout(function () {
                            window.alert('Warning! Some images or objects appear to be taking a long time to load...');
                        }, 15000);
                    }

                    return deferred.promise;
                }

            };

            /*============ SERVICE PASS BACK ============*/


            return AssessmentSessionItemPrintService;
        }
    ]);

})();
